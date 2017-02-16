#!/bin/sh

set -e

echo "Start cron service"
cron

echo "Cron started"
touch /var/log/cron.log
tail -F /var/log/syslog /var/log/cron.log
