package com.lern.schulphysikapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.RangeBarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.NumberFormat;
import java.util.Enumeration;

import android.view.View.OnTouchListener;

/* Carlos Trujillo   29/09/2015                                           */
/* This class is used to create the statistic shown at the end of the app */
public class Statistic extends ActionBarActivity implements OnTouchListener {
    public String nextAufgabe;
    int indx_graph = 0; // this variable is use as a pointer to read the notes of the user for each task
    int indx_graph_Avg = 0; // this variable is use as a pointer to read the average note for each task
    int sizeY;
    /* These variables stores the Qualification data to create the chart */
    //int[] x_Q;
    int[] y_Q_max;
    int[] y_Q_min;
    //int[] y_Q;
    //int[] x_Q;
    //double x_temp_Q;
    double y_temp_Q_max;
    double y_temp_Q_min;
    //double y_temp_Q;
    //double x_temp_Q;
    // These variables stores the Empfindung data to create the chart
    int[] x_E;
    int[] y_E;
    double x_temp_E;
    double y_temp_E;
    String[] time_temp = new String[10];
    String language;

    String[] sTests = new String[10];
    int indx_sTests = 0;

    LinearLayout layout;
    GraphicalView gView;
    String j;
    XYSeriesRenderer renderer_Q = new XYSeriesRenderer();
    XYSeriesRenderer renderer_E = new XYSeriesRenderer();
    //XYSeriesRenderer renderer3 = new XYSeriesRenderer();
    RangeCategorySeries series_Q = new RangeCategorySeries("Tests");
    CategorySeries series_E; // Trujillo 07_03_2016
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    Button btnRew;
    Button btnFwd;
    public final static String EXTRA_MESSAGE = "";

    Boolean timeGraph_boolean = false;
    Boolean ResultGraph_boolean = false;
    Boolean feelingGraph_boolean = false;
    Context var_Context;
    String[] types;
    String timeBlankSpace, timeBlankSpaceFront; // Trujillo 28.04.16

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        try{
            nextAufgabe = MainActivity.vecTest.get(indx_graph).toString();
            series_Q = new RangeCategorySeries(getString(R.string.chartBarLegend));
            series_E = new CategorySeries(getString(R.string.chartLineLegend)); // Trujillo 07_03_2016

            MainActivity.statisticData = true;

            /* displays the first values of the chart on the screen */
            layout = (LinearLayout) findViewById(R.id.BarGraph);
            gView = getViewFwd(this);
            layout.addView(gView);

            btnRew = (Button) findViewById(R.id.btnAktBack);
            btnFwd = (Button) findViewById(R.id.btnAktFwd);
            btnRew.setEnabled(false);
            var_Context = this;

            LinearLayout lnrLayout = (LinearLayout) findViewById(R.id.BarGraph);

            lnrLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //Toast.makeText(getApplicationContext(),"OnTouchListener", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            gView.setOnTouchListener(this);
        } catch (Exception e){
            System.out.println("Error: Statistic.onCreate " + e);
        }
    } //onCreate

    /* This function generates all the values and format for the chart */
    public GraphicalView getViewFwd(Context var_context) {
        /* evaluates how many task has the TEST*/
        sizeY = evalQtest(nextAufgabe);

        if (MainActivity.vecTest.size() == sizeY) {
            btnFwd = (Button) findViewById(R.id.btnAktFwd);
            btnFwd.setEnabled(false);
        }

        //sTests[indx_sTests] = nextAufgabe + "," + sizeY;
        sTests[indx_sTests] = nextAufgabe;

        y_Q_max = new int[sizeY];
        y_Q_min = new int[sizeY];
        //x_Q = new int[sizeY];
        y_E = new int[sizeY];
        x_E = new int[sizeY];
        //y3 = new int[sizeY];
        //x3 = new int[sizeY];
        int i2 = indx_graph;
        //int i3 = indx_graph;

        while (indx_graph < sizeY) {
            j = MainActivity.vecQualifikation.get(indx_graph).toString();
            //time_temp[indx_graph] = MainActivity.vecTime.get(indx_graph).toString();
            y_Q_max[indx_graph] = Integer.parseInt(j);
            //y_Q_min[indx_graph] = indx_graph +1;
            y_Q_min[indx_graph] = 0;

            indx_graph++;
        } // while

        while (i2 < sizeY) {
            //j = MainActivity.vecEmpfindung.get(i2).toString();
            j = MainActivity.vecAvgUc.get(i2).toString();
            y_E[i2] = Integer.parseInt(j);
            x_E[i2] = i2+1;

            i2++;
        }
        indx_graph_Avg = i2;

        /*while (i3 < sizeY) {
            j = vecFeeling2.get(i3).toString();
            y3[i3] = Integer.parseInt(j);
            x3[i3] = i3+1;

            i3++;
        }*/

        for (int w = 0; w < y_Q_max.length; w++) {
            y_temp_Q_max = (double) y_Q_max[w];
            y_temp_Q_min = (double) y_Q_min[w];
            series_Q.add(y_temp_Q_min, y_temp_Q_max);
        }

        for (int w = 0; w < x_E.length; w++) {
            x_temp_E = (double) x_E[w];
            y_temp_E = (double) y_E[w];
            series_E.add("line" + x_temp_E, y_temp_E);
        }

        /*for (int w = 0; w < x3.length; w++) {
            x3_temp = (double) x3[w];
            y3_temp = (double) y3[w];
            series3.add("bar" + x3_temp, y3_temp);
        }*/

        dataset.addSeries(series_Q.toXYSeries());
        dataset.addSeries(series_E.toXYSeries());
        //dataset.addSeries(series3.toXYSeries());

        renderer_Q.setColor(Color.GREEN);
        renderer_Q.setPointStyle(PointStyle.SQUARE);
        renderer_Q.setFillPoints(true);
        renderer_Q.setDisplayChartValues(true);

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(3);
        renderer_Q.setChartValuesFormat(format);

        renderer_E.setColor(Color.YELLOW);
        renderer_E.setPointStyle(PointStyle.SQUARE);
        renderer_E.setFillPoints(true);

        //renderer3.setColor(Color.RED);
        //renderer3.setPointStyle(PointStyle.SQUARE);
        //renderer3.setFillPoints(true);
        //renderer3.setLineWidth(7);

        mRenderer.addSeriesRenderer(renderer_Q);
        mRenderer.addSeriesRenderer(renderer_E);
        //mRenderer.addSeriesRenderer(renderer3);
        mRenderer.setXTitle("                                             " + getString(R.string.Aufgabe));

        // Sets the tittle fo the chart depending on the language
        /*language = Locale.getDefault().getLanguage().toString();
        switch (language) {
            case "en":
                mRenderer.setChartTitle("Results for " + nextAufgabe);
                mRenderer.setYTitle("Result");
                break;
            case "de":
                break;
        }*/
        mRenderer.setChartTitle(getString(R.string.chartResultTitle) + " " + nextAufgabe);
        mRenderer.setYTitle(getString(R.string.yResultTitle));

        mRenderer.setPanEnabled(false, false);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setBarSpacing(0.1);
        //mRenderer.setBarWidth(50);
        mRenderer.setYAxisMin(0);
        mRenderer.setXAxisMin(0.5);
        mRenderer.setXAxisMax(5.5);

        // Sets fonts size according to the definition of the device
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi)
        {
            case 640:
                mRenderer.setChartTitleTextSize(50);
                mRenderer.setAxisTitleTextSize(50);
                mRenderer.setLabelsTextSize(50);
                mRenderer.setMargins(new int[]{80, 80, 80, 80});
                mRenderer.setLegendTextSize(50); // Trujillo 07_03_2016
                renderer_E.setLineWidth(8);
                renderer_Q.setChartValuesTextSize(50);
                timeBlankSpace = "                                           ";
                timeBlankSpaceFront = "                      ";;
                break;
            case 480:  //  -->  Samsung Galaxy, S4
                mRenderer.setChartTitleTextSize(40);
                mRenderer.setAxisTitleTextSize(40);
                mRenderer.setLabelsTextSize(40);
                mRenderer.setMargins(new int[]{65, 60, 60, 60});
                mRenderer.setLegendTextSize(40); // Trujillo 07_03_2016
                renderer_E.setLineWidth(7);
                renderer_Q.setChartValuesTextSize(40);
                timeBlankSpace = "                                        ";
                timeBlankSpaceFront = "                    ";
                break;
            case 320:
                mRenderer.setChartTitleTextSize(30);
                mRenderer.setAxisTitleTextSize(30);
                mRenderer.setLabelsTextSize(30);
                mRenderer.setMargins(new int[]{50, 50, 50, 50});
                mRenderer.setLegendTextSize(30); // Trujillo 07_03_2016
                renderer_E.setLineWidth(6);
                renderer_Q.setChartValuesTextSize(30);
                timeBlankSpace = "                                      ";
                timeBlankSpaceFront = "                  ";
                break;
            case 240:
                mRenderer.setChartTitleTextSize(20);
                mRenderer.setAxisTitleTextSize(20);
                mRenderer.setLabelsTextSize(20);
                mRenderer.setMargins(new int[]{40, 40, 40, 40});
                mRenderer.setLegendTextSize(20); // Trujillo 07_03_2016
                renderer_E.setLineWidth(5);
                renderer_Q.setChartValuesTextSize(20);
                timeBlankSpace = "                                   ";
                timeBlankSpaceFront = "               ";
                break;
            case 213:
                mRenderer.setChartTitleTextSize(10);
                mRenderer.setAxisTitleTextSize(10);
                mRenderer.setLabelsTextSize(10);
                mRenderer.setMargins(new int[]{30, 30, 30, 30});
                mRenderer.setLegendTextSize(10); // Trujillo 07_03_2016
                renderer_E.setLineWidth(4);
                renderer_Q.setChartValuesTextSize(10);
                timeBlankSpace = "                                ";
                timeBlankSpaceFront = "             ";
                break;
            case 160: //MDPI --> Samsung GT-p5210
                mRenderer.setChartTitleTextSize(10);
                mRenderer.setAxisTitleTextSize(10);
                mRenderer.setLabelsTextSize(10);
                mRenderer.setMargins(new int[]{20, 20, 20, 20});
                mRenderer.setLegendTextSize(10); // Trujillo 07_03_2016
                renderer_E.setLineWidth(3);
                renderer_Q.setChartValuesTextSize(10);
                timeBlankSpace = "                             ";
                timeBlankSpaceFront = "           ";
                break;
            case 120:  //LDPI  --> Samsung galaxz pocket s5310
                mRenderer.setMargins(new int[]{ 20, 10, 20, 10 });
                mRenderer.setLegendTextSize(10); // Trujillo 07_03_2016
                renderer_E.setLineWidth(2);
                renderer_Q.setChartValuesTextSize(10);
                timeBlankSpace = "     ";
                timeBlankSpaceFront = "          ";
                break;
        }

        /* For single series */
        //return ChartFactory.getBarChartView(context, dataset, mRenderer, BarChart.Type.DEFAULT);

        timeGraph_boolean = true;
        ResultGraph_boolean = false;
        feelingGraph_boolean = false;

        /* For multiple series */
        types = new String[]{RangeBarChart.TYPE, LineChart.TYPE};
        return ChartFactory.getCombinedXYChartView(var_context, dataset, mRenderer, types);

    } //getViewFwd

    /* Returns the quantity of task that a Test has */
    public static int evalQtest(String h) {
        Enumeration vEnumTest = MainActivity.vecTest.elements();
        String j = "";
        int count = 0;

        while(vEnumTest.hasMoreElements()) {
            j = vEnumTest.nextElement().toString();

            if (j.equals(h)){
                count++;
            }
        } // while

        return count;
    }

    /* This function generates the graph of the next Test */
    public void nextGraph(View view){
        System.out.println("--> nextGraph");
        /* Measures the quantitiy of tasks that the next Test has */
        nextAufgabe = MainActivity.vecTest.get(indx_graph).toString();
        sizeY = evalQtest(nextAufgabe);
        /* array to control which test is beeing displayed */
        indx_sTests++;
        sTests[indx_sTests] = nextAufgabe;

        int[] y_Qfwd_max = new int[sizeY];
        int[] y_Qfwd_min = new int[sizeY];
        int h = 0;
        int[] x_Efwd = new int[sizeY];
        int[] y_Efwd = new int[sizeY];
        int h2 = 0;

        int i2 = indx_graph;
        time_temp = new String[10];

        while (h < sizeY) {
            j = MainActivity.vecQualifikation.get(indx_graph).toString();
            //time_temp[h] = MainActivity.vecTime.get(indx_graph).toString();
            y_Qfwd_max[h] = Integer.parseInt(j);
            //y_Qfwd_min[h] = h+1;
            y_Qfwd_min[h] = 0;

            indx_graph++;
            h++;
        } // while

        while (h2 < sizeY) {
            //j = MainActivity.vecEmpfindung.get(i2).toString();
            //_/_/_/j = MainActivity.vecAvgUc.get(i2).toString();
            j = MainActivity.vecAvgUc.get(indx_graph_Avg).toString();
            y_Efwd[h2] = Integer.parseInt(j);
            x_Efwd[h2] = i2+1;

            i2++;
            h2++;
            indx_graph_Avg++;
        } // while

        if (MainActivity.vecQualifikation.size() == indx_graph) {
            btnFwd.setEnabled(false);
        }
        if (indx_graph > 0) btnRew.setEnabled(true);

        series_Q.clear();
        series_E.clear();

        //_/_/TimeSeries series = new TimeSeries("Line1");
        //series = new CategorySeries("Tests");
        for (int w = 0; w < y_Qfwd_max.length; w++) {
            y_temp_Q_max = (double) y_Qfwd_max[w];
            y_temp_Q_min = (double) y_Qfwd_min[w];
            series_Q.add(y_temp_Q_min, y_temp_Q_max);
        }

        for (int w = 0; w < x_Efwd.length; w++) {
            x_temp_E = (double) x_Efwd[w];
            y_temp_E = (double) y_Efwd[w];
            series_E.add("line" + x_temp_E, y_temp_E);
        }

        dataset.clear();
        dataset.addSeries(series_Q.toXYSeries());
        dataset.addSeries(series_E.toXYSeries());
        mRenderer.setXTitle("                                        " + getString(R.string.Aufgabe));

        // Set the name or the graph at the top of it
       /* switch (language) {
            case "en":
                mRenderer.setChartTitle("Results for " + nextAufgabe);
                break;
            case "de":
                mRenderer.setChartTitle("Ergebniss fÃ¼r " + nextAufgabe);
                break;
        }*/
        mRenderer.setChartTitle(getString(R.string.chartTitle) + " " + nextAufgabe);

        if (nextAufgabe.toLowerCase().contains("pool")) {
            mRenderer.setXAxisMax(3.5);
        } else {
            mRenderer.setXAxisMax(5.5);
        }

        timeGraph_boolean = true;
        ResultGraph_boolean = false;
        feelingGraph_boolean = false;

        gView.repaint();

        if (sizeY < 5) { //When it enters the pool it just has 2 or 3 exercises, that is why the indx_graph_Avg should be added as it were 5 exercises performed.
            if (sizeY == 4) {
                indx_graph_Avg = indx_graph_Avg + 1;
            } else if (sizeY == 3) {
                indx_graph_Avg = indx_graph_Avg + 2;
            } else if (sizeY == 2) {
                indx_graph_Avg = indx_graph_Avg + 3;
            }
        }
    } //nextGraph

    /* This function generates the graph of the previous Test */
    public void lastGraph(View view){
        System.out.println("-->lastGraph");

        try {
            int sizeY_prev = evalQtest(nextAufgabe);
            indx_sTests--;
            nextAufgabe = sTests[indx_sTests];
            sizeY = evalQtest(nextAufgabe);

            int[] y_Qrew_max = new int[sizeY];
            int[] y_Qrew_min = new int[sizeY];
            int h = 0;
            int[] x_Erew = new int[sizeY];
            int[] y_Erew = new int[sizeY];
            int h2 = 0;

            indx_graph = indx_graph - sizeY - sizeY_prev;
            indx_graph_Avg = indx_graph_Avg - 10;
            int i2 = indx_graph;
            //time_temp = new String[10];

            if (indx_graph == 0) btnRew.setEnabled(false);

            while (h < sizeY) {
                j = MainActivity.vecQualifikation.get(indx_graph).toString();
                //time_temp[h] = MainActivity.vecTime.get(indx_graph).toString();
                y_Qrew_max[h] = Integer.parseInt(j);
                //y_Qrew[h] = h + 1;
                y_Qrew_min[h] = 0;

                indx_graph++;
                h++;
            } // while

            if (MainActivity.vecQualifikation.size() > indx_graph) {
                btnFwd.setEnabled(true);
            }

            while (h2 < sizeY) {
                //j = MainActivity.vecEmpfindung.get(i2).toString();
                //_/_/_/j = MainActivity.vecAvgUc.get(i2).toString();
                j = MainActivity.vecAvgUc.get(indx_graph_Avg).toString();
                y_Erew[h2] = Integer.parseInt(j);
                x_Erew[h2] = i2 + 1;

                i2++;
                h2++;
                indx_graph_Avg++;
            } // while

            series_Q.clear();
            series_E.clear();

            //_/_/TimeSeries series = new TimeSeries("Line1");
            //series = new CategorySeries("Tests");
            for (int w = 0; w < y_Qrew_max.length; w++) {
                y_temp_Q_max = (double) y_Qrew_max[w];
                y_temp_Q_min = (double) y_Qrew_min[w];
                series_Q.add(y_temp_Q_min, y_temp_Q_max);
            }

            for (int w = 0; w < x_Erew.length; w++) {
                x_temp_E = (double) x_Erew[w];
                y_temp_E = (double) y_Erew[w];
                series_E.add("line" + x_temp_E, y_temp_E);
            }

            dataset.clear();
            dataset.addSeries(series_Q.toXYSeries());
            dataset.addSeries(series_E.toXYSeries());
            mRenderer.setXTitle("                                        " + getString(R.string.Aufgabe));

            // Set the name or the graph at the top of it
            mRenderer.setChartTitle(getString(R.string.chartTitle) + " " + nextAufgabe);

            if (nextAufgabe.toLowerCase().contains("pool")) {
                mRenderer.setXAxisMax(3.5);
            } else {
                mRenderer.setXAxisMax(5.5);
            }

            gView.repaint();
        } catch (Exception e) {
            System.out.println("Error: TestAufgabe.lastgraph " + e);
        }

        timeGraph_boolean = true;
        ResultGraph_boolean = false;
        feelingGraph_boolean = false;

        if (sizeY < 5) { //When it enters the pool it just has 2 or 3 exercises, that is why the indx_graph_Avg should be added as it were 5 exercises performed.
            if (sizeY == 4) {
                indx_graph_Avg = indx_graph_Avg + 1;
            } else if (sizeY == 3) {
                indx_graph_Avg = indx_graph_Avg + 2;
            } else if (sizeY == 2) {
                indx_graph_Avg = indx_graph_Avg + 3;
            }
        }
    } //lastGraph

    public void correctnessGraph() {
        System.out.println("--> correctnessGraph");
        try {
            int sizeY_temp = evalQtest(nextAufgabe);

            float[] y_Corr_max = new float[sizeY_temp];
            float[] y_Corr_min = new float[sizeY_temp];
            int h = 0;
            float[] x_Lim  = new float[sizeY_temp];
            float[] y_Lim = new float[sizeY_temp];
            int h2 = 0;
            int minutes;
            int seconds;
            String sSeconds;
            String sMinutes;
            int posMin;

            int indx_temp = indx_graph - sizeY_temp;
            int indx_temp_Avg = indx_graph_Avg - 5;
            int i2 = indx_temp;

            while (h < sizeY_temp) {
                j = MainActivity.vecQualifikation.get(indx_temp).toString();
                y_Corr_max[h] = Integer.parseInt(j);
                //y_Corr_min[h] = h + 1;
                y_Corr_min[h] = 0;

                indx_temp++;
                h++;
            } // while

            while (h2 < sizeY) {
                //_/_/_/j = MainActivity.vecAvgUc.get(i2).toString();
                j = MainActivity.vecAvgUc.get(indx_temp_Avg).toString();
                y_Lim[h2] = Integer.parseInt(j);
                x_Lim[h2] = h2 + 1;

                i2++;
                h2++;
                indx_temp_Avg++;
            } // while

            series_Q.clear();
            series_E.clear();

            for (int w = 0; w < y_Corr_max.length; w++) {
                y_temp_Q_max = (double) y_Corr_max[w];
                y_temp_Q_min = (double) y_Corr_min[w];
                series_Q.add(y_temp_Q_min, y_temp_Q_max);
            }

            for (int w = 0; w < x_Lim.length; w++) {
                x_temp_E = (double) x_Lim[w];
                y_temp_E = (double) y_Lim[w];
                series_E.add("line" + x_temp_E, y_temp_E);
            }

            dataset.clear();
            dataset.addSeries(series_Q.toXYSeries());
            dataset.addSeries(series_E.toXYSeries());

            mRenderer.setXTitle("                                        " + getString(R.string.Aufgabe));
            mRenderer.setChartTitle(getString(R.string.chartTitle) + " " + nextAufgabe);
            mRenderer.setYTitle(getString(R.string.yResultTitle));

            gView.repaint();
        } catch (Exception e) {
            System.out.println("Error: Statistic.timeGraph " + e);
        }
        timeGraph_boolean = true;
        ResultGraph_boolean = false;
        feelingGraph_boolean = false;
    } // correctnessGraph

    public void timeGraph() {
        System.out.println("--> timeGraph");

        try {
            int sizeY_temp = evalQtest(nextAufgabe);
            //Next Aufgabe has the name of the next task, and the method evalQtest returns
            // the number of tasks that are recorded in the vector MainActivity.vecTest

            //int indx_sTest_temp = indx_sTests -1;
            //nextAufgabe = sTests[indx_sTest_temp];
            //int sizeY_temp = evalQtest(nextAufgabe);

            double[] y_Time_max = new double[sizeY_temp];
            double[] y_Time_min = new double[sizeY_temp];
            int h = 0;
            double[] x_Lim = new double[sizeY_temp];
            double[] y_Lim = new double[sizeY_temp];
            int h2 = 0;
            int minutes;
            int seconds;
            String sSeconds;
            String sMinutes;
            int posMin;
            double y_Time_Temp;
            double j_temp;

            int indx_temp = indx_graph - sizeY_temp;
            int indx_temp_Avg = indx_graph_Avg - 5;
            int i2 = indx_temp;

            while (h < sizeY_temp) {
                j = MainActivity.vecTime.get(indx_temp).toString();
                j_temp = Double.parseDouble(MainActivity.vecTimeTask.get(indx_temp).toString()); // Trujillo 06_03_2016
                posMin = j.lastIndexOf(":");
                sMinutes = j.substring(0,posMin);
                //minutes = Integer.parseInt(j.substring(0,posMin));
                sSeconds = j.substring(j.lastIndexOf(":") + 1);
                seconds = ((Integer.parseInt(sSeconds))*100)/60;
                y_Time_Temp = Double.parseDouble(sMinutes + "." + seconds); // Trujillo 06_03_2016
                if (y_Time_Temp > j_temp){
                    y_Time_Temp = j_temp - 0.01;
                }
                y_Time_max[h] = j_temp; // Trujillo 06_03_2016
                //x_Time[h] = h + 1;
                y_Time_min[h] = y_Time_Temp;

                indx_temp++;
                h++;
            } // while

            while (h2 < sizeY) {
                indx_temp = indx_graph - sizeY_temp;
                //_/_/_/j = MainActivity.vecTime4taskUt.get(i2).toString();
                j = MainActivity.vecTime4taskUt.get(indx_temp_Avg).toString();
                j_temp = Float.parseFloat(MainActivity.vecTimeTask.get(indx_temp).toString()); // Trujillo 06_03_2016


                //Aufgabe j_Auf = MainActivity.aufgb2Eval.get(0);

                //j = j_Auf.getTime();
                //posMin = j.lastIndexOf(":");
                //minutes = Integer.parseInt(j.substring(0,posMin));
                //sSeconds = j.substring(j.lastIndexOf(":") + 1);
                //seconds = ((Integer.parseInt(sSeconds))*100)/60;

                if (j.equals("")){
                    j = "0.0";
                }
                // It doesn't need to be substracted y_Lim[h2] = j_temp - Double.parseDouble(j);
                y_Lim[h2] = Double.parseDouble(j);
                x_Lim[h2] = h2 + 1;

                i2++;
                h2++;
                indx_temp_Avg++;
            } // while

            series_Q.clear();
            series_E.clear();

            for (int w = 0; w < y_Time_max.length; w++) {
                y_temp_Q_max = (double) y_Time_max[w];
                y_temp_Q_min = (double) y_Time_min[w];
                series_Q.add(y_temp_Q_min, y_temp_Q_max);
            }

            for (int w = 0; w < x_Lim.length; w++) {
                x_temp_E = (double) x_Lim[w];
                y_temp_E = (double) y_Lim[w];
                series_E.add("line" + x_temp_E, y_temp_E);
            }

            dataset.clear();
            dataset.addSeries(series_Q.toXYSeries());
            dataset.addSeries(series_E.toXYSeries());

            mRenderer.setXTitle("                                        " + getString(R.string.Aufgabe));
            mRenderer.setChartTitle(getString(R.string.chartTimeTitle) + " " + nextAufgabe);
            mRenderer.setYTitle(timeBlankSpaceFront + getString(R.string.yTimeTitle_1) + timeBlankSpace + getString(R.string.yTimeTitle_2));

            gView.repaint();
        } catch (Exception e) {
            System.out.println("Error: Statistic.timeGraph " + e);
        }
        timeGraph_boolean = false;
        ResultGraph_boolean = false;
        feelingGraph_boolean = true;
    } // timeGraph

    public void fellingGraph() {
        System.out.println("--> feelingGraph");
        try {
            int sizeY_temp = evalQtest(nextAufgabe);
            //int indx_sTest_temp = indx_sTests -1;
            //nextAufgabe = sTests[indx_sTest_temp];
            //int sizeY_temp = evalQtest(nextAufgabe);

            float[] y_Feel_max = new float[sizeY_temp];
            float[] y_Feel_min = new float[sizeY_temp];
            int h = 0;
            float[] x_Lim = new float[sizeY_temp];
            float[] y_Lim = new float[sizeY_temp];
            int h2 = 0;
            //int minutes;
            //int seconds;
            //String sSeconds;
            //String sMinutes;
            //int posMin;

            int indx_temp = indx_graph - sizeY_temp;
            int indx_temp_Avg = indx_graph_Avg - 5;
            int i2 = indx_temp;
            int y_Feel_temp;

            while (h < sizeY_temp) {
                j = MainActivity.vecEmpfindung.get(indx_temp).toString();
                //posMin = j.lastIndexOf(":");
                //sMinutes = j.substring(0,posMin);
                //minutes = Integer.parseInt(j.substring(0,posMin));
                //sSeconds = j.substring(j.lastIndexOf(":") + 1);
                //seconds = ((Integer.parseInt(sSeconds))*100)/60;
                y_Feel_temp = Integer.parseInt(j);

                if (y_Feel_temp > 0) {
                    y_Feel_max[h] = y_Feel_temp;
                    //x_Feel[h] = h + 1;
                    y_Feel_min[h] = 0;
                } else {
                    y_Feel_max[h] = 0;
                    y_Feel_min[h] = y_Feel_temp;
                }

                if (y_Feel_temp < 0) {
                    if ((y_Feel_temp == -1) && (mRenderer.getYAxisMin() == 0)) {
                        mRenderer.setYAxisMin(-1);
                    } else if (y_Feel_temp == -2) {
                        mRenderer.setYAxisMin(-2);
                    }
                }

                indx_temp++;
                h++;
            } // while


            //indx_temp = indx_graph - sizeY_temp;
            while (h2 < sizeY) {
                //_/_/_/j = MainActivity.vecEmpUf.get(indx_temp).toString();
                j = MainActivity.vecEmpUf.get(indx_temp_Avg).toString();
                y_Lim[h2] = Float.parseFloat(j);
                x_Lim[h2] = h2 + 1;

                i2++;
                h2++;
                indx_temp_Avg++;
            } // while

            series_Q.clear();
            series_E.clear();

            for (int w = 0; w < y_Feel_max.length; w++) {
                y_temp_Q_max = (double) y_Feel_max[w];
                y_temp_Q_min = (double) y_Feel_min[w];
                series_Q.add(y_temp_Q_min, y_temp_Q_max);
            }

            for (int w = 0; w < x_Lim.length; w++) {
                x_temp_E = (double) x_Lim[w];
                y_temp_E = (double) y_Lim[w];
                series_E.add("line" + x_temp_E, y_temp_E);
            }

            dataset.clear();
            dataset.addSeries(series_Q.toXYSeries());
            dataset.addSeries(series_E.toXYSeries());

            mRenderer.setXTitle("                                        " + getString(R.string.Aufgabe));
            mRenderer.setChartTitle(getString(R.string.chartFeelTitle) + " " + nextAufgabe);
            mRenderer.setYTitle(getString(R.string.yFeelTitle));


            String[] types = new String[]{LineChart.TYPE, LineChart.TYPE};
            ChartFactory.getCombinedXYChartView(var_Context, dataset, mRenderer, types);
            gView.repaint();
        } catch (Exception e) {
            System.out.println("Error: Statistic.feelingGraph " + e);
        }

        timeGraph_boolean = false;
        ResultGraph_boolean = true;
        feelingGraph_boolean = false;
    } // feelingGraph

    /* Returns to the first screen */
    public void back2Start(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        //sends the name of which layout to use to the new intent
        //int xmlScreen = R.layout.activity_main;
        //intent.putExtra(EXTRA_MESSAGE, xmlScreen);
        //startActivity(intent);
        //finish();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            //Goes back to the first screen when the android back button is pressed
            Intent intent = new Intent(this, MainActivity.class);
            int xmlScreen = R.layout.activity_main;
            intent.putExtra(EXTRA_MESSAGE, xmlScreen);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onTouch(View v, MotionEvent event) {
        // Shows the next graph (Time, feeling or back to ergebniss) when the graph is touched
        // Carlos Trujillo 08.11.2015
        System.out.println("--> OnTouch");

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (timeGraph_boolean) {
                    mRenderer.setYAxisMin(0);
                    //String[] types = new String[]{BarChart.TYPE, LineChart.TYPE};
                    //ChartFactory.getCombinedXYChartView(var_Context, dataset, mRenderer, types);
                    timeGraph();
                } else if(ResultGraph_boolean) {
                    mRenderer.setYAxisMin(0);
                    //String[] types = new String[]{BarChart.TYPE, LineChart.TYPE};
                    //ChartFactory.getCombinedXYChartView(var_Context, dataset, mRenderer, types);
                    correctnessGraph();
                } else if (feelingGraph_boolean) {
                    mRenderer.setYAxisMin(0);
                    //String[] types = new String[]{LineChart.TYPE, LineChart.TYPE};
                    //ChartFactory.getCombinedXYChartView(var_Context, dataset, mRenderer, types);
                    fellingGraph();
                }
                //Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT) .show();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //Toast.makeText(this, "ACTION_POINTER_DOWN", Toast.LENGTH_SHORT) .show();
                break;
            case MotionEvent.ACTION_UP:
                //Toast.makeText(this, "ACTION_UP", Toast.LENGTH_SHORT) .show();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //Toast.makeText(this, "ACTION_POINTER_UP", Toast.LENGTH_SHORT) .show();
                break;
            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this, "ACTION_MOVE", Toast.LENGTH_SHORT) .show();
                break;
        }

        return true; // indicate event was handled
    }
}
