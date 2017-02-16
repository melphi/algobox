#!/usr/bin/env python3

from datetime import datetime
from os import environ, path, makedirs
from subprocess import Popen
from sys import stderr, stdout

_TEMPORARY_FOLDER = '/tmp/mongo-backup/'


class BackupMongo(object):
    def __init__(self, *, databases, target_folder):
        """
        Args:
            databases (list of dict)
            target_folder (str)
        """

        self._databases = databases
        self._target_folder = target_folder

    @staticmethod
    def _create_mongo_backup(archive_path, database_host, database_name):
        command_args = ["mongodump",
                        "--host=" + database_host,
                        "--db=" + database_name,
                        "--archive=" + archive_path,
                        "--gzip"]
        process = Popen(command_args)
        result, err = process.communicate(timeout=600)
        assert not err, 'Process raised error [%s].' % err

    def _move_to_buket(self, archive_path):
        command_args = ["mv", archive_path, self._target_folder]
        process = Popen(command_args)
        result, err = process.communicate(timeout=600)
        assert not err, 'Process raised errorr [%s].' % err

    def _backup_database(self, database_host, database_name):
        if not path.exists(_TEMPORARY_FOLDER):
            makedirs(_TEMPORARY_FOLDER)
        archive_path = "%s%s_%s.gz" % \
                       (_TEMPORARY_FOLDER,
                        database_name,
                        datetime.utcnow().strftime("%Y%m%d_%H%M"))
        self._create_mongo_backup(archive_path, database_host, database_name)
        self._move_to_buket(archive_path)
        stdout.write("Backup of [%s]/[%s] completed.\n" %
                     (database_host, database_name))

    def execute(self):
        success = True
        for database in self._databases:
            try:
                self._backup_database(database['host'], database['database'])
            except Exception as e:
                success = False
                stderr.write("Backup error: [%s]\n" % e)
        return success

if __name__ == '__main__':
    target = None
    dbs = None
    try:
        target = environ['TARGET_FOLDER']
        dbs = [{'host': environ['MONGO_STAGE_HOST'],
                'database': environ['MONGO_STAGE_DATABASE']},
               {'host': environ['MONGO_MASTER_HOST'],
                'database': environ['MONGO_MASTER_DATABASE']}]
    except Exception as e:
        stderr.write('Missing environment variable [%s].' % e)
        exit(1)
    backup = BackupMongo(databases=dbs, target_folder=target)
    if backup.execute():
        stderr.write("Database backup completed.")
        exit(0)
    else:
        stderr.write("Database backup failed.")
        exit(1)
