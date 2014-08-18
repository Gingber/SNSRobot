package spader.method;

import java.util.List;

import spader.bean.SpaderBean;
import spader.bean.TaskBean;
import spader.util.SpaderProductMethod;
import spader.util.SpaderReadTask;
import spader.util.TWEncodeURL;
import another.dao.GoogleSpaderDAO;
import another.dao.TwitterSpaderDAO;

public class GPSpaderWork {

	private static List<TaskBean> tasklist;
	private static List<String> contentlist;
	public GPSpaderWork(String taskfiledir){
		SpaderReadTask srt = new SpaderReadTask();
		tasklist = srt.readFromTxt(taskfiledir);
		contentlist = srt.readContentFromTxt("task/file/gpcomments.txt");
	}
	
	public boolean product(){
		boolean resultStatus = true;
		
		SpaderProductMethod spm = new SpaderProductMethod();
		GoogleSpaderDAO sd = new GoogleSpaderDAO();
		sd.dropSpiderTable();
		List<Integer> accountIDlist = sd.getAccountID();
		if(tasklist.size()<=0){
			resultStatus = false;
			System.out.print("任务不正确，请检查输入的任务文件");
			return resultStatus;
		}
		
		
		
		
		for(TaskBean t : tasklist){
			
			t.setWebsite("google+");
					
			
			List<SpaderBean> commentlist = spm.productComment_GL(t, accountIDlist,contentlist);
			if(commentlist!=null){
				sd.insertToSpaderTable(commentlist);
			}
			
			
			List<SpaderBean> forwardlist = spm.productRetweet_GL(t, accountIDlist,contentlist);
			if(forwardlist!=null){
				sd.insertToSpaderTable(forwardlist);
			}
			
			
			List<SpaderBean> postlist = spm.productPost_GL(t, accountIDlist, contentlist);
			if(postlist!=null){
				sd.insertToSpaderTable(postlist);
			}	
			
			List<SpaderBean> likelist = spm.productLike_GL(t, accountIDlist);
			if(likelist!=null){
				sd.insertToSpaderTable(likelist);
			}	
			
		}
		
	
		return resultStatus;
	} 
	
	
	public static void main(String[] args) {
//		SpiderReadTask srt = new SpiderReadTask();
//		tasklist = srt.readFromTxt("E:\\2013-9-9_update\\task1.txt");

		GPSpaderWork sw = new GPSpaderWork("task/file/GL_task.txt");
		
		if(sw.product()){
			System.out.println("分配任务成功，可以查看spader表啦");
		}else{
			System.out.println("分配任务失败，看来还要加班啊");
		}
		
	}


}
