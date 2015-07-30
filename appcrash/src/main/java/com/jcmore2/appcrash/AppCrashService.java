package com.jcmore2.appcrash;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


public class AppCrashService extends Service {

    private static final String TAG = "AppCrashService";

    private WindowManager windowManager;

    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams paramsF;

    private AppCrashView appCrashView;

    boolean isCrashViewVisible = false;

    private int newContent;
    private int newBackgroundColor;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        newContent = intent.getExtras().getInt(
                AppCrash.CONTENT);
        newBackgroundColor = intent.getExtras().getInt(
                AppCrash.BG_COLOR);

        appCrashView.setNewBgColor(newBackgroundColor);
        appCrashView.setNewContent(newContent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createCrashView();


        BackgroundManager.init(getApplication()).registerListener(new BackgroundManager.Listener() {
            @Override
            public void onBecameForeground(Activity activity) {

            }

            @Override
            public void onBecameBackground(Activity activity) {

                removeCrashView();
                stopService();
            }
        });

    }

    /**
     * Create the crash view
     */
    private void createCrashView() {

        appCrashView = new AppCrashView(getApplicationContext());
        appCrashView.setAppCrashListener(new AppCrashView.AppCrashListener() {
            @Override
            public void onClosed() {
                removeCrashView();
                stopService();
            }
        });


        try {
            appCrashView.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:

                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(appCrashView, paramsF);

                            break;
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "ON TOUCH ERROR--->" + e.getMessage());
        }

        setCrashViewPosition();
        addCrashView();

    }

    /**
     * Remove crash view from window
     */
    private void removeCrashView() {
        Log.i(TAG, "removeCrashView");

        if (appCrashView != null && isCrashViewVisible) {
            isCrashViewVisible = false;
            windowManager.removeView(appCrashView);
        }
    }

    /**
     * Add crash view to window
     */
    private void addCrashView() {
        Log.i(TAG, "addCrashView");

        if (appCrashView != null && !isCrashViewVisible) {
            isCrashViewVisible = true;
            setCrashViewPosition();
            windowManager.addView(appCrashView, params);
        }
    }

    /**
     * Set initial position
     */
    private void setCrashViewPosition() {

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        paramsF = params;
    }

    /**
     * Stop service
     */
    private void stopService() {

        this.stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCrashView();
    }


}

