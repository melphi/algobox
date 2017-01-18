FROM java:openjdk-8-jre

# Create non root user.
RUN groupadd user
RUN useradd -g user user
USER user
WORKDIR /home/user

