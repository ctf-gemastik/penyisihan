# Fluttish

by: aseng

---

## Flag

```
gemastik{wonder_why_IDA_doesnt_rec0gn!ze_that_.snapshot_segments_by_default}
```

## Description

Our Administrator needs your help!

Vaints, ex-CEO of Gemastik Branch Office, `fluttish.org` company has just been fired since he was caught becoming a black hat hacker. However, the corporate's files and directories need a masterkey which he embeds on the binary file that triggers some kind of an API call to our **database**. 

Sadly, our administrator has blacklisted his email yet the binary requires to have other attributes information related
to Vaints, as his account already deleted and blacklisted. Are you able to reverse engineer this binary app and finds a way to get that masterkey (`flag.txt`) ?

A gentle reminder from the Administrator, this mission **does not** require you to:

1. Do Brute Force
2. Do a Service Exploitation

Yet, there might be a chance of **security misconfiguration** from the corp's backend which may aid you to complete the objective, unfortunately no backend source provided so it's blackbox.


## Difficulty

Medium/Hard

## Hints
* This binary is built-in with an unusual programming language and mostly used in a well-known framework, [Flutter](https://flutter.dev/).
* Understand the DartVM Object and its logic.
* Have you figured out the database service? There's a hardcoded URL and other attributes in there.

## Tags

`web`,`rev`,`misc`

## Deployment
No remote hosted deployment is required.

Local compilation from `src`:
```
dart compile exe fluttish.dart
```