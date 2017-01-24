#!/bin/sh

set -e

echo "Start redis service"
redis-server &

echo "Start celery"
celery -A scheduler.app worker -B --loglevel=warn &
flower -A scheduler.app --port=5555
