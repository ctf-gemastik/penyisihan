FROM ubuntu:22.04

RUN apt-get update 
RUN apt-get install -y xinetd 
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /etc/xinetd.d/*

RUN useradd -U -m -s /bin/bash apaini

WORKDIR /home/apaini

COPY ./flag.txt .
COPY ./chall .
COPY ./run.sh ./run
COPY ./xinetd /etc/xinetd.d/xinetd
COPY ./startchall.sh /usr/bin/startchall
COPY ./username-* .

RUN chown -R root:apaini /home/apaini
RUN chmod -R 750 /home/apaini && chmod 440 flag.txt 
RUN mv flag.txt flag-$(md5sum flag.txt | awk '{print $1}').txt
RUN chmod 500 /etc/xinetd.d/xinetd && chmod 500 /usr/bin/startchall

EXPOSE 11101

CMD ["/usr/bin/startchall"]
