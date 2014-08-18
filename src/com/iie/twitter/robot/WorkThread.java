package com.iie.twitter.robot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iie.util.TxtReader;

/**
 * �Զ���Ĺ����̣߳����з��ɸ���ִ�е������б�
 */
public class WorkThread extends Thread {
	// ���̴߳�ִ�е������б���Ҳ����ָΪ������������ʼֵ
	private List<runTask> taskList = null;
	@SuppressWarnings("unused")
	private int threadId;

	/**
	 * ���칤���̣߳�Ϊ��ָ�������б��������߳� ID
	 * 
	 * @param taskList
	 *            ��ִ�е������б�
	 * @param threadId
	 *            �߳� ID
	 */
	@SuppressWarnings("unchecked")
	public WorkThread(List taskList, int threadId) {
		this.taskList = taskList;
		this.threadId = threadId;
	}

	/**
	 * ִ�б�ָ�ɵ���������
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