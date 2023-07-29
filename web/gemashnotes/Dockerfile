FROM node:18-alpine

RUN apk update && apk add curl

WORKDIR /opt/apps

ENV NODE_ENV=production
RUN npm install pm2 -g
RUN mkdir /.pm2 && \
    chmod -R 777 /.pm2

COPY src/gemashnotes/package*.json ./
RUN npm install
RUN mkdir /.npm && \
    chmod -R 777 /.npm

COPY src/flag.txt /y0r_pr1z3
RUN chmod 444 /y0r_pr1z3

COPY src/gemashnotes .
COPY src/startup.sh .
RUN chmod +x startup.sh

USER nobody

EXPOSE 3000

ENTRYPOINT [ "./startup.sh" ]