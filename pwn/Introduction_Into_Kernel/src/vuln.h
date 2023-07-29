long vuln_ioctl (struct file *filp, unsigned int cmd, unsigned long arg);
int vuln_open(struct inode *inod, struct file *fil);
ssize_t vuln_read(struct file *, char *, size_t, loff_t *);
ssize_t vuln_write(struct file *, const char *, size_t, loff_t *);
int vuln_release(struct inode *inod,struct file *fil);