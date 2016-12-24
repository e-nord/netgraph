package com.norddev.netgraph.traffic;

public class AppTrafficStat extends BaseTrafficStat<AppTrafficStat> {
    private final int mUid;
    private final long mTotalRXBytes;
    private final long mTotalTXBytes;

    public AppTrafficStat(long timeMillis, int uid, long totalRXBytes, long totalTXBytes) {
        super(timeMillis);
        mUid = uid;
        mTotalRXBytes = totalRXBytes;
        mTotalTXBytes = totalTXBytes;
    }

    public int getUid() {
        return mUid;
    }

    public long getTotalRXBytes() {
        return mTotalRXBytes;
    }

    public long getTotalTXBytes() {
        return mTotalTXBytes;
    }

    public long getTotalBytes() {
        return mTotalRXBytes + mTotalTXBytes;
    }

    @Override
    public AppTrafficStat subtract(AppTrafficStat other) {
        return new AppTrafficStat(
                getTimeMillis(),
                getUid(),
                getTotalRXBytes() - other.getTotalRXBytes(),
                getTotalTXBytes() - other.getTotalTXBytes()
        );
    }
}