#!/usr/bin/env python3

from datetime import datetime
from os import path, makedirs
from subprocess import Popen
from sys import stderr, stdout

_MONGO_HOST = '10.132.0.3'
_MONGO_DATABASE_NAMES = ['datamaster', 'datacollector']
_TEMPORARY_FOLDER = '/tmp/mongo-backup/'
_TARGET_FOLDER = 'gs://algobox-backup/'


def _create_mongo_backup(archive_path, database_name):
    command_args = ["mongodump",
                    "--host=" + _MONGO_HOST,
                    "--db=" + database_name,
                    "--archive=" + archive_path,
                    "--gzip"]
    process = Popen(command_args)
    result, err = process.communicate(timeout=600)
    assert not err, 'Process raised errorr [%s].' % err


def _move_to_buket(archive_path):
    command_args = ["gsutil", "mv", archive_path, _TARGET_FOLDER]
    process = Popen(command_args)
    result, err = process.communicate(timeout=600)
    assert not err, 'Process raised errorr [%s].' % err


def _backup_database(database_name):
    if not path.exists(_TEMPORARY_FOLDER):
        makedirs(_TEMPORARY_FOLDER)
    archive_path = "%s%s_%s.gz" % \
                   (_TEMPORARY_FOLDER,
                    database_name,
                    datetime.utcnow().strftime("%Y%m%d_%H%M"))
    _create_mongo_backup(archive_path, database_name)
    _move_to_buket(archive_path)
    stdout.write("Backup of [%s]/[%s] completed.\n" %
                 (_MONGO_HOST, database_name))

if __name__ == '__main__':
    error = False
    for database in _MONGO_DATABASE_NAMES:
        try:
            _backup_database(database)
        except Exception as e:
            error = True
            stderr.write("Backup error: [%s]\n" % e)
    exit(1 if error else 0)
