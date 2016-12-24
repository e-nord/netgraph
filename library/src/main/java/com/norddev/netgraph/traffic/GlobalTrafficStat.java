package com.norddev.netgraph.traffic;

public class GlobalTrafficStat extends BaseTrafficStat<GlobalTrafficStat> {
    private final long mMobileRXBytes;
    private final long mMobileTXBytes;
    private final long mTotalRXBytes;
    private final long mTotalTXBytes;

    public GlobalTrafficStat(long timeMillis, long mobileRXBytes, long mobileTXBytes, long totalRXBytes, long totalTXBytes) {
        super(timeMillis);
        mMobileRXBytes = mobileRXBytes;
        mMobileTXBytes = mobileTXBytes;
        mTotalRXBytes = totalRXBytes;
        mTotalTXBytes = totalTXBytes;
    }

    public long getMobileRXBytes() {
        return mMobileRXBytes;
    }

    public long getMobileTXBytes() {
        return mMobileTXBytes;
    }

    public long getTotalMobileBytes() {
        return mMobileRXBytes + mMobileTXBytes;
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
    public GlobalTrafficStat subtract(GlobalTrafficStat other) {
        return new GlobalTrafficStat(
                getTimeMillis(),
                getMobileRXBytes() - other.getMobileRXBytes(),
                getMobileTXBytes() - other.getMobileTXBytes(),
                getTotalRXBytes() - other.getTotalRXBytes(),
                getTotalTXBytes() - other.getTotalTXBytes()
                );
    }
}