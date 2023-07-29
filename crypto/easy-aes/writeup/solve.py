from pwn import *
import os
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Util.number import bytes_to_long, long_to_bytes

p = process(['python3', '../src/chall.py'])

t = os.urandom(128)
p.sendlineafter(b'> ', b'1')
p.sendlineafter(b'plaintext = ', str(bytes_to_long(t)).encode())
res1 = int(p.recvuntil(b'\n')[len('ciphertext = '):-1])

p.sendlineafter(b'> ', b'2')
res2 = int(p.recvuntil(b'\n')[len('ciphertext = '):-1])

p.sendlineafter(b'> ', b'3')
p.sendlineafter(b'secret: ', str(bytes_to_long(unpad(long_to_bytes(bytes_to_long(pad(t, AES.block_size)) ^ res1 ^ res2), AES.block_size))).encode())
p.interactive()