package spader.method;

import java.util.ArrayList;
import java.util.List;

import another.dao.TwitterSpaderDAO;
import spader.bean.SpaderBean;
import spader.bean.TaskBean;
import spader.util.SpaderProductMethod;
import spader.util.SpaderReadTask;
import spader.util.TWEncodeURL;

public class TWSpaderWork {

	private static List<TaskBean> tasklist;
	private static List<String> contentlist;
	private static List<String> keyuserlist;
	public TWSpaderWork(String taskfiledir, String contentfiledir){
		SpaderReadTask srt = new SpaderReadTask();
		tasklist = srt.readFromTxt(taskfiledir);
		contentlist = srt.readContentFromTxt(contentfiledir);
		keyuserlist = srt.readKeyUser();
	}
	
	public boolean product(){
		boolean resultStatus = true;
		
		SpaderProductMethod spm = new SpaderProductMethod();
		TwitterSpaderDAO sd = new TwitterSpaderDAO();
		sd.dropSpiderTable();
		List<Integer> accountIDlist =  new ArrayList<Integer>();
		if(tasklist.size()<=0){
			resultStatus = false;
			System.out.print("任务不正确，请检查输入的任务文件");
			return resultStatus;
		}
		
		for(TaskBean t : tasklist){
			
			accountIDlist = sd.getAccountID();
			t.setWebsite("twitter");
			
			TWEncodeURL tu = new TWEncodeURL(t.getMessage_url());
			t.setMessage_id(tu.getMessageID());
			t.setUser_id(tu.getUserID());
			
			List<SpaderBean> commentlist = spm.productComment_Tw(t, accountIDlist, contentlist);
			if(commentlist!=null){
				sd.insertToSpaderTable(commentlist);
			}
			
			
			List<SpaderBean> forwardlist = spm.productRetweet_Tw(t, accountIDlist);
			if(forwardlist!=null){
				sd.insertToSpaderTable(forwardlist);
			}
			
			
			List<SpaderBean> postlist = spm.productPost_Tw(t, accountIDlist, contentlist);
			if(postlist!=null){
				sd.insertToSpaderTable(postlist);
			}
			
//			List<SpaderBean> postlist = spm.productPostAT(t, accountIDlist, contentlist,keyuserlist);
//			if(postlist!=null){
//				sd.insertToSpaderTable(postlist);
//			}	
			
		}
		
	
		return resultStatus;
	}
	

	public static void main(String[] args) {
//		SpiderReadTask srt = new SpiderReadTask();
//		tasklist = srt.readFromTxt("E:\\2013-9-9_update\\task1.txt");

		TWSpaderWork sw = new TWSpaderWork("task/file/TW_task1.txt", "");
		
		if(sw.product()){
			System.out.println("分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("分配任务失败，看来还要加班啊");
		}
		
	}

}
