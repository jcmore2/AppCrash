package com.jcmore2.appcrash;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by jcmore2 on 28/7/15.
 * <p/>
 * AppCrash let you relaunch the app and manage crash message when your app has an exception.
 */
public class AppCrash {

    private static final String TAG = "AppCrash";

    private static Application application;
    private static AppCrash sInstance;

    private static Intent service;

    protected static Class<? extends Activity> initActivity;
    protected static String initActivityName;
    protected static int contentView = 0;
    protected static int backgroundColor = 0;

    protected static final String CONTENT = "CONTENT";
    protected static final String BG_COLOR = "BG_COLOR";
    protected static final String INIT_ACTIVITY = "INIT_ACTIVITY";


    protected static boolean showDialog = false;

    private static AppCrashListener mListener;

    /**
     * Init the AppCrash instance
     *
     * @param context
     */
    public static AppCrash init(Context context) {

        if (sInstance == null) {
            sInstance = new AppCrash(context);
        }

        return sInstance;
    }


    /**
     * get the AppCrash instance
     *
     * @return
     */
    public static AppCrash get() {
        if (sInstance == null) {
            throw new IllegalStateException(
                    "AppCrash is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return sInstance;
    }

    /**
     * Set Content view
     *
     * @param resourceLayout
     * @return
     */
    public static AppCrash withView(int resourceLayout) {
        contentView = resourceLayout;
        return sInstance;
    }

    /**
     * Set Background color view
     *
     * @param color
     * @return
     */
    public static AppCrash withBackgroundColor(int color) {
        backgroundColor = color;
        return sInstance;
    }

    /**
     * Set Activity to init App when crash
     *
     * @param activity
     * @return
     */
    public static AppCrash withInitActivity(Class<? extends Activity> activity) {
        initActivity = activity;
        initActivityName = activity.getName();
        return sInstance;
    }

    /**
     * ShowDialog
     */
    public static AppCrash showDialog() {
        showDialog = true;
        return sInstance;
    }

    /**
     * ShowDialog
     */
    public static void setListener(AppCrashListener listener) {
        mListener = listener;
    }


    /**
     * Constructor
     *
     * @param context
     */
    private AppCrash(Context context) {
        try {
            if (context == null) {
                Log.e(TAG, "Cant init, context must not be null");
            } else {

                application = (Application) context.getApplicationContext();

                final Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

                // setup handler for uncaught exception
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {

                        traceExcetion(ex);
                        if (mListener != null)
                            mListener.onAppCrash(ex);

                        if (showDialog) {
                            if (initActivity != null) {
                                launch(initActivity);
                            } else {
                                launch(getLauncherActivity(application));
                            }
                            launchService();
                        } else {
                            launch(createAppCrashActivity());
                        }

                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(10);

                        // re-throw critical exception further to the os (important)
                        defaultUEH.uncaughtException(thread, ex);
                    }
                });

                BackgroundManager.init(application).registerListener(new BackgroundManager.Listener() {
                    @Override
                    public void onBecameForeground(Activity activity) {

                    }

                    @Override
                    public void onBecameBackground(Activity activity) {

                        if (service != null)
                            application.stopService(service);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write exception throw in Log
     *
     * @param ex
     */
    public static String traceExcetion(Throwable ex) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        String stacktrace = result.toString();
        Log.e(TAG, "ERROR ---> " + stacktrace);

        return stacktrace;
    }

    /**
     * This method return default AppCrashActivity
     *
     * @return
     */
    private static Class<? extends Activity> createAppCrashActivity() {
        return AppCrashActivity.class;
    }

    /**
     * This Method return default App launcher activity
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends Activity> getLauncherActivity(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            try {
                return (Class<? extends Activity>) Class.forName(intent.getComponent().getClassName());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Error", e);
            }
        }

        return null;
    }

    /**
     * Launch the activity with params
     *
     * @param activity
     */
    protected static void launch(Class<? extends Activity> activity) {

        final Intent intent = new Intent(application, activity);
        intent.putExtra(CONTENT, contentView);
        intent.putExtra(BG_COLOR, backgroundColor);
        intent.putExtra(INIT_ACTIVITY, initActivityName);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    /**
     * Launch service with params
     */
    private static void launchService() {

        service = new Intent(application, AppCrashService.class);
        service.putExtra(CONTENT, contentView);
        service.putExtra(BG_COLOR, backgroundColor);
        application.startService(service);

    }

    public interface AppCrashListener {


        void onAppCrash(Throwable ex);
    }

}
