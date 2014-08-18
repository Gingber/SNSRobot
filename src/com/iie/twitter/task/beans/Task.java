package com.iie.twitter.task.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public  class Task implements Serializable{
	public enum TaskType {
		comment, post, retweet;
		private static final Map<String, TaskType> stringToEnum = new HashMap<String, TaskType>();
	    static {
	        // Initialize map from constant name to enum constant
	        for(TaskType t : values()) {
	            stringToEnum.put(t.toString(), t);
	        }
	    }
	    
	    // Returns Blah for string, or null if string is invalid
	    public static TaskType fromString(String symbol) {
	        return stringToEnum.get(symbol);
	    }
	}
	public enum MainType{
		KeyUser,KeyWord,Topic,Urgent,Normal
	}
	
	public TaskType ownType;
	public String targetString;
	public String resultString;
	public int accountId;
	public MainType mainType;
	public int mainTypeID;
	public boolean isTrack=false;
	public int taskTrackID;
	public int pageCount;
	public String targetTableName;
	public String addParameter;

	public Task(TaskType ownType, String targetString, String resultString, int accountId) {
		this();
		this.ownType = ownType;
		this.mainType=MainType.Normal;
		this.targetString = targetString;
		this.resultString = resultString;
		this.accountId = accountId;
	}
	public Task(){
		super();
		this.pageCount=-1;
	}

	public String getURL(){
		if(ownType==TaskType.comment){
			return "/"+targetString+"/comment";
		}else if(ownType==TaskType.post){
			return "/"+targetString+"/post";
		}else if(ownType==TaskType.retweet){
			return "/"+targetString+"/retweet";
		}else{
			return "ERROR";
		}
	}
	public TaskType getOwnType() {
		return ownType;
	}
	public void setOwnType(TaskType ownType) {
		this.ownType = ownType;
	}
	public String getTargetString() {
		return targetString;
	}
	public void setTargetString(String targetString) {
		this.targetString = targetString;
	}
	public String getResultString() {
		return resultString;
	}
	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String toString(){		
		return "["+targetString+"-"+ownType+"]";		
	}
	public String TaskTOString(){
		StringBuffer sb=new StringBuffer();
		sb.append("<type>");
		sb.append(ownType);
		sb.append("</type>");		
		sb.append("<value>");
		sb.append(targetString);
		sb.append("</value>");
		sb.append("<isTrack>");
		sb.append(isTrack);
		sb.append("</isTrack>");
		sb.append("<taskTrackID>");
		sb.append(taskTrackID);
		sb.append("</taskTrackID>");
		sb.append("<MainType>");
		sb.append(mainType);
		sb.append("</MainType>");
		sb.append("<MainTypeID>");
		sb.append(this.getMainTypeID());
		sb.append("</MainTypeID>");
		sb.append("<PageCount>");
		sb.append(this.getPageCount());
		sb.append("</PageCount>");
		sb.append("<TargetTableName>");
		sb.append(this.getTargetTableName());
		sb.append("</TargetTableName>");
		sb.append("<AddParameter>");
		sb.append(this.getAddParameter());
		sb.append("</AddParameter>");
		return sb.toString();		
	}
	public String TaskToFileName(){
		StringBuffer sb=new StringBuffer();
		sb.append(this.getTargetString());
		sb.append("-");
		sb.append(this.getOwnType());
		sb.append("-");
		sb.append(this.getTaskTrackID());
		sb.append("-");
		sb.append(this.getMainTypeID());
		return sb.toString();		
	}
	
	public void StringToTask(String src){
		
		
	}
	public boolean isTrack() {
		return isTrack;
	}
	public void setTrack(boolean isTrack) {
		this.isTrack = isTrack;
	}
	public int getTaskTrackID() {
		return taskTrackID;
	}
	public void setTaskTrackID(int taskTrackID) {
		this.taskTrackID = taskTrackID;
	}
	public MainType getMainType() {
		return mainType;
	}
	public void setMainType(MainType mainType) {
		this.mainType = mainType;
	}
	public int getMainTypeID() {
		return mainTypeID;
	}
	public void setMainTypeID(int mainTypeID) {
		this.mainTypeID = mainTypeID;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public String getTargetTableName() {
		return targetTableName;
	}
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}
	public String getAddParameter() {
		return addParameter;
	}
	public void setAddParameter(String addParameter) {
		this.addParameter = addParameter;
	}

	
}
