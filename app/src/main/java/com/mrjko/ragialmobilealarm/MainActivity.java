package com.mrjko.ragialmobilealarm;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ROItems currentItems;
    public static EditText searchField;
    public ListView listView;
    private boolean isAlarmSet = false;
   // public boolean isItemBeingSold;
    public boolean isNotificationActive;
    public int notificationID;
    private NotificationManager notificationManager;
    public static ROItems roItems = new ROItems();
    private Button searchBtn, setAlarmBtn;
    private static RadioButton classicBtn, renewalBtn;
    private int previousItemCount;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // needed for jSoup parsing:
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        searchField = (EditText) findViewById(R.id.searchField);
        renewalBtn = (RadioButton) findViewById(R.id.renewalBtn);
        classicBtn = (RadioButton) findViewById(R.id.classicBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmBtn);
        listView = (ListView) findViewById(R.id.vendItems);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> arrayAdapter = getVendInfo();
                listView.setAdapter(arrayAdapter);
            }
        });

        setAlarmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

    }

    private void setAlarm() {
        if (isAlarmSet){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Cancel:")
                    .setMessage("Are you sure you want to cancel the alarm?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Log.d("test", "alarm is cancelled");
                            setAlarmBtn.setText("Set Alarm");
                            timer.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            Log.d("test", "alarm is still running");
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmation")
                    .setMessage("Set alarm for the items?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setAlarmBtn.setText("Alarmed!");
                            getVendInfo();
                            previousItemCount = currentItems.getTotalQuantity();
                            isAlarmSet = true;
                            timer.scheduleAtFixedRate(new TimerTask() {

                                public void run() {
                                    Log.d("test", "updating info...");
                                    getVendInfo();

                                    if (isAlarmSet) {
                                        if (previousItemCount != currentItems.getTotalQuantity()) {
                                            notifyUser();
                                        }
                                    }

                                }

                            }, 500, 2 * 60 * 1000); // every 2 min

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyUser()
    {
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Ragial Notification")
                .setContentText("An item has been sold!")
                .setTicker("Ragial Notification")
                .setSmallIcon(R.drawable.alerticon);

        Intent moreInfoIntent = new Intent(this, MoreInfoNotification.class);
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);
        tStackBuilder.addParentStack(MainActivity.class);
        tStackBuilder.addNextIntent(moreInfoIntent);

        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificBuilder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notificBuilder.build());

        isNotificationActive = true;

    }

    private ArrayAdapter<String> getVendInfo() {
        ParseCharacterURL url = new ParseCharacterURL();
        ParseVendingItems parse = new ParseVendingItems();
        currentItems = parse.getItemList();
        ArrayList<String> itemNames = new ArrayList<String>();
        for (int i = 0; i < currentItems.getSize(); i++){
            itemNames.add(currentItems.get(i).getName()
                    +  "\n" + currentItems.get(i).getPrice() + " - " + currentItems.get(i).getQuantity());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemNames);
        return arrayAdapter;
    }

    public static String getDatabaseURL(){
        String url = "http://ropd.info/?name=" + getCharName() + "&" + getServerName();
        Log.d("test", url);
        return url;
    }

    private static String getCharName(){
        return searchField.getText().toString();
    }

    private static String getServerName(){
        if (classicBtn.isChecked()){
            return "s=6";
        }
        if (renewalBtn.isChecked()){
            return "s=7";
        }
        return "";
    }


}
