package com.norddev.netgraph;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.norddev.netgraph.traffic.GlobalTrafficStat;

import java.math.BigInteger;

public class NetGraph extends LineChart {

    private static final String TAG = "NetGraphView";
    private static final long DEFAULT_MAX_VISIBLE_WINDOW_LENGTH_SECONDS = 30;
    private static final long DEFAULT_MAX_ENTRY_WINDOW_LENGTH_SECONDS = DEFAULT_MAX_VISIBLE_WINDOW_LENGTH_SECONDS + 10;

    private static final BigInteger ONE_KBIT = BigInteger.valueOf(1000);
    private static final BigInteger ONE_MBIT = BigInteger.valueOf(1000*1000);

    public NetGraph(Context context) {
        super(context);
    }

    public NetGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public class ThroughputAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(value == 0){
                return "";
            }
            BigInteger size = BigInteger.valueOf((long) value);
            String displaySize;
            if (size.divide(ONE_MBIT).compareTo(BigInteger.ZERO) > 0) {
                displaySize = String.valueOf(size.divide(ONE_MBIT)) + " Mbps";
            } else if (size.divide(ONE_KBIT).compareTo(BigInteger.ZERO) > 0) {
                displaySize = String.valueOf(size.divide(ONE_KBIT)) + " Kbps";
            } else {
                displaySize = String.valueOf(size) + " Bps";
            }
            return displaySize;
        }
    }

    public void configure(){
        setData(new LineData());
        setDrawGridBackground(true);
        getDescription().setEnabled(false);
        setBackgroundColor(Color.rgb(240, 240, 240));
        setAlpha(.7f);

        Legend l = getLegend();
        l.setEnabled(false);

        XAxis xl = getXAxis();
        xl.setEnabled(false);

        setViewPortOffsets(120, 20, 0, 0);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(4f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new ThroughputAxisValueFormatter());
        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(4);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
    }

    public void setTrafficStatsCollector(TrafficStatsCollector collector){
        collector.setListener(new TrafficStatsCollector.GlobalStatsListener() {
            @Override
            public void onGlobalStatsUpdated(final GlobalTrafficStat stat) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        addNextStat(stat);
                    }
                });
            }
        });
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Total Bytes");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setDrawFilled(true);
        return set;
    }

    private int mIndex = 0;

    private void addNextStat(GlobalTrafficStat stat){
        LineData data = getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            if(set.getYMax() > ONE_KBIT.longValue()){
                getAxisLeft().setGranularity(ONE_KBIT.longValue());
            } else if(set.getYMax() > ONE_MBIT.longValue()){
                getAxisLeft().setGranularity(ONE_MBIT.longValue());
            }
            set.addEntry(new Entry(mIndex++, stat.getTotalBytes() * 8, stat));

            if(set.getEntryCount() > DEFAULT_MAX_ENTRY_WINDOW_LENGTH_SECONDS){
                set.removeEntryByXValue(mIndex - DEFAULT_MAX_ENTRY_WINDOW_LENGTH_SECONDS);
            }
            set.calcMinMax();
            data.notifyDataChanged();
            notifyDataSetChanged();

            setVisibleXRange(DEFAULT_MAX_VISIBLE_WINDOW_LENGTH_SECONDS, DEFAULT_MAX_VISIBLE_WINDOW_LENGTH_SECONDS);
            moveViewToX(mIndex);
        }
    }
}
