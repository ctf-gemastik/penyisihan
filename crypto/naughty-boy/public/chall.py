from Crypto.Util.number import *
import os

print(f'Generating secret and hints... Be patient and sing this song :)')
print(f'''
-------------------------------------------
La La La - Naughty Boy

Lyrics
La la, la la la la la na na na na na
La la na na, la la la la la na na na na na
La la, la la la la la na na na na na
La la na na, la la la la la na na na na na
...
-------------------------------------------
''')
secret_val = bytes_to_long(os.urandom(100))
z1 = getStrongPrime(512)
z2 = getStrongPrime(512)
z3 = getPrime(256)
modd = getPrime(2048)

n = z1*z2
e = 65537
c = pow(secret_val, e, n)

rand_1 = getRandomNBitInteger(modd.bit_length() - 1013)
rand_2 = bytes_to_long(os.urandom(128))

hidden_val = z1*z2*z3 + rand_1
hint_1 = (z3**8)*z2 + 0x1337*z2*(z1**2) + rand_2
hint_2 = pow(hidden_val, 4*modd, modd)
print(f'Finished generating secret and hints! Below is the known values:')
print(f'{e = }')
print(f'{c = }')
print(f'{n = }')
print(f'{modd = }')
print(f'{hint_1 = }')
print(f'{hint_2 = }')

res = int(input('What is the secret: '))
if secret_val == res:
    print('GG! Here is your prize:')
    os.system('cat flag.txt')
    print()
else:
    print('Try harder naughty boy!')
