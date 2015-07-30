package com.jcmore2.appcrashsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jcmore2 on 29/7/15.
 */
public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));
    }
}
