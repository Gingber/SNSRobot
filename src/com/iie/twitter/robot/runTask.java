package com.iie.twitter.robot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.iie.twitter.plantform.LogSys;
import com.iie.util.TxtReader;

/**
 * Ҫִ�е����񣬿���ִ��ʱ�ı�����ĳ��״̬���������ĳ������ ��������������״̬�����������У���ɣ�Ĭ��Ϊ����̬ Ҫ��һ�����ƣ���Ϊ Task
 * ����״̬��Ǩ�ļ���������֮����UI����ʾ
 */
public class runTask {
	public static final int READY = 0;
	public static final int RUNNING = 1;
	public static final int FINISHED = 2;
	@SuppressWarnings("unused")
	private int status;
	// ����һ�����������ҵ����ı��������ڱ�ʶ����
	private String taskId;

	// ����ĳ�ʼ������
	public runTask(String taskId) {
		this.status = READY;
		this.taskId = taskId;
	}

	/**
	 * ִ������
	 */
	public void execute(ArrayList<String> comments) {
		// ����״̬Ϊ������
		setStatus(runTask.RUNNING);
		System.out.println("��ǰ�߳� ID �ǣ�" + Thread.currentThread().getName()
				+ " | ���� ID �ǣ�" + this.taskId);
		
		// ����һ����ʱ
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// ִ����ɣ���״̬Ϊ���
		setStatus(FINISHED);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}
}
