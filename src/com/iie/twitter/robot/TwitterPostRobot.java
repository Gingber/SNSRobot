/**
 * 
 */
package com.iie.twitter.robot;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import spader.bean.Task;
import spader.bean.Task.TaskType;
import spader.dao.SpaderDao;

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.BasePath;
import com.iie.twitter.tools.ReadTxtFile;
import com.iie.twitter.tools.TwitterClientManager;
import com.iie.twitter.tools.WebOperationResult;

/**
 * @author Gingber
 *
 */
public class TwitterPostRobot extends AjaxCrawl {
	
	private static DefaultHttpClient httpclient;
	private static String proxyHost = "";
	private static int proxyPort = 0;
	
	public TwitterPostRobot(DefaultHttpClient httpclient) {		
		this.httpclient = httpclient;
	}
	
	public static void InitProxy() {
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
	
	public boolean InitHttpclientAndConnection(Task task) {
		TwitterClientManager cm = new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort(proxyHost, proxyPort);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000); 
		AdvanceLoginManager lgtest = new AdvanceLoginManager(httpclient);
		boolean loginflag = lgtest.trylogin(task.getAccountId());
		if(loginflag) {
			TwitterPostRobot  tpr = new TwitterPostRobot(httpclient);
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean doWork(Task task) {
		InitProxy();
		if(InitHttpclientAndConnection(task)) {
			int retryCount = 0;
			WebOperationResult webres = WebOperationResult.Success;
			String html = openLink(httpclient, task, retryCount, webres);
			if(html != null) {
				LogSys.nodeLogger.debug("Successfully sent a new tweet: [" + task.getContent() + "]");
				try {
					SpaderDao.updateToSpaderTable("tw_spadertask", task.getId(), "success");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			} else {
				LogSys.nodeLogger.debug("Failure sent a new tweet: [" + task.getContent() + "]");
				try {
					SpaderDao.updateToSpaderTable("tw_spadertask", task.getId(), "fail");
					SpaderDao.updateAccountTable("tw_account", "frozen", task.getAccountId());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			} 		
		} else {
			System.out.println("账号不可使用啦~");
			try {
				SpaderDao.updateToSpaderTable("tw_spadertask", task.getId(), "frozen");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TwitterClientManager cm = new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.180", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest = new AdvanceLoginManager(httpclient);
		lgtest.trylogin(0);
		
		TwitterPostRobot  tpr = new TwitterPostRobot(httpclient);
		
		Task task = new Task(TaskType.post, null, null, "hey, Good afternoon, I'm marker~", 0, 1);
		
		boolean  flag = tpr.doWork(task);
		if(flag) {
			System.out.println("我试验成功了~");
			System.exit(0);
		}
		

	}

}
