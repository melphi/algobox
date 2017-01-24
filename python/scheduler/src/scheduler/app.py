from celery import Celery
from celery.schedules import crontab
from os import environ
from scheduler.task.migrate_prices import MigratePrices
from scheduler.task.backup_mongo import BackupMongo


app = Celery('app',
             broker='redis://localhost',
             backend='redis://localhost')
app.conf.timezone = 'UTC'


@app.task
def task_backup_mongo(mongo_host, database_names, target_folder):
    BackupMongo(mongo_host=mongo_host, database_names=database_names,
                target_folder=target_folder).execute()


@app.task
def task_migrate_prices(mongo_host, database_master, database_stage):
    MigratePrices(mongo_host=mongo_host, database_master=database_master,
                  database_stage=database_stage).execute()


@app.on_after_configure.connect
def schedule_tasks(sender, **kwargs):
    migrate_params = {
        'mongo_host': environ['MIGRATION_MONGO_CONNECTION'],
        'database_master': environ['MIGRATION_DATABASE_MASTER'],
        'database_stage': environ['MIGRATION_DATABASE_STAGE']
    }
    sender.add_periodic_task(crontab(hour=5),
                             task_migrate_prices.si([], migrate_params),
                             name='Migrate prices')
    backup_params = {
        'mongo_host': environ['BACKUP_MONGO_HOST'],
        'database_names': environ['BACKUP_DATABASE_NAMES'].split(','),
        'target_folder': environ['BACKUP_TARGET_FOLDER']
    }
    sender.add_periodic_task(crontab(hour=5),
                             task_backup_mongo.si([], backup_params),
                             name='Backup mongo')


if __name__ == '__main__':
    app.start()
