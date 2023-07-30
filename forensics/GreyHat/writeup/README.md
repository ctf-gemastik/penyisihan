## GreyHat

**Proof of Concept**
1. given a zip contain PCAP and EXE
2. analyze the EXE, using strings we can see its a Python-based
3. decompile using pyinstractor, it will give a malware.pyc
4. decompile the pyc using uncompyle6 and pycdc, combine the result for more understanding
5. from that we know the EXE encrypt data from Downloads dir to desktop,ini
6. the encryption is only using XOR using value based by time and index encryption file
7. parsing PCAP file using scapy
8. unzip all file
