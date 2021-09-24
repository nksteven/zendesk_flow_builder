package com.aragoncs.zendesk_flow_builder;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.zopim.android.sdk.prechat.ZopimChatActivity;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** ZendeskFlowBuilderPlugin */
public class ZendeskFlowBuilderPlugin implements FlutterPlugin, ActivityAware, Application.ActivityLifecycleCallbacks {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private MethodCallHandlerImpl methodCallHandler = new MethodCallHandlerImpl();
  private Activity activity;
  public static boolean isFore=false;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    startListening(flutterPluginBinding.getBinaryMessenger());
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    ZendeskFlowBuilderPlugin plugin=new ZendeskFlowBuilderPlugin();
    plugin.startListening(registrar.messenger());
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private void startListening(BinaryMessenger messenger) {
    if(channel==null){
      channel = new MethodChannel(messenger, "zendesk_flow_builder");
      channel.setMethodCallHandler(methodCallHandler);
      methodCallHandler.setMethodCall(channel);
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity=binding.getActivity();
    methodCallHandler.setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    methodCallHandler.setActivity(null);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    methodCallHandler.setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    methodCallHandler.setActivity(null);
  }


  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=true;
    }
  }

  @Override
  public void onActivityResumed(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=true;
    }
  }

  @Override
  public void onActivityPaused(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=false;
      methodCallHandler.getInitCountMessage();
    }
  }

  @Override
  public void onActivityStopped(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=false;
    }
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
  }

}
