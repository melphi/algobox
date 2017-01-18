FROM nginx:alpine

RUN apk add --no-cache openssl

COPY index.html /var/www/html/index.html
COPY default.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /usr/bin/docker-entrypoint.sh
RUN chmod 755 /usr/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]


