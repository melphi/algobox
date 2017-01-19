FROM nginx:alpine

RUN apk add --no-cache openssl

COPY default.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /usr/bin/docker-entrypoint.sh

RUN mkdir mkdir /etc/nginx/.ssl
RUN chmod 755 /usr/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
