FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && apt-get upgrade -y
RUN apt-get install qemu-system-x86 socat -y
RUN /usr/sbin/useradd --no-create-home -u 1000 ctf

COPY release/bzImage /home/ctf/
COPY release/initramfs.cpio.gz /home/ctf/
COPY release/launch.sh /home/ctf/
COPY run.py /home/ctf/

WORKDIR "/home/ctf/"

RUN chmod 555 bzImage initramfs.cpio.gz launch.sh run.py
RUN chmod +x /home/ctf/launch.sh

USER ctf

CMD socat \
      TCP-LISTEN:1337,reuseaddr,fork \
      EXEC:"python3 /home/ctf/run.py"