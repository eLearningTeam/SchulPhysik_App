package com.lern.schulphysikapp;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class XmlParser {
    //variables for parsing the XML
    private static boolean done = false;
    private static String currentTag = null;
    static int aufgabeNumber = 0;

    // names of the XML tags for test files
    static final String AUFGABEN = "aufgaben";
    static final String AUFGABE = "aufgabe";
    static final String BILD = "bild";
    static final String HILFE = "hilfe";
    static final String VALUE = "value";
    static final String LOESUNG = "loesung";
    static final String TEXT = "text";
    static final String TIME = "time";
    static final String TEST = "test";
    static final String TIMEREQUIRED = "timerequired";
    static final String ZUSTANG = "zustang";
    static final String QUALIFIKATION = "qualifikation";
    static final String EMPFINDUNG = "empfindung";
    static final String AVGUPCORR = "avgupcorr";
    static final String AVGUPFEEL = "avgupfeel";
    static final String AVGUPTIME = "avguptime";

    // Variable helpOn is to know when reading a help tag
    static boolean helpOn = false;
    static String helpValue = null;

    static ArrayList<Aufgabe> aufgabeList = null;
    private static Aufgabe currentAufgabe = null;
    private static Pool currentPool = null;

    //variable for parseIntro
    private static Vector vectorIntro;
    static final String INTRO = "intro";
    static final String STEP = "step";
    static final String INSTRUCTION = "instruction";

    public static Vector parseIntro(Context context, int xmlEval) {
        System.out.println("--> parseIntro");

        XmlPullParser parser = context.getResources().getXml(xmlEval);

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        vectorIntro = new Vector<String>();

                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(INTRO)) {
                            vectorIntro.addElement(parser.nextText());
                        } else if (currentTag.equalsIgnoreCase(STEP)) {
                            String AttVal = (parser.getAttributeValue(0));
                            vectorIntro.addElement(parser.nextText());
                            MainActivity.vectorIntroTitle.addElement(AttVal);
                        } // end if

                        break;
                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(INSTRUCTION)) {
                            //done = true;  Trujillo 28/04/16
                        }

                        break;
                } // end switch

                eventType = parser.next();
            } // end while
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error ???: " + e);
        }

        return vectorIntro;
    } //end parseIntro

    public static void parse(Context context, int xmlEval, int testNumbr) {
        // Parse the information from the XML that contains the different Tasks (Aufgaben)
        System.out.println("--> parse");

        XmlPullParser parser = context.getResources().getXml(R.xml.test01);

        try {
            int eventType = parser.getEventType();
            int aufgabeNumber = 0;

            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        MainActivity.aufgb2Eval = new ArrayList<Aufgabe>();
                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(AUFGABE)) {
                            currentAufgabe = new Aufgabe();
                            aufgabeNumber++;
                            currentAufgabe.setAufgabe(aufgabeNumber);

                        } else if (currentAufgabe != null) {
                            if (currentTag.equalsIgnoreCase(BILD)) {
                                currentAufgabe.setImageAufgabe(parser.nextText());

                            } else if (currentTag.equalsIgnoreCase(HILFE)) {
                                helpOn = true;
                                eventType = parser.next();
                                currentTag = parser.getName();

                                // Loop to read the different values that the tag HILFE could have
                                while (currentTag.equalsIgnoreCase(VALUE)) {
                                    helpValue = parser.nextText();

                                    if (helpValue.length() > 0) {
                                        currentAufgabe.setHilfe(helpValue);
                                    }

                                    eventType = parser.next();
                                    currentTag = parser.getName();
                                } // end while

                            } else if (currentTag.equalsIgnoreCase(LOESUNG)) {
                                currentAufgabe.setImageLoesung(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(TEXT)) {
                                currentAufgabe.setText(parser.nextText());
                            } else if (currentTag.equalsIgnoreCase(TIME)) {
                                currentAufgabe.setTime(parser.nextText());
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(AUFGABE) && currentAufgabe != null) {
                            // add the current Aufgabe (class Aufgabe) to the Aufgabe List
                            currentAufgabe.setZustand(0); //Standby
                            currentAufgabe.setTest(testNumbr);
                            MainActivity.aufgb2Eval.add(currentAufgabe);
                        } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {
                            done = true;
                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: .java parse --> " + e);
        } // try
    } // parse

    public static void parseXmlAufgabe(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben)
        // so the application can start from the last point
        System.out.println("--> parseXmlAufgabe");

        //if (MainActivity.xmlMemoryReadAuf) {  // To be sure that this process ir executed just once
            try {
                XmlPullParser parser = Xml.newPullParser(); //_/_/_/
                parser.setInput(fis, "UTF-8"); //_/_/_/

                int eventType = parser.getEventType();
                int testNum = 0;
                int testNumTemp;
                int aufNum = 0;
                int empAuf = 0;
                int counter = 0; //Trujillo 03/05/2016
                String timeAuf;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:

                            break;
                        case XmlPullParser.START_TAG:
                            currentTag = parser.getName();

                            if (currentTag.equalsIgnoreCase(TEST)) {
                                testNum = Integer.parseInt(parser.getAttributeValue(0));
                                //currentAufgabe = MainActivity.aufgb2Eval.get(testNum - 1);
                                //currentAufgabe = MainActivity.aufgb2Eval.get(aufNum); // Trujillo 03/05/2016
                                currentAufgabe = MainActivity.aufgb2Eval.get(counter); // Trujillo 03/05/2016
                                currentAufgabe.setTest(testNum);
                                //_/1 if (testNum > 1) {
                                //_/1     MainActivity.n_Test = testNum - 1;
                                //_/1 } else {
                                //_/1     MainActivity.n_Test = testNum;
                                //_/1 }

                                //_/1 //Reads the xml with the necessary values for the next test
                                //_/1 if (testNum > 1) {
                                //_/1     MainActivity.readNextTest = true;
                                //_/1 }

                            } else if (currentTag != null) {
                                if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                    aufNum = Integer.parseInt(parser.nextText());
                                    currentAufgabe.setAufgabe(aufNum);
                                    //_/1 MainActivity.n_Aufgbe = aufNum;
                                    //MainActivity.n_QntityAufEval++;

                                    // Data to create the statistic Graph
                                    MainActivity.vecTest.add("Test " + testNum);
                                } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                    timeAuf = parser.nextText();
                                    currentAufgabe.setTimeRequired(timeAuf);
                                    //Data for the Statistic
                                    MainActivity.vecTime.add(timeAuf);
                                } else if (currentTag.equalsIgnoreCase(ZUSTANG)) {
                                    currentAufgabe.setZustand(Integer.parseInt(parser.nextText()));
                                } else if (currentTag.equalsIgnoreCase(QUALIFIKATION)) {
                                    int intQual = Integer.parseInt(parser.nextText());
                                    currentAufgabe.setQualifikation(intQual);
                                    MainActivity.sumQualfktion = MainActivity.sumQualfktion + intQual;
                                    //Data for the Statistic
                                    MainActivity.vecQualifikation.add(intQual);
                                } else if (currentTag.equalsIgnoreCase(EMPFINDUNG)) {
                                    empAuf = Integer.parseInt(parser.nextText());
                                    currentAufgabe.setEmpfindung(empAuf);
                                    //Data for the Statistic
                                    MainActivity.vecEmpfindung.add(empAuf);
                                } // if
                            } // if
                            break;

                        case XmlPullParser.END_TAG:
                            currentTag = parser.getName();

                            if (currentTag.equalsIgnoreCase(TEST) && currentAufgabe != null) {
                                //_/1 MainActivity.n_QntityAufEval++;
                                //testNumTemp = MainActivity.n_QntityAufEval - 1;
                                //testNumTemp = aufNum - 1; // Trujillo 24/05/2016

                                MainActivity.aufgb2Eval.set(counter, currentAufgabe); // Trujillo 24/05/2016
                                counter++; // Trujillo 03/05/2016


                                MainActivity.startsFromSavedInfo = false;
                            } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {

                            }
                            break;
                    } // switch
                    eventType = parser.next();
                } // while
            } catch (Exception e) {
                System.out.println("ERROR ???: XmlParser.java parseXmlAufgabe --> " + e);
            } // try
            //MainActivity.xmlMemoryReadAuf = false;
        //}
    } // parseXmlAufgabe

    public static void parseXmlPool(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben)
        // so the application can start from the last point
        System.out.println("--> parseXmlPool");

        //if (MainActivity.xmlMemoryReadPool) {  // To be sure that this process ir executed just once
            try {
                XmlPullParser parser = Xml.newPullParser(); //_/_/_/
                parser.setInput(fis, "UTF-8"); //_/_/_/

                int eventType = parser.getEventType();
                int testNum = 0;
                int poolNum = 0;
                int empAuf = 0;
                int counter = 0; // Trujillo 03/05/2016
                String timeAuf;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:

                            break;
                        case XmlPullParser.START_TAG:
                            currentTag = parser.getName();

                            if (currentTag.equalsIgnoreCase(TEST)) {
                                testNum = Integer.parseInt(parser.getAttributeValue(0));

                                //_/2 // When MainActivity.poolAfg2Eval have not been already created, it throws an Exception and inside
                                //_/2 // the exception it declares poolAfg2Eval.
                                //_/2 try {
                                //currentPool = MainActivity.poolAfgb2Eval.get(testNum - 1);
                                //currentPool = MainActivity.poolAfgb2Eval.get(poolNum); // Trujillo 03/05/2016
                                currentPool = MainActivity.poolAfgb2Eval.get(counter);  // Trujillo 03/05/2016
                                //_/2 } catch (Exception e) {
                                //_/2     MainActivity.poolAfgb2Eval = new ArrayList<Pool>();
                                //_/2     currentPool = new Pool();
                                //_/2     MainActivity.poolAfgb2Eval.add(currentPool);
                                //_/2     //currentPool = MainActivity.poolAfgb2Eval.get(testNum - 1);
                                //_/2 }
                                currentPool.setPoolTest(testNum);
                            } else if (currentTag != null) {
                                if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                    poolNum = Integer.parseInt(parser.nextText());
                                    currentPool.setPoolAufgb(poolNum);
                                    //_/2 MainActivity.n_poolAufgb = poolNum;
                                    // Data to create the Graph
                                    MainActivity.vecTest.add("Pool " + testNum);
                                } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                    timeAuf = parser.nextText();
                                    currentPool.setTimeRequired(timeAuf);
                                    //Data for the Statistic
                                    MainActivity.vecTime.add(timeAuf);
                                } else if (currentTag.equalsIgnoreCase(ZUSTANG)) {
                                    currentPool.setZustand(Integer.parseInt(parser.nextText()));
                                } else if (currentTag.equalsIgnoreCase(QUALIFIKATION)) {
                                    int intQual = Integer.parseInt(parser.nextText());
                                    currentPool.setQualifikation(intQual);
                                    //Data for the Statistic
                                    MainActivity.vecQualifikation.add(intQual);
                                } else if (currentTag.equalsIgnoreCase(EMPFINDUNG)) {
                                    empAuf = Integer.parseInt(parser.nextText());
                                    currentAufgabe.setEmpfindung(empAuf);
                                    //Data for the Statistic
                                    MainActivity.vecEmpfindung.add(empAuf);
                                } // if
                            } // if
                            break;

                        case XmlPullParser.END_TAG:
                            currentTag = parser.getName();

                            if (currentTag.equalsIgnoreCase(TEST) && currentAufgabe != null) {
                                //_/2 MainActivity.n_poolQntAufEvl++;
                                // int testNumTemp = MainActivity.n_poolQntAufEvl - 1;
                                //int testNumTemp = poolNum - 1; Trujillo 24/05/2016
                                int testNumTemp = counter;

                                if (testNum > 1) {
                                    if (MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) {
                                        if (poolNum < 4) {
                                            MainActivity.poolAfgb2Eval.set(testNumTemp, currentPool);
                                        }
                                    } else if (poolNum > 3) {
                                        MainActivity.poolAfgb2Eval.set(testNumTemp, currentPool);
                                    }
                                } else {
                                    if (MainActivity.averageQualfktion <= MainActivity.qntPoolAufgaben) {
                                        if (poolNum < 4) {
                                            MainActivity.poolAfgb2Eval.set(testNumTemp, currentPool);
                                        }
                                    } else if (poolNum > 3) {
                                        MainActivity.poolAfgb2Eval.set(testNumTemp, currentPool);
                                    }
                                }

                                counter++; // Trujillo 03/05/2016

                            } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {

                            }
                            break;
                    } // switch
                    eventType = parser.next();
                } // while
            } catch (Exception e) {
                System.out.println("ERROR ???: XmlParser.java parseXmlPool --> " + e);
            } // try
            //MainActivity.xmlMemoryReadPool = false;
        //}
    } // parseXmlPool

    public static void parseXmlAufQntTest(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben)
        // in order to create in the array aufgb2Eval the necessary space to fill after with the
        // method parseXmlAufgabe
        System.out.println("--> parseXmlAufQntTest");

        try {
            XmlPullParser parser = Xml.newPullParser(); //_/_/_/
            parser.setInput(fis, "UTF-8"); //_/_/_/

            int eventType = parser.getEventType();
            int testNum = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            MainActivity.startsFromSavedInfo = true;
                            testNum = Integer.parseInt(parser.getAttributeValue(0));
                            if (testNum > 1) {
                                MainActivity.n_Test = testNum - 1;
                            } else {
                                MainActivity.n_Test = testNum;
                            }

                            // When readNextTest = true --> Reads the xml with the necessary values for the next test
                            if (testNum > 1) {
                                MainActivity.readNextTest = true;
                            }

                        } else if (currentTag != null) {
                            if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                int aufNum = Integer.parseInt(parser.nextText());
                                MainActivity.n_Aufgbe = aufNum;
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            MainActivity.n_QntityAufEval++;
                            //MainActivity.startsFromSavedInfo = false;
                        } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {

                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: XmlParser.java parseXmlAufQntTest --> " + e);
        } // try
    } // parseXmlAufQntTest

    public static void parseXmlPoolQntTest(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben)
        // so the application can start from the last point
        System.out.println("--> parseXmlPoolQntTest");

        try {
            XmlPullParser parser = Xml.newPullParser(); //_/_/_/
            parser.setInput(fis, "UTF-8"); //_/_/_/

            int eventType = parser.getEventType();
            int testNum = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            // When MainActivity.poolAfg2Eval have not been already created, it throws an Exception and inside
                            // the exception it declares poolAfg2Eval.
                           //_/_/_/try {
                                // this line is just to know if the poolAfgb2Eval was already delcare if not it goes to the catch
                                // and it will be declare.
                            //_/_/_/     Pool test2VerifyIfExists = MainActivity.poolAfgb2Eval.get(testNum - 1); // Trujillo 10/05/2016
                            //_/_/_/} catch (Exception e) {
                            //_/_/_/     MainActivity.poolAfgb2Eval = new ArrayList<Pool>();
                            //_/_/_/}
                            // in the curren pool are recorded all the values that will be added later to the poolAfgab2Eval Array
                            //_/_/_/currentPool = new Pool();  // Trujillo 10/05/2016

                            testNum = Integer.parseInt(parser.getAttributeValue(0));
                            MainActivity.n_poolTest = testNum;
                            //_/_/_/currentPool.setPoolTest(testNum);
                            // When readNextTest = true --> Reads the xml with the necessary values for the next test
                            //if (testNum > 0) {
                            //    MainActivity.readNextPoolTest = true; // Trujillo 08/05/2016
                            //}

                            // The next "if" statement sets the variable Pool Activated when there is a pool to display
                            if (MainActivity.n_poolTest == MainActivity.n_Test) {
                                MainActivity.poolActivated = true;
                            } else {
                                MainActivity.poolActivated = false;
                            }
                        } else if (currentTag != null) {
                            if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                int poolNum = Integer.parseInt(parser.nextText());

                                if (poolNum == 5) { // Trujillo 09/05/2016
                                    MainActivity.n_poolAufgb = 2;
                                    //_/_/_/currentPool.setPoolAufgb(2);
                                } else if (poolNum == 4) {
                                    MainActivity.n_poolAufgb = 1;
                                    //_/_/_/currentPool.setPoolAufgb(1);
                                } else {
                                    MainActivity.n_poolAufgb = poolNum;
                                    //_/_/_/currentPool.setPoolAufgb(poolNum);
                                }
                            } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                //_/_/_/currentPool.setTimeRequired(parser.nextText());
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            MainActivity.n_poolQntAufEvl++;
                            //_/_/_/MainActivity.poolAfgb2Eval.add(currentPool); // Trujillo 09/05/16
                        } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {

                        } else if (currentTag.equalsIgnoreCase(AUFGABE)) {
                            //_/_/_/MainActivity.poolAfgb2Eval.add(currentPool);
                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: XmlParser.parseXmlPoolQntTest --> " + e);
        } // try
    } // parseXmlPoolQntTest

    public static void lectorDarchivos(FileInputStream fis){  //_/_/_/ PRUEBAS _/_/_/
    // Parse the information from the XML (in virtual memory) that contains the different SAVED Tasks (Aufgaben)
    // so the application can start from the last point and display it on the System.out
        System.out.println("--> lectorDarchivos");

        try {
            XmlPullParser parser = Xml.newPullParser(); //_/_/_/
            parser.setInput(fis, "UTF-8"); //_/_/_/

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            System.out.println("<test id=\"" + parser.getAttributeValue(0) + "\">");
                        } else if (currentTag != null) {
                            if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                System.out.println("   <aufgabe>" + parser.nextText() + "</aufgabe>");
                            } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                System.out.println("   <timerequired>" + parser.nextText() + "</timerequired>");
                            } else if (currentTag.equalsIgnoreCase(ZUSTANG)) {
                                System.out.println("   <zustang>" + parser.nextText() + "</zustang>");
                            } else if (currentTag.equalsIgnoreCase(QUALIFIKATION)) {
                                System.out.println("   <qualifikation>" + parser.nextText() + "</qualifikation>");
                            } // if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST) && currentAufgabe != null) {
                            System.out.println("</test>");
                        } else if (currentTag.equalsIgnoreCase(AUFGABEN)) {
                            done = true;
                        }
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch(Exception e) {
            System.out.println("ERROR ???: XmlParser.java lectorDarchivos --> " + e);
        } // try
    } // Lector de archivos

    public static int fillsTest2Eval() {
        Enumeration vEnumTest = MainActivity.vecTest.elements();
        Enumeration vEnumComp = MainActivity.vecTest.elements();
        String j, h = "";
        int count = 0;
        int e_row=0;

        while(vEnumTest.hasMoreElements()) {
            j = vEnumTest.nextElement().toString();
            vEnumComp = MainActivity.vecTest.elements();

            if (j != h){
                while (vEnumComp.hasMoreElements()) {
                    h = vEnumTest.nextElement().toString();
                    if (h == j) {
                        count++;
                    }
                }// while

            }
        } // while

        return 0;
    }

    public static int evalQtest(String h) {
        Enumeration vEnumTest = MainActivity.vecTest.elements();
        String j = "";
        int count = 0;

        while(vEnumTest.hasMoreElements()) {
            j = vEnumTest.nextElement().toString();

            if (j == h){
                        count++;
            }
        } // while

        return count;
    } //evalQTest

    public static void parseXmlAufStatistic(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben) Just for the Statistic
        System.out.println("--> parseXmlAufStatistic");

        try {
            XmlPullParser parser = Xml.newPullParser(); //_/_/_/
            parser.setInput(fis, "UTF-8"); //_/_/_/

            int eventType = parser.getEventType();
            int testNum = 0;
            int testNumTemp;
            int aufNum = 0;
            int empAuf = 0;
            String timeAuf, timeReqAuf;

            String vecEmpUf, vecTimeUt, vecCorrUc;

            MainActivity.vecEmpUf = new Vector<String>();;
            MainActivity.vecTime4taskUt = new Vector<String>();;
            MainActivity.vecAvgUc = new Vector<String>();;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            testNum = Integer.parseInt(parser.getAttributeValue(0));
                        } else if (currentTag != null) {
                            if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                aufNum = Integer.parseInt(parser.nextText());
                                // Data to create the statistic Graph
                                MainActivity.vecTest.add("Test " + testNum);
                            } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                timeReqAuf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTime.add(timeReqAuf);
                            } else if (currentTag.equalsIgnoreCase(QUALIFIKATION)) {
                                int intQual = Integer.parseInt(parser.nextText());
                                //Data for the Statistic
                                MainActivity.vecQualifikation.add(intQual);
                            } else if (currentTag.equalsIgnoreCase(EMPFINDUNG)) {
                                empAuf = Integer.parseInt(parser.nextText());
                                //Data for the Statistic
                                MainActivity.vecEmpfindung.add(empAuf);
                            } else if (currentTag.equalsIgnoreCase(TIME)) {
                                timeAuf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTimeTask.add(timeAuf);
                            }else if (currentTag.equalsIgnoreCase(AVGUPCORR)) {
                                 vecCorrUc = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecAvgUc.add(vecCorrUc);
                            } else if (currentTag.equalsIgnoreCase(AVGUPFEEL)) {
                                 vecEmpUf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecEmpUf.add(vecEmpUf);
                            } else if (currentTag.equalsIgnoreCase(AVGUPTIME)) {
                                 vecTimeUt = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTime4taskUt.add(vecTimeUt);
                            }// if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: XmlParser.parseXmlAufStatistic --> " + e);
        } // try
    } // parseXmlAufStatistic

    public static void parseXmlPoolStatistic(FileInputStream fis) {
        // Parse the information from the XML that contains the different SAVED Tasks (Aufgaben)
        // so the application can start from the last point
        System.out.println("--> parseXmlPoolStatistic");

        //if (MainActivity.xmlMemoryReadPool) {  // To be sure that this process ir executed just once
        try {
            XmlPullParser parser = Xml.newPullParser(); //_/_/_/
            parser.setInput(fis, "UTF-8"); //_/_/_/

            int eventType = parser.getEventType();
            int testNum = 0;
            int poolNum = 0;
            int empAuf = 0;
            String timeAuf, timeReqAuf;
            String vecEmpUf, vecTimeUt, vecCorrUc;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if (currentTag.equalsIgnoreCase(TEST)) {
                            testNum = Integer.parseInt(parser.getAttributeValue(0));
                        } else if (currentTag != null) {
                            if (currentTag.equalsIgnoreCase(AUFGABE)) {
                                poolNum = Integer.parseInt(parser.nextText());
                                MainActivity.vecTest.add("Pool " + testNum);
                            } else if (currentTag.equalsIgnoreCase(TIMEREQUIRED)) {
                                timeReqAuf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTime.add(timeReqAuf);
                            } else if (currentTag.equalsIgnoreCase(QUALIFIKATION)) {
                                int intQual = Integer.parseInt(parser.nextText());
                                //Data for the Statistic
                                MainActivity.vecQualifikation.add(intQual);
                            } else if (currentTag.equalsIgnoreCase(EMPFINDUNG)) {
                                empAuf = Integer.parseInt(parser.nextText());
                                //Data for the Statistic
                                MainActivity.vecEmpfindung.add(empAuf);
                            } else if (currentTag.equalsIgnoreCase(TIME)) {
                                timeAuf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTimeTask.add(timeAuf);
                            } else if (currentTag.equalsIgnoreCase(AVGUPCORR)) {
                                vecCorrUc = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecAvgUc.add(vecCorrUc);
                            } else if (currentTag.equalsIgnoreCase(AVGUPFEEL)) {
                                vecEmpUf = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecEmpUf.add(vecEmpUf);
                            } else if (currentTag.equalsIgnoreCase(AVGUPTIME)) {
                                vecTimeUt = parser.nextText();
                                //Data for the Statistic
                                MainActivity.vecTime4taskUt.add(vecTimeUt);
                            }// if
                        } // if
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                } // switch
                eventType = parser.next();
            } // while
        } catch (Exception e) {
            System.out.println("ERROR ???: XmlParser.parseXmlPoolStatistic --> " + e);
        } // try
    } // parseXmlPoolStatistic
} // XmlParser
