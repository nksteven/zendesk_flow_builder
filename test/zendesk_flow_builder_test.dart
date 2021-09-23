import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:zendesk_flow_builder/zendesk_flow_builder.dart';

void main() {
  const MethodChannel channel = MethodChannel('zendesk_flow_builder');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await ZendeskFlowBuilder.platformVersion, '42');
  });
}
