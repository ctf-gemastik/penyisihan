from pwn import *

#p = process(['python3', '../src/chall.py'])
p = remote('localhost', '10000')

xy = []
k = int(p.recvuntil(b'\n')[len('k = '):-1].decode())
for _ in range(k - 1):
    xy.append(eval(p.recvuntil(b'\n')[:-1]))

rows = []

cols = []
for i in range(len(xy)):
    cols.append(xy[i][1])
cols.append(1)
rows.append(cols[:])

for i in range(1, k):
    cols = []
    for j in range(k - 1):
        cols.append(-xy[j][0]^i)
    cols.append(1)
    rows.append(cols[:])

L = Matrix(rows)
print(f'{k = }')
L = L.LLL()
#pretty_print(L)
print(str(L[1][0]).encode())

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