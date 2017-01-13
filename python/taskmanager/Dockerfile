FROM python:3.6-slim

RUN apt-get update -y
RUN apt-get install redis

COPY src/ /opt/
COPY entrypoint.sh /opt/
COPY requirements.txt /opt/

WORKDIR /opt/
RUN pip install -r requirements.txt

RUN useradd -ms /bin/bash foo
USER foo
WORKDIR /opt/taskmanager

ENTRYPOINT ["sh", "/opt/entrypoint.sh"]
CMD[]

EXPOSE 5555
