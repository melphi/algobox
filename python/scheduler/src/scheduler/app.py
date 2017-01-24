from celery import Celery
from celery.schedules import crontab
from scheduler.task.migrate_prices import MigratePrices


app = Celery('app',
             broker='redis://localhost',
             backend='redis://localhost')
app.conf.timezone = 'UTC'


@app.task
def migrate_prices_task(arg):
    # TODO: pass parameters.
    MigratePrices().execute()


@app.on_after_configure.connect
def schedule_tasks(sender, **kwargs):
    sender.add_periodic_task(crontab(hour=5), migrate_prices_task.si('hello'),
                             name='migrate prices')

if __name__ == '__main__':
    app.start()
