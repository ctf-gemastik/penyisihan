# Writeup

Construct a lattice from equations below, but with `k` dimensions:

```
(x1, y1)
(x2, y2)

y1 = a0 + x1 * a1 + x1^2 * a2
y2 = a0 + x2 * a1 + x2^2 * a2

a0 = y1 - x1 * a1 - x1^2 * a2
a0 = y2 - x2 * a1 - x2^2 * a2

Matrix:
[
    [y1, y2, 1],
    [-x1, -x2, 1],
    [-x1^2, -x2^2, 1],
]
```

References:
- https://bronson113.github.io/2023/02/25/acsc-2023-writeups.html#crypto---dsa