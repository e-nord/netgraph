package com.norddev.netgraph;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import com.norddev.netgraph.traffic.AppTrafficStat;
import com.norddev.netgraph.traffic.GlobalTrafficStat;

public class TrafficStatsCollector {

    private GlobalTrafficStat mGlobalPrev;
    private Handler mHandler;
    private GlobalStatsListener mGlobalListener;
    private HandlerThread mThread;
    private boolean mIsRunning;

    interface GlobalStatsListener {
        void onGlobalStatsUpdated(GlobalTrafficStat stat);
    }

    public TrafficStatsCollector() {
        mThread = new HandlerThread("TrafficStatsCollector", HandlerThread.MIN_PRIORITY){
            @Override
            protected void onLooperPrepared() {
                mHandler = new Handler(getLooper());
            }
        };
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    void start(){
        mIsRunning = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGlobalListener != null) {
                    mGlobalListener.onGlobalStatsUpdated(getGlobal());
                }
                if(mIsRunning){
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    void stop(){
        mIsRunning = false;
    }

    public void setListener(GlobalStatsListener listener){
        mGlobalListener = listener;
    }

    private GlobalTrafficStat getGlobal(){
        GlobalTrafficStat stat;
        if(mGlobalPrev == null){
            mGlobalPrev = collectGlobal();
        }
        stat = collectGlobal();
        GlobalTrafficStat result = stat.subtract(mGlobalPrev);
        mGlobalPrev = stat;
        return result;
    }

    private GlobalTrafficStat collectGlobal() {
        return new GlobalTrafficStat(
                SystemClock.elapsedRealtime(),
                TrafficStats.getMobileRxBytes(),
                TrafficStats.getMobileTxBytes(),
                TrafficStats.getTotalRxBytes(),
                TrafficStats.getTotalTxBytes()
        );
    }

    private AppTrafficStat collectApp(int uid) {
        return new AppTrafficStat(
                SystemClock.elapsedRealtime(),
                uid,
                TrafficStats.getUidRxBytes(uid),
                TrafficStats.getUidTxBytes(uid)
        );
    }
}