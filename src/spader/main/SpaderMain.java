package spader.main;

import spader.method.FBSpaderWork;
import spader.method.GPSpaderWork;
import spader.method.TWSpaderWork;

public class SpaderMain {

	public static void main(String[] args) {
		
		TWSpaderWork tw = new TWSpaderWork("file/TW_task1.txt", "");
		
		if(tw.product()){
			System.out.println("Twitter��������ɹ������Բ鿴spader����");
		}else{
			System.out.println("Twitter��������ʧ�ܣ�������Ҫ�Ӱడ");
		}
		
		
		
		FBSpaderWork fw = new FBSpaderWork("file/FB_task.txt" , "file/content.txt");
		
		if(fw.product()){
			System.out.println("Facebook��������ɹ������Բ鿴spader����");
		}else{
			System.out.println("Facebook��������ʧ�ܣ�������Ҫ�Ӱడ");
		}
		
		
		

		GPSpaderWork gw = new GPSpaderWork("file/GL_task.txt");
		
		if(gw.product()){
			System.out.println("Google+��������ɹ������Բ鿴spader����");
		}else{
			System.out.println("Google+��������ʧ�ܣ�������Ҫ�Ӱడ");
		}
		
		
	}

}
