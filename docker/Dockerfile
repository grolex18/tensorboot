FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk-headless

ADD run.sh /usr/local/tensorboot/run.sh
RUN chmod a+rx /usr/local/tensorboot/run.sh

ADD tensorboot-*.jar /usr/local/tensorboot/
ADD mobilenet_v2_1.4_224_frozen.pb /usr/local/tensorboot/model/mobilenet_v2_1.4_224_frozen.pb

CMD cd /usr/local/tensorboot && ./run.sh

EXPOSE 8080