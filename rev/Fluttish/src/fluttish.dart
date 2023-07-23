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
  List<int> compressedData = [REDACTED];
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
    password: "KrunkerYuk!"
    );
  } catch(error){
      print('Error : {User is already not authorized anymore. Please report to admin of fluttish.org for further confirmation}');
      exit(0);
  }
  var storage = FirebaseStorage.instanceFor(app: app);
  var ref = storage.ref().child('flag.txt');
  var m = await ref.getMetadata();
  var v = utf8.decode((await ref.getData(m.size))!);
  print('Flag : $v');

}