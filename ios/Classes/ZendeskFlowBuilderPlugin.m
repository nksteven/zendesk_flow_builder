#import "ZendeskFlowBuilderPlugin.h"

//#import <ChatSDK/ChatSDK.h>
//#import <ChatProvidersSDK/ChatProvidersSDK.h>
//#import <MessagingSDK/MessagingSDK.h>
//#import <MessagingAPI/MessagingAPI.h>
//#import <SDKConfigurations/SDKConfigurations.h>
//#import <ZendeskCoreSDK/ZendeskCoreSDK.h>
//#import <SupportSDK/SupportSDK.h>
#import <ZendeskSDKMessaging/ZendeskSDKMessaging.h>
#import <ZendeskSDK/ZendeskSDK.h>


#define ARGB_COLOR(c) [UIColor colorWithRed:((c>>16)&0xFF)/255.0 green:((c>>8)&0xFF)/255.0 blue:((c)&0xFF)/255.0  alpha:((c>>24)&0xFF)/255.0]


@implementation ZendeskFlowBuilderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"zendesk_flow_builder"
            binaryMessenger:[registrar messenger]];
  ZendeskFlowBuilderPlugin* instance = [[ZendeskFlowBuilderPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"init" isEqualToString:call.method]) {
      NSNumber *counter = [NSNumber numberWithInt: 1];
      [[NSUserDefaults standardUserDefaults] setObject: counter forKey:@"zendesk_counter"];
      [[NSUserDefaults standardUserDefaults] synchronize];
      [Zendesk initializeWithChannelKey:call.arguments[@"channelKey"] messagingFactory:[[ZDKDefaultMessagingFactory alloc] init]
                                  completionHandler:^(Zendesk * _Nullable zendesk, NSError * _Nullable error) {
                  // Tracking the error from initialization failures in your crash
                  // reporting dashboard will help to triage any unexpected failures in production
                 if (error != nil) {
                     NSLog(@"Messaging did not initialize.\nError: %@", error.localizedDescription);
                 } else {
                     NSLog(@"Messaging did  initialize.");
                 }
             }];
    result(@(true));
  } else if ([@"setVisitorInfo" isEqualToString:call.method]) {

      result(@(true));
  } else if ([@"startChat" isEqualToString:call.method]) {
      NSNumber *navigationBarColor = call.arguments[@"iosNavigationBarColor"];
      NSNumber *navigationTitleColor = call.arguments[@"iosNavigationTitleColor"];

      UINavigationController *navVc = [[UINavigationController alloc] init];
      navVc.navigationBar.translucent = YES;
      if ([navigationBarColor isKindOfClass:[NSNumber class]]) {
          navVc.navigationBar.barTintColor = ARGB_COLOR([navigationBarColor integerValue]);//
      } else {
          navVc.navigationBar.barTintColor = [UIColor colorWithRed:245.0/255.0 green:245.0/255.0 blue:245.0/255.0  alpha:1.0];//
      }
      
      if ([navigationTitleColor isKindOfClass:[NSNumber class]]) {
          navVc.navigationBar.titleTextAttributes = @{
                                                               NSForegroundColorAttributeName: ARGB_COLOR([navigationTitleColor integerValue])
                                                               };
      } else {
          navVc.navigationBar.titleTextAttributes = @{
                                                               NSForegroundColorAttributeName: UIColor.blackColor
                                                               };
      }

      [self start_chat:navVc];

    result(@(true));
  } else if ([@"version" isEqualToString:call.method]) {
    result(@"SDK V2");
  } else if ([@"endChat" isEqualToString:call.method]) {
//      [ZDKChat.chatProvider endChat:^(BOOL _, NSError* __){}];
      result(@(true));
  } else if ([@"setToken" isEqualToString:call.method]) {
    result(@(true));
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)start_chat:(UINavigationController *)navVc {
    UIColor *navigationTitleUIColor = UIColor.whiteColor;
    UIViewController *viewController = NULL;
        viewController = [Zendesk.instance.messaging messagingViewController];
        if (viewController != NULL) {
            // Present view controller
            [navVc pushViewController:viewController animated:YES];

            UIViewController *rootVc = [UIApplication sharedApplication].keyWindow.rootViewController ;
            [rootVc presentViewController:navVc
                                 animated:true
                               completion:^{
                UIBarButtonItem *back = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Back", comment: @"")
                                                                         style:UIBarButtonItemStylePlain
                                                                        target:self
                                                                        action:@selector(close:)];
                [back setTitleTextAttributes:@{ NSForegroundColorAttributeName: navigationTitleUIColor} forState:UIControlStateNormal];

                navVc.topViewController.navigationItem.leftBarButtonItem = back;
                
            }];
            NSNumber *counter = [NSNumber numberWithInt: 1];
            [[NSUserDefaults standardUserDefaults] setObject: counter forKey:@"zendesk_counter"];
            [[NSUserDefaults standardUserDefaults] synchronize];
        } else {
            NSNumber *counter = [[NSUserDefaults standardUserDefaults] objectForKey:@"zendesk_counter"];
            int counter_int = counter.intValue;
            NSLog(@"----start_chat------%d", counter_int);
            if (counter_int <= 10) {
                [self performSelector:@selector(start_chat:) withObject:navVc afterDelay:1.5];
                counter = [NSNumber numberWithInt: counter_int + 1];
            } else {
                counter = [NSNumber numberWithInt: 1];
            }
            [[NSUserDefaults standardUserDefaults] setObject: counter forKey:@"zendesk_counter"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
        }
}

- (void)close:(id)sender {
    [[UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:true completion:nil];
}

@end
