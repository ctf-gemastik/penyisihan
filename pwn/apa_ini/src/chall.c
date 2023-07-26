#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#define buffer_size 0x70
#define num_files 5

char *usernames[num_files] = {};

FILE *fp;


int main(){
    setbuf(stdout, 0);

    puts("Data base username");

    int i;
    char filename[16];
    for (i = 0; i < num_files; i++)
    {
        usernames[i] = malloc(buffer_size);
        snprintf(filename, 16, "username-%d.txt", i+1);
        fp = fopen(filename, "r");
        fread(usernames[i], 1, buffer_size, fp);
    }

    int choose=1;
    printf("Show Username\n");
    printf("Choose index username (1 - 5): ");
    scanf("%d", &choose);
    if (choose > 5 ){
        puts(":(");
        exit(1);
    }
    printf("Username : %lx\n", (long unsigned int)usernames[choose-1]);

    choose=1;
    printf("Edit Username\n");
    printf("Choose index username (1 - 5): ");
    scanf("%d", &choose);
    if (choose > 5 ){
        puts(":(");
        exit(1);
    }
    int j = (choose - 1);
    snprintf(filename, 16, "username-%d.txt", j);
    fp = fopen(filename, "r");

    printf("Username : ");
    ssize_t bytes_read = read(0, usernames[j], buffer_size);
    usernames[bytes_read - 1] = 0;

    puts("Saved");
    fread(usernames[j], 1, buffer_size, fp);  
    // TODO
    return 0;
}
// gcc -Wl,-z,relro,-z,now chall.c -o chall