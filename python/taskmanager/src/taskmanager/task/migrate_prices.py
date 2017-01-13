#!/usr/bin/env python3

from pymongo import ASCENDING, MongoClient
from pymongo.collection import Collection
from pymongo.errors import DuplicateKeyError, OperationFailure
from re import sub
from sys import stderr, stdout

_MAX_ITERATIONS = 5000
_BUFFER_LIMIT = 200
_MONGO_HOST = 'mongodb://10.132.0.3,10.132.0.2'
_DATABASE_STAGE = 'datacollector'
_DATABASE_MASTER = 'datamaster'
_COLLECTION_STAGE = 'priceTicksStage'
_COLLECTION_MASTER_PREFIX = 'priceTicks_'
_MONGO_REPLICA_SET = 'rs0'


class MigratePrices(object):
    """Migrates data from the stage collection to the master collection.

    Prices are bulk loaded from stage and saved in target collections
    by instrument id. Record not imported due to key violation errors are
    market as imported: False and require manual intervention."""

    def __init__(self, max_iterations, buffer_limit, mongo_host,
                 database_stage, database_master):
        self._indexed = []
        self._max_iterations = max_iterations
        self._buffer_limit = buffer_limit
        self._mongo_host = mongo_host
        self._database_master = database_master
        self._client = MongoClient(mongo_host)
        self._collection_stage = self._client[
            database_stage][_COLLECTION_STAGE]

    @staticmethod
    def _get_master_collection_name(instrument_id):
        """
        Args:
            instrument_id (str)
        """
        return _COLLECTION_MASTER_PREFIX + sub(
            '[^A-Za-z0-9]', '_', instrument_id).upper()

    def _get_prices_stage(self):
        return self._collection_stage.find({'imported': {'$exists': False}}) \
            .limit(self._buffer_limit)

    def _get_or_create_collection(self, collection_name):
        """Returns the collection with the required indexes.
        Args:
            collection_name (pymongo.collection.Collection)
        Returns:
            pymongo.collection.Collection
        """
        collection = self._client[self._database_master][collection_name]
        if collection_name in self._indexed:
            return collection

        try:
            indexes = collection.index_information()
        # When the table does not exist it needs to be created.
        except OperationFailure:
            collection = Collection(
                database=self._client[self._database_master],
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
        for _ in range(self._max_iterations):
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
    try:
        stdout.write('Migrating data from [%s.%s] to [%s.%s*].\n' % (
            _DATABASE_STAGE, _COLLECTION_STAGE, _DATABASE_MASTER,
            _COLLECTION_MASTER_PREFIX))
        migrate_prices = MigratePrices(
            max_iterations=_MAX_ITERATIONS, buffer_limit=_BUFFER_LIMIT,
            mongo_host=_MONGO_HOST, database_stage=_DATABASE_STAGE,
            database_master=_DATABASE_MASTER)
        migrate_prices.execute()
        stdout.write('Data migration completed.\n')
        exit(0)
    except OperationFailure as e:
        stderr.write("Data migration error: [%s] [%r]\n" % (e, e.details))
        exit(1)
    except Exception as e:
        stderr.write("Data migration error: [%s]\n" % e)
        exit(1)
