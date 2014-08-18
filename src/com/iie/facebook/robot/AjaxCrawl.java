package com.iie.facebook.robot;

import java.util.Vector;
import java.util.concurrent.*;

import org.apache.http.impl.client.DefaultHttpClient;

import spader.bean.Task;

import com.iie.facebook.robot.AdvanceLoginManager.logonItem;
import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.DbOperation;
import com.iie.twitter.tools.MulityInsertDataBase;
import com.iie.twitter.tools.WebOperationResult;

public abstract class AjaxCrawl {

	/**
	 * @param args
	 */

	public ExecutorService service = Executors.newCachedThreadPool();
	public DbOperation dboperation;
	
	public abstract boolean doWork(Task task);
	
	public String openLink(final DefaultHttpClient httpclient, final Task task, final logonItem lgitem, final int count, WebOperationResult webres) {
		String WebPageContent = null;
		Future<String> future = service.submit(new Callable<String>() {
			public String call() throws Exception {
				try {
					return WebOperationAjax.openLink(httpclient, task, lgitem, count);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}
		});
		
		try{
			WebPageContent = (String) future.get(20000, TimeUnit.MILLISECONDS);			
		}catch(TimeoutException ex){
			webres=WebOperationResult.TimeOut;
		}catch (Exception e) {
			e.printStackTrace();
			LogSys.nodeLogger.error(e.getMessage());
			webres=WebOperationResult.Fail;
			WebPageContent = null;
		}
		if(WebPageContent == null){
			
		}else{
			
		}
		return WebPageContent;

			
	}
	}
