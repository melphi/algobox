FROM jupyter/base-notebook

# Install system requirements.
USER root
RUN apt-get update -y
RUN apt-get install tk build-essential libsnappy-dev -y
RUN apt-get autoclean -y
RUN apt-get autoremove -y

ADD http://downloads.sourceforge.net/project/ta-lib/ta-lib/0.4.0/ta-lib-0.4.0-src.tar.gz?r=&ts=1482845301 /opt/src/talib.tgz
RUN tar xvfz /opt/src/talib.tgz -C /opt/src
WORKDIR /opt/src/ta-lib
RUN ./configure --prefix=/usr
RUN make
RUN make install
