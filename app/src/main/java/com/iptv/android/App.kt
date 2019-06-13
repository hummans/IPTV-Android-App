package com.iptv.android

import android.app.Application
import com.onesignal.OneSignal

/**
 * Developed by tcbaras on 20.03.2019.
 */
class App: Application() {


    override fun onCreate() {
        super.onCreate()

     //   OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG)
        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}