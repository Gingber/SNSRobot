package spader.method;

import java.util.ArrayList;
import java.util.List;

import another.dao.FacebookSpaderDAO;
import spader.bean.SpaderBean;
import spader.bean.TaskBean;
import spader.util.FBEncodeURL;
import spader.util.SpaderProductMethod;
import spader.util.SpaderReadTask;


public class FBSpaderWork {

	private static List<TaskBean> tasklist;
	private static List<String> contentlist;
	public FBSpaderWork(String taskfiledir, String contentfiledir){
		SpaderReadTask srt = new SpaderReadTask();
		tasklist = srt.readFromTxt(taskfiledir);
		contentlist = srt.readContentFromTxt(contentfiledir);
	}
	
	public boolean product(){
		boolean resultStatus = true;
		
		SpaderProductMethod spm = new SpaderProductMethod();
		FacebookSpaderDAO sd = new FacebookSpaderDAO();
		sd.dropSpiderTable();
		List<Integer> accountIDlist =  new ArrayList<Integer>();
		if(tasklist.size()<=0){
			resultStatus = false;
			System.out.print("任务不正确，请检查输入的任务文件");
			return resultStatus;
		}
	
		
		for(TaskBean t : tasklist){
			
			accountIDlist = sd.getAccountID();
			
			t.setWebsite("facebook");
			FBEncodeURL fu = new FBEncodeURL(t.getMessage_url());
			t.setMessage_id(fu.getMessageID());
			t.setUser_id(fu.getUserID());
			
			List<SpaderBean> commentlist = spm.productComment_FB(t, accountIDlist,contentlist);
			if(commentlist!=null){
				sd.insertToSpaderTable(commentlist);
			}
			
			
			List<SpaderBean> forwardlist = spm.productRetweet_FB(t, accountIDlist,contentlist);
			if(forwardlist!=null){
				sd.insertToSpaderTable(forwardlist);
			}
			
			
			List<SpaderBean> postlist = spm.productPost_FB(t, accountIDlist, contentlist);
			if(postlist!=null){
				sd.insertToSpaderTable(postlist);
			}	
			
			List<SpaderBean> likelist = spm.productLike_FB(t, accountIDlist);
			if(likelist!=null){
				sd.insertToSpaderTable(likelist);
			}	
			
		}
		
	
		return resultStatus;
	} 
	
	
	public static void main(String[] args) {
//		SpiderReadTask srt = new SpiderReadTask();
//		tasklist = srt.readFromTxt("E:\\2013-9-9_update\\task1.txt");

		FBSpaderWork sw = new FBSpaderWork("task/file/FB_task.txt", "");
		
		if(sw.product()){
			System.out.println("分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("分配任务失败，看来还要加班啊");
		}
		
	}



}
