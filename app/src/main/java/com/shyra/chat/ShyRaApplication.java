package com.shyra.chat;

import android.app.Application;
import android.content.ContextWrapper;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by rachitgoyal on 10/1/16.
 */

public class ShyRaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this, getString(R.string.facebook_app_id));

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
