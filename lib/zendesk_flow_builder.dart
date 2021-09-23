import 'dart:async';

import 'package:flutter/services.dart';

class ZendeskFlowBuilder {
  static const MethodChannel _channel =
      const MethodChannel('zendesk_flow_builder');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
