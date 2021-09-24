package com.aragoncs.zendesk_flow_builder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import zendesk.logger.Logger;
import com.zendesk.service.ZendeskCallback;
import com.zopim.android.sdk.api.ChatApi;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.data.LivechatChatLogPath;
import com.zopim.android.sdk.data.observers.ChatLogObserver;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.PushData;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.util.AppInfo;
import com.zopim.android.sdk.widget.ChatWidgetService;

import zendesk.messaging.android.FailureCallback;
import zendesk.messaging.android.Messaging;
import zendesk.messaging.android.MessagingError;
import zendesk.messaging.android.SuccessCallback;

public class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {


    public static String CHANNEL_KEY="CHANNEL_KEY";

    @Nullable
    private Activity activity;
    int initCount= 0;
    private MethodChannel methodCallHandler;



    Handler mhandler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            methodCallHandler.invokeMethod("UnreadListener",msg.what);
        }
    };

    void setMethodCall(MethodChannel methodCallHandler){
        this.methodCallHandler=methodCallHandler;
    }
    void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "init":
                handleInit(call, result);
                break;
            case "setVisitorInfo":
                handleSetVisitorInfo(call, result);
                break;
            case "setToken":
                handleSetToken(call, result);
                break;
            case "startChat":
                handleStartChat(call, result);
                break;
            case "version":
                handleVersion(result);
                break;
            case "openSystemAlert":
                openSystemAlert(result);
                break;
            case "checkSystemAlertPermission":
                checkSystemAlertPermission(result);
                break;
            case "closeChatWidget":
                closeChatWidget(result);
                break;
            case "onReceivedChatMessage":
                onReceiveMessage(call,result);
                break;
            default:
                try{
                    result.notImplemented();
                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void onReceiveMessage(MethodCall call, MethodChannel.Result result) {
        Log.d("tag","step 1 call.arguments="+call.arguments);
        Map<String,String> map= (Map<String, String>) call.arguments;
        Log.d("tag","step 2 call.arguments="+call.arguments);
        PushData data=PushData.getChatNotification(map);
        Log.d("tag","step 3 call.arguments="+call.arguments);
        ZopimChatApi.onMessageReceived(data);
        Log.d("tag","step 4 call.arguments="+call.arguments);
        result.success("");
    }


    private void closeChatWidget(MethodChannel.Result result) {
        ChatWidgetService.stopService(activity);
        result.success("");
    }


    private void handleInit(MethodCall call, MethodChannel.Result result) {
        Logger.setLoggable(true);
        String channelKey=call.argument("channelKey").toString();
        Messaging.initialize(
                activity,
                channelKey,
                new SuccessCallback<Messaging>() {
                    @Override
                    public void onSuccess(Messaging value) {
                        android.util.Log.i("IntegrationApplication", "Initialization successful");
                    }
                },
                new FailureCallback<MessagingError>() {
                    @Override
                    public void onFailure(@Nullable MessagingError cause) {
                        // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
                        android.util.Log.e("IntegrationApplication", "Messaging failed to initialize", cause);
                    }
                });
        PreChatForm defaultPreChat = new PreChatForm.Builder()
                .name(PreChatForm.Field.OPTIONAL)
                .email(PreChatForm.Field.OPTIONAL)
                .phoneNumber(PreChatForm.Field.OPTIONAL)
                .department(PreChatForm.Field.OPTIONAL)
                .message(PreChatForm.Field.OPTIONAL)
                .build();
        ZopimChat.DefaultConfig zopimConfig = ZopimChat.init((String) call.argument("accountKey"));
        if (call.hasArgument("department")) {
            zopimConfig.department((String) call.argument("department"));
        }
        if (call.hasArgument("appName")) {
            zopimConfig.visitorPathOne((String) call.argument("appName"));
        }
        zopimConfig.preChatForm(defaultPreChat);
        result.success(true);
    }

    public void getInitCountMessage(){
        initCount= LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
        Log.d("onActivity","initCount=="+initCount);
    }


    private void handleSetVisitorInfo(MethodCall call, MethodChannel.Result result) {
        VisitorInfo.Builder builder = new VisitorInfo.Builder();
        if (call.hasArgument("name")) {
            builder = builder.name((String) call.argument("name"));
        }
        if (call.hasArgument("email")) {
            if(call.argument("email")!=null&&call.argument("email")!=""){
                Log.d("email","email="+call.argument("email").toString());
                builder = builder.email((String) call.argument("email"));
            }
        }
        if (call.hasArgument("phoneNumber")) {
            if(call.argument("phoneNumber")!=null&&call.argument("phoneNumber")!=""){
                builder = builder.phoneNumber((String) call.argument("phoneNumber"));
            }
        }
        if (call.hasArgument("note")) {
            builder = builder.note((String) call.argument("note"));
        }
        if (call.hasArgument("firebase_token")) {
            if(call.argument("firebase_token")!=null&&call.argument("firebase_token")!=""){
                ZopimChat.setPushToken((String) call.argument("firebase_token"));
            }
        }
        ZopimChat.setVisitorInfo(builder.build());

        result.success(true);
    }

    private void handleSetToken(MethodCall call, MethodChannel.Result result) {
        if (call.hasArgument("firebase_token")) {
            if(call.argument("firebase_token")!=null&&call.argument("firebase_token")!=""){
                Log.d("firebase_token","firebasetoken="+call.argument("firebase_token").toString());
                ZopimChat.setPushToken((String) call.argument("firebase_token"));
            }
        }
        result.success(true);
    }

    private void handleStartChat(MethodCall call, MethodChannel.Result result) {
        ZopimChatApi.getDataSource().addChatLogObserver(mChannelLogObserver);
        if (activity != null) {
            Messaging.instance().showMessaging(activity);
//      Intent intent = new Intent(activity, ZopimChatActivity.class);
//      intent.setAction("zopim.action.RESUME_CHAT");
//      activity.startActivity(intent);
        }
        result.success(true);
    }

    private void handleVersion(MethodChannel.Result result) {
        result.success(AppInfo.getChatSdkVersionName());
    }

    private void openSystemAlert(MethodChannel.Result result){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(!Settings.canDrawOverlays(activity)){

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

                intent.setData(Uri.parse("package:" + activity.getPackageName()));

                activity.startActivity(intent);

            }
        }
        result.success("");
    }

    private void checkSystemAlertPermission(MethodChannel.Result result) {
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean check=Settings.canDrawOverlays(activity);

                if(result!=null){
                    result.success(check);
                    result=null;
                }

            }else{
                if(result!=null){
                    result.success(true);
                    result=null;
                }
            }
        }catch (IllegalStateException e){

            if(result!=null){
                result.success(true);
                result=null;
            }
        }
    }

    ChatLogObserver mChannelLogObserver = new ChatLogObserver() {
        public void update(LinkedHashMap<String, ChatLog> chatLog) {
            if(!ZendeskFlowBuilderPlugin.isFore){
                int currentCount = LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
                if(initCount==0){
                    initCount=LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
                }
                final int unread = currentCount-initCount;
                mhandler.sendEmptyMessage(unread);
            }
        }
    };

}
