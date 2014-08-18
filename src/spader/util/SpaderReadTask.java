package spader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import spader.bean.TaskBean;
import spader.dao.DAO;

public class SpaderReadTask {

	public static void main(String[] args) {//
		SpaderReadTask srt = new SpaderReadTask();
		List<TaskBean> tasklist = srt.readFromTxt("task/file/task1.txt");
		System.out.println(tasklist.size());
		List<String> contentlist = srt.readContentFromTxt("task/file/twcomments.txt");
		System.out.println(contentlist.size());
	}
	
	private File file = null;
	private BufferedReader br = null;
	public List<TaskBean> readFromTxt(String filedir){
		List<TaskBean> tasklist = new ArrayList<TaskBean>();
		
		file = new File(filedir);
		if (!file.exists() != false) {
			System.out.println("任务文件路径不正确，请检查后再次执行");
			return null;
		}
		
		try {
			FileReader fr = new FileReader(filedir);
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] strs = line.split(",");
				TaskBean tb = new TaskBean();
				if(strs.length != 5){
					System.out.println("strs.length 等于 "+strs.length +"***  按逗号拆分一行任务失败");
				}
				tb.setMessage_url(strs[0]);				
				tb.setComment(Integer.parseInt(strs[1]));
				tb.setRetweet(Integer.parseInt(strs[2]));
				tb.setPost(Integer.parseInt(strs[3]));
				tb.setLike(Integer.parseInt(strs[4]));
				tasklist.add(tb);
			}
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return tasklist;
	}
	
	public List<String> readContentFromTxt(String filedir){
		List<String> contentlist = new ArrayList<String>();
		
		file = new File(filedir);
		if (!file.exists() != false) {
			System.out.println("任务文件路径不正确，请检查后再次执行");
			return null;
		}
		
		try {	
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				contentlist.add(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return contentlist;
	}
	
	public List<String> readKeyUser(){
		List<String> keyuserlist = new ArrayList<String>();
		
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
			PreparedStatement pst=con.prepareStatement("select user_name from key_user");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				keyuserlist.add(rs.getString("user_name"));
			}
			System.out.println("得到KeyUser账号成功");
			rs.close();
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyuserlist;
	}
	
	
}
