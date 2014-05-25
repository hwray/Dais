package com.stanford.dais; 


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.firebase.client.*;

public class Globals extends Application {
	
	public Presentation pres; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		String username = getUsername().replaceAll("[^A-Za-z0-9]", "");
		pres = new Presentation(username);
	}
	
	public void clearGlobals() {
		pres.reset();
	}
	
    public String getUsername(){
	    AccountManager manager = AccountManager.get(this);
	   
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	      possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");
	        if(parts.length > 0 && parts[0] != null)
	            return parts[0];
	        else
	            return null;
	    }else
	        return null;
	}
	
}