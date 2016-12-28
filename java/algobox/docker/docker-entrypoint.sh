#!/bin/sh
set -e

/consul/consul agent \
    -data-dir=/consul/data \
    -config-dir=/consul/config \
    -bind=127.0.0.1 \
    -datacenter=algobox \
    -join="$CONSUL_SERVER_HOST"

exec "$1"
