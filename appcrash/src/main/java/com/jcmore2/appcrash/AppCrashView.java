package com.jcmore2.appcrash;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;


/**
 * Created by jcmore2 on 25/2/15.
 */
public class AppCrashView extends LinearLayout {

    private static final String TAG = "AppCrashView";

    private Context mContext;
    private View appCrashView;
    private AppCrashListener appCrashListener;

    private LinearLayout parent;
    private LinearLayout contentLayout;


    /**
     * @param context context
     */
    public AppCrashView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attributes
     */
    public AppCrashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle styles
     */
    public AppCrashView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Function use to init the visual components of view
     *
     * @param context context
     */
    private synchronized void init(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appCrashView = layoutInflater.inflate(R.layout.app_crash_view, null);

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(appCrashView, layoutParams);

        createView();
    }

    /**
     * Create crash view
     */
    private void createView() {

        parent = (LinearLayout) appCrashView.findViewById(R.id.parent);
        contentLayout = (LinearLayout) appCrashView.findViewById(R.id.content);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mContent = layoutInflater.inflate(R.layout.content, null);

        contentLayout.addView(mContent, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        appCrashView.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.slide_up));

        appCrashView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation slide_down = AnimationUtils.loadAnimation(mContext,
                        R.anim.slide_down);
                appCrashView.startAnimation(slide_down);
                slide_down.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (appCrashListener != null)
                            appCrashListener.onClosed();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }
        });
    }

    /**
     * Set New Bg color to crash view
     * @param bgColor color
     */
    protected void setNewBgColor(int bgColor){

        if (bgColor != 0) {
            parent.setBackgroundColor(getResources().getColor(bgColor));
            invalidate();
        }
    }

    /**
     * Set new content crash
     * @param content content
     */
    protected void setNewContent(int content){

        if(content != 0) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View mContent = layoutInflater.inflate(content, null);
            contentLayout.removeViewAt(0);
            contentLayout.addView(mContent, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            invalidate();
        }
    }


    protected void setAppCrashListener(AppCrashListener appCrashListener) {

        this.appCrashListener = appCrashListener;

    }

    protected interface AppCrashListener {

        void onClosed();

    }


}
