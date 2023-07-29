#!/usr/bin/env sh
PERIOD=300 # second
while true
do
    npm run resetMongo
    pm2 start npm -- start
    sleep $PERIOD
    pm2 stop all
done
