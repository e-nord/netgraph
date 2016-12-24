package com.norddev.netgraph;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import static android.app.PendingIntent.getService;

public class NetGraphOverlayService extends Service {

    private static final String ACTION_HIDE = "com.norddev.netgraph.ACTION_HIDE";
    private static final String ACTION_SHOW = "com.norddev.netgraph.ACTION_SHOW";
    private WindowManager mWindowManager;
    private NetGraph mGraphView;
    private TrafficStatsCollector mTrafficStatsCollector;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mTrafficStatsCollector = new TrafficStatsCollector();
        mTrafficStatsCollector.start();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mGraphView = new NetGraph(this);
        mGraphView.configure();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                600,
                300,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = 0;
        mGraphView.setTrafficStatsCollector(mTrafficStatsCollector);
        mGraphView.setOnTouchListener(new DraggableTouchListener(mWindowManager, layoutParams, mGraphView));
        mWindowManager.addView(mGraphView, layoutParams);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setContentTitle("NetGraph Running");

        showNotification(ACTION_HIDE, "Tap to hide");
    }

    private void showNotification(String action, String text){
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        PendingIntent pi = getService(this, 0, new Intent(action, null, this, NetGraphOverlayService.class), 0);
        mBuilder.setContentText(text);
        mBuilder.setContentIntent(pi);
        startForeground(1337, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrafficStatsCollector.stop();
        if (mGraphView != null) {
            mWindowManager.removeView(mGraphView);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                case ACTION_HIDE:
                    mGraphView.setVisibility(View.GONE);
                    showNotification(ACTION_SHOW, "Tap to show");
                    break;
                case ACTION_SHOW:
                    mGraphView.setVisibility(View.VISIBLE);
                    showNotification(ACTION_HIDE, "Tap to hide");
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}