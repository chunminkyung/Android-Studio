package com.example.practice;

import android.app.Application;

public class myApplication extends Application {
    public static String api_url;

    @Override
    public void onCreate() {
        super.onCreate();

        api_url="http://api.kiki-bus.com/";
    }

}

