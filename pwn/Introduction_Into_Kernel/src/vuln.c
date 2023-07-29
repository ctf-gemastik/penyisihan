#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/miscdevice.h>
#include <linux/fs.h>
#include <linux/uaccess.h>
#include <linux/errno.h>
#include "vuln.h"
MODULE_LICENSE("Dual BSD/GPL");


#define DEVICE_NAME "vuln"
#define BUFFER_SIZE 64

static int Major;
int gift = 0;


struct miscdevice vuln_dev;
struct file_operations vuln_fops =
{
.unlocked_ioctl=vuln_ioctl,
.open=vuln_open,
.read=vuln_read,
.write=vuln_write,
.release=vuln_release,
};

struct rwRequest {
	void *kaddr;
	void *uaddr;
	size_t length;
};


static int __init vuln_init(void)
{
  Major = register_chrdev(0, DEVICE_NAME, &vuln_fops);

  if (Major < 0) {
    printk(KERN_ALERT "Registering char device failed with %d\n", Major);
    return Major;
  }
  return 1;
}

static void __exit vuln_exit(void){
	unregister_chrdev(Major, DEVICE_NAME);
	printk(KERN_INFO "EBBChar: Goodbye from the LKM!\n");
}

long vuln_ioctl (struct file *filp, unsigned int cmd, unsigned long arg) {
	if (cmd == 0x1337){
		gift = arg;
	}
	return 0;
}


ssize_t vuln_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos)
{
    char data[BUFFER_SIZE];
    if (_copy_from_user(&data, buf, count) != 0) {
        return -EFAULT;
    }
    return count;
}
  
ssize_t vuln_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos)
{
    struct rwRequest req;

	if (copy_from_user(&req, (void *)buf, sizeof(req))) {
		printk(KERN_ERR "invalid address in read");
		return -EFAULT;
	}
	if(gift == 0xdeadbeef){
		void *stack_addr = &req;
		if (copy_to_user(req.uaddr, &stack_addr, sizeof(stack_addr))) {
			printk(KERN_ERR "invalid address in leak");
			return -EFAULT;
		}
	}
	else if(gift == 0xcafebeef){
		if (copy_to_user(req.uaddr, req.kaddr, req.length)) {
			printk(KERN_ERR "invalid address in read");
			return -EFAULT;
		}
	}
  
    return count;
}



int vuln_open(struct inode *inod, struct file *fil) {
	return 0;
}

int vuln_release(struct inode *inod,struct file *fil){
	return 0;
}

module_init(vuln_init);
module_exit(vuln_exit);