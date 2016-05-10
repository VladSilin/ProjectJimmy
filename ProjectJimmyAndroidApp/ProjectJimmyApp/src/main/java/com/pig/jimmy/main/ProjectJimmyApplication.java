package com.pig.jimmy.main;

import android.app.Application;
import android.content.Context;

/**
 * Created by Vlad Silin on 19/03/16.
 *
 * This is an Application class existing for convenient access to an Application context.
 * Additional functionality should be added here with care, to avoid excessive global state
 */
public class ProjectJimmyApplication extends Application {
    private static ProjectJimmyApplication instance;

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
