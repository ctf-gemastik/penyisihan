FROM ubuntu:18.04
RUN apt-get update && \
    apt-get -y install python3 socat && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /home/nobody
COPY src .

USER nobody
EXPOSE 9999
CMD socat TCP-LISTEN:9999,reuseaddr,fork EXEC:"python3 chall.py"
