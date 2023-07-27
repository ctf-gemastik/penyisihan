# Writeup

Just FSOP

You can input a negative number into the variable 'choose.' This allows you to leak the libc address and enabling control over variables 'usernames[j]' and 'fp'.
```
---
fread(usernames[j], 1, buffer_size, fp); 
---
```

First, you can change the content of stdin to write anywhere. 

Inside fwrite, there are instructions as follows.
```
0x7f424d76149e <__GI__IO_file_xsgetn+366> call   0x7f424d6fe620 <*ABS*+0xa9c90@plt>
```
You need to change got `*ABS*+0xa9c90@` to system
RDI is usernames[j]

Then you can get shell