package com.ebabu.tooreest.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ebabu.tooreest.activity.OtpActivity;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.AppPreference;


public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = SmsReceiver.class.getSimpleName();
    private final static String OTP_MSG_PREFIX = "Your OTP for Jokaamo is: ";

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive()");

        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }
        for (SmsMessage msg : sms) {
            if (msg.getOriginatingAddress().contains("jokaam")) {
                if (AppPreference.getInstance(context).isOtpScreen()) {
                    String message = msg.getMessageBody().toLowerCase();
                    Log.d(TAG, "message: " + message);
                    String otp = message.replace(OTP_MSG_PREFIX.toLowerCase(), IKeyConstants.EMPTY);
                    OtpActivity.updateMessageBox(otp);
                }

            }
        }
    }
}
