/**
 * 
 */
package com.iie.twitter.robot;

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

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.BasePath;
import com.iie.twitter.tools.ReadTxtFile;
import com.iie.twitter.tools.TwitterClientManager;
import com.iie.twitter.tools.WebOperationResult;
import com.iie.util.TxtReader;

/**
 * @author Gingber
 *
 */
public class TwitterCommentRobot extends AjaxCrawl {

	private static DefaultHttpClient httpclient;
	private static String proxyHost = "";
	private static int proxyPort = 0;
	
	public TwitterCommentRobot(DefaultHttpClient httpclient) {		
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
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest = new AdvanceLoginManager(httpclient);
		boolean loginflag = lgtest.trylogin(task.getAccountId());
		if(loginflag) {
			TwitterCommentRobot  tpr = new TwitterCommentRobot(httpclient);
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
				LogSys.nodeLogger.debug("Successfully sent a new comment: [" + task.getContent() + "] "
						+ "to [" + task.getTargetString() + "]");
				try {
					SpaderDao.updateToSpaderTable("tw_spadertask", task.getId(), "success");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			} else {
				LogSys.nodeLogger.debug("Failure sent a new comment: [" + task.getContent() + "] "
						+ "to [" + task.getTargetString() + "]");
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

		ArrayList<String> list = new ArrayList<String>();
		try {
			list = TxtReader.loadVectorFromFile(new File("task/file/twcomments.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "https://twitter.com/kanzhongguo/status/470353551051329536";
		String messgeId = url.substring(url.lastIndexOf("/")+1, url.length());
		String userName = url.substring("https://twitter.com/".length()).split("/")[0];

		
		for(int i = 0; i < list.size(); i++) {

			/*try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			TwitterCommentRobot  tpr = new TwitterCommentRobot(httpclient); 
			
			Task task = new Task(TaskType.comment, messgeId, "@" + userName + list.get(i), null, i%9 + 1, 1);
			
			boolean  flag = tpr.doWork(task);
			if(flag) {
				System.out.println("我试验成功了~");
			}
		}
		System.exit(0);
		
	}

}
