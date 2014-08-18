/**
 * 
 */
package com.iie.facebook.robot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import spader.bean.Task;
import spader.bean.Task.TaskType;
import spader.dao.SpaderDao;

import com.iie.facebook.robot.AdvanceLoginManager.logonItem;
import com.iie.facebook.tools.BasePath;
import com.iie.facebook.tools.ReadTxtFile;
import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.WebOperationResult;
import com.iie.util.TxtReader;
/**
 * @author Gingber
 *
 */
public class FacebookPostRobot extends AjaxCrawl{
	
	private static DefaultHttpClient httpclient;
	private static String proxyHost = "";
	private static int proxyPort = 0;
	
	public FacebookPostRobot(DefaultHttpClient httpclient) {		
		this.httpclient = httpclient;
	}
	
	public static void InitConfig() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.proxyHost")){
				String res = t.substring(t.indexOf('=') + 1);
				proxyHost = res;
			}
			
			if (t.startsWith("http.proxyPort")) {
				String res = t.substring(t.indexOf('=') + 1);
				proxyPort = Integer.parseInt(res);
			}
		}
	}
	
	public logonItem InitHttpclientAndConnection(Task task) {
		FacebookClientManager cm = new FacebookClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort(proxyHost, proxyPort);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest = new AdvanceLoginManager(httpclient);
		logonItem lgitem = lgtest.trylogin(task.getAccountId());
		if(lgitem != null) {
			FacebookPostRobot  tpr = new FacebookPostRobot(httpclient);
			return lgitem;
		} else {
			return null;
		}
		
	}
	
	public boolean doWork(Task task) {
		InitConfig();
		logonItem lgitem = InitHttpclientAndConnection(task);
		if(lgitem != null) {
			int retryCount = 0;
			WebOperationResult webres = WebOperationResult.Success;
			String html = openLink(httpclient, task, lgitem, retryCount, webres);
			if(html != null && html.contains(task.getTargetString())) {
				LogSys.nodeLogger.debug("Successfully sent a new comment: [" + task.getResultString() + "] "
						+ "to [" + task.getTargetString() + "]");
				try {
					SpaderDao.updateToSpaderTable("fb_spadertask", task.getId(), "success");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				LogSys.nodeLogger.debug("Failure sent a new comment: [" + task.getResultString() + "] "
						+ "to [" + task.getTargetString() + "]");
				System.out.println("此账号已被临时阻止执行此操作~");
				
				try {
					SpaderDao.updateToSpaderTable("fb_spadertask", task.getId(), "fail");
					SpaderDao.updateAccountTable("fb_account", "frozen", task.getAccountId());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("账号不可使用啦~");
			return false;
		}
		
		return true;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
