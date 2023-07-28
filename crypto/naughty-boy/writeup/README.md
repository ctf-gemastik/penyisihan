# Writeup

- Recover `hidden_val` by applying Fermat's Little Theorem + do nth_root(4) of the recovered value
- The recovered `hidden_val` will be `hidden_val % n`. However, `k` is bruteforce-able, so you can recover the real `hidden_val` by doing a little bruteforce.
- Now that you have `hidden_val`, you can recover `z3`.
- Now, after you recover `z3`, the `z1` and `z2` can be recovered using lattice (LLL).
- Decrypt the encrypted secret after you recover the `z1` and `z2` value.
- Send it to the server, and server will print the flag
