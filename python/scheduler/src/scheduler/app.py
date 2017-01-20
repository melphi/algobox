from celery import Celery
from scheduler.task.test import test_task

app = Celery('app',
             broker='redis://localhost',
             backend='redis://localhost')
app.conf.timezone = 'UTC'


@app.on_after_configure.connect
def schedule_tasks(sender, **kwargs):
    # Calls test('hello') every 10 seconds.
    sender.add_periodic_task(10.0, test_task.s('hello'), name='add every 10')

    # # Calls test('world') every 30 seconds
    # sender.add_periodic_task(30.0, test_task.s('world'), expires=10)
    #
    # # Executes every Monday morning at 7:30 a.m.
    # sender.add_periodic_task(
    #     crontab(hour=7, minute=30, day_of_week=1),
    #     test.s('Happy Mondays!'),
    # )

@app.task
def test_task(arg):
    test_task(arg)
