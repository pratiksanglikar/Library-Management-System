package edu.cmpe275.team13.beans;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

@Service
public class AppSettings {
	
	private static AppSettings appSettings = null;
	private static Timestamp appDate;
	
	private AppSettings(){
		AppSettings.appDate = new Timestamp(new java.util.Date().getTime());
	}

	public static AppSettings getInstance() {
	if(appSettings == null)	{
			appSettings = new AppSettings();
		}
	return appSettings;
	}

	public void setAppDate(Timestamp appDate) {
		AppSettings.appDate = appDate;
	}

	public Timestamp getAppDate() {
		return appDate;
	}
}
