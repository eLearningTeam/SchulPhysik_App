package com.lern.schulphysikapp;

import java.util.Vector;

public class Pool {
	private int poolTest;
	private int poolAufgb;
	private String imagePool;
	private String imagePoolLsng = new String();
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

	public Pool() {
	// Constructor without parameters	
	}

	public Pool(int poolTest, int poolAufgb, String imagePool, String hilfe, String iconPoolLsng, String Text, String time,
				String timeRequired, int zustand, int qlfktn, int empfng, String avgCorr, String avgFeel, String avgTime) {
		// Constructor with parameters
		
		super();
		this.poolAufgb = poolAufgb;
		this.imagePool = imagePool;
		this.hilfe.add(hilfe);
		this.imagePoolLsng = iconPoolLsng;
		this.text = Text;
		this.time = time;
        this.timeRequired = timeRequired;
		this.zustand = zustand;
		this.qualifikation = qlfktn;
		this.poolTest = poolTest;
		this.empfindung = empfng;
		this.avgUpCorrectness = avgCorr;
		this.avgUpFeeling = avgFeel;
		this.avgUpTime = avgTime;
	}
	
	//Getters
	public int getPoolAufgb() {
		return poolAufgb;
	}
	
	public String getImagePool() {
		return (imagePool.trim());
	}

	public String getImageLoesung() {
		return imagePoolLsng;
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

    public String getTimeRequired() {return timeRequired;}
	
	public int getZustand(){
		return zustand;
	}
	
	public int getQualifikation(){
		return qualifikation;
	}

	public int getEmpfindung(){
		return empfindung;
	}

	public int getPoolTest(){
		return poolTest;
	}

	public String getAvgUpCorrectness() { return avgUpCorrectness; }

	public String getAvgUpFeeling() { return avgUpFeeling; }

	public String getAvgUpTime() { return avgUpTime; }
	
	//Setters
	public void setImagePool(String imagePool) {
		String temp = imagePool.replaceAll(" ", "");;
		this.imagePool = temp.trim();
	}

	public void setImagePoolLsng(String imageLoesung) {
		this.imagePoolLsng = imageLoesung;
	}

	public void setPoolAufgb(int poolAfgb) {
		this.poolAufgb = poolAfgb;
	}

	public void setHilfe(String hilfe) {
		this.hilfe.add(hilfe);
	}

	public void setText(String text){
		this.text = text;
	}
	
	public void setTime(String time){this.time = time;}

    public void setTimeRequired(String timeReq){this.timeRequired = timeReq;}
	
	public void setZustand(int zstndVal){
		// Indicates the different values from the Pool:
		//  (StandBy = 0, Started = 1, TaskSolved = 2, Personal evaluated = 3, % evaluated = 4)
			this.zustand = zstndVal;
	}
	
	public void setQualifikation(int qlfktn){
		this.qualifikation = qlfktn;
	}

	public void setEmpfindung(int empfdng){
		this.empfindung = empfdng;
	}
	
	public void setPoolTest(int poolTst) {
		this.poolTest = poolTst;
	}

	public void setAvgUpCorrectness(String corr) { this.avgUpCorrectness = corr; }

	public void setAvgUpFeeling(String feel) { this.avgUpFeeling = feel; }

	public void setAvgUpTime(String time) { this.avgUpTime = time; }
}

