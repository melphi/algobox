#!/usr/bin/env python3

from pymongo import ASCENDING, MongoClient
from pymongo.collection import Collection
from pymongo.errors import DuplicateKeyError, OperationFailure
from os import environ
from re import sub
from sys import stderr, stdout

_DEFAULT_COLLECTION_STAGE = 'priceTicksStage'
_DEFAULT_COLLECTION_MASTER_PREFIX = 'priceTicks_'


class MigratePrices(object):
    MAX_ITERATIONS = 5000
    BUFFER_LIMIT = 200

    """Migrates data from the stage collection to the master collection.

    Prices are bulk loaded from stage and saved in target collections
    by instrument id. Record not imported due to key violation errors are
    market as imported: False and require manual intervention."""

    def __init__(self, *, database_master, database_stage):
        self._indexed = []
        self._database_master = database_master
        self._client_master = MongoClient(database_master['host'])
        if database_master['host'] != database_stage['host']:
            client_stage = MongoClient(database_stage['host'])
        else:
            client_stage = self._client_master
        self._collection_stage = client_stage[
            database_stage][_DEFAULT_COLLECTION_STAGE]

    @staticmethod
    def _get_master_collection_name(instrument_id):
        """
        Args:
            instrument_id (str)
        """
        return _DEFAULT_COLLECTION_MASTER_PREFIX + sub(
            '[^A-Za-z0-9]', '_', instrument_id).upper()

    def _get_prices_stage(self):
        return self._collection_stage.find({'imported': {'$exists': False}}) \
            .limit(self.BUFFER_LIMIT)

    def _get_or_create_collection(self, collection_name):
        """Returns the collection with the required indexes.
        Args:
            collection_name (pymongo.collection.Collection)
        Returns:
            pymongo.collection.Collection
        """
        collection = self._client_master[
            self._database_master['database']][collection_name]
        if collection_name in self._indexed:
            return collection

        try:
            indexes = collection.index_information()
        # When the table does not exist it needs to be created.
        except OperationFailure:
            collection = Collection(
                database=self._client_master[self._database_master],
                name=collection_name,
                create=True)
            indexes = collection.index_information()

        for index_name in indexes:
            if str(index_name).startswith('time'):
                self._indexed.append(collection_name)
                return collection
                # If index was found create one and update indexed list.
        collection.create_index([('time', ASCENDING)], unique=True)
        self._indexed.append(collection_name)
        return collection

    def _migrate_buffer(self, buffer):
        """Prices are moved one by one to reduce consistency issues, imported
        records are market with imported True.
        Args:
            buffer (dict)
        """
        failed_ids = []
        completed_ids = []
        for collection_name, prices in buffer.items():
            collection_master = self._get_or_create_collection(collection_name)
            for price_stage in prices:
                try:
                    collection_master.insert_one(price_stage)
                    completed_ids.append(price_stage['_id'])
                except DuplicateKeyError:
                    print('Duplicated record, skipping [%r].' %
                          price_stage['_id'])
                    failed_ids.append(price_stage['_id'])
        self._collection_stage.update_many(
            filter={'_id': {'$in': failed_ids}},
            update={'$set': {'imported': False}})
        self._collection_stage.delete_many(
            filter={'_id': {'$in': completed_ids}})

    def execute(self):
        for _ in range(self.MAX_ITERATIONS):
            prices_stage = self._get_prices_stage()
            if not prices_stage.count() > 0:
                return
            buffer = {}
            for price_stage in prices_stage:
                master_collection_name = self._get_master_collection_name(
                    price_stage['instrument'])
                if master_collection_name in buffer:
                    buffer[master_collection_name].append(price_stage)
                else:
                    buffer[master_collection_name] = [price_stage]
            self._migrate_buffer(buffer)

if __name__ == '__main__':
    db_master = db_stage = None
    try:
        db_stage = {'host': environ['MONGO_STAGE_HOST'],
                    'database': environ['MONGO_STAGE_DATABASE']}
        db_master = {'host': environ['MONGO_MASTER_HOST'],
                     'database': environ['MONGO_MASTER_DATABASE']}
    except Exception as e:
        stderr.write('Missing environment variable [%s].' % e)
        exit(1)
    try:
        stdout.write('Migrating data from [%s.%s] to [%s.%s*].\n' % (
            db_stage, _DEFAULT_COLLECTION_STAGE,
            db_master, _DEFAULT_COLLECTION_MASTER_PREFIX))
        migrate_prices = MigratePrices(
            database_stage=db_stage, database_master=db_master)
        migrate_prices.execute()
        stdout.write('Data migration completed.\n')
        exit(0)
    except OperationFailure as e:
        stderr.write("Data migration error: [%s] [%r]\n" % (e, e.details))
        exit(1)
    except Exception as e:
        stderr.write("Data migration error: [%s]\n" % e)
        exit(1)
