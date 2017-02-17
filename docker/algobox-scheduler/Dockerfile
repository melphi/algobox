FROM python:3.6-slim

# Install prerequisites
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
RUN echo "deb http://repo.mongodb.org/apt/debian jessie/mongodb-org/3.4 main" | tee /etc/apt/sources.list.d/mongodb-org-3.4.list
RUN apt-get update -y
RUN apt-get install -y cron mongodb-org-tools
RUN pip install pymongo

# Copy files
RUN rm -fr /etc/cron.hourly/
RUN rm -fr /etc/cron.daily/

COPY crontab /etc/crontab
COPY cron.daily/* /etc/cron.daily/
COPY cron.hourly/* /etc/cron.hourly/
COPY entrypoint.sh /usr/local/bin/

RUN chmod 755 /usr/local/bin/entrypoint.sh

# Entrypoint
ENTRYPOINT []
CMD ["/usr/local/bin/entrypoint.sh"]
