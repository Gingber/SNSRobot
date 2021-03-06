/**
 * 
 */
package com.iie.twitter.robot;

/**
 * @author Gingber
 *
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.stream.events.Comment;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.TwitterClientManager;
import com.iie.util.TxtReader;

/**
 * 指派任务列表给线程的分发器
 */
public class TaskDistributor {
	
	public static DefaultHttpClient httpclient;
	
	/**
	 * 测试方法
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.180", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest=new AdvanceLoginManager(httpclient);
		
		TwitterCommentRobot cr = new TwitterCommentRobot(httpclient);
		
		// 初始化要执行的任务列表
		List taskList = new ArrayList();
		List<String> fbIdList = new ArrayList<String>();
		
		try {
			fbIdList = TxtReader.loadVectorFromFile(new File("task/taskID.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < fbIdList.size(); i++) {
			taskList.add(new runTask(fbIdList.get(i)));
		}
		// 设定要启动的工作线程数为 4 个
		int threadCount = 4;
		List[] taskListPerThread = distributeTasks(taskList, threadCount);
		System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
		for (int i = 0; i < taskListPerThread.length; i++) {
			Thread workThread = new WorkThread(taskListPerThread[i], i);
			workThread.start();
		}
	}

	/**
	 * 把 List 中的任务分配给每个线程，先平均分配，剩于的依次附加给前面的线程 返回的数组有多少个元素 (List) 就表明将启动多少个工作线程
	 * 
	 * @param taskList
	 *            待分派的任务列表
	 * @param threadCount
	 *            线程数
	 * @return 列表的数组，每个元素中存有该线程要执行的任务列表
	 */
	@SuppressWarnings("unchecked")
	public static List[] distributeTasks(List taskList, int threadCount) {
		// 每个线程至少要执行的任务数,假如不为零则表示每个线程都会分配到任务
		int minTaskCount = taskList.size() / threadCount;
		// 平均分配后还剩下的任务数，不为零则还有任务依个附加到前面的线程中
		int remainTaskCount = taskList.size() % threadCount;
		// 实际要启动的线程数,如果工作线程比任务还多
		// 自然只需要启动与任务相同个数的工作线程，一对一的执行
		// 毕竟不打算实现了线程池，所以用不着预先初始化好休眠的线程
		int actualThreadCount = minTaskCount > 0 ? threadCount
				: remainTaskCount;
		// 要启动的线程数组，以及每个线程要执行的任务列表
		List[] taskListPerThread = new List[actualThreadCount];
		int taskIndex = 0;
		// 平均分配后多余任务，每附加给一个线程后的剩余数，重新声明与 remainTaskCount
		// 相同的变量，不然会在执行中改变 remainTaskCount 原有值，产生麻烦
		int remainIndces = remainTaskCount;
		for (int i = 0; i < taskListPerThread.length; i++) {
			taskListPerThread[i] = new ArrayList();
			// 如果大于零，线程要分配到基本的任务
			if (minTaskCount > 0) {
				for (int j = taskIndex; j < minTaskCount + taskIndex; j++) {
					taskListPerThread[i].add(taskList.get(j));
				}
				taskIndex += minTaskCount;
			}
			// 假如还有剩下的，则补一个到这个线程中
			if (remainIndces > 0) {
				taskListPerThread[i].add(taskList.get(taskIndex++));
				remainIndces--;
			}
		}
		// 打印任务的分配情况
		for (int i = 0; i < taskListPerThread.length; i++) {
			System.out.println("线程 "
					+ i
					+ " 的任务数："
					+ taskListPerThread[i].size()
					+ " 区间["
					+ ((runTask) taskListPerThread[i].get(0)).getTaskId()
					+ ","
					+ ((runTask) taskListPerThread[i].get(taskListPerThread[i].size() - 1))
							.getTaskId() + "]");
		}
		return taskListPerThread;
	}
}
