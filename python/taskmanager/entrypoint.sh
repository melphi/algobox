#!/usr/bin/env bash

set -e
celery -A app worker -B --loglevel=info
flower -A app --port=5555
