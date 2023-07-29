# Network Discovery

by orie

---

## Flag

```
gemastik{11fcdf5c2c217495e2c16fb2f20c136fe776c763}
```

## Description
Our network was repeatedly bombarded by a port scanner targeting a specific `port`. Interestingly, our server seems to provide different responses depending on the specific request it receives. Although nothing major happened, we believe there was a hidden intention behind the attacks.

## Difficulty
easy

## Hints
* We discovered that the actor was attempting to determine whether our service was open, closed, or blocked by the firewall.

## Tags
syn scan, 3-way handshake

## Notes
> Intentionally left empty
