# k-1

by prajnapras19

---

## Flag

```
gemastik{crypto_k-1_5a9bdf2fdfdb7c94dd9ec78bb31e35fb611069749ac093542d3f1}
```

## Description
Shamir said we need `k` shares to recover the secret.

```
nc <HOST> <PORT>
```

## Difficulty
easy

## Hints
* You just need to solve the equations. Start figuring out the pattern on small `k` and scale it on bigger `k`. Note that the `password` is a random value between 0 and 999999 (inclusive). So, it must be small enough.

## Tags
Shamir's secret sharing, linear algebra

## Deployment
- Run the container using:
    ```
    docker-compose up --build --detach
    ```

## Notes
> Intentionally left empty
