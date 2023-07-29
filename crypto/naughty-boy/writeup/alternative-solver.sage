from Crypto.Util.number import *
from Crypto.Util.number import long_to_bytes as l2b
from pwn import *

# r = process(['python3', '../src/chall.py'])
r = remote('localhost', '11000')
r.recvuntil(b'e = ')
e = int(r.recvline().strip())
r.recvuntil(b'c = ')
c = int(r.recvline().strip())
r.recvuntil(b'n = ')
n = int(r.recvline().strip())
r.recvuntil(b'modd = ')
modd = int(r.recvline().strip())
r.recvuntil(b'hint_1 = ')
hint_1 = int(r.recvline().strip())
r.recvuntil(b'hint_2 = ')
hint_2 = int(r.recvline().strip())

print(f'Known values:')
print(f'{e = }')
print(f'{c = }')
print(f'{n = }')
print(f'{modd = }')
print(f'{hint_1 = }')
print(f'{hint_2 = }')

F.<x> = GF(modd)[]
sols = factor(x^4 - hint_2)

for sol in sols:
    hidden_val = int(sol[0][0])
    print(f'Try to use {hidden_val} as the hidden_val')
    print(f'Try to recover z3')
    rand_1_mod_n = (hidden_val % n)
    flagz = l2b(82630896202304637885663396615175081716360279860691360161478499884042377443809948950113903679253802194646004409523974680836559549039594450158731212157)
    found = False
    for k in range(10000):
        # The value that we got is rand_1 % n, and rand_1 is larger than n. So, the real rand_1 value is actually
        # in form of rand_1 = rand_1_mod_n + k*n
        # However, it is only slighty larger than n, so we can bruteforce the k value, which means
        # z3 = (hidden_val - (rand_1_mod_n + k*n)) // n
        rand_1 = rand_1_mod_n + k*n
        curr_z3 = (hidden_val - rand_1) // n
        print(f'{curr_z3 = }, predicted {k = }')  

        # Perform lattice reduction again with the hint_1. We hope that after reduction,
        # one of the vector will be (-rand_2, z2*s, z1*s).
        s = 2 ** (1024 - 512)
        m = matrix( 
            [ 
                [(curr_z3**8)     , 1 * s, 0    ],
                [(0x1337 * n), 0    , 1 * s],
                [-hint_1     , 0    , 0    ],
            ] 
        ) 
        L = m.LLL()
        for vct in L:
            if (vct[1] // s).nbits() == 512 and is_prime(vct[1] // s):
                z2 = vct[1] // s
                z1 = n // z2
                break
            elif (vct[2] // s).nbits() == 512 and is_prime(vct[2] // s):
                z1 = vct[2] // s
                z2 = n // z1
                break
        try:
            print(f'{z1 = }')
            print(f'{z2 = }')
            print(f'Assert z1*z2 == n: {z1*z2 == n}')
            d = inverse_mod(e, (z1-1)*(z2-1))
            secret = int(pow(c, d, n))
            print(f'{secret = }')
            r.sendlineafter(b'the secret: ', str(secret).encode())
            r.recvuntil(b'prize:\n')
            flag = r.recvline().strip()
            print(f'Flag: {flagz}')
            found=True
        except:
            continue
        if found:
            break
    if found:
        break
