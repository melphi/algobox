FROM python:3.6-slim

# Install prerequisites
RUN apt-get update
RUN apt-get -y install cron
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
