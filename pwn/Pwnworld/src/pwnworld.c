#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>

#define BUFFSIZE 16
int gift;

int get_random(){
	srand(time(0));
	return rand() % 417;
}

void helper()
{
		__asm__("popq %rdi\n\t"
			"ret\n\t");
}

int setup()
{


	setvbuf(stdout, NULL, _IONBF, 0);
	setvbuf(stdin, NULL, _IONBF, 0);
	setvbuf(stderr, NULL, _IONBF, 0);
}

int game(){
	int guess = get_random();
	int res = 0;
	char buf[BUFFSIZE];
	printf("What number would you like to guess? ");
	fgets(buf, BUFFSIZE, stdin);
	int ans = atoi(buf);
	if(!ans){
		printf("Oops that's not the number\n");
		exit(0);
	}
	else{
		if(ans == guess){
			printf("Congrats! You win!\n");
			res = 1;
		}
		else{
			printf("Oops You lose\n");
		}
	}
	return res;
}

int main(int argc, char const *argv[])
{
	setup();
	char buf[0x100];
	int res = game();
	if(res){
		printf("Since you win, I will give this to you: %p\n", &gift);
		printf("Any feedback? ");
		gets(buf);
	}
	else{
		printf("You lose! have any feedback for my game? ");
		fgets(buf, 0x100, stdin);
		printf("Thanks for your feedback\n");
	}
	printf("See yaa\n");
	return 0;
}