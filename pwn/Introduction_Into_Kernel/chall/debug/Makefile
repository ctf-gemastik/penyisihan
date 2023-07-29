run:
	../launch.sh ../initramfs.cpio.gz exploit

debug:
	tmux split-window -h "sudo gdb ../vmlinux -x ../gdb.sh"
	$(MAKE) run