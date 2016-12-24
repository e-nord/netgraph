package com.norddev.netgraph.traffic;

abstract class BaseTrafficStat<T> {
    private final long mTimeMillis;

    BaseTrafficStat(long timeMillis) {
        mTimeMillis = timeMillis;
    }

    public long getTimeMillis() {
        return mTimeMillis;
    }

    abstract T subtract(T other);
}