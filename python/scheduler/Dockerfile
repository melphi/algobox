FROM python:3.6-slim

RUN apt-get update -y
RUN apt-get install redis-server -y

COPY src/ /opt/
COPY entrypoint.sh /opt/
COPY requirements.txt /opt/

WORKDIR /opt/
RUN pip install -r requirements.txt

RUN useradd -ms /bin/bash foo
RUN chown foo.foo -R /opt
USER foo
WORKDIR /opt/

ENV MIGRATION_MONGO_CONNECTION mongodb://algobox-mongo
ENV MIGRATION_DATABASE_MASTER datamaster
ENV MIGRATION_DATABASE_STAGE priceTicksStage
ENV BACKUP_MONGO_HOST algobox-mongo
ENV BACKUP_DATABASE_NAMES datamaster,datacollector
ENV BACKUP_TARGET_FOLDER gs://algobox-backup/

ENTRYPOINT ["sh", "/opt/entrypoint.sh"]
CMD []

EXPOSE 5555
