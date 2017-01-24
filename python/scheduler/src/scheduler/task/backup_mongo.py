#!/usr/bin/env python3

from datetime import datetime
from os import path, makedirs
from subprocess import Popen
from sys import stderr, stdout

_DEFAULT_MONGO_HOST = 'algobox-mongo'
_DEFAULT_MONGO_DATABASE_NAMES = ['datamaster', 'datacollector']
_TEMPORARY_FOLDER = '/tmp/mongo-backup/'
_DEFAULT_TARGET_FOLDER = 'gs://algobox-backup/'


class BackupMongo(object):
    def __init__(self, *, mongo_host, database_names, target_folder):
        self._mongo_host = mongo_host
        self._database_names = database_names
        self._target_folder = target_folder

    def _create_mongo_backup(self, archive_path, database_name):
        command_args = ["mongodump",
                        "--host=" + self._mongo_host,
                        "--db=" + database_name,
                        "--archive=" + archive_path,
                        "--gzip"]
        process = Popen(command_args)
        result, err = process.communicate(timeout=600)
        assert not err, 'Process raised errorr [%s].' % err

    def _move_to_buket(self, archive_path):
        command_args = ["gsutil", "mv", archive_path, self._target_folder]
        process = Popen(command_args)
        result, err = process.communicate(timeout=600)
        assert not err, 'Process raised errorr [%s].' % err

    def _backup_database(self, database_name):
        if not path.exists(_TEMPORARY_FOLDER):
            makedirs(_TEMPORARY_FOLDER)
        archive_path = "%s%s_%s.gz" % \
                       (_TEMPORARY_FOLDER,
                        database_name,
                        datetime.utcnow().strftime("%Y%m%d_%H%M"))
        self._create_mongo_backup(archive_path, database_name)
        self._move_to_buket(archive_path)
        stdout.write("Backup of [%s]/[%s] completed.\n" %
                     (self._mongo_host, database_name))

    def execute(self):
        success = True
        for database_name in self._database_names:
            try:
                self._backup_database(database_name)
            except Exception as e:
                success = False
                stderr.write("Backup error: [%s]\n" % e)
        return success

if __name__ == '__main__':
    error = not BackupMongo(mongo_host=_DEFAULT_MONGO_HOST,
                            database_names=_DEFAULT_MONGO_DATABASE_NAMES,
                            target_folder=_DEFAULT_TARGET_FOLDER).execute()
    exit(1 if error else 0)
