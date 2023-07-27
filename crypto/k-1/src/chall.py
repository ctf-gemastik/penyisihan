import random
import os

bits = 32
k = 3
password = random.getrandbits(bits) % 20000

def get_shares():
    coeffs = [password] + [random.getrandbits(bits) for _ in range(k - 1)]
    x_list = set()
    while len(x_list) < k - 1:
        x_list.add(random.getrandbits(bits))
    
    shares = []
    for x in x_list:
        y = sum(map(lambda i : coeffs[i] * pow(x, i), range(len(coeffs))))
        shares.append((x, y))
    
    for share in shares:
        print(share)

def get_flag():
    res = int(input('password: '))
    if password == res:
        os.system('cat flag.txt')
        print()

try:
    get_shares()
    get_flag()        
except:
    print('something error happened.')