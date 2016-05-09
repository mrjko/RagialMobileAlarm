package com.mrjko.ragialmobilealarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jko on 16-04-12.
 */
public class VendHeadService extends Service implements Observer {

    public int soldItemCount;
    private WindowManager windowManager;
    public ImageView chatHead;
    WindowManager.LayoutParams params;


    @Override
    public void onCreate() {
        super.onCreate();
        soldItemCount = 0;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.ic_launcher);

        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        //this code is for dragging the chat head
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long action_down_time;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        action_down_time = System.currentTimeMillis();
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        chatHead.setImageResource(R.drawable.alerticon);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - action_down_time < 0.25*1000) {
                            /*
                            Intent intent = new Intent(VendHeadService.this, TradeInfo.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            */
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        chatHead.setImageResource(R.drawable.alerticon);

                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(chatHead, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null)
            windowManager.removeView(chatHead);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void update() {
        soldItemCount++;
        if (soldItemCount < 3){
            Log.d("test", "item count is smaller than 3 so we set image as few image");
            this.chatHead.setImageResource(R.drawable.ic_launcher);
        } else {
            Log.d("test", "item count is greater than 3 so we set iamge as a greater image");
            this.chatHead.setImageResource(R.drawable.alerticon);
        }
    }
}
