/**
 * 
 */
package com.iie.facebook.robot;

import java.util.List;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import spader.bean.Task;
import spader.method.FBSpaderWork;
import another.dao.FacebookSpaderDAO;
import another.dao.TwitterSpaderDAO;

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.BasePath;
import com.iie.twitter.tools.ReadTxtFile;


/**
 * @author Gingber
 *
 */
public class FacebookRobot {

	private static DefaultHttpClient httpclient;
	private static String task = "";
	private static String content = "";
	
	public static void InitTask() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.task")){
				String res = t.substring(t.indexOf('=') + 1);
				task = res;
			}
			
			if (t.startsWith("http.content")) {
				String res = t.substring(t.indexOf('=') + 1);
				content = res;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		InitTask();
		FBSpaderWork sw = new FBSpaderWork(task, content);
		
		if(sw.product()){
			System.out.println("分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("分配任务失败，看来还要加班啊");
		}
		
		FacebookPostRobot fbpr = new FacebookPostRobot(null);
		FacebookShareRobot fbsr = new FacebookShareRobot(null);
		FacebookCommentRobot fbcr = new FacebookCommentRobot(null);
		FacebookLikeRobot fblr = new FacebookLikeRobot(null);
		
		//String conSQL = "where message_id='471063229846659072' and verb='comment'";
		String conSQL = "";
		List<Task> tasks = FacebookSpaderDAO.getTask();
		
		for(int i = 0; i < tasks.size(); i++) {
			switch(tasks.get(i).ownType) {
				case post:
					fbpr.doWork(tasks.get(i));
					break;
				case retweet:
					fbsr.doWork(tasks.get(i));
					break;
				case comment:
					fbcr.doWork(tasks.get(i));
					break;
				case like:
					fblr.doWork(tasks.get(i));
					break;
				default: {
					LogSys.nodeLogger.error("未知的TaskType数据类型 exit");
					break;
				}	
			}
		}
		
		System.exit(0);

	}
}
