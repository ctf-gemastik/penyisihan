# Writeup

- Get `flag checker` binary and analyze input mapping
```python
from arc4 import ARC4

key = b"gemastik2023"

ciphertext = <elf_ciphertext>

random_index_flag_value = [7561, 9309, 11821, 6059, 10569, 9057, 13325, 10317, 7061, 10065, 8059, 12573, 12073, 9561, 14073, 16073, 15823, 15073, 15571, 11573, 5057, 12821, 13073, 5557, 8307, 7809, 7311, 10817, 8555, 11067, 13821, 14573, 9815, 13573, 14323, 6311, 12325, 15323, 11321, 8807, 6813, 5305, 6561, 16325, 5809, 4809, 14823]
random_index_flag = [11, 18, 28, 5, 23, 17, 34, 22, 9, 21, 13, 31, 29, 19, 37, 45, 44, 41, 43, 27, 1, 32, 33, 3, 14, 12, 10, 24, 15, 25, 36, 39, 20, 35, 38, 6, 30, 42, 26, 16, 8, 2, 7, 46, 4, 0, 40]
ks_flag = [165, 232, 233, 1, 194, 47, 170, 71, 129, 182, 107, 130, 219, 12, 71, 224, 54, 172, 188, 138, 7, 123, 111, 187, 45, 182, 133, 64, 87, 115, 227, 19, 208, 188, 70, 151, 58, 254, 114, 31, 189, 125, 137, 157, 234, 26, 197]

index_digest = [4910, 5158, 5406, 5660, 5910, 6160, 6412, 6664, 6914, 7162, 7412, 7662, 7910, 8160, 8408, 8656, 8908, 9160, 9412, 9662, 9918, 10166, 10418, 10670, 10918, 11170, 11424, 11674, 11922, 12174, 12426, 12674, 12922, 13163, 13426, 13674, 13922, 14174, 14424, 14676, 14926, 15174, 15424, 15672, 15924, 16176, 16428]
index_flag_digest = [37, 34, 43, 46, 41, 18, 44, 42, 45, 22, 19, 31, 7, 9, 27, 40, 0, 17, 1, 6, 32, 20, 38, 23, 4, 29, 2, 14, 36, 13, 12, 39, 24, 8, 25, 3, 26, 21, 5, 35, 16, 15, 11, 33, 30, 10, 28]
ks_digest = [121, 255, 199, 153, 77, 143, 20, 12, 47, 63, 47, 111, 149, 229, 224, 211, 140, 92, 252, 124, 200, 33, 116, 118, 25, 234, 59, 7, 33, 193, 151, 70, 242, 179, 255, 244, 204, 39, 163, 224, 126, 117, 216, 70, 249, 213, 76]

input_flag = b"a"*47 # modify to analyze input mapping

for i in range(47):
	ciphertext[random_index_flag_value[i]] = input_flag[random_index_flag[i]] ^ ks_flag[i];
	ciphertext[index_digest[i]] = input_flag[index_flag_digest[i]] ^ ks_digest[i];

arc4 = ARC4(key)
pt = arc4.decrypt(bytes(ciphertext))
out = open("out", "wb")
out.write(pt)
out.close()
```
- After getting all input mapping (index values), leak some part by utilizing known value. After that bruteforce each value (one byte) by utilizing partial known md5 hash
```python
import hashlib
import string

def get_hash(a1, a2):
	tmp = []
	for i in range(len(a1)):
		tmp.append(a1[i] ^ a2)
	return hashlib.md5(bytes(tmp)).digest(), tmp

def xor_op(a1, a2):
	tmp = []
	for i in range(len(a1)):
		tmp.append(a1[i]^a2[i])
	return tmp

known = b"gemastik{"

index_flag = [37, 34, 43, 46, 41, 18, 44, 42, 45, 22, 19, 31, 7, 9, 27, 40, 0, 17, 1, 6, 32, 20, 38, 23, 4, 29, 2, 14, 36, 13, 12, 39, 24, 8, 25, 3, 26, 21, 5, 35, 16, 15, 11, 33, 30, 10, 28]
index_digest = [[9, 4, 13, 0], [13, 1, 6, 11], [11, 1, 4, 6], [3, 15, 2, 12], [8, 13, 15, 10], [5, 8, 15, 9], [6, 14, 0, 1], [0, 12, 15, 1], [8, 15, 12, 9], [0, 14, 15, 7], [12, 5, 15, 7], [11, 9, 8, 2], [5, 2, 11, 1], [10, 6, 3, 15], [13, 2, 6, 3], [6, 8, 7, 14], [9, 13, 11, 12], [12, 11, 9, 2], [7, 0, 5, 4], [13, 0, 4, 2], [3, 10, 4, 11], [14, 13, 3, 12], [9, 3, 12, 5], [15, 0, 8, 6], [14, 0, 11, 7], [4, 2, 12, 14], [5, 11, 6, 8], [12, 5, 4, 9], [14, 1, 7, 5], [0, 15, 14, 2], [13, 9, 2, 12], [15, 9, 2, 13], [4, 9, 11, 10], [14, 5, 0, 3], [10, 2, 5, 9], [5, 2, 7, 14], [0, 1, 4, 2], [1, 2, 8, 0], [9, 5, 15, 13], [10, 8, 12, 0], [13, 4, 5, 8], [5, 0, 6, 11], [8, 10, 14, 1], [1, 9, 11, 6], [7, 4, 12, 0], [9, 14, 15, 6], [9, 4, 12, 2]]
index_result = [121, 83, 60, 4, 116, 77, 84, 28, 128, 104, 163, 182, 160, 135, 40, 15, 103, 33, 70, 156, 8, 142, 147, 53, 127, 120, 174, 54, 149, 68, 76, 134, 45, 2, 57, 107, 73, 9, 58, 168, 27, 43, 167, 38, 17, 80, 112, 75, 125, 85, 82, 183, 99, 87, 26, 34, 20, 18, 51, 106, 59, 161, 150, 101, 110, 86, 44, 137, 154, 29, 146, 56, 138, 166, 100, 113, 69, 145, 186, 184, 159, 22, 66, 109, 55, 170, 63, 132, 117, 181, 122, 131, 32, 25, 6, 50, 119, 148, 31, 78, 158, 36, 171, 187, 162, 47, 92, 172, 114, 118, 94, 35, 64, 16, 139, 151, 12, 175, 177, 62, 5, 46, 3, 1, 7, 126, 97, 115, 67, 108, 179, 185, 0, 144, 140, 136, 81, 88, 41, 37, 30, 61, 48, 10, 74, 130, 96, 176, 91, 178, 19, 13, 93, 152, 105, 98, 165, 90, 49, 14, 141, 102, 95, 124, 89, 71, 164, 72, 11, 123, 23, 21, 65, 173, 143, 79, 111, 153, 39, 24, 133, 129, 52, 42, 180, 157, 155, 169]
check = [0xc3, 0x5f, 0x71, 0x9f, 0x9c, 0x9a, 0x1e, 0xd5, 0x6b, 0x66, 0xfd, 0xb2, 0xb1, 0x24, 0x97, 0xd1, 0x71, 0xae, 0xf1, 0xc2, 0xa2, 0xc8, 0xab, 0x68, 0xb8, 0x1f, 0x5, 0x9b, 0x5c, 0xb3, 0x26, 0xe8, 0x8e, 0x8b, 0x9c, 0x9d, 0x5c, 0x90, 0x4c, 0x43, 0x7a, 0xb9, 0x3, 0xca, 0x45, 0x6b, 0xa1, 0x4d, 0x72, 0x6d, 0x52, 0x5d, 0x18, 0x7a, 0x74, 0x3f, 0xb2, 0x8d, 0x32, 0xcf, 0x91, 0xf6, 0xff, 0x4a, 0xd0, 0x7, 0x58, 0x63, 0xa7, 0xad, 0x22, 0x5e, 0x1d, 0xb7, 0x80, 0x96, 0xbf, 0xfa, 0x49, 0x9c, 0x6d, 0xf8, 0xec, 0xb9, 0x92, 0x3f, 0xea, 0xa3, 0x1a, 0x7c, 0xa5, 0xf8, 0x5d, 0x8b, 0x7b, 0xaf, 0x13, 0x96, 0x6a, 0xbe, 0x14, 0x32, 0x7f, 0x38, 0x30, 0x9, 0x40, 0xec, 0xea, 0x96, 0x6a, 0xaa, 0x8d, 0xa2, 0xb4, 0xa2, 0x1a, 0x32, 0x15, 0x52, 0xd2, 0x18, 0xab, 0x18, 0x69, 0x44, 0x29, 0xe5, 0x99, 0x3a, 0xb7, 0xe1, 0x95, 0xb5, 0x8f, 0x80, 0x6d, 0x9c, 0x76, 0xc5, 0x4b, 0x4a, 0x3, 0xc, 0xbc, 0xb4, 0x97, 0xad, 0xf1, 0x5c, 0x97, 0xf6, 0x8f, 0x80, 0xda, 0x20, 0x14, 0x19, 0xe0, 0x74, 0x3b, 0x4e, 0x93, 0xa0, 0x46, 0x6f, 0xca, 0xf1, 0xed, 0x46, 0xef, 0x21, 0xda, 0x74, 0x82, 0xb5, 0xd9, 0x92, 0x1f, 0x35, 0x50, 0x45, 0x5a, 0x9b, 0xd4, 0x36, 0x7c, 0x8e]

flag = [0 for _ in range(47)]

for i in range(len(known)):
	flag[i] = known[i]

leaked_hash = b"\x8e" * 16

last_pt = xor_op(b"\x00"*16, leaked_hash)

for i in range(len(known)):
	hash_val, xor_val = get_hash(last_pt, flag[i])
	flag[index_flag[i]] = hash_val[index_digest[i][0]] ^ check[index_result[i*4]]
	last_pt = xor_op(xor_val, hash_val)


for i in range(len(known), 47):
	if(flag[i] == 0):
		for j in string.printable[:-6]:
			hash_val, xor_val = get_hash(last_pt, ord(j))	
			tmp = [(hash_val[index_digest[i][1]] ^ index_result[i*4 + 1] == check[index_result[i*4 + 1]])]
			tmp.append((hash_val[index_digest[i][2]] ^ index_result[i*4 + 2] == check[index_result[i*4 + 2]]))
			tmp.append((hash_val[index_digest[i][3]] ^ index_result[i*4 + 3] == check[index_result[i*4 + 3]]))
			if(all(tmp) == True):
				flag[index_flag[i]] = hash_val[index_digest[i][0]] ^ check[index_result[i*4]]
				flag[i] = ord(j)
				last_pt = xor_op(xor_val, hash_val)
				print(i, bytes(flag))
				break
	else:
		hash_val, xor_val = get_hash(last_pt, flag[i])
		last_pt = xor_op(xor_val, hash_val)

print(bytes(flag))
```
