/**
 * 
 */
package com.iie.facebook.robot;

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

import com.iie.twitter.plantform.LogSys;
import com.iie.util.TxtReader;

/**
 * ָ�������б���̵߳ķַ���
 */
public class TaskDistributor {
	
	public static DefaultHttpClient httpclient;
	
	/**
	 * ���Է���
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
	
	}

	/**
	 * �� List �е���������ÿ���̣߳���ƽ�����䣬ʣ�ڵ����θ��Ӹ�ǰ����߳� ���ص������ж��ٸ�Ԫ�� (List) �ͱ������������ٸ������߳�
	 * 
	 * @param taskList
	 *            �����ɵ������б�
	 * @param threadCount
	 *            �߳���
	 * @return �б�����飬ÿ��Ԫ���д��и��߳�Ҫִ�е������б�
	 */
	@SuppressWarnings("unchecked")
	public static List[] distributeTasks(List taskList, int threadCount) {
		// ÿ���߳�����Ҫִ�е�������,���粻Ϊ�����ʾÿ���̶߳�����䵽����
		int minTaskCount = taskList.size() / threadCount;
		// ƽ�������ʣ�µ�����������Ϊ�����������������ӵ�ǰ����߳���
		int remainTaskCount = taskList.size() % threadCount;
		// ʵ��Ҫ�������߳���,��������̱߳����񻹶�
		// ��Ȼֻ��Ҫ������������ͬ�����Ĺ����̣߳�һ��һ��ִ��
		// �Ͼ�������ʵ�����̳߳أ������ò���Ԥ�ȳ�ʼ�������ߵ��߳�
		int actualThreadCount = minTaskCount > 0 ? threadCount
				: remainTaskCount;
		// Ҫ�������߳����飬�Լ�ÿ���߳�Ҫִ�е������б�
		List[] taskListPerThread = new List[actualThreadCount];
		int taskIndex = 0;
		// ƽ��������������ÿ���Ӹ�һ���̺߳��ʣ���������������� remainTaskCount
		// ��ͬ�ı�������Ȼ����ִ���иı� remainTaskCount ԭ��ֵ�������鷳
		int remainIndces = remainTaskCount;
		for (int i = 0; i < taskListPerThread.length; i++) {
			taskListPerThread[i] = new ArrayList();
			// ��������㣬�߳�Ҫ���䵽����������
			if (minTaskCount > 0) {
				for (int j = taskIndex; j < minTaskCount + taskIndex; j++) {
					taskListPerThread[i].add(taskList.get(j));
				}
				taskIndex += minTaskCount;
			}
			// ���绹��ʣ�µģ���һ��������߳���
			if (remainIndces > 0) {
				taskListPerThread[i].add(taskList.get(taskIndex++));
				remainIndces--;
			}
		}
		// ��ӡ����ķ������
		for (int i = 0; i < taskListPerThread.length; i++) {
			System.out.println("�߳� "
					+ i
					+ " ����������"
					+ taskListPerThread[i].size()
					+ " ����["
					+ ((runTask) taskListPerThread[i].get(0)).getTaskId()
					+ ","
					+ ((runTask) taskListPerThread[i].get(taskListPerThread[i].size() - 1))
							.getTaskId() + "]");
		}
		return taskListPerThread;
	}
}
