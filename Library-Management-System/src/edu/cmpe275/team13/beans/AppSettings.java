package edu.cmpe275.team13.beans;

import java.sql.Date;

import org.springframework.stereotype.Service;

@Service
public class AppSettings {
	
	private static AppSettings appSettings = null;
	private static Date appDate;
	
	private AppSettings(){
		AppSettings.appDate = new Date(new java.util.Date().getTime());
	}

	public static AppSettings getInstance() {
	if(appSettings == null)	{
			appSettings = new AppSettings();
		}
	return appSettings;
	}

	public void setAppDate(Date appDate) {
		AppSettings.appDate = appDate;
	}

	public Date getAppDate() {
		return appDate;
	}
}
