package com.lern.schulphysikapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import android.view.View;
import android.view.View.OnTouchListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/* Carlos Trujillo   29/09/2015                                           */
/* This class is used to create the statistic shown at the end of the app */
public class TestAufgabe extends ActionBarActivity implements OnTouchListener {
    public String message;
    public int cntntVw; // content View
    private ImageView image, mloesung;
    private TextView txtAufNum, txtAufComment, txtVw;
    private Button btnAkt1, btnAkt2, btnHilfe1, btnHilfe2, btnHilfe3, btnHilfe4, btnHilfe5;
    private String hilfe1, hilfe2, hilfe3, hilfe4, hilfe5;
    private LinearLayout layout;
    private LayoutInflater inflater;
    private TestAufgabe timer;
    private ProgressBar progssBar;
    int prg;
    int count;
    private RadioGroup rGroup;
    public static int intZahl1;
    public static int intEmpf;
    private String str1 = "20";
    private String str2 = "40";
    private String str3 = "60";
    private String str4 = "80";
    private String str5 = "100";

    //for the graph, so that the scale (from -2 to 2) is known    L.E. 08_03_2016
    private String feeling1 = "-2";
    private String feeling2 = "-1";
    private String feeling3 = "0";
    private String feeling4 = "1";
    private String feeling5 = "2";

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    boolean sumActive;
    String language, AufLanguage;
    private String timeTemp; // Trujillo 06_03_2016

    // Variables for Zoom
    private static final String TAG = "Touch";
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    boolean valMatrix = true;
    int screenWidth, screenHeight;

    // parseNext variables
    private static String currentTag = null;
    private static Aufgabe currentAufgabe = null;
    private static Pool currentPoolAfgb = null;

    // Variable helpOn is needed to know when reading a help tag
    static boolean helpOn = false;
    static String helpValue = null;

    //Variables for Chronometer
    Chronometer chrono;
    TextView textViewTime;
    CounterClass timerCntDwn = null;
    float time4task;
    long time4taskMillis;
    String timeString;
    float pgsBarInc;
    float pgsBarSum;

    //Variables for Statistic
    boolean bool_showsGraph = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("-> TestAufgabe --> OnCreate");

        //Reads the EXTRA_MESSAGE from the intent to know which XML to use (Content View)
        Intent intent = getIntent();
        cntntVw = intent.getExtras().getInt(MainActivity.EXTRA_MESSAGE);
        setContentView(cntntVw);

        // find view elements
        txtAufComment = (TextView) findViewById(R.id.textAufComment);
        txtAufNum = (TextView) findViewById(R.id.textAufNum);
        image = (ImageView) findViewById(R.id.image);
        btnAkt2 = (Button) findViewById(R.id.btnAkt2);
        btnAkt1 = (Button) findViewById(R.id.btnAkt1);
        btnHilfe1 = (Button) findViewById(R.id.hilfeButton1);
        btnHilfe2 = (Button) findViewById(R.id.hilfeButton2);
        btnHilfe3 = (Button) findViewById(R.id.hilfeButton3);
        btnHilfe4 = (Button) findViewById(R.id.hilfeButton4);
        btnHilfe5 = (Button) findViewById(R.id.hilfeButton5);

        // zoom elements
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setMaxWidth(screenWidth);
        btnAkt1.setText(R.string.Zurück_zum_Start);

        // Creates the click listener
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnAkt2 || v == btnAkt1) {
                    onClickBtnAkt(v);
                }
            }
        };

        // assign click listener to the button
        btnAkt2.setOnClickListener(oclBtnOk);
        btnAkt1.setOnClickListener(oclBtnOk);

        evaluateAufgabe();
        //readXML(); // Trujillo08/05/2016
        //poolVerification(); //Checks if the pool needs to be initialized

        //Call to methods
        if (MainActivity.poolActivated) {
            loadPoolScreen(MainActivity.n_poolQntAufEvl);
        } else {
            loadScreen(MainActivity.n_QntityAufEval);
        }
        createHelpButtons();

        // For zooming the images
        //image.setOnTouchListener(this);
        PhotoViewAttacher photoView = new PhotoViewAttacher(image);
        photoView.update();
    } // onCreate

    public int poolAnalyzer(int test2eval) {
        // Analyze the score of the last test to check if it should go to the pool or not
        int x = 0;
        int sum_temp = 0;
        int sum = 0;
        int qntAufgabe = 0;
        int avrgQ = 0;

        for (x = 0; x < MainActivity.aufgb2Eval.size(); x++) {
            Aufgabe aufTemp = MainActivity.aufgb2Eval.get(x);

            if (aufTemp.getTest() == test2eval) {
                sum_temp = aufTemp.getQualifikation();

                if (sum_temp != 0) {
                    sum = aufTemp.getQualifikation() + sum;
                    qntAufgabe++;
                } // if
            } // if
        } // for

        if (qntAufgabe > 0) {
            avrgQ = sum / qntAufgabe;
        }

        if (qntAufgabe < 5) {
            avrgQ = 0;
        }

        return avrgQ;
    } // poolAnalyzer

    public void poolVerification_pre() {
        // Verifies if the pool should be activated before runing the function evaluateAufgabe()
        // Trujillo 07/05/2016
        System.out.println("--> poolVerification_pre");

        int x;
        int sum_temp = 0;
        int sum = 0;
        int qntAufgabe = 0;
        int qnt_poolQntAufEvl = 0;
        int testPoolEval = 0;
        boolean go2pool2 = false;
        boolean doneOnce = false;

        /*if ((MainActivity.n_Test == 2) && ((MainActivity.n_QntityAufEval == 10) || (MainActivity.n_QntityAufEval == 6)) &&
                (MainActivity.aufgb2Eval.size() == 5)) {
            parseNext(MainActivity.xml02, 2);
        }

        if ((MainActivity.n_Test == 3) && ((MainActivity.n_QntityAufEval == 15) || (MainActivity.n_QntityAufEval == 11)) &&
                (MainActivity.aufgb2Eval.size() == 5)) {
            parseNext(MainActivity.xml02, 2);
            parseNext(MainActivity.xml03, 3);
        }*/

        //readXML();  // Trujillo 80/05/2016

        FileInputStream fin = null;  // Trujillo 80/05/2016

        try {
            if (MainActivity.startsFromSavedInfo) {
                if ((MainActivity.n_Test == 1) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1)&& (!MainActivity.poolActivated) &&
                        (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1)) {
                    if (MainActivity.parseNextxml02 == false) {
                        parseNext(MainActivity.xml02, 2);
                        MainActivity.parseNextxml02 = true;
                    }
                } else if ((MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) &&
                        (!MainActivity.poolActivated) && (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1 + MainActivity.qntAufTest2)) {
                    if (MainActivity.parseNextxml03 == false) {
                        parseNext(MainActivity.xml03, 3);
                        MainActivity.parseNextxml03 = true;
                    }
                }

                if((MainActivity.n_Test == 1) && (MainActivity.n_QntityAufEval > MainActivity.qntAufTest1) && (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1)) {
                    if (MainActivity.parseNextxml02 == false) {
                        parseNext(MainActivity.xml02, 2);
                        MainActivity.parseNextxml02 = true;
                    }
                    MainActivity.n_Test = 2; // Si lo dejo funciona el pool 2, pero y no funciona el TEST 2
                    MainActivity.n_Aufgbe = MainActivity.n_QntityAufEval - MainActivity.qntAufTest1; // Trujillo 30/05/2016
                }

                if((MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval > MainActivity.qntAufTest1 + MainActivity.qntAufTest2) && (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1)) {
                    if (MainActivity.parseNextxml02 == false) {
                        parseNext(MainActivity.xml02, 2);
                        MainActivity.parseNextxml02 = true;
                    }
                    if (MainActivity.parseNextxml03 == false) {
                        parseNext(MainActivity.xml03, 3);
                        MainActivity.parseNextxml03 = true;
                    }
                    MainActivity.n_Test = 3;

                    MainActivity.n_Aufgbe = MainActivity.n_QntityAufEval - (MainActivity.qntAufTest1 + MainActivity.qntAufTest2); // Trujillo 31/05/2016
                }

                readXML(); // Trujillo 01/06/2016

                int poolAuf2Eval_size = 0;
                try {
                    poolAuf2Eval_size = MainActivity.poolAfgb2Eval.size();
                } catch (Exception e4) {
                    poolAuf2Eval_size = 0;
                }

                if (MainActivity.n_QntityAufEval >= MainActivity.qntAufTest1) {
                    testPoolEval = poolAnalyzer(1);
                    if ((testPoolEval <= MainActivity.score2pool) && (testPoolEval > 0)) {
                        if (poolAuf2Eval_size == 0) {
                            MainActivity.averageQualfktion = testPoolEval;
                            if (MainActivity.parsePool01 == false) {
                                poolParse(MainActivity.xmlPool01, 1);
                                MainActivity.parsePool01 = true;
                            }
                        }
                    }
                }

                if (MainActivity.n_QntityAufEval > MainActivity.qntAufTest1 + MainActivity.qntAufTest2) {
                    testPoolEval = poolAnalyzer(2);
                    if ((testPoolEval <= MainActivity.score2pool) && (testPoolEval > 0)) {
                        if ((poolAuf2Eval_size == 3) || (poolAuf2Eval_size == 2)) {
                            MainActivity.averageQualfktion = testPoolEval;
                            if (MainActivity.parsePool02 == false) {
                                poolParse(MainActivity.xmlPool02, 2);
                                MainActivity.parsePool02 = true;
                            }
                        }
                    }
                }

                readXML(); // Trujillo 23/05/2016
            } else {
                if ((MainActivity.n_Test == 1) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1) && (!MainActivity.poolActivated) &&
                        (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1)) {
                    if (MainActivity.parseNextxml02 == false) {
                        parseNext(MainActivity.xml02, 2);
                        MainActivity.parseNextxml02 = true;
                    }
                } /*else if ((MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) &&
                        (!MainActivity.poolActivated) && (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1 + MainActivity.qntAufTest2)) {
                    if (parseNextxml03 == false) {
                        parseNext(MainActivity.xml03, 3);
                        parseNextxml03 = true;
                    }
                }*/
            } // if
            //} catch (FileNotFoundException e) {
            //    System.out.println("poolVerification_pre " + e);
        } catch (Exception e) {
            System.out.println("poolVerification_pre " + e);
        }

        try {
            for (x = 0; x < MainActivity.aufgb2Eval.size(); x++) { // adds each note of the current Test to see if it goes to the pool or not
                Aufgabe aufTemp = MainActivity.aufgb2Eval.get(x);
                int getTestTemp = aufTemp.getTest();
                if (aufTemp.getTest() == MainActivity.n_Test) {
                    sum_temp = aufTemp.getQualifikation();

                    if (sum_temp != 0) {
                        sum = aufTemp.getQualifikation() + sum;
                        sumActive = true;
                        qntAufgabe++;
                        doneOnce = true;
                    } else {
                        if (!doneOnce) {
                            sumActive = false;
                        }
                    }
                }
            } // for

            if ((MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1)&& (qntAufgabe < MainActivity.qntAufTest1) && (MainActivity.n_Test == 1)) {
                sumActive = false;
            }

            // if ((MainActivity.aufgb2Eval.size() == (MainActivity.qntAufTest1 + MainActivity.qntAufTest2))&& (qntAufgabe < MainActivity.qntAufTest1) && (MainActivity.n_Test == 1)) {
            //     sumActive = false;
            // }

            if ((qntAufgabe < MainActivity.qntAufTest2) && (MainActivity.n_Test == 2)) {
                sumActive = false;
                MainActivity.poolActivated = false;
            }

            if ((qntAufgabe < MainActivity.qntAufTest3) && (MainActivity.n_Test == 3)) {
                sumActive = false;
                MainActivity.poolActivated = false;
            }

            // If the test needs to go to the pool
            if (sumActive) {
                int avrgQ = sum / qntAufgabe;
                MainActivity.averageQualfktion = avrgQ;

                if ((MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) &&
                        (!MainActivity.poolActivated) && (MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) &&
                        ((MainActivity.averageQualfktion > MainActivity.score2pool) || (MainActivity.parsePool02))) {
                    if (MainActivity.parseNextxml03 == false) {
                        parseNext(MainActivity.xml03, 3);
                        MainActivity.parseNextxml03 = true;
                    }
                }

                try {
                    if ((MainActivity.averageQualfktion < MainActivity.score2pool) && (MainActivity.n_Test < 3)) {
                        if ((MainActivity.n_Test == 1) && (MainActivity.n_QntityAufEval >= MainActivity.qntAufTest1) && (MainActivity.n_poolQntAufEvl == 0)) {
                            if (MainActivity.parsePool01 == false) {
                                poolParse(MainActivity.xmlPool01, 1);
                                MainActivity.parsePool01 = true;
                            }
                        } else if (((MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) && (MainActivity.n_poolAufgb <= 3)) ||
                                ((MainActivity.n_Test == 1) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) && (MainActivity.n_poolQntAufEvl <= 3) &&
                                        (MainActivity.poolActivated))) {
                            int poolAuf2Eval_size;
                            try {
                                poolAuf2Eval_size = MainActivity.poolAfgb2Eval.size();
                            } catch (Exception e) {
                                poolAuf2Eval_size = 0;
                            }

                            if ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 3) && (MainActivity.n_poolTest == 1) ||
                                    ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolAufgb <= 3) && (MainActivity.n_poolTest == 2))) {
                                qnt_poolQntAufEvl = 3;
                            } else if ((avrgQ > MainActivity.qntPoolAufgaben) && (MainActivity.averageQualfktion < MainActivity.score2pool)) {
                                qnt_poolQntAufEvl = 2;
                            }

                            if (((MainActivity.n_Test == 1) && (MainActivity.n_poolQntAufEvl == qnt_poolQntAufEvl) && (poolAuf2Eval_size == 0)) ||
                                    ((MainActivity.n_Test == 2) && (MainActivity.n_poolQntAufEvl == qnt_poolQntAufEvl) && (poolAuf2Eval_size == 0)) ||
                                    ((MainActivity.n_Test == 2) && (MainActivity.n_poolAufgb <= qnt_poolQntAufEvl) && (poolAuf2Eval_size == 0) && (MainActivity.n_poolTest == 2))) {
                                testPoolEval = poolAnalyzer(1);
                                if (testPoolEval <= MainActivity.score2pool ) {
                                    if (MainActivity.parsePool01 == false) {
                                        poolParse(MainActivity.xmlPool01, 1);
                                        MainActivity.parsePool01 = true;
                                    }
                                }
                            }

                            if (MainActivity.parsePool02 == false) {
                                poolParse(MainActivity.xmlPool02, 2);
                                MainActivity.parsePool02 = true;
                            }
                            //MainActivity.n_poolTest = 2;
                            //MainActivity.n_poolAufgb = 0;
                        }
                    }

                    //if (MainActivity.startsFromSavedPoolInfo) { // Trujillo 08/05/2016
                    //fin = openFileInput("poolValues.xml");
                    //XmlParser.parseXmlPool(fin);
                    //fin.close();
                    readXML();
                    //}
                    //} catch (FileNotFoundException e) {
                    //    System.out.println("poolVerification_pre " + e);
                } catch (Exception e) {
                    System.out.println("poolVerification_pre " + e);
                }

                // In case that Test 1 completed (the average qualification is bellow 80),
                // but the user returned to the main screen before doing any exercise from the Pool 1
                if ((MainActivity.n_QntityAufEval == MainActivity.qntAufTest1) && (avrgQ < MainActivity.score2pool) && (MainActivity.n_poolQntAufEvl == 0) && (MainActivity.n_poolTest == 0) && (MainActivity.n_Test == 1)) {
                    MainActivity.poolActivated = true;
                }

                if ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 3) && (MainActivity.n_poolTest == 1)) {
                    MainActivity.pool01Completed = true;
                    MainActivity.poolActivated = false;
                } else if ((avrgQ > MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 2) && (MainActivity.n_poolTest == 1) && (MainActivity.averageQualfktion < MainActivity.score2pool)) {
                    MainActivity.pool01Completed = true;
                    MainActivity.poolActivated = false;
                } else if ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolAufgb < 3) && (MainActivity.n_Test < 3)) {
                    MainActivity.poolActivated = true;
                } else if ((avrgQ > MainActivity.qntPoolAufgaben) && (MainActivity.n_poolAufgb < 2) && (MainActivity.averageQualfktion < MainActivity.score2pool) && (MainActivity.n_Test < 3)) {
                    MainActivity.poolActivated = true;
                }

                if ((avrgQ <= MainActivity.qntPoolAufgaben) && ((MainActivity.n_poolQntAufEvl == 5) || (MainActivity.n_poolQntAufEvl == 6)) && (MainActivity.n_poolTest == 2) && (MainActivity.n_poolAufgb == 3)) {
                    MainActivity.pool02Completed = true;
                    MainActivity.poolActivated = false;
                } else if ((avrgQ > MainActivity.qntPoolAufgaben) && ((MainActivity.n_poolQntAufEvl == 4) || (MainActivity.n_poolQntAufEvl == 5)) && (MainActivity.n_poolTest == 2) &&
                        (MainActivity.averageQualfktion < MainActivity.score2pool) && (MainActivity.n_poolAufgb == 2)) {
                    MainActivity.pool02Completed = true;
                    MainActivity.poolActivated = false;
                } else if ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 3) && (MainActivity.n_poolTest == 2) && (MainActivity.n_poolAufgb == 3)) {
                    MainActivity.pool02Completed = true;
                    MainActivity.poolActivated = false;
                } else if ((avrgQ > MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 2) && (MainActivity.n_poolTest == 2) &&
                        (MainActivity.averageQualfktion < MainActivity.score2pool) && (MainActivity.n_poolAufgb == 2)) {
                    MainActivity.pool02Completed = true;
                    MainActivity.poolActivated = false;
                }

                if ((avrgQ <= MainActivity.score2pool) && (MainActivity.n_Test == 2) && (MainActivity.pool01Completed) && (MainActivity.n_poolQntAufEvl == qnt_poolQntAufEvl)) {
                    go2pool2 = true;
                    MainActivity.poolActivated = true;
                }

                if ((avrgQ <= MainActivity.score2pool) && (MainActivity.n_Test == 2) && (MainActivity.pool01Completed) &&
                        (MainActivity.aufgb2Eval.size() == MainActivity.n_QntityAufEval)) {
                    if (!MainActivity.pool02Completed) {
                        MainActivity.poolActivated = true;
                    }

                }

                if ((avrgQ <= MainActivity.score2pool) && (MainActivity.n_poolQntAufEvl == 1) && (MainActivity.n_poolTest == 2)) {
                    MainActivity.poolActivated = true;
                }

                try {
                    if ((avrgQ <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == MainActivity.poolAfgb2Eval.size()) && (MainActivity.n_poolTest == 2)) {
                        MainActivity.pool02Completed = true;
                        MainActivity.poolActivated = false;
                    }

                    if ((avrgQ <= MainActivity.score2pool) && (MainActivity.n_Test == 2) && (MainActivity.n_QntityAufEval == 10) &&
                            (MainActivity.n_poolQntAufEvl < MainActivity.poolAfgb2Eval.size()) && (!MainActivity.pool02Completed)) {
                        MainActivity.poolActivated = true;
                    }
                } catch (Exception e) {

                }

                if (((MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1 + MainActivity.qntAufTest2) && (MainActivity.pool01Completed) && (!go2pool2) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1)) ||
                        ((MainActivity.n_poolTest == 0) && (MainActivity.n_Test == 1) && (MainActivity.averageQualfktion >= MainActivity.score2pool) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1) && (!go2pool2))) {
                    MainActivity.n_Test = 2;
                    MainActivity.n_Aufgbe = 0;
                }

                if (((MainActivity.aufgb2Eval.size() == MainActivity.qntAufTest1 + MainActivity.qntAufTest2 + MainActivity.qntAufTest3) && (MainActivity.pool02Completed)) ||
                        (((MainActivity.n_poolTest == 0) || (MainActivity.n_poolTest == 2)) && (MainActivity.n_Test == 2) && (MainActivity.averageQualfktion >= MainActivity.score2pool) &&
                                (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1 + MainActivity.qntAufTest2))) {
                    MainActivity.n_Test = 3;
                    MainActivity.n_Aufgbe = 0;
                }

                if ((MainActivity.n_poolTest == 1) && (MainActivity.aufgb2Eval.size() == (MainActivity.qntAufTest1 + MainActivity.qntAufTest2)) && (MainActivity.averageQualfktion < MainActivity.score2pool) &&
                        (MainActivity.n_Test == 2)){
                    MainActivity.n_poolTest = 2;
                    MainActivity.n_poolAufgb = 0;
                }
            } // if
        } catch (Exception e) {
            System.out.println("Erro TestAufgabe.java.poolVerification_pre " + e);
        }
    } //poolVerification_pre()

    public void evaluateAufgabe() {
        // When the score is < than the value of MainActivity.score2pool, goes to the respective pool exercises
        // When is a new test to be evaluated, resets the variables and parse the respective xml.
        System.out.println("--> evaluateAufgabe");

        poolVerification_pre();

        try {
            if ((MainActivity.poolActivated) && (MainActivity.n_poolTest < 2)) {
                System.out.println("Pool activated " + MainActivity.poolActivated);

                // In case that the n_poolTest == 0 because the Pool01 was not executed
                if ((MainActivity.n_Test > 1) && (MainActivity.n_poolTest == 0)){
                    MainActivity.n_poolTest = 1;
                    MainActivity.starts2ndPool = true;
                }

                // The next if is just for the first test (n_poolTest = 0)
                if ((MainActivity.n_poolTest == 0) || (MainActivity.readNextPoolTest)) {
                    if (MainActivity.poolAfgb2Eval.size() == 0) {
                        if (MainActivity.parsePool01 == false) {
                            poolParse(MainActivity.xmlPool01, 1);
                            MainActivity.parsePool01 = true;
                        }
                    }

                    //writeSystemOutAufgabeXML(); //Trujillo 07_03_2016
                    if (!MainActivity.readNextPoolTest) {
                        // The next code is just executed when readNextPoolTest is false
                        MainActivity.n_poolTest++; // next Test
                        MainActivity.n_poolAufgb = 0; // Starts from the first Aufgabe
                    }
                }

                if ((MainActivity.starts2ndPool) || ((MainActivity.poolAfgb2Eval.size() == MainActivity.n_poolQntAufEvl) && (MainActivity.n_QntityAufEval == MainActivity.qntAufTest1))) {
                    Boolean continue2Pool2 = false;

                    if ((MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 3) && (MainActivity.n_poolTest == 1) &&
                            (MainActivity.n_QntityAufEval == (MainActivity.qntAufTest1 + MainActivity.qntAufTest2))) {
                        continue2Pool2 = true;
                    } else if ((MainActivity.averageQualfktion > MainActivity.qntPoolAufgaben) && (MainActivity.n_poolQntAufEvl == 2) && (MainActivity.n_poolTest == 1) &&
                            (MainActivity.n_QntityAufEval == (MainActivity.qntAufTest1 + MainActivity.qntAufTest2))) {
                        continue2Pool2 = true;
                    }

                    // Call to parser for the pool
                    switch (MainActivity.n_poolTest) {
                        case 1:
                            if (continue2Pool2) {
                                if (MainActivity.parsePool02 == false) {
                                    poolParse(MainActivity.xmlPool02, 2);
                                    MainActivity.parsePool02 = true;
                                }
                            } else {
                                // poolParse(MainActivity.xmlPool01, 1);  // Trujillo 11/05/2016
                            }

                            //writeSystemOutAufgabeXML(); Trujillo 07_03_2016
                            break;
                        case 2:

                            break;
                    } // switch

                    MainActivity.n_poolTest++; // next Test
                    if (MainActivity.readNextPoolTest) {
                        MainActivity.readNextPoolTest = false;
                    } else {
                        MainActivity.n_poolAufgb = 0; // Starts from the first Aufgabe
                    }

                    MainActivity.starts2ndPool = false;
                } // if

                // Trujillo 10/05/2016
                // In the case that only one task from the first pool has been evaluated and the next tasks should also be in the  Array
                if ((MainActivity.n_QntityAufEval == MainActivity.qntAufTest1) && (MainActivity.n_poolQntAufEvl == 1) && (MainActivity.n_poolTest == 1)) {

                } // if
            } else if ((MainActivity.aufgb2Eval.size() == MainActivity.n_QntityAufEval) || (MainActivity.readNextTest)) {
                // Call to parser for the next Test
                switch (MainActivity.n_Test) {
                    case 1:
                        if (MainActivity.parseNextxml02 == false) {
                            parseNext(MainActivity.xml02, 2);
                            MainActivity.parseNextxml02 = true;
                        }
                        MainActivity.sumQualfktion = 0;
                        MainActivity.n_Test = 2; // Trujillo 23/05/2016
                        break;
                    case 2:
                        if (MainActivity.n_QntityAufEval == (MainActivity.qntAufTest1 + MainActivity.qntAufTest2) && (!MainActivity.poolActivated)) {
                            if (MainActivity.parseNextxml03 == false) {
                                parseNext(MainActivity.xml03, 3);
                                MainActivity.parseNextxml03 = true;
                            }
                            MainActivity.sumQualfktion = 0;
                            MainActivity.n_Test = 3; // Trujillo 23/05/2016
                            MainActivity.n_Aufgbe = 0; // Trujillo 31/05/2016
                        }
                        break;
                    case 3:
                        /* Calls to the Statistic screen */
                        if (MainActivity.n_QntityAufEval >= (MainActivity.qntAufTest1 + MainActivity.qntAufTest2 + MainActivity.qntAufTest3)) {
                            Intent intent = new Intent(this, Statistic.class);
                            startActivity(intent);
                        }

                        break;
                } // switch

                //MainActivity.n_Test++; // next Test Trujillo 31/05/2016
                if (MainActivity.readNextTest) {
                    MainActivity.readNextTest = false;
                } else {
                    MainActivity.n_Aufgbe = 0; // Starts from the first Aufgabe
                }
            } // if
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.evaluateAufgabe -->" + e);
        }
    } // evaluateAufgabe

    public void loadScreen(int numAufgabe) {
        // This method loads all the information necessary on screen, to start a new Aufgabe
        System.out.println("--> loadScreen");

        int image2Disp, aufNumbr;
        String imageAufgabe, txtAufgabe;

        try {
            if (MainActivity.aufgb2Eval.size() > MainActivity.n_QntityAufEval) {
                MainActivity.aufLoad = MainActivity.aufgb2Eval.get(numAufgabe);

                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageAufgabe = MainActivity.aufLoad.getImageAufgabe();

                // Shows (or not) the text when all the exercise have already been done
                if (imageAufgabe.length()> 0){
                    layout = (LinearLayout) findViewById(R.id.EndText);
                    layout.removeAllViewsInLayout();
                    btnAkt2.setEnabled(true);
                }

                // Turns the String imageAufgabe into an int with the value R.drawable.image.
                image2Disp = getResources().getIdentifier(imageAufgabe, "drawable", getPackageName());
                Resources res = getResources();
                Drawable shape = res.getDrawable(image2Disp);

                // displays the image in the XML file
                image.setImageDrawable(shape);

                txtAufgabe = MainActivity.aufLoad.getText();
                txtAufComment.setText(txtAufgabe);
                aufNumbr = MainActivity.n_Aufgbe + 1;
                txtAufNum.setText(" TEST " + MainActivity.n_Test +  "   " + getString(R.string.Aufgabe) + " "  + aufNumbr);

                btnAkt2.setText(R.string.Lösung_beginnen);

            } // if
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.loadScreen --> " + e);
        } // try

        // This code is to show the Message Box between each Test of Pool
        if ((MainActivity.n_Test > 1) && (MainActivity.n_QntityAufEval < MainActivity.aufgb2Eval.size())){
            if (MainActivity.n_Aufgbe == 0) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setTitle(getString(R.string.StartingTest) + " " + MainActivity.n_Test);
                int lastTest = 0;
                int lastPool = 0;

                // The next block of code is to decide if the "Test was successfully completed" or "The pool was successfully completed"
                if ((MainActivity.n_Test == 2) && (MainActivity.n_poolTest == 2)) {
                    lastPool = MainActivity.n_poolTest - 1;
                    dlgAlert.setMessage("Pool " + lastPool + " " + getString(R.string.Message2Test) + ". " + getString(R.string.secondMessage2Test));
                } else if ((MainActivity.n_Test == 2) && (MainActivity.n_poolTest == 0)) {
                    lastTest = MainActivity.n_Test-1;
                    dlgAlert.setMessage(getString(R.string.test) + " " + lastTest + " " + getString(R.string.Message2Test) + ". " + getString(R.string.secondMessage2Test));
                } else if ((MainActivity.n_Test == 3) && (MainActivity.n_poolTest == 2)) {
                    if (MainActivity.pool02Completed) {
                        lastPool = MainActivity.n_poolTest;
                        dlgAlert.setMessage("Pool " + lastPool + " " + getString(R.string.Message2Test) + ". " + getString(R.string.secondMessage2Test));
                    } else {
                        lastTest = MainActivity.n_Test-1;
                        dlgAlert.setMessage(getString(R.string.test) + " " + lastTest + " " + getString(R.string.Message2Test) + ". " + getString(R.string.secondMessage2Test));
                    }
                } else if ((MainActivity.n_Test == 3) && (MainActivity.n_poolTest == 0)) {
                    lastTest = MainActivity.n_Test-1;
                    dlgAlert.setMessage(getString(R.string.test) + " " + lastTest + " " + getString(R.string.Message2Test) + ". " + getString(R.string.secondMessage2Test));
                }

                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    } // loadScreen

    public void loadPoolScreen(int numPoolAufgabe) {
        // This method loads all the information necessary on screen, to start a new Aufgabe
        System.out.println("--> loadPoolScreen");

        int image2Disp, aufPoolNumbr;
        String imageAufgabe, txtAufgabe;

        //evaluateAufgabe();
        readXML();

        try {
            if (MainActivity.poolAfgb2Eval.size() >= MainActivity.n_poolQntAufEvl) {
                MainActivity.aufPoolLoad = MainActivity.poolAfgb2Eval.get(numPoolAufgabe);
                imageAufgabe = MainActivity.aufPoolLoad.getImagePool();

                // Shows (or not) the text when all the exercise have already been done
                if (imageAufgabe.length()> 0){
                    layout = (LinearLayout) findViewById(R.id.EndText);
                    layout.removeAllViewsInLayout();
                    btnAkt2.setEnabled(true);
                }

                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                // Turns the String imageAufgabe into an int with the value R.drawable.image.
                image2Disp = getResources().getIdentifier(imageAufgabe, "drawable", getPackageName());
                Resources res = getResources();
                Drawable shape = res.getDrawable(image2Disp);

                // displays the image in the XML file
                image.setImageDrawable(shape);

                // zoom
                //image.setOnTouchListener(this);
                PhotoViewAttacher photoView = new PhotoViewAttacher(image);
                photoView.update();

                txtAufgabe = MainActivity.aufPoolLoad.getText();
                txtAufComment.setText(txtAufgabe);
                aufPoolNumbr = MainActivity.n_poolAufgb + 1;
                txtAufNum.setText(" POOL " + MainActivity.n_poolTest + " " + getString(R.string.Aufgabe) + " " + aufPoolNumbr);

                btnAkt2.setText(R.string.Lösung_beginnen);
            } // if
        } catch (Exception e) {
            FileInputStream fin = null;

            try {
                fin = openFileInput(MainActivity.poolValues);

                if (MainActivity.n_QntityAufEval < 10) {
                    if (MainActivity.parsePool01 == false) {
                        poolParse(MainActivity.xmlPool01, 1);
                        MainActivity.parsePool01 = true;
                    }
                } else {
                    if (MainActivity.n_Test < 3) {
                        if (MainActivity.parsePool02 == false) {
                            poolParse(MainActivity.xmlPool02, 2);
                            MainActivity.parsePool02 = true;
                        }
                        MainActivity.n_poolTest = 2;  //_/_/ Trujillo 28/03/2017
                        MainActivity.n_poolAufgb = 0;
                    }
                }

                fin.close();

                MainActivity.aufPoolLoad = MainActivity.poolAfgb2Eval.get(numPoolAufgabe);
                imageAufgabe = MainActivity.aufPoolLoad.getImagePool();

                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                // Turns the String imageAufgabe into an int with the value R.drawable.image.
                image2Disp = getResources().getIdentifier(imageAufgabe, "drawable", getPackageName());
                Resources res = getResources();
                Drawable shape = res.getDrawable(image2Disp);

                // displays the image in the XML file
                image.setImageDrawable(shape);

                // zoom
                //image.setOnTouchListener(this);
                PhotoViewAttacher photoView = new PhotoViewAttacher(image);
                photoView.update();

                txtAufgabe = MainActivity.aufPoolLoad.getText();
                txtAufComment.setText(txtAufgabe);
                aufPoolNumbr = MainActivity.n_poolAufgb + 1;

                txtAufNum.setText(" POOL " + MainActivity.n_poolTest + "  Aufgabe " + aufPoolNumbr);

                btnAkt2.setText(R.string.Lösung_beginnen);
            } catch (FileNotFoundException e1) {
                //e1.printStackTrace();
                System.out.println("FileNotFoundException: TestAufgabe.loadPoolScreen --> " + e1);
            } catch (Exception e2) {
                System.out.println("Error: TestAufgabe.loadPoolScreen --> " + e2);
            } // try 2
        } // try 1

        // Code to show the Message Box when before going to a Pool
        if (MainActivity.n_poolAufgb == 0) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle(getString(R.string.StartingPool) + " " + MainActivity.n_poolTest);
            dlgAlert.setMessage(getString(R.string.Message2Pool) + ". " + getString(R.string.secondMessage2Pool));
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    } // loadPoolScreen

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.test_aufgabe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createHelpButtons() {
        // Create the Help buttons
        System.out.println("--> createHelpButtons");

        String hilfe, strHilfe;
        String hilfeTemp[];
        int cont = 1;
        int nextStep = 0;
        TextView txtLink;

        try {
            List<String> list = new ArrayList<String>();

            if (MainActivity.poolActivated) {
                list = MainActivity.aufPoolLoad.getHilfe();
            } else {
                list = MainActivity.aufLoad.getHilfe();
            }

            Iterator<String> interatorList = list.iterator();

            while (interatorList.hasNext()) {
                String elemento = interatorList.next();
                strHilfe = "hilfeButton" + cont;

                nextStep = getResources().getIdentifier(strHilfe, "id", getPackageName());
                txtLink = (TextView) findViewById(nextStep);

                String url = elemento;

                txtLink.setText(url);
                txtLink.setVisibility(View.VISIBLE);

                android.view.ViewGroup.LayoutParams lp = txtLink.getLayoutParams();
                lp.height = ActionBar.LayoutParams.WRAP_CONTENT;

                switch (cont) {
                    case 1:
                        hilfe1 = url;
                        break;
                    case 2:
                        hilfe2 = url;
                        break;
                    case 3:
                        hilfe3 = url;
                        break;
                    case 4:
                        hilfe4 = url;
                        break;
                    case 5:
                        hilfe5 = url;
                        break;
                }

                cont++;
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.createHelpButtons --> " + e);
        } // try
    } // createHelpButtons

    public void openHilfe(View view) {
        // gives the adecuate url to the
        System.out.println("--> openHilfe");

        String urlStr = "";

        if (view == btnHilfe1) {
            urlStr = hilfe1;
        } else if (view == btnHilfe2) {
            urlStr = hilfe2;
        } else if (view == btnHilfe3) {
            urlStr = hilfe3;
        } else if (view == btnHilfe4) {
            urlStr = hilfe4;
        } else if (view == btnHilfe5) {
            urlStr = hilfe5;
        }
        goToUrl(urlStr);
    }

    private void goToUrl(String url) {
        // Opens the url indicated in the help button
        System.out.println("--> goToUrl");

        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void onClickBtnAkt(View v) {
        // Displays on the screen the necessary values according
        // to the state (variable Zustand) of the current Aufgabe
        System.out.println("--> onClickBtnAkt");

        try {
            int ActualZustand;

            if (MainActivity.poolActivated) {
                ActualZustand = MainActivity.aufPoolLoad.getZustand();
            } else {
                ActualZustand = MainActivity.aufLoad.getZustand();
            }

            int image2Disp;
            String imageAufgabe;

            switch (v.getId()) {
                case R.id.btnAkt1:
                    System.exit(0);
                    break;
                case R.id.btnAkt2:
                    switch (ActualZustand) {
                        case 0: //task started
                            System.out.println("0 task started");
                            MainActivity.startsFromSavedPoolInfo = false;

                            btnAkt2.setText(R.string.Lösung_ist_fertig);

                            // Add the timer in the Screen
                            layout = (LinearLayout) findViewById(R.id.layTimer);
                            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            ViewGroup timer = (ViewGroup) inflater.inflate(R.layout.timer, null);
                            layout.addView(timer);
                            layout.invalidate();

                            progssBar = (ProgressBar) findViewById(R.id.progressBar);
                            chrono = (Chronometer) findViewById(R.id.chronometer);
                            txtVw = (TextView) findViewById(R.id.testView01);
                            new Thread(myThread).start();

                            // Chronometer
                            textViewTime = (TextView) findViewById(R.id.textViewTime);
                            textViewTime.setText("00:00:00"); // Chrono Countdown

                            // Change the state of the current Aufgabe
                            if (MainActivity.poolActivated) {
                                MainActivity.aufPoolLoad.setZustand(1); // Started
                                // Get the required time for the task from the pool
                                timeString = MainActivity.aufPoolLoad.getTime().toString();
                            } else {
                                MainActivity.aufLoad.setZustand(1); // Started
                                // Get the required time for the task from the Ausgabe
                                timeString = MainActivity.aufLoad.getTime().toString();
                            }

                            // Loads the time required for the task in the Countdown Chrono
                            time4task = Float.parseFloat(timeString);
                            //++++++++MainActivity.vecTime4taskUt.add(timeString); // to generate the statistic time at the end. // Trujillo 06_03_2016
                            pgsBarSum = 100/(time4task*60); // How much the ProgressBar should decrement

                            if (time4task < 1.6) {
                                pgsBarSum = pgsBarSum / 2; // How much the ProgressBar should decrement
                            }

                            prg = 99;
                            count = 1;
                            pgsBarInc = 0;
                            progssBar.setProgress(prg);

                            time4taskMillis = (long)(1000*(time4task*60));
                            timerCntDwn = new CounterClass(time4taskMillis, 1000);
                            timerCntDwn.start(); // Chrono Countdown
                            chrono.start(); // Chrono to meassure the time required for the task
                            break;
                        case 1: // task performed
                            timerCntDwn.cancel(); // Stops the Countdown Chrono
                            chrono.stop(); // Stops the time required for the task
                            chrono.setBase(chrono.getBase()-1000);
                            //Saves into the pool/Aufgabe the time used to solve the task
                            if (MainActivity.poolActivated) {
                                MainActivity.aufPoolLoad.setTimeRequired(chrono.getText().toString());
                            } else {
                                MainActivity.aufLoad.setTimeRequired(chrono.getText().toString());
                            }
                            // Saves the time of the Chrono to display it in the Time Statistic
                            MainActivity.vecTime.add(chrono.getText());

                            // Removes the timer View from the layout
                            LinearLayout layoutT;
                            layoutT = (LinearLayout) findViewById(R.id.layTimer);
                            layoutT.removeAllViewsInLayout();
                            layoutT.setVisibility(View.GONE);
                            layoutT.invalidate();

                            System.out.println("1 task performed");
                            btnAkt2.setText(R.string.zur_Lösung);
                            layout = (LinearLayout) findViewById(R.id.item);
                            layout.removeAllViewsInLayout();

                            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout einschaetzung = (LinearLayout) inflater.inflate(R.layout.einschaetzung, null);
                            layout.addView(einschaetzung);
                            btnAkt2.setEnabled(false);

                            // Change the state of the current Aufgabe
                            if (MainActivity.poolActivated) {
                                MainActivity.aufPoolLoad.setZustand(2); // TaskSolved
                            } else {
                                MainActivity.aufLoad.setZustand(2); // TaskSolved
                            }

                            rGroup = (RadioGroup) findViewById(R.id.radioGrup1);
                            RadioButton checkedRadioButton1 = (RadioButton) rGroup.findViewById(rGroup.getCheckedRadioButtonId());
                            rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                                    // This will get the radiobutton that has changed in its
                                    // check state
                                    RadioButton checkedRadioButton1 = (RadioButton) rGroup.findViewById(checkedId);
                                    // This puts the value (true/false) into the variable
                                    boolean isChecked = checkedRadioButton1.isChecked();
                                    // If the radiobutton that has changed in check state is
                                    // now checked...
                                    if (isChecked) {
                                        btnAkt2.setEnabled(true);
                                        //ScrollView view = (ScrollView) findViewById(R.id.scroll);
                                        RelativeLayout view = (RelativeLayout) findViewById(R.id.scroll);
                                        view.setScrollContainer(false);
                                        AbsListView.OnScrollListener scroll = new AbsListView.OnScrollListener() {
                                            @Override
                                            public void onScroll(AbsListView view,
                                                                 int firstVisibleItem,
                                                                 int visibleItemCount, int totalItemCount) {
                                                //Toast.makeText(getApplicationContext(),"OnTouchListener", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onScrollStateChanged(
                                                    AbsListView view, int scrollState) {
                                                //Toast.makeText(getApplicationContext(),"OnTouchListener", Toast.LENGTH_SHORT).show();
                                            }
                                        };

                                        LinearLayout lnrLayout = (LinearLayout) findViewById(R.id.teinschaetzung);
                                        //l.setScrollbarFadingEnabled(false);

                                        lnrLayout.setOnTouchListener(new View.OnTouchListener() {

                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {

                                                //Toast.makeText(getApplicationContext(),"OnTouchListener", Toast.LENGTH_SHORT).show();
                                                return false;
                                            }
                                        });
                                    } // if

                                    switch (checkedId) { //set the Model to hold the answer the user picked
                                        case R.id.radio_2:
                                            intEmpf = Integer.parseInt(feeling1);
                                            break;
                                        case R.id.radio_1:
                                            intEmpf = Integer.parseInt(feeling2);
                                            break;
                                        case R.id.radio0:
                                            intEmpf = Integer.parseInt(feeling3);
                                            break;
                                        case R.id.radio1:
                                            intEmpf = Integer.parseInt(feeling4);
                                            break;
                                        case R.id.radio2:
                                            intEmpf = Integer.parseInt(feeling5);
                                            break;
                                        default:
                                    }
                                } // onCheckedChanged
                            }); // rGroup.setOnCheckedChangeListener
                            break;
                        case 2: // personal evaluated
                            System.out.println("2 personal evaluated");
                            try {
                                //mloesung = (ImageView) findViewById(R.id.image);
                                //mloesung.setAdjustViewBounds(true);
                                //mloesung.setScaleType(ImageView.ScaleType.FIT_CENTER);

                                // Get the image from the current Test / Pool
                                // And sets the empfindung of the current task/Pool
                                if (MainActivity.poolActivated) {
                                    imageAufgabe = MainActivity.aufPoolLoad.getImageLoesung();
                                    MainActivity.aufPoolLoad.setEmpfindung(intEmpf);
                                } else {
                                    imageAufgabe = MainActivity.aufLoad.getImageLoesung();
                                    MainActivity.aufLoad.setEmpfindung(intEmpf);
                                }

                                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                // Turns the String imageAufgabe into an int with the value R.drawable.image.
                                image2Disp = getResources().getIdentifier(imageAufgabe, "drawable", getPackageName());
                                Resources res = getResources();
                                Drawable shape = res.getDrawable(image2Disp);
                                // displays the image in the XML file
                                image.setImageDrawable(shape);

                                layout = (LinearLayout) findViewById(R.id.item);
                                layout.removeAllViewsInLayout();
                                btnAkt2.setText(R.string.Weiter);
                                //Toast.makeText(getApplicationContext(), "msg msg3",Toast.LENGTH_SHORT).show();

                                ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.loesung, null);
                                layout.addView(vg);
                                layout.invalidate();
                                TextView txt = (TextView) findViewById(R.id.textAufComment);
                                txt.setText("");
                                /*int n_poolAufgb_4display = MainActivity.n_poolAufgb + 1;
                                int n_testAufgb_4display = MainActivity.n_Aufgbe + 1;
                                if (MainActivity.poolActivated) {
                                    txt.setText(" POOL " + MainActivity.n_poolTest + " Aufgabe " + n_poolAufgb_4display);
                                } else {
                                    txt.setText(" TEST " + MainActivity.n_Test + " Aufgabe " + n_testAufgb_4display);
                                }*/

                                btnAkt2.setEnabled(false);
                                CheckBox box = (CheckBox) findViewById(R.id.chkIos);
                                box.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        if (v.getId() == R.id.chkIos) {
                                            //Toast.makeText(getApplicationContext(),"box.isSelected", Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            image = (ImageView) findViewById(R.id.image);
                                        }
                                    }
                                });

                                // Change the state of the current Aufgabe
                                if (MainActivity.poolActivated) {
                                    MainActivity.aufPoolLoad.setZustand(3); // Personal Evaluated
                                } else {
                                    MainActivity.aufLoad.setZustand(3); // Personal Evaluated
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                                System.out.println("ERROR ???: TestAufgabe.onClickBtnAkt --> " + e);
                            } //try
                            break;
                        case 3: // answer compared
                            //mloesung = (ImageView) findViewById(R.id.image);
                            //mloesung.setAdjustViewBounds(true);
                            //mloesung.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            // Get the image from the current Test / Pool
                            if (MainActivity.poolActivated) {
                                imageAufgabe = MainActivity.aufPoolLoad.getImageLoesung();
                            } else {
                                imageAufgabe = MainActivity.aufLoad.getImageLoesung();
                            }

                            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            // Turns the String imageAufgabe into an int with the value R.drawable.image.
                            image2Disp = getResources().getIdentifier(imageAufgabe, "drawable", getPackageName());
                            Resources res = getResources();
                            Drawable shape = res.getDrawable(image2Disp);
                            // displays the image in the XML file
                            image.setImageDrawable(shape);

                            System.out.println("3 answer compared");
                            // Toast.makeText(getApplicationContext(), "msg msg4", Toast.LENGTH_SHORT).show();
                            btnAkt2.setText(R.string.Weiter);
                            layout = (LinearLayout) findViewById(R.id.item);
                            layout.removeAllViewsInLayout();
                            ViewGroup vg1 = (ViewGroup) inflater.inflate(R.layout.richtigkeit, null);
                            layout.addView(vg1);
                            layout.invalidate();
                            btnAkt2.setEnabled(false);

                            // Change the state of the current Aufgabe
                            if (MainActivity.poolActivated) {
                                MainActivity.aufPoolLoad.setZustand(4); // % Evaluated
                            } else {
                                MainActivity.aufLoad.setZustand(4); // % Evaluated
                            }

                            /*TextView txt = (TextView) findViewById(R.id.textAufComment);
                            int n_poolAufgb_4display = MainActivity.n_poolAufgb + 1;
                            int n_testAufgb_4display = MainActivity.n_Aufgbe + 1;


                            if (MainActivity.poolActivated) {
                                txt.setText(" POOL " + MainActivity.n_poolTest + " Aufgabe " + n_poolAufgb_4display);
                            } else {
                                txt.setText(" TEST " + MainActivity.n_Test + " Aufgabe " + n_testAufgb_4display);
                            }*/

                            rGroup = (RadioGroup) findViewById(R.id.radioGrup2);
                            RadioButton list = (RadioButton) rGroup.findViewById(rGroup.getCheckedRadioButtonId());

                            rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    Integer pos = (Integer) group.getTag();

                                    // Model element = list.get(pos);
                                    switch (checkedId) { //set the Model to hold the answer the user picked
                                        case R.id.radio20:
                                            intZahl1 = Integer.parseInt(str1);
                                            //Toast.makeText(getApplicationContext(), "" + intZahl1, Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            break;
                                        case R.id.radio40:
                                            intZahl1 = Integer.parseInt(str2);
                                            //Toast.makeText(getApplicationContext(), "" + intZahl1, Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            break;
                                        case R.id.radio60:
                                            intZahl1 = Integer.parseInt(str3);
                                            //Toast.makeText(getApplicationContext(), "" + intZahl1, Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            break;
                                        case R.id.radio80:
                                            intZahl1 = Integer.parseInt(str4);
                                            //Toast.makeText(getApplicationContext(), "" + intZahl1, Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            break;
                                        case R.id.radio100:
                                            intZahl1 = Integer.parseInt(str5);
                                            //Toast.makeText(getApplicationContext(), "" + intZahl1, Toast.LENGTH_SHORT).show();
                                            btnAkt2.setEnabled(true);
                                            break;
                                        default:

                                    }
                                } // onCheckedChanged

                            }); // rGgroup
                            break;
                        case 4: // evaluated
                            System.out.println("4 evaluated");

                            // Set the qualification of the current task/Pool and saves the info in the XML
                            if (MainActivity.poolActivated) {
                                MainActivity.aufPoolLoad.setQualifikation(intZahl1);
                                writePoolXML();
                            } else {
                                MainActivity.aufLoad.setQualifikation(intZahl1);
                                writeAufgabeXML();
                                //writeSystemOutAufgabeXML();
                                //call2read2();
                            }

                            // Stores the qualification of each task is stored (To use it in the Chart)
                            MainActivity.vecQualifikation.add(intZahl1);
                            // Stores the self Empfindung of each task (to be used in the chart)
                            MainActivity.vecEmpfindung.add(intEmpf);

                            if (MainActivity.poolActivated) {
                                MainActivity.n_poolAufgb++; //the next Pool-Aufgabe is going to be evaluated
                                MainActivity.n_poolQntAufEvl++; // Increments the number of Aufgabe evaluated from the list Aufgb2Eval
                                MainActivity.vecTest.add("Pool " + MainActivity.n_poolTest);
                            } else {
                                MainActivity.n_Aufgbe++; //the next Aufgabe is going to be evaluated
                                MainActivity.n_QntityAufEval++; // Increments the number of Aufgabe evaluated from the list Aufgb2Eval
                                MainActivity.vecTest.add("Test " + MainActivity.n_Test);
                            }

                            MainActivity.sumQualfktion = MainActivity.sumQualfktion + intZahl1;

                            if ((!MainActivity.poolActivated) && (MainActivity.n_Test <= 2)) {
                                if (MainActivity.aufgb2Eval.size() <= MainActivity.n_QntityAufEval) {

                                    // Evaluates the average of the current Test
                                    MainActivity.averageQualfktion = MainActivity.sumQualfktion / (MainActivity.n_Aufgbe);

                                    if (MainActivity.averageQualfktion < MainActivity.score2pool) {
                                        MainActivity.poolActivated = true;
                                    }
                                }
                            } else if (((MainActivity.n_poolTest == 2) || (MainActivity.n_Test < 3)) && (MainActivity.poolAfgb2Eval.size() <= MainActivity.n_poolQntAufEvl)) {
                                MainActivity.poolActivated = false;
                            }

                            Intent intent = new Intent(this, TestAufgabe.class);

                            //sends the name of which layout to use to the new intent
                            int xmlScreen = R.layout.activity_test_aufgabe;
                            intent.putExtra(EXTRA_MESSAGE, xmlScreen);
                            startActivity(intent);
                            finish();

                            break;
                    } // switch
            } // switch
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.java onClickBtnAkt --> " + e);
        } // try

        // Turning on zoom & drag
        //image.setOnTouchListener(this);
        //valMatrix = true;
        PhotoViewAttacher photoView = new PhotoViewAttacher(image);
        photoView.update();
    } // onClickBtnAkt

    private void showsGraph() {
        layout = (LinearLayout) findViewById(R.id.item);
        layout.removeAllViewsInLayout();
        ViewGroup vg2 = (ViewGroup) inflater.inflate(R.layout.activity_statistic, null);
        layout.addView(vg2);
        layout.invalidate();
    }

    private Runnable myThread = new Runnable() {
        @Override
        public void run() {
            while (prg < 100) {
                try {
                    hnd.sendMessage(hnd.obtainMessage());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread was Interrupted");
                }
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    txtVw.setText("fertig");
                }
            });
        } // run

        Handler hnd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //if (pgsBarInc >= count) {
                //System.out.println("pgsBarInc = " + pgsBarInc + " prg = " + prg + " count = " + count);
                //prg--;
                //progssBar.setProgress(prg);
                //count++;
                //}
                //pgsBarInc = pgsBarInc + pgsBarInc;

                String perc = String.valueOf(prg).toString();
                txtVw.setText(perc + "% abgelaufen");
            }
        };
    }; // Runnable myThread

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            textViewTime.setText(hms);

            pgsBarInc = pgsBarInc + pgsBarSum;
            if (pgsBarInc >= count) {
                prg--;
                if (time4task < 1.6) {
                    prg--;
                }
                progssBar.setProgress(prg);
                count++;
            }
            //System.out.println("pgsBarInc = " + pgsBarInc + " prg = " + prg + " count = " + count);
        }

        @Override
        public void onFinish() {
            textViewTime.setText("Completed.");
        }
    }

    public void parseNext(int xmlEval, int testNumbr) {
        // Parse the information from the XML that contains the different Tasks (Aufgaben)
        System.out.println("--> parseNext test" + testNumbr + " xml " + xmlEval);

        Context context = this;
        XmlPullParser parser = context.getResources().getXml(xmlEval);
        String valTime;

        try{
            int eventType = parser.getEventType();
            int aufgabeNumber = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        //MainActivity.aufgb2Eval = new ArrayList<Aufgabe>();
                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(MainActivity.AUFGABE)) {
                            currentAufgabe = new Aufgabe();
                            aufgabeNumber = Integer.parseInt(parser.getAttributeValue(0));
                            currentAufgabe.setAufgabe(aufgabeNumber);

                        } else if (currentAufgabe != null) {
                            if (currentTag.equalsIgnoreCase(MainActivity.BILD)) {
                                currentAufgabe.setImageAufgabe(parser.nextText());

                            } else if (currentTag.equalsIgnoreCase(MainActivity.HILFE)) {
                                helpOn = true;
                                eventType = parser.next();
                                currentTag = parser.getName();

                                // Loop to read the different values that the tag HILFE could have
                                while (currentTag.equalsIgnoreCase(MainActivity.VALUE)) {
                                    helpValue = parser.nextText();

                                    if (helpValue.length() > 0){
                                        currentAufgabe.setHilfe(helpValue);
                                    }

                                    eventType = parser.next();
                                    currentTag = parser.getName();
                                } // end while

                            } else if (currentTag.equalsIgnoreCase(MainActivity.LOESUNG)) {
                                currentAufgabe.setImageLoesung(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.TEXT)) {
                                currentAufgabe.setText(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.TIME)) {
                                timeTemp = parser.nextText(); //Trujillo 06_03_2016
                                currentAufgabe.setTime(timeTemp); //Trujillo 06_03_2016
                                MainActivity.vecTimeTask.add(timeTemp); //Trujillo 06_03_2016
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUC)) {
                                MainActivity.vecAvgUc.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDC)) {
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUF)) {
                                MainActivity.vecEmpUf.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDF)) {
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUT)) {
                                MainActivity.vecTime4taskUt.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDT)) {
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(MainActivity.AUFGABE) && currentAufgabe != null) {
                            // add the current Aufgabe (class Aufgabe) to the Aufgabe List
                            currentAufgabe.setZustand(0); //Standby
                            currentAufgabe.setTest(testNumbr);
                            MainActivity.aufgb2Eval.add(currentAufgabe);
                        } else if (currentTag.equalsIgnoreCase(MainActivity.AUFGABEN)) {

                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.java parseNext --> " + e);
        } // try
    } // parseNext

    public void poolParse(int xmlEval, int poolTestNumbr) {
        // Parse the information from the XML that contains the different Tasks (Aufgaben)
        System.out.println("--> poolParse pool " + poolTestNumbr + " xml " + xmlEval );

        Context context = this;
        XmlPullParser parser = context.getResources().getXml(xmlEval);
        String valTime;

        try{
            int eventType = parser.getEventType();
            int poolAufgbNum = 0;
            int poolAufgbNum_Avg = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        try { // Try to consult the size of poolAfgb2Eval, and when it has not been initialized it goes to the Catch
                            int trySize = MainActivity.poolAfgb2Eval.size();
                        } catch (Exception e){ // initialize the poolAfgb2Eval
                            MainActivity.poolAfgb2Eval = new ArrayList<Pool>();
                        }
                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(MainActivity.AUFGABE)) {
                            currentPoolAfgb = new Pool();
                            poolAufgbNum ++;

                            if (MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) {
                                if (poolAufgbNum < 4) {
                                    currentPoolAfgb.setPoolAufgb(poolAufgbNum);
                                }
                            } else if (poolAufgbNum > 3){
                                poolAufgbNum_Avg++;
                                currentPoolAfgb.setPoolAufgb(poolAufgbNum_Avg);
                            }
                        } else if (currentPoolAfgb != null) {
                            if (currentTag.equalsIgnoreCase(MainActivity.BILD)) {
                                currentPoolAfgb.setImagePool(parser.nextText());

                            } else if (currentTag.equalsIgnoreCase(MainActivity.HILFE)) {
                                helpOn = true;
                                eventType = parser.next();
                                currentTag = parser.getName();

                                // Loop to read the different values that the tag HILFE could have
                                while (currentTag.equalsIgnoreCase(MainActivity.VALUE)) {
                                    helpValue = parser.nextText();

                                    if (helpValue.length() > 0){
                                        currentPoolAfgb.setHilfe(helpValue);
                                    }

                                    eventType = parser.next();
                                    currentTag = parser.getName();
                                } // end while

                            } else if (currentTag.equalsIgnoreCase(MainActivity.LOESUNG)) {
                                currentPoolAfgb.setImagePoolLsng(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.TEXT)) {
                                currentPoolAfgb.setText(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.TIME)) {
                                timeTemp = parser.nextText(); //Trujillo 06_03_2016
                                currentPoolAfgb.setTime(timeTemp); //Trujillo 06_03_2016
                                MainActivity.vecTimeTask.add(timeTemp); //Trujillo 06_03_2016
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUC)) {
                                MainActivity.vecAvgUc.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDC)) {
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUF)) {
                                MainActivity.vecEmpUf.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDF)) {
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGUT)) {
                                MainActivity.vecTime4taskUt.add(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(MainActivity.AVGDT)) {
                                //MainActivity.         .add(parser.nextText());
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(MainActivity.AUFGABE) && currentPoolAfgb != null) {
                            // add the current Aufgabe (class Aufgabe) to the Aufgabe List
                            currentPoolAfgb.setZustand(0); //Standby
                            currentPoolAfgb.setPoolTest(poolTestNumbr);

                            //if (poolTestNumbr > 1) {
                            if (MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) {
                                if (poolAufgbNum < 4) {
                                    MainActivity.poolAfgb2Eval.add(currentPoolAfgb);
                                }
                            } else if (poolAufgbNum > 3){
                                MainActivity.poolAfgb2Eval.add(currentPoolAfgb);
                            }
                            //} else {
                            //    if (MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) {
                            //        if (poolAufgbNum < 4) {
                            //            MainActivity.poolAfgb2Eval.add(currentPoolAfgb);
                            //        }
                            //    } else if (poolAufgbNum > 3){
                            //        MainActivity.poolAfgb2Eval.add(currentPoolAfgb);
                            //    }
                            //}
                        } else if (currentTag.equalsIgnoreCase(MainActivity.AUFGABEN)) {

                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.java poolParse --> " + e);
        } // try
    } // poolParse

    public void writeAufgabeXML(){
        // writes the values of the sample file created for the Aufgabe
        System.out.println("--> writeAufgabeXML");

        Context context = this.getApplicationContext();

        try {
            FileOutputStream fOutCntxt = null;
            fOutCntxt = context.openFileOutput(MainActivity.aufgabeValues, Context.MODE_PRIVATE);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOutCntxt);

            File myFile = new File("/sdcard/" + MainActivity.aufgabeValues);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter OSW = new OutputStreamWriter(fOut);

            myOutWriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            myOutWriter.append("<aufgaben>\n");

            OSW.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            OSW.append("<aufgaben>\n");

            for(int x=0; x < MainActivity.aufgb2Eval.size(); x++) {
                if (x <= MainActivity.n_QntityAufEval) {
                    Aufgabe aufTemp = MainActivity.aufgb2Eval.get(x);
                    int test = aufTemp.getTest();
                    int aufgabe = aufTemp.getAufgabe();
                    String timeRequired = aufTemp.getTimeRequired();
                    int zustand = aufTemp.getZustand();
                    int qualifikation = aufTemp.getQualifikation();
                    int empfindung = aufTemp.getEmpfindung();

                    myOutWriter.append("\t<test id=\"" + test + "\">\n");
                    myOutWriter.append("\t\t<aufgabe>" + aufgabe + "</aufgabe>\n");
                    myOutWriter.append("\t\t<timeRequired>" + timeRequired + "</timeRequired>\n");
                    myOutWriter.append("\t\t<zustang>" + zustand + "</zustang>\n");
                    myOutWriter.append("\t\t<qualifikation>" + qualifikation + "</qualifikation>\n");
                    myOutWriter.append("\t\t<empfindung>" + empfindung + "</empfindung>\n");
                    myOutWriter.append("\t</test>\n");

                    OSW.append("\t<test id=\"" + test + "\">\n");
                    OSW.append("\t\t<aufgabe>" + aufgabe + "</aufgabe>\n");
                    OSW.append("\t\t<timeRequired>" + timeRequired + "</timeRequired>\n");
                    OSW.append("\t\t<zustang>" + zustand + "</zustang>\n");
                    OSW.append("\t\t<qualifikation>" + qualifikation + "</qualifikation>\n");
                    OSW.append("\t\t<empfindung>" + empfindung + "</empfindung>\n");
                    OSW.append("\t</test>\n");
                }
            } // for

            myOutWriter.append("</aufgaben>\n");
            OSW.append("</aufgaben>");

            OSW.close();
            fOut.close();
            System.out.println("crea archivo");

            myOutWriter.close();
            fOutCntxt.close();
            System.out.println("escrito correctamente");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR ???: WriteXML.java writeFileXML --> " + e);
        } catch (Exception e) {
            System.out.println("ERROR ???: WriteXML.java writeFileXML --> " + e);
        }  // try
    } // writeAufgabeXML

    public void writePoolXML(){
        // writes the values of the sample file created for the pool
        System.out.println("--> writePoolXML");

        Context context = this.getApplicationContext();

        try {
            FileOutputStream fOutCntxt = null;
            fOutCntxt = context.openFileOutput(MainActivity.poolValues, Context.MODE_PRIVATE);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOutCntxt);

            File myFile = new File("/sdcard/" + MainActivity.poolValues); // Trujillo 07_03_2016
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter OSW = new OutputStreamWriter(fOut);

            myOutWriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            myOutWriter.append("<Poolaufgaben>\n");

            OSW.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"); // Trujillo 07_03_2016
            OSW.append("<pool>\n");

            for(int x=0; x < MainActivity.poolAfgb2Eval.size(); x++) {
                if (x <= MainActivity.n_poolQntAufEvl) {
                    Pool poolTemp = MainActivity.poolAfgb2Eval.get(x);
                    int test = poolTemp.getPoolTest();
                    int aufgabe = poolTemp.getPoolAufgb();
                    String timeRequired = poolTemp.getTimeRequired();
                    int zustand = poolTemp.getZustand();
                    int qualifikation = poolTemp.getQualifikation();
                    int empfindung = poolTemp.getEmpfindung();

                    myOutWriter.append("\t<test id=\"" + test + "\">\n");
                    myOutWriter.append("\t\t<aufgabe>" + aufgabe + "</aufgabe>\n");
                    myOutWriter.append("\t\t<timeRequired>" + timeRequired + "</timeRequired>\n");
                    myOutWriter.append("\t\t<zustang>" + zustand + "</zustang>\n");
                    myOutWriter.append("\t\t<qualifikation>" + qualifikation + "</qualifikation>\n");
                    myOutWriter.append("\t\t<empfindung>" + empfindung + "</empfindung>\n");
                    myOutWriter.append("\t</test>\n");

                    OSW.append("\t<test id=\"" + test + "\">\n"); // trujillo 07_03_2016
                    OSW.append("\t\t<aufgabe>" + aufgabe + "</aufgabe>\n");
                    OSW.append("\t\t<timeRequired>" + timeRequired + "</timeRequired>\n");
                    OSW.append("\t\t<zustang>" + zustand + "</zustang>\n");
                    OSW.append("\t\t<qualifikation>" + qualifikation + "</qualifikation>\n");
                    OSW.append("\t\t<empfindung>" + empfindung + "</empfindung>\n");
                    OSW.append("\t</test>\n");
                }
            } // for

            myOutWriter.append("</Poolaufgaben>\n");

            OSW.append("</pool>"); //Trujillo 07_03_2016
            OSW.close();
            fOut.close();

            myOutWriter.close();
            fOutCntxt.close();
            System.out.println("escrito correctamente");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR ???: WriteXML.java writeFileXML --> " + e);
        } catch (Exception e) {
            System.out.println("ERROR ???: WriteXML.java writeFileXML --> " + e);
        }  // try
    } // writePoolXML

    public static void writeSystemOutAufgabeXML(){
        // writes the actual values of the class array (works for both, Aufgabe and pool
        System.out.println("--> writeSystemOutAufgabeXML");

        try {
            System.out.println("------AUFGABEn------");
            for(int x=0; x < MainActivity.aufgb2Eval.size(); x++) {
                Aufgabe aufTemp = MainActivity.aufgb2Eval.get(x);

                System.out.println("---AUFGABE---");
                System.out.println("test id= " + aufTemp.getTest());
                System.out.println("aufgabe " + aufTemp.getAufgabe());
                System.out.println("image " + aufTemp.getImageAufgabe());
                System.out.println("imageLoesung " + aufTemp.getImageLoesung());
                System.out.println("hilfe " + aufTemp.getHilfe());
                System.out.println("timeRequired " + aufTemp.getTimeRequired());
                System.out.println("text "+ aufTemp.getText());
                System.out.println("time " + aufTemp.getTime());
                System.out.println("Zustang " + aufTemp.getZustand());
                System.out.println("qualifikation " + aufTemp.getQualifikation());
                System.out.println("---/AUFGABE---");
            } // for
            System.out.println("------/AUFGABEN------");

            System.out.println("------POOL_/------");
            for(int x=0; x < MainActivity.poolAfgb2Eval.size(); x++) {
                Pool poolTemp = MainActivity.poolAfgb2Eval.get(x);

                System.out.println("---POOL---");
                System.out.println("poolTest id= " + currentPoolAfgb.getPoolTest());
                System.out.println("PoolAufgabe " + currentPoolAfgb.getPoolAufgb());
                System.out.println("imagePool " + currentPoolAfgb.getImagePool());
                System.out.println("ImageLoesung " + currentPoolAfgb.getImageLoesung());
                System.out.println("hilfe " + currentPoolAfgb.getHilfe());
                System.out.println("timeRequired " + currentPoolAfgb.getTimeRequired());
                System.out.println("text " + currentPoolAfgb.getText());
                System.out.println("time " + currentPoolAfgb.getTime());
                System.out.println("Zustand " + currentPoolAfgb.getZustand());
                System.out.println("qualifikation " + currentPoolAfgb.getQualifikation());
                System.out.println("---/POOL---");
            } // for
            System.out.println("------/POOL_/------");
        } catch (NullPointerException e) {
            System.out.println("---No existe archivo de pool---");
        } catch (Exception e) {
            System.out.println("ERROR ???: writeSystemOutAufgabeXML --> " + e);
        }  // try
    } // writeSystemOutAufgabeXML

    public void call2read2() {
        // muestra los valores en los archivos (xml en memoria)que guardan la info de la aplicación
        System.out.println("--> call2read");

        FileInputStream fin = null;
        try {
            fin = openFileInput(MainActivity.aufgabeValues);
            XmlParser.lectorDarchivos(fin);
            fin.close();

            fin = openFileInput(MainActivity.poolValues);
            XmlParser.lectorDarchivos(fin);
            fin.close();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void readXML() {
        // Reads the values saved in the memory (Virtual) in a .xml file
        // aufgabeValues.xml --> saves the values for the normal tests
        // poolValues.xml --> saves the values for the pool
        System.out.println("--> readXML");

        FileInputStream fin = null;
        try {
            if (MainActivity.startsFromSavedInfo) {
                fin = openFileInput(MainActivity.aufgabeValues);
                XmlParser.parseXmlAufgabe(fin);
                fin.close();
            }

            if (MainActivity.startsFromSavedPoolInfo) {
                fin = openFileInput(MainActivity.poolValues);
                XmlParser.parseXmlPool(fin);
                fin.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR ???: TestAufgabe.readXML --> " + e);
        }
    } // readXML

    public boolean onTouch(View v, MotionEvent event) {
        // makes the corresponding action when the screen is touch
        ImageView view = (ImageView) v;

        // Dump touch event to log
        //dumpEvent(event);

        if (valMatrix) {
            image.setScaleType(ImageView.ScaleType.MATRIX);

            valMatrix = false;
        }

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix); // Gets the values of the matrix before it is moved

                    float imgWidthRight = 0; // Free space allowed in the left part of the image
                    float imgWidthLeft = -(image.getWidth() - (image.getWidth()/4)); // free space allowed in the right part of the image
                    float xValue;

                    float imgHeightUp = -(image.getHeight() - (image.getHeight()/4)); // Free space allowed bellow the image
                    float imgHeightDown = 0; // Free space allowed above the image
                    float yValue;

                    float[] f_mtrx = new float[9];
                    float[] f_img = new float[9];
                    matrix.getValues(f_mtrx); // Gets the actual values of the matrix into f_mtrx
                    image.getImageMatrix().getValues(f_img); // Gets the actual values of the image into f_img

                    if ((f_img[2] > imgWidthRight) && (event.getX() - start.x > 0)) {
                        // (if the new X position of the image > allow space to move right) && (image is being dragged to the right)
                        xValue = 0; // don't move the image
                    } else if ((f_img[2] < imgWidthLeft) && (event.getX() - start.x < 0)) {
                        // (if the new X position of the image < allow space to move left) && (image is being dragged to the left)
                        xValue = 0; // don't move the image
                    } else {
                        xValue = event.getX() - start.x; // new x position for the image
                    }

                    if ((f_img[5] > imgHeightDown) && (event.getY() - start.y > 0)) {
                        // (if the new Y position of the image > allow space to move up) && (image is being dragged up)
                        yValue = 0; // don't move the image
                    } else if ((f_img[5] < imgHeightUp) && (event.getY() - start.y < 0)) {
                        // (if the new Y position of the image < allow space to move down) && (image is being dragged down)
                        yValue = 0; // don't move the image
                    } else {
                        yValue = event.getY() - start.y; // new Y position for the image
                    }

                    //matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    matrix.postTranslate(xValue, yValue); // Sets the image in its new position
                }
                else if (mode == ZOOM) {
                    float[] f = new float[9];

                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }

                    matrix.getValues(f);
                    float scaleX = f[Matrix.MSCALE_X];
                    float scaleY = f[Matrix.MSCALE_Y];

                    // Sets the MAX and the MIN zoom values
                    if(scaleX <= 0.5f) {
                        matrix.postScale((0.5f)/scaleX, (0.5f)/scaleY, mid.x, mid.y);
                    } else if(scaleX >= 1.5f) {
                        matrix.postScale((1.5f)/scaleX, (1.5f)/scaleY, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /* Executes the instruction System.exit(0) when the android back button is pressed*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            System.exit(0);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
} // TestAufgabe
