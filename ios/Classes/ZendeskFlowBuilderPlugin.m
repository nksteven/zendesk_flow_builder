#import "ZendeskFlowBuilderPlugin.h"
#if __has_include(<zendesk_flow_builder/zendesk_flow_builder-Swift.h>)
#import <zendesk_flow_builder/zendesk_flow_builder-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "zendesk_flow_builder-Swift.h"
#endif

@implementation ZendeskFlowBuilderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftZendeskFlowBuilderPlugin registerWithRegistrar:registrar];
}
@end
