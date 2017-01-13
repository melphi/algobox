FROM java:openjdk-8-jre

# Create non root user.
RUN groupadd user
RUN useradd -g user user
USER user
WORKDIR /home/user

# TODO: Run Consul in agent mode.
#RUN mkdir -p /consul/data && \
#    mkdir -p /consul/config && \
#    chown -R user:user /consul
#COPY consul /consul/
#COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
#RUN chmod 755 /usr/local/bin/docker-entrypoint.sh
#ENV CONSUL_SERVER_HOST=<undefined>
#EXPOSE 8301 8301/udp
#ENTRYPOINT ["docker-entrypoint.sh"]
#CMD []
