package com.stanford.dais; 

import java.util.Arrays;

import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import android.app.Activity;
import android.os.Bundle;

/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class VolumePlotActivity extends Activity
{
 
    private XYPlot plot;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Globals g = (Globals) getApplication(); 
        
        setContentView(R.layout.activity_volume_plot);
 
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        XYSeries series1 = new SimpleXYSeries(g.pres.decibels,          
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series
 
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_1);
 
        // add a new series to the xyplot:
        plot.addSeries(series1, series1Format);
 
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);
        
        plot.getLayoutManager()
        	.remove(plot.getLegendWidget());
 
        plot.setTicksPerDomainLabel(Integer.MAX_VALUE);
        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());

        plot.getGraphWidget().setMarginTop(10);        

        plot.setBackgroundPaint(null);
        plot.getGraphWidget().setBackgroundPaint(null);
        plot.getGraphWidget().setGridBackgroundPaint(null);

        plot.getGraphWidget().setDomainLabelPaint(null);
        plot.getGraphWidget().setDomainOriginLabelPaint(null);

        plot.getGraphWidget().setDomainGridLinePaint(null);
        plot.getGraphWidget().setDomainOriginLinePaint(null);
        plot.getGraphWidget().setRangeOriginLinePaint(null);

        plot.setBorderPaint(null);
        
        Widget gw = plot.getGraphWidget();

        // FILL mode with values of 0 means fill 100% of container:
        SizeMetrics sm = new SizeMetrics(0,SizeLayoutType.FILL,
                                      0,SizeLayoutType.FILL);
        gw.setSize(sm);

        // position the upper left corner of gw in the upper left corner of the space 
        // controlled by lm.
        gw.position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0, YLayoutStyle.ABSOLUTE_FROM_TOP);
        
    }
}