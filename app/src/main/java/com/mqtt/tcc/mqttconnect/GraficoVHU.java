package com.mqtt.tcc.mqttconnect;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

/**
 * Created by gabri on 15/05/2018.
 */

public class GraficoVHU implements OnChartValueSelectedListener{



    private LineChart grafVHU;
    private MqttConnect mqttConnect;


    public GraficoVHU(LineChart grafico, MqttConnect mqttConnect){
       this.grafVHU= grafico;
       this.mqttConnect= mqttConnect;

    }
    public void iniciaGrafico(){

        grafVHU.setOnChartValueSelectedListener(this);
        grafVHU.setNoDataText("You need to provide data for the chart.");

        // enable touch gestures
        grafVHU.setTouchEnabled(true);

        // enable scaling and dragging
        grafVHU.setDragEnabled(true);
        grafVHU.setScaleEnabled(true);
        grafVHU.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        grafVHU.setPinchZoom(true);

        // set an alternative background color
        grafVHU.setBackgroundColor(Color.BLACK);
        grafVHU.setBorderColor(Color.BLACK);



        LineData data = new LineData();
        data.setValueTextColor(WHITE);


        // add empty data
        grafVHU.setData(data);

        // get the legend (only possible after setting data)
        Legend l = grafVHU.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.DEFAULT);

        l.setTypeface(Typeface.MONOSPACE);
        l.setTextColor(WHITE);




        XAxis xl = grafVHU.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        xl.setTextColor(WHITE);


        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date hora = Calendar.getInstance().getTime();

                return simpleDateFormat.format(hora);
            }
        });
        xl.setDrawGridLines(true);
        xl.setAxisLineColor(Color.WHITE);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);



        YAxis leftAxis = grafVHU.getAxisLeft();
        leftAxis.setTypeface(Typeface.MONOSPACE);
        leftAxis.setTextColor(WHITE);

        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = grafVHU.getAxisRight();
        rightAxis.setEnabled(true);

    }
    public void addEntry(float value) {
        LineData data = grafVHU.getData();
        if (data != null){

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }


            //set.getEntryCount()
            data.addEntry(new Entry(set.getEntryCount(), value),0);
            Log.w("chart", set.getEntryForIndex(set.getEntryCount()-1).toString());

            data.notifyDataChanged();

            // let the chart know it's data has changed
            grafVHU.notifyDataSetChanged();

            // limit the number of visible entries
            grafVHU.setVisibleXRangeMaximum(3);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            grafVHU.moveViewTo(set.getEntryCount()-1, data.getYMax(), YAxis.AxisDependency.LEFT);

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Umidade");
        set.setDrawFilled(true);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(147, 224, 191));
        set.setCircleColor(Color.rgb(147, 224, 191));
        set.setLineWidth(2f);
        // PREENCHENDO COM COR SOB A LINHA
        //set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.rgb(147, 224, 191));
        set.setHighLightColor(RED);
        set.setValueTextColor(RED);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
