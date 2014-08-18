package spader.main;

import spader.method.FBSpaderWork;
import spader.method.GPSpaderWork;
import spader.method.TWSpaderWork;

public class SpaderMain {

	public static void main(String[] args) {
		
		TWSpaderWork tw = new TWSpaderWork("file/TW_task1.txt", "");
		
		if(tw.product()){
			System.out.println("Twitter分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("Twitter分配任务失败，看来还要加班啊");
		}
		
		
		
		FBSpaderWork fw = new FBSpaderWork("file/FB_task.txt" , "file/content.txt");
		
		if(fw.product()){
			System.out.println("Facebook分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("Facebook分配任务失败，看来还要加班啊");
		}
		
		
		

		GPSpaderWork gw = new GPSpaderWork("file/GL_task.txt");
		
		if(gw.product()){
			System.out.println("Google+分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("Google+分配任务失败，看来还要加班啊");
		}
		
		
	}

}
