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
                  builder.init("accountKey", "channelKey",department: "department",appName: "appName");
                },
              ),
              Padding(padding: EdgeInsets.only(top: 30)),
              InkWell(
                child: Text("start"),
                onTap: (){
                  builder.startChat();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
