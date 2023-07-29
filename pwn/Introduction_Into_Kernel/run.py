#!/usr/bin/env python3

import urllib.request
import subprocess
import os
import uuid

fname = str(uuid.uuid4())

path = ""

try:
    subprocess.run(["./launch.sh", path])
except Exception:
	print("Some error occurred while running qemu. Try again or contact support :(\n")

if path != "":
	os.unlink(path)