# Writeup

Given a Network Packet Capture (PCAP) file named "log.pcap". At this point, we started by examining the protocol hierarchy and its general structure

### Case Identification

```sh
» tshark -r log.pcap -qz io,phs

===================================================================
Protocol Hierarchy Statistics
Filter:

frame                                    frames:3256 bytes:177584
  eth                                    frames:3256 bytes:177584
    arp                                  frames:36 bytes:1512
    ip                                   frames:3220 bytes:176072
      tcp                                frames:3220 bytes:176072
===================================================================

» tshark -r log.pcap -Y tcp | head
    3   0.039372     10.2.0.1 → 10.2.0.2     TCP 54 20 → 105 [SYN] Seq=0 Win=8192 Len=0
    4   0.039405     10.2.0.2 → 10.2.0.1     TCP 58 105 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
    5   0.039418     10.2.0.1 → 10.2.0.2     TCP 54 20 → 105 [RST] Seq=1 Win=0 Len=0
    6   1.111596     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 105 [SYN] Seq=0 Win=8192 Len=0
    7   1.111648     10.2.0.2 → 10.2.0.1     TCP 54 105 → 20 [RST, ACK] Seq=1246143506 Ack=1 Win=0 Len=0
    8   2.631135     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 105 [SYN] Seq=0 Win=8192 Len=0
    9   4.271274     10.2.0.1 → 10.2.0.2     TCP 54 20 → 86 [SYN] Seq=0 Win=8192 Len=0
   10   5.940913     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 86 [SYN] Seq=0 Win=8192 Len=0
   11   5.940951     10.2.0.2 → 10.2.0.1     TCP 58 86 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   12   5.940965     10.2.0.1 → 10.2.0.2     TCP 54 20 → 86 [RST] Seq=1 Win=0 Len=0
```

It turned out that the packet capture contained a large number of TCP packets, specifically TCP requests with the TCP flag set to "SYN". Upon reviewing the challenge's title and description, we can observe the correlation, revealing that it is all about SYN-based port scanning. Furthermore, the challenge stated that a single request might elicit a different response based on what port the server receives it on, specifically the "target scanning port".

### Literature Study

Based on our previous findings, we are aware that there are hidden mechanisms that could impact the TCP response flow. However, before delving deeper into these mechanisms, let's first discuss the TCP protocol itself.

TCP (Transmission Control Protocol) is one of the core protocols of the Internet Protocol Suite (TCP/IP). It is a reliable, connection-oriented transport layer protocol responsible for ensuring the reliable delivery of data between devices over an IP network. TCP uses a three-way handshake to establish a reliable connection. The connection is full duplex, and both sides synchronize (SYN) and acknowledge (ACK) each other. Here's how a normal TCP handshake works:

* The client first sends a Synchronization packet (SYN).
* If the server accepts, it responds with a Synchronization Acknowledgment (SYN-ACK) to let the client know it's open and ready for communication.
* The client responds with an Acknowledgment (ACK). Now the session begins and the socket is created.

![alt](https://media.geeksforgeeks.org/wp-content/uploads/handshake-1.png)

However, in term of port-scanning there's some slight changes with its mechanism. This was affected by whatever server's state such as: when the server does not respond or does not allow connections to be made to a port (because of a firewall for example)

There are several port-scanning methods, and one of them is called "SYN-scan", often referred to as half-open scanning because it does not establish a full TCP connection. Instead, with this behavior, the SYN-scan sends a request and waits for the response before terminating the connection. 

Let's break down how the TCP handshake handles SYN-scan based on different server states:

#### Open port:
* When the server receives a SYN packet and the port is open, it responds with a SYN-ACK (Synchronize-Acknowledge) packet.
* The SYN-ACK packet means that the server is willing to establish a connection with the client.
* In response to the SYN-ACK, the SYN-scan will send an RST packet to terminate the connection. This is because the SYN-scan is only interested in determining the port's status and not in establishing a full connection.

#### Closed port

* In this scenario, when the server receives a SYN packet (a request to open a connection), it responds with a RST (Reset) packet. This indicates that the port is closed, and the connection request is rejected immediately.
* The RST packet serves as a response to the SYN packet, and the SYN-scan identifies this as an indication that the port is closed.

#### Filtered port or Blocked by Firewall:
* If the server port is filtered or blocked by a firewall, it may not respond to the SYN packet at all.
* The lack of any response from the server, whether SYN-ACK or RST, indicates that the port's state is uncertain, and further analysis is needed to determine its status.

### Case Analysis

By using the previous base knowledge, let's try to analyze the given packet capture, commencing with the open-port state.

#### Open port

As we already know, to determine whether the server's port is open, all we need to do is find the occurrence of "SYN-ACK" (0x0012) in the TCP flag. For simplicity, we used "tshark" to extract the packets based on our specified condition

```sh
# Get all of server response with SYN-ACK TCP flag
» tshark -r log.pcap -Y 'tcp.flags eq 0x12' | head
    4   0.039405     10.2.0.2 → 10.2.0.1     TCP 58 105 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   11   5.940951     10.2.0.2 → 10.2.0.1     TCP 58 86 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   22  11.651495     10.2.0.2 → 10.2.0.1     TCP 58 [TCP Port numbers reused] 66 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   27  14.212918     10.2.0.2 → 10.2.0.1     TCP 58 [TCP Port numbers reused] 79 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   30  15.701330     10.2.0.2 → 10.2.0.1     TCP 58 [TCP Port numbers reused] 82 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   34  18.863610     10.2.0.2 → 10.2.0.1     TCP 58 [TCP Port numbers reused] 119 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   37  20.423450     10.2.0.2 → 10.2.0.1     TCP 58 48 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   43  26.733769     10.2.0.2 → 10.2.0.1     TCP 58 75 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   46  28.203339     10.2.0.2 → 10.2.0.1     TCP 58 71 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460
   53  31.750355     10.2.0.2 → 10.2.0.1     TCP 58 103 → 20 [SYN, ACK] Seq=0 Ack=1 Win=64240 Len=0 MSS=1460

# Get all of the source port from the server response 
» tshark -r log.pcap -Y 'tcp.flags eq 0x12' -Tfields -e tcp.srcport | head
105
86
66
79
82
119
48
75
71
103

# Transcribe the port in ASCII form
» tshark -r log.pcap -Y 'tcp.flags eq 0x12' -Tfields -e tcp.srcport | perl -nE 'print map(chr, split)'
iVBORw0KGgoAAAANSUhEUgAAALkAAAC5AQAAAABc1qPxAAABYUlEQVR4nO2XMa6EMAxEZ0VByRFyE7gYEkhcbLlJjpAyBcJ/bND+vxIt86XVunMehWV7JgF2HTO+4INBAfDw5IkxtU9mrRBMZqvNydbS5HblF0owgumWbPeq1oIkB3OqgFf1D4B9YbroQczDMquaLgZ1M/BNLDGKixW9GUSUhrUc3XgX591gA+XXZAzsC+ubpMBy60PxeVgdXIg6wG6wFlAGnVntMcJ0wFviWoi+xE5KQQLizDdx7+hDQkD5eSN45gZAH2qFIKo6XQC9u4AOsBH0+8nirD5CjUpA44n0XYMKwOAoZmA47h5TgiNdMsBloBBfm6gArsH9kN+M2hO0QhA37PnU4E6OUuBRfBO7DX7tQAiOu7ah5XYWr4yXIStAaH9xC/J3zh99KMA5iuqjiHmoQRmPteBd+9sSGQDoerTcvXt/0N8NYh4MtyAOpckmBPEf5Z7DpwZ8HYXgMr7gc8EPhWX+0w4T3RYAAAAASUVORK5CYII=

# Decode the base64-encoded text
» tshark -r log.pcap -Y 'tcp.flags eq 0x12' -Tfields -e tcp.srcport | perl -nE 'print map(chr, split)' | base64 -d | xxd | head
00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
00000010: 0000 00b9 0000 00b9 0100 0000 005c d6a3  .............\..
00000020: f100 0001 6149 4441 5478 9ced 9731 ae84  ....aIDATx...1..
00000030: 300c 4467 4541 c911 7213 b818 1248 5c6c  0.DgEA..r....H\l
00000040: b949 8e90 3205 c27f 6cd0 febf 122d f3a5  .I..2...l....-..
00000050: d5ba 731e 8565 7b26 0176 1d33 bee0 8341  ..s..e{&.v.3...A
00000060: 01f0 f0e4 8931 b54f 66ad 104c 66ab cdc9  .....1.Of..Lf...
00000070: d6d2 e476 e517 4a30 82e9 966c f7aa d682  ...v..J0...l....
00000080: 2407 73aa 8057 f50f 807d 61ba e841 ccc3  $.s..W...}a..A..
00000090: 32ab 9a2e 0675 33f0 4d2c 318a 8b15 bd19  2....u3.M,1.....
```

As we can see, we have successfully discovered a certain concealed message encoded in Base64 by converting the TCP "target port" into ASCII representation.

#### Closed port

In this case, we can employ the same approach to determine whether the server port is closed or not. However, this time, we need to find the occurrence of "RESET-ACK" (0x0014) in the TCP flag

```sh
# Get all of server response with RST-ACK TCP flag
» tshark -r log.pcap -Y 'tcp.flags eq 0x14' | head
    7   1.111648     10.2.0.2 → 10.2.0.1     TCP 54 105 → 20 [RST, ACK] Seq=1246143506 Ack=1 Win=0 Len=0
   14   6.961717     10.2.0.2 → 10.2.0.1     TCP 54 86 → 20 [RST, ACK] Seq=1121383599 Ack=1 Win=0 Len=0
   16   8.041167     10.2.0.2 → 10.2.0.1     TCP 54 66 → 20 [RST, ACK] Seq=1 Ack=1 Win=0 Len=0
   18   9.060643     10.2.0.2 → 10.2.0.1     TCP 54 79 → 20 [RST, ACK] Seq=1 Ack=1 Win=0 Len=0
   20  10.133643     10.2.0.2 → 10.2.0.1     TCP 54 82 → 20 [RST, ACK] Seq=1 Ack=1 Win=0 Len=0
   25  12.702735     10.2.0.2 → 10.2.0.1     TCP 54 119 → 20 [RST, ACK] Seq=1 Ack=1 Win=0 Len=0
   49  29.211215     10.2.0.2 → 10.2.0.1     TCP 54 48 → 20 [RST, ACK] Seq=3144735014 Ack=1 Win=0 Len=0
   51  30.261831     10.2.0.2 → 10.2.0.1     TCP 54 75 → 20 [RST, ACK] Seq=1572524603 Ack=1 Win=0 Len=0
   56  32.792077     10.2.0.2 → 10.2.0.1     TCP 54 71 → 20 [RST, ACK] Seq=1966382955 Ack=1 Win=0 Len=0
   65  38.670936     10.2.0.2 → 10.2.0.1     TCP 54 103 → 20 [RST, ACK] Seq=3029903613 Ack=1 Win=0 Len=0

# Get all of the source port from the server response 
» tshark -r log.pcap -Y 'tcp.flags eq 0x14' -Tfields -e tcp.srcport | head
105
86
66
79
82
119
48
75
71
103

# Transcribe the port in ASCII form
» tshark -r log.pcap -Y 'tcp.flags eq 0x14' -Tfields -e tcp.srcport | perl -nE 'print map(chr, split)'
iVBORw0KGgoAAAANSUhEUgAAALkAAAC5AQAAAABc1qPxAAABbklEQVR4nO2WMY6FMAxEjShS5gi5CVwMCSQuBjfJEShTRMyOA7uw+rTxl75+CgS8xsqMxxY8n0m+4IPBJiLNNgLrNgS38MsZgvKvhfTIgpUlWYJB3HqWFvUtWAPMEbvexltADlhkjLAGhw/UAjm4F6EqgyIFTfhs0bqgHPpAGiC+NGdtkNUClGKRIYj4/FeVAWD7OT6i2hGppzyWYJDUaAB0WhWVEdgBTAG7R2RBQyjGdIYg04n6tvsppM5PAkMwqx50f+dPWzg7oHew+DnShPMxdmAIWvpPe4FtIEnkqsoA8EpK+hwhuFAU2AENPNLIWTsF6W96WADqQQsgUQ+NvnwXqj7Q9tMxy88jhywBigV4JaAd5YolA3BOHHYARXG4NacBKLOWKiQtyOmaB0NwFvSbQ3cn1gfHZhlKaaxQrh60Am1k7up6qflnDcYDpP9DuD4oenDWdtLy4cuSZQaKE6egvQ/d6oMheDxf8LngByTqBAWJ8fIsAAAAAElFTkSuQmCC

# Decode the base64-encoded text
» tshark -r log.pcap -Y 'tcp.flags eq 0x14' -Tfields -e tcp.srcport | perl -nE 'print map(chr, split)' | base64 -d | xxd | head
00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
00000010: 0000 00b9 0000 00b9 0100 0000 005c d6a3  .............\..
00000020: f100 0001 6e49 4441 5478 9ced 9631 8e85  ....nIDATx...1..
00000030: 300c 448d 2852 e608 b909 5c0c 0924 2e06  0.D.(R....\..$..
00000040: 37c9 1128 5344 cc8e 03bb b0fa b4f1 97be  7..(SD..........
00000050: 7e0a 04bc c6ca 8cc7 163c 9f49 bee0 83c1  ~........<.I....
00000060: 2622 cd36 02eb 3604 b7f0 cb19 82f2 af85  &".6..6.........
00000070: f4c8 8295 2559 8241 dc7a 9616 f52d 5803  ....%Y.A.z...-X.
00000080: cc11 bbde c65b 400e 5864 8cb0 0687 0fd4  .....[@.Xd......
00000090: 0239 b817 a12a 8322 054d f86c d1ba a01c  .9...*.".M.l....
```

As we can see,  now it becomes evident that the concealed message within the "closed-port" state was, in fact, an image in PNG format.

#### Filtered port

Here comes the tricky part, which is identifying filtered ports becomes challenging since the lack of a TCP server response, thus makes it difficult to determine their status directly. However, there is a proper workaround available to address this issue. One of the approaches involves searching for the complement of the "SYN scan" request, which does not trigger either an open or closed port state

```sh
# Get whatever packet number from client that triggers open/closed port state
» tshark -r log.pcap -Y 'tcp.flags in {0x12, 0x14}' -Tfields -e frame.number | awk '{print $1-1}' | paste -sd ',' > excluded_frame

# Excluding the packet number and select SYN-request only
» tshark -r log.pcap -Y "tcp.flags eq 0x02 and not frame.number in {$(<excluded_frame)}" | head
    8   2.631135     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 105 [SYN] Seq=0 Win=8192 Len=0
    9   4.271274     10.2.0.1 → 10.2.0.2     TCP 54 20 → 86 [SYN] Seq=0 Win=8192 Len=0
   32  17.223818     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 66 [SYN] Seq=0 Win=8192 Len=0
   39  21.951469     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 79 [SYN] Seq=0 Win=8192 Len=0
   40  23.564643     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 82 [SYN] Seq=0 Win=8192 Len=0
   41  25.133015     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 119 [SYN] Seq=0 Win=8192 Len=0
   63  37.461455     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 48 [SYN] Seq=0 Win=8192 Len=0
   72  43.619832     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 75 [SYN] Seq=0 Win=8192 Len=0
   73  45.411814     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 71 [SYN] Seq=0 Win=8192 Len=0
   74  47.271322     10.2.0.1 → 10.2.0.2     TCP 54 [TCP Retransmission] [TCP Port numbers reused] 20 → 103 [SYN] Seq=0 Win=8192 Len=0

# Since we're using the SYN-request, get all of the destination port
» tshark -r log.pcap -Y "tcp.flags eq 0x02 and not frame.number in {$(<excluded_frame)}" -Tfields -e tcp.dstport | head
105
86
66
79
82
119
48
75
71
103

# Trancribe the port in ASCII form
» tshark -r log.pcap -Y "tcp.flags eq 0x02 and not frame.number in {$(<excluded_frame)}" -Tfields -e tcp.dstport | perl -nE 'print map(chr, sp
lit)'
iVBORw0KGgoAAAANSUhEUgAAAKUAAAClAQAAAAAVUAB3AAABF0lEQVR4nO2WMQ6DMAxFjRgYOUJuUi6GFKRejN4kR2DMgOr6Oy2C0pGPOpDJvCU/zv8Ooj9Wlov+P51EpJpikm7qrZTIpVH1YVTHdg7+SaW9NAuFGj7VQbJIfx5FdQIt/dV8+9H146l7pw/W5J2jjqe+plr12X6nhUFneEeCUWvy+NHAonfsO9hZW03WZOXSAY6pUzOKNzlyqcI2A058Nw3CpXBM9c68LBqIFLZJ0FAMRKaWedRSpjab5k6hQeq01kCiIZtJFeFYa+DQknl79QCa7SQ4nvrOFkXMs7CdcgwabVaXKK41sKi/sbVF0c3TnkFtenq1/X9gUTvsiBTSqffXxnSHe+3Y1L0zezjS8g6x6H5d9O/pC22hwpT+PYUyAAAAAElFTkSuQmCC

# Decode the base64-encoded text
» tshark -r log.pcap -Y "tcp.flags eq 0x02 and not frame.number in {$(<excluded_frame)}" -Tfields -e tcp.dstport | perl -nE 'print map(chr, split)' | base64 -d | xxd | head
00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
00000010: 0000 00a5 0000 00a5 0100 0000 0015 5000  ..............P.
00000020: 7700 0001 1749 4441 5478 9ced 9631 0e83  w....IDATx...1..
00000030: 300c 458d 1818 3942 6e52 2e86 14a4 5e8c  0.E...9BnR....^.
00000040: de24 4760 cc80 eafa 3b2d 82d2 918f 3a90  .$G`....;-....:.
00000050: c9bc 253f ceff 0ea2 3f56 968b fe3f 9d44  ..%?....?V...?.D
00000060: a49a 6292 6eea ad94 c8a5 51f5 6154 c776  ..b.n.....Q.aT.v
00000070: 0efe 49a5 bd34 0b85 1a3e d541 b248 7f1e  ..I..4...>.A.H..
00000080: 4575 022d fdd5 7cfb d1f5 e3a9 7ba7 0fd6  Eu.-..|.....{...
00000090: e49d a38e a7be a65a f5d9 7ea7 8541 6778  .......Z..~..Agx
```

### Wrapping it up

Finally, we have reached the end of the challenge, which involves obtaining the flag. As we can observe, the resulting PNG image is a QR code. Therefore, our task now is to scan and decode all of the barcodes to retrieve the flag

For simplicity, here is the complete solver written in Python:
```py
from pyzbar.pyzbar import decode
from pyshark import FileCapture
from base64 import b64decode
from PIL import Image

import re

tcp_flags = {
    '0x0004': 'R',  # RESET
    '0x0002': 'S',  # SYN
    '0x0014': 'RA', # RESET-ACK
    '0x0012': 'SA'  # SYN-ACK
}

result = {
    'open': '',
    'closed': '',
    'filtered': ''
}

current_tcp_flag = ''
prev_port = None
packets = FileCapture('log.pcap', use_json=True)

for packet in packets:
    if hasattr(packet, 'tcp'):
        dst_port = packet.tcp.dstport.int_value
        tcp_flag = packet.tcp.flags.strip()
        current_tcp_flag += tcp_flags[tcp_flag]

        if re.match(r'SSA|SRA|R', current_tcp_flag):
            if current_tcp_flag == 'SSA':
                result['open'] += chr(prev_port)
            elif current_tcp_flag == 'SRA':
                result['closed'] += chr(prev_port)
            current_tcp_flag = ''

        elif current_tcp_flag == 'SS':
            result['filtered'] += chr(prev_port)
            current_tcp_flag = tcp_flags[tcp_flag]           
        prev_port = dst_port

if current_tcp_flag == 'S':
    result['filtered'] += chr(prev_port)

flag = b''
for k,v in result.items():
    with open('tmp.png', 'wb') as f:
        f.write(b64decode(v))

    img = Image.open('tmp.png')
    flag += decode(img)[0].data

print(flag)
```

### Flag
`gemastik{11fcdf5c2c217495e2c16fb2f20c136fe776c763}`

### References
* https://www.coengoedegebure.com/tcp-3-way-handshake-port-scanning/