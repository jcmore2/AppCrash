package com.jcmore2.appcrash;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by jcmore2 on 28/7/15.
 */
class AppCrashActivity extends Activity {

    private static final String TAG = "AppCrashActivity";

    private Class<? extends Activity> restartActivityClass;

    private int newContent;
    private int newBackgroundColor;
    private String initActivityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_crash_activity);

        newContent = getIntent().getExtras().getInt(
                AppCrash.CONTENT);
        newBackgroundColor = getIntent().getExtras().getInt(
                AppCrash.BG_COLOR);
        initActivityName = getIntent().getExtras().getString(
                AppCrash.INIT_ACTIVITY);

        LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
        LinearLayout content = (LinearLayout) findViewById(R.id.content);

        if (newBackgroundColor != 0) {
            parent.setBackgroundColor(getResources().getColor(newBackgroundColor));
        }

        LayoutInflater layoutInflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mContent;
        if (newContent != 0) {
            mContent = layoutInflater.inflate(newContent, null);
        } else {
            mContent = layoutInflater.inflate(R.layout.content, null);
        }
        content.addView(mContent, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        restartActivityClass = AppCrash.getLauncherActivity(this);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                if (initActivityName != null) {
                    Class<?> initClass = null;
                    try {
                        initClass = Class.forName(initActivityName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (initClass != null)
                        intent = new Intent(AppCrashActivity.this, initClass);
                    else
                        intent = new Intent(AppCrashActivity.this, restartActivityClass);


                } else {
                    intent = new Intent(AppCrashActivity.this, restartActivityClass);
                }
                finish();
                startActivity(intent);

            }
        });
    }
}
