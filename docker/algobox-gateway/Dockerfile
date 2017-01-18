FROM nginx:alpine

RUN apk add --no-cache openssh

COPY index.html /var/www/html/index.html
COPY default.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]

