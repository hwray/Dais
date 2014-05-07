package com.stanford.dais; 

import android.app.Application;

class Globals extends Application {

	  private String myState;

	  public String getState(){
	    return myState;
	  }
	  public void setState(String s){
	    myState = s;
	  }
}