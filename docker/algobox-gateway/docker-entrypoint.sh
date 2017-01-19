#!/bin/sh
set -e

if [ ! -f /etc/nginx/.ssl/nginx.key ]; then
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout /etc/nginx/.ssl/nginx.key \
        -out /etc/nginx/.ssl/nginx.crt \
        -subj "/C=/ST=/L=/O=/OU=/CN=0.0.0.0"
        chown nginx.nginx /etc/nginx/.ssl/nginx.key
        chmod 440 /etc/nginx/.ssl/nginx.key
        chown nginx.nginx /etc/nginx/.ssl/nginx.crt
        chmod 440 /etc/nginx/.ssl/nginx.crt
fi

if [ -n "$HTPASSWD_CONTENT" ]; then
	echo "$HTPASSWD_CONTENT" > /etc/nginx/.htpasswd
	chown nginx.nginx /etc/nginx/.htpasswd
	chmod 440 /etc/nginx/.htpasswd
fi

exec "$@"
