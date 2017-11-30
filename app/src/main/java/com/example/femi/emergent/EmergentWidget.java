package com.example.femi.emergent;

import android.*;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import Utils.Utils;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class EmergentWidget extends AppWidgetProvider {
    private static final String CALL = "call";
    private static final String REPORT = "report";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    // utilities object
    Utils util = new Utils();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Timber.plant(new Timber.DebugTree());

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.emergent_widget);
        views.setOnClickPendingIntent(R.id.remote_call,
                getPendingSelfIntent(context, CALL));
        views.setOnClickPendingIntent(R.id.remote_send,
                getPendingSelfIntent(context, REPORT));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action){
        Intent intent = new Intent(context, EmergentWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context,0,  intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(CALL.equals(intent.getAction())){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + context.getString(R.string.emergency_number)));
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "open Emergent app and grant " +
                        "call permission to use this", Toast.LENGTH_SHORT).show();

                return;
            }
            context.startActivity(callIntent);

        }
        if(REPORT.equals(intent.getAction())){
            Toast.makeText(context, "report called", Toast.LENGTH_SHORT).show();
            Intent reportIntent = new Intent(context, HomeActivity.class);
            reportIntent.putExtra("report","report");
            reportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(reportIntent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

