#!/bin/sh

set -e

echo "Start redis service"
redis-server &

echo "Start celery"
celery -A scheduler.app worker -B --loglevel=info
sudo flower -A scheduler.app --port=5555
