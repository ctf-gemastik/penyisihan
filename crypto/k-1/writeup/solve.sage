from pwn import *

p = process(['python3', '../src/chall.py'])

xy = []

for _ in range(2):
    xy.append(eval(p.recvuntil(b'\n')[:-1]))

L = Matrix([
    [xy[0][1], xy[1][1], 1],
    [-xy[0][0], -xy[1][0], 1],
    [-xy[0][0]^2, -xy[1][0]^2, 1],
])
L = L.LLL()
pretty_print(L)

p.sendlineafter(b'password: ', str(L[1][0]).encode())
p.interactive()

'''
# example, the result should be 1337
(x1, y1) = (10880, 7225308917177)
(x2, y2) = (1249, 95288335886)


L = Matrix(
    [
        [y1, y2, 1],
        [-x1, -x2, 1],
        [-x1^2, -x2^2, 1],
    ]
)

pretty_print(L.LLL())
'''