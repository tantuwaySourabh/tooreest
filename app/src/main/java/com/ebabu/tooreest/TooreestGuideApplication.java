package com.ebabu.tooreest;

import android.app.Application;

/**
 * Created by hp on 30/03/2017.
 */
public class TooreestGuideApplication extends Application {

    private Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = (Application) getApplicationContext();
//        InstallConfig installConfig = new InstallConfig.Builder()
//                .setFont("ROBOTO-REGULAR.TTF")
//                .setEnableInAppNotification(true)
//                .setNotificationIcon(Utils.getNotificationsIcon())
//                .build();
//
//        Core.init(All.getInstance());
//        try {
//            Core.install(application,
//                    IKeyConstants.HELPSHIFT_API_KEY,
//                    IKeyConstants.HELPSHIFT_DOMAIN,
//                    IKeyConstants.HELPSHIFT_APP_ID,
//                    installConfig);
//        } catch (InstallException e) {
//            e.printStackTrace();
//        }
    }
}
