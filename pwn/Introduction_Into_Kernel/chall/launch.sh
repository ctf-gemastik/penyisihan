#!/bin/sh

KERNEL_PATH="../bzImage"

if [ -z "$1" ]
then
    echo "NO CPIO"
    return 1
fi

if [ -z "${2}" ]; then
    echo "Missing second argument: C source file"
    exit 1
fi

gcc -static "${2}.c" -o "$2" -lpthread
cp exploit ../chall/exploit
cd ../chall && find . | cpio  -H newc -o | gzip > ../initramfs.cpio.gz

/usr/bin/qemu-system-x86_64 \
    -kernel "$KERNEL_PATH" \
    -m 256M \
    -initrd "$1" \
    -nographic \
    -monitor none \
    -no-reboot \
    -cpu kvm64,+smep \
    -s \
    -append "console=ttyS0 nokaslr kpti=1 quiet panic=1 oops=panic" \
    -smp cores=2