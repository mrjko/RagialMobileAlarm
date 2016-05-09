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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Observer {

    public ROItems previousItems = new ROItems();
    public ROItems currentItems = new ROItems();
    public VendItems vendItems;
    public static ROItems itemsSold;
    public static EditText searchField;
    public ListView listView;
    public VendHeadService vendBubble;
    public boolean isAlarmSet = false;
    public boolean isNotificationActive;
    public int notificationID;
    private NotificationManager notificationManager;
    public Button searchBtn, setAlarmBtn;
    public static RadioButton classicBtn, renewalBtn;
    public int previousItemCount;
    private Timer timer = new Timer();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                arrayAdapter = getVendInfo();
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(arrayAdapter);
            }
        });

        setAlarmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        /*

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //    view.animate();
              //  Log.d("test", new ParseItemInfo(currentItems.get(position).getLink()).lowestPrice);
                Log.d("test", arrayAdapter.getItem(position));
                arrayAdapter.getItem(position).replaceAll(arrayAdapter.getItem(position), getLowestAmount(position));
                Log.d("test", arrayAdapter.getItem(position));
                arrayAdapter.notifyDataSetChanged();

            }
        });
        */

    }

    private void setAlarm() {

        if (currentItems.getSize() == 0){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No items!")
                    .setMessage("Please find a shop from vender first.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        } else {

            if (isAlarmSet) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Cancel:")
                        .setMessage("Are you sure you want to cancel the alarm?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("test", "alarm is cancelled");
                                vendItems.removeObserver(MainActivity.this);
                                isAlarmSet = false;
                                setAlarmBtn.setBackgroundResource(R.drawable.ic_alarmicon);
                                stopService(new Intent(getApplication(), VendHeadService.class));
                                timer.cancel();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                startService(new Intent(getApplication(), VendHeadService.class));
                                getVendInfo();
                                vendItems = new VendItems(currentItems, MainActivity.this);
                                vendItems.saveItems();
                                isAlarmSet = true;
                                setAlarmBtn.setBackgroundResource(R.drawable.ic_alarmedicon);
                                timer = new Timer();
                                timer.scheduleAtFixedRate(new TimerTask() {

                                    public void run() {
                                        Log.d("test", "updating info...");
                                        getVendInfo();
                                        vendItems.updateItems(currentItems);
                                        if (isAlarmSet) {
                                            vendItems.checkVendStatus();
                                        }
                                    }

                                }, 500, 3 * 1000); // every 2 min

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        }
    }

    public String convertToZeny(int amt){
        String str = Integer.toString(amt);
        String result = "z";
        int j = 0;
        for (int i= str.length(); i > 0; i--){
            if (j == 3){
                result = str.charAt(i) + "," + result;
                j++;
            } else {
                result = str.charAt(i) + result;
                j++;
            }
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyUser() {
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Ragial Notification")
                .setContentText("An item has been sold!")
                .setTicker("Ragial Notification")
                .setSmallIcon(R.drawable.alerticon);

        notificBuilder.setVibrate(new long[] {1000, 1000});
        Intent moreInfoIntent = new Intent(this, MoreInfoNotification.class);
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);
        tStackBuilder.addParentStack(MoreInfoNotification.class);
        tStackBuilder.addNextIntent(moreInfoIntent);

        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificBuilder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notificBuilder.build());

        isNotificationActive = true;

    }

    private ArrayAdapter<String> getVendInfo() {
       // ParseCharacterURL url = new ParseCharacterURL();
        new ParseCharacterURL();
        ParseVendingItems parse = new ParseVendingItems();
        currentItems = parse.getItemList();
        ArrayList<String> itemNames = new ArrayList<String>();
        for (int i = 0; i < currentItems.getSize(); i++) {
            itemNames.add(currentItems.get(i).getName()
                    + "\n" + currentItems.get(i).getPrice() + " - " + currentItems.get(i).getQuantity());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemNames);
        return arrayAdapter;
    }

    public static String getDatabaseURL() {
        String url = "http://ropd.info/?name=" + getCharName() + "&" + getServerName();
        //    Log.d("test", url);
        return url;
    }

    public static String getCharName() {
        return searchField.getText().toString();
    }

    public static String getServerName() {
        if (classicBtn.isChecked()) {
            return "s=6";
        }
        if (renewalBtn.isChecked()) {
            return "s=7";
        }
        return "";
    }

    public String getLowestAmount(int position){
        String result = arrayAdapter.getItem(position)
                + "       " + new ParseItemInfo(currentItems.get(position).getLink()).lowestPrice;
        return result;
    }

    @Override
    public void update() {
        notifyUser();
    }

}
