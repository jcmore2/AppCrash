package com.jcmore2.appcrashsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jcmore2.appcrash.AppCrash;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.crashButtonActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("CRASH Activity!!!!!!");
            }
        });

        findViewById(R.id.crashButtonDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCrash.get().showDialog().setListener(new AppCrash.AppCrashListener() {
                    @Override
                    public void onAppCrash(Throwable ex) {
                        Toast.makeText(MainActivity.this, AppCrash.traceExcetion(ex), Toast.LENGTH_LONG).show();
                    }
                });
                throw new RuntimeException("CRASH Dialog!!!!!!");
            }
        });

        findViewById(R.id.crashButtonActivityCustom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCrash.get().withInitActivity(InitActivity.class)
                        .withBackgroundColor(android.R.color.black)
                        .withView(R.layout.custom_error_view).setListener(new AppCrash.AppCrashListener() {
                    @Override
                    public void onAppCrash(Throwable ex) {
                        Toast.makeText(MainActivity.this, AppCrash.traceExcetion(ex), Toast.LENGTH_LONG).show();
                    }
                });
                ;

                throw new RuntimeException("CRASH Activity CUSTOM!!!!!!");
            }
        });

        findViewById(R.id.crashButtonDialogCustom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCrash.get().withInitActivity(InitActivity.class)
                        .withBackgroundColor(android.R.color.black)
                        .withView(R.layout.custom_error_view).showDialog().setListener(new AppCrash.AppCrashListener() {
                    @Override
                    public void onAppCrash(Throwable ex) {
                        Toast.makeText(MainActivity.this, AppCrash.traceExcetion(ex), Toast.LENGTH_LONG).show();
                    }
                });
                ;

                throw new RuntimeException("CRASH Dialog CUSTOM!!!!!!");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
