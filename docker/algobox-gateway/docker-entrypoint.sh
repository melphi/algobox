#!/bin/sh
set -e

if [ ! -f /etc/nginx/.ssl/nginx.key ]; then
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout /etc/nginx/.ssl/nginx.key \
        -out /etc/nginx/.ssl/nginx.crt \
        -subj "/C=/ST=/L=/O=/OU=/CN=0.0.0.0"
fi

if [ -n "$HTPASSWD_CONTENT" ]; then
	echo "$HTPASSWD_CONTENT" > /etc/nginx/.htpasswd
	echo "File content "
	cat /etc/nginx/.htpasswd
fi

exec "$@"

