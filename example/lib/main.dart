import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:zendesk_flow_builder/zendesk_flow_builder.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  ZendeskFlowBuilder builder=ZendeskFlowBuilder();

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = '1.0.0';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              InkWell(
                child: Text("click to init"),
                onTap: (){
                  builder.init("accountKey", ZendeskChannelKey,department: "department",appName: "appName");
                },
              ),
              Padding(padding: EdgeInsets.only(top: 30)),
              InkWell(
                child: Text("start"),
                onTap: (){
                  startChat();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

void startZendesk() {
  initZendesk();

  zendesk.startChat().then((r) {
    print('startChat finished');
  }).catchError((e) {
    print('error $e');
  });
}

const ZendeskAccountKey = '';
const ZendeskChannelKey =
    '';
final ZendeskFlowBuilder zendesk = ZendeskFlowBuilder();

Future<void> initZendesk({String defaultInputFieldValue = ""}) async {
  zendesk
      .init(
    ZendeskAccountKey,
    ZendeskChannelKey,
    department: 'Staging Part',
    appName: 'NTFoods App',
  )
      .then((r) {
    zendesk.addUnreadListener((count) {});
  }).catchError((e) {
    print('failed with error $e');
  });

  String username = "test";
  String email = "test";
  String phoneNumber = "test";
  String firebaseToken = "FCMTOKEN";
  zendesk
      .setVisitorInfo(
          email: email, name: username, phoneNumber: phoneNumber, note: '')
      .then((r) {
    print('setVisitorInfo finished');
  }).catchError((e) {
    print('failed with error $e');
  }).whenComplete(() {
    zendesk.setToken(firebaseToken).whenComplete(() {
      zendesk
          .startChat(
              iosNavigationBarColor: Color.fromRGBO(245, 245, 245, 255),
              iosNavigationTitleColor: Colors.black)
          .then((r) {
        print('startChat finished');
      }).catchError((e) {
        print('error $e');
      });
    });
  });
}

void startChat() {
  zendesk
      .startChat(
      iosNavigationBarColor: Color.fromRGBO(245, 245, 245, 255),
      iosNavigationTitleColor: Colors.black);
}
