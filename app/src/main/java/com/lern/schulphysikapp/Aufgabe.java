package com.lern.schulphysikapp;

import java.util.Vector;

public class Aufgabe {
	private int test;
	private int aufgabe;
	private String imageAufgabe;
	private String imageLoesung = new String();
	private Vector hilfe = new Vector<String>();
	private String text = new String();
	private String time = new String();
    private String timeRequired = new String();
	private int zustand = 0;
	private int qualifikation;
	private int empfindung;
	private String avgUpCorrectness; // Trujillo 05/07/17
	private String avgUpFeeling; // Trujillo 13/07/17
	private String avgUpTime; // Trujillo 13/07/17

	public Aufgabe() {
	// Constructor without parameters	
	}

	public Aufgabe(int test, int aufgabe, String imageAufgabe, String hilfe, String iconLoesung, String Text, String time,
				   String timeRequired, int zustand, int qlfktn, int empfng, String avgCorr, String avgFeel, String avgTime) {
		// Constructor with parameters
		
		super();
		this.aufgabe = aufgabe;
		this.imageAufgabe = imageAufgabe;
		this.hilfe.add(hilfe);
		this.imageLoesung = iconLoesung;
		this.text = Text;
		this.time = time;
        this.timeRequired = timeRequired;
		this.zustand = zustand;
		this.qualifikation = qlfktn;
		this.test = test;
		this.empfindung = empfng;
		this.avgUpCorrectness = avgCorr;
		this.avgUpFeeling = avgFeel;
		this.avgUpTime = avgTime;
	}
	
	//Getters
	public int getAufgabe() {
		return aufgabe;
	}
	
	public String getImageAufgabe() {
		return (imageAufgabe.trim());
	}

	public String getImageLoesung() {
		return imageLoesung;
	}

	public Vector getHilfe() {
		return hilfe;
	}
	
	public String getText() {
		return text;
	}
	
	public String getTime(){
		return time;
	}

    public String getTimeRequired(){
        return timeRequired;
    }
	
	public int getZustand(){
		return zustand;
	}
	
	public int getQualifikation(){
		return qualifikation;
	}

	public int getEmpfindung(){
		return empfindung;
	}
	
	public int getTest(){
		return test;
	}

	public String getAvgUpCorrectness() { return avgUpCorrectness; }

	public String getAvgUpFeeling() { return avgUpFeeling; }

	public String getAvgUpTime() { return avgUpTime; }

	//Setters
	public void setImageAufgabe(String imageAufgabe) {
		String temp = imageAufgabe.replaceAll(" ", "");;
		this.imageAufgabe = temp.trim();
	}

	public void setImageLoesung(String imageLoesung) {
		this.imageLoesung = imageLoesung;
	}

	public void setAufgabe(int aufgabe) {
		this.aufgabe = aufgabe;
	}

	public void setHilfe(String hilfe) {
		this.hilfe.add(hilfe);
	}

	public void setText(String text){
		this.text = text;
	}
	
	public void setTime(String time){
		this.time = time;
	}

    public void setTimeRequired(String timeReq){
        this.timeRequired = timeReq;
    }
	
	public void setZustand(int zstndVal){
		// Indicates the different values from the Aufgabe:
		//  (StandBy = 0, Started = 1, TaskSolved = 2, Personal evaluated = 3, % evaluated = 4)
			this.zustand = zstndVal;
	}
	
	public void setQualifikation(int qlfktn){
		this.qualifikation = qlfktn;
	}

	public void setEmpfindung(int empfndng){
		this.empfindung = empfndng;
	}
	
	public void setTest(int tst) {
		this.test = tst;
	}

	public void setAvgUpCorrectness(String corr) { this.avgUpCorrectness = corr; }

	public void setAvgUpFeeling(String feel) { this.avgUpFeeling = feel; }

	public void setAvgUpTime(String time) { this.avgUpTime = time; }
}
