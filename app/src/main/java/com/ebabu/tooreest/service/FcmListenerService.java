package com.ebabu.tooreest.service;

import android.util.Log;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by hp on 03/10/2016.
 */
public class FcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FcmListenerService", "remoteMessage=" + remoteMessage.getData());
        super.onMessageReceived(remoteMessage);
        if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
            if(AppPreference.getInstance(this).isChatNotiOn())
            MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
        } else {
            if(AppPreference.getInstance(this).isOtherNotiOn()) {
                Map<String, String> data = remoteMessage.getData();
                String origin = data.get("origin");
                if (origin != null && origin.equals("helpshift")) {
                    //Core.handlePush(this, data);
                }else {
                    Utils.generateNotification(FcmListenerService.this, remoteMessage.getData());
                }
            }
        }


    }
}
