#!/bin/sh
set -e

mkdir /etc/nginx/.ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
	-keyout /etc/nginx/.ssl/nginx.key \
	-out /etc/nginx/.ssl/nginx.crt \
	-subj "/C=/ST=/L=/O=/OU=/CN=0.0.0.0"

exec "$@"

