/**
 * 
 */
package com.iie.twitter.robot;

import java.util.List;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import another.dao.TwitterSpaderDAO;

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.BasePath;
import com.iie.twitter.tools.ReadTxtFile;

import spader.bean.Task;
import spader.method.TWSpaderWork;


/**
 * @author Gingber
 *
 */
public class TwitterRobot {
	
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
		TWSpaderWork sw = new TWSpaderWork(task, content);
		
		if(sw.product()){
			System.out.println("分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("分配任务失败，看来还要加班啊");
		}
		
		
		TwitterPostRobot tpr = new TwitterPostRobot(null);
		TwitterRetweetRobot trr = new TwitterRetweetRobot(null);
		TwitterCommentRobot tcr = new TwitterCommentRobot(null);
		
		//String conSQL = "where message_id='471063229846659072' and verb='comment'";
		String conSQL = "";
		List<Task> tasks = TwitterSpaderDAO.getTask(conSQL);
		
		for(int i = 0; i < tasks.size(); i++) {
			switch(tasks.get(i).ownType) {
				case post:
					tpr.doWork(tasks.get(i));
					break;
				case retweet:
					trr.doWork(tasks.get(i));
					break;
				case comment:
					tcr.doWork(tasks.get(i));
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
