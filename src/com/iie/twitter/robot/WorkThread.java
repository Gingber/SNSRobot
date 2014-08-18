package com.iie.twitter.robot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iie.util.TxtReader;

/**
 * 自定义的工作线程，持有分派给它执行的任务列表
 */
public class WorkThread extends Thread {
	// 本线程待执行的任务列表，你也可以指为任务索引的起始值
	private List<runTask> taskList = null;
	@SuppressWarnings("unused")
	private int threadId;

	/**
	 * 构造工作线程，为其指派任务列表，及命名线程 ID
	 * 
	 * @param taskList
	 *            欲执行的任务列表
	 * @param threadId
	 *            线程 ID
	 */
	@SuppressWarnings("unchecked")
	public WorkThread(List taskList, int threadId) {
		this.taskList = taskList;
		this.threadId = threadId;
	}

	/**
	 * 执行被指派的所有任务
	 */
	public void run() {
		
		System.out.println("Auto Comment Robot is Starting~");
//		while(true){
//			// do something;
//		}
		
		for (runTask task : taskList) {
			ArrayList<String> comments = new ArrayList<String>();
			
			if(task.getTaskId().equals("469004139318358017")) {
				try {
					comments = TxtReader.loadVectorFromFile(new File("task/469004139318358017_dialog.txt"), "utf-8");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if(task.getTaskId().equals("468857753755451395")) {
				try {
					comments = TxtReader.loadVectorFromFile(new File("task/468857753755451395_dialog.txt"), "utf-8");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			task.execute(comments);
		}
	}
}