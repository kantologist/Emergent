package com.example.femi.emergent;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 01/12/2017.
 */

public class BaseApplication  extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
