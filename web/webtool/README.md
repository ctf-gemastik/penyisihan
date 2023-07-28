# Web Tool

by Bonceng

---

## Flag

```
gemastik{inc0sistent_impl3mentation_br0ww}
```

## Description
Running program without installing

## Difficulty
medium

## Hints
* Every session is not linked with database.
* User execution is safe because I'm using whitelist method, right?
* Debian is using C for their OS implementation.

## Tags
command injection, null-byte

## Deployment
- Install docker engine>=19.03.12 and docker-compose>=1.26.2.
- Run the container using:
    ```
    docker-compose up --build --detach
    ```

## Notes
- Ada kemungkinan service menyimpan terlalu banyak file dari user yang telah dibuat. Solusi termudah adalah mematikan service dan delete volume, lalu build ulang kembali.