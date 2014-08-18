package another.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import spader.bean.SpaderBean;
import spader.bean.Task;
import spader.bean.Task.TaskType;
import spader.dao.DAO;

public class FacebookSpaderDAO {


	private static String spaderTableName = "fb_spadertask";

	private String accountTableName = "fb_account";
	public List<Integer> getAccountID(){
		List<Integer> accountIDlist = new ArrayList<Integer>();
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
			PreparedStatement pst=con.prepareStatement("select id from " + accountTableName + " where health = true ORDER BY rand()");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				accountIDlist.add(rs.getInt("id"));
			}
			System.out.println("�õ��˺ųɹ�");
			rs.close();
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return accountIDlist;
	}
	
	public static List<Task> getTask() {
		List<Task> tasklist = new ArrayList<Task>();
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
			PreparedStatement pst=con.prepareStatement("select id, message_id, verb, account_id, content from " + spaderTableName + "");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				Task task = new Task();
				if(rs.getString("verb").equals("post")) {
					task.setOwnType(TaskType.post);
					task.setId(rs.getInt("id"));
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				} else if(rs.getString("verb").equals("comment")) {
					task.setOwnType(TaskType.comment);
					task.setId(rs.getInt("id"));
					task.setTargetString(rs.getString("message_id"));
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				} else if(rs.getString("verb").equals("retweet")) {
					task.setOwnType(TaskType.retweet);
					task.setId(rs.getInt("id"));
					task.setTargetString(rs.getString("message_id"));
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				} else if(rs.getString("verb").equals("like")) {
					task.setOwnType(TaskType.like);
					task.setId(rs.getInt("id"));
					task.setTargetString(rs.getString("message_id"));
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				}
				
			}
			System.out.println("�õ��˺ųɹ�");
			rs.close();
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tasklist;
		
	}	
	
	
	
	public void insertToSpaderTable(List<SpaderBean> spaderlist){
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
			if(!isExist(spaderTableName)){
				createSpiderTable();
			}
			
			con.setAutoCommit(false);
			PreparedStatement pst=con.prepareStatement("insert into "+spaderTableName+""
					+ "(website,message_url,message_id,verb,account_id, status, content, create_time) values(?,?,?,?,?,?,?,?)");
			for(SpaderBean s : spaderlist){
				pst.setString(1, s.getWebsite());
				pst.setString(2, s.getMessage_url());
				pst.setString(3, s.getMessage_id());
				pst.setString(4, s.getVerb());
				pst.setInt(5, s.getAccount_id());
				pst.setString(6, s.getStatus());
				pst.setString(7, s.getContent());
				pst.setTimestamp(8, s.getCreate_time());
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("����ɹ�");
			con.setAutoCommit(true);
			System.out.println("�ύ�ɹ�");
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean isExist(String tableName) {
		boolean result = false;
		try {
			DAO dao = new DAO();
			Connection con = dao.con;
			DatabaseMetaData meta = (DatabaseMetaData) con.getMetaData();
			ResultSet set = meta.getTables(null, null, tableName, null);
			while (set.next()) {
				result = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public void createSpiderTable(){
		DAO dao = new DAO();
		Connection con = dao.con;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sql = "create table "+spaderTableName+" (`id` int(10) NOT NULL AUTO_INCREMENT,website varchar(255),message_url varchar(255),"
					+ "message_id varchar(255),verb varchar(255),account_id int,status varchar(255),content text,create_time Timestamp NULL DEFAULT NULL, "
					+ "finish_time Timestamp NULL DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			stmt.executeUpdate(sql);
					
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally{

			try {
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	
	public void dropSpiderTable(){
		DAO dao = new DAO();
		Connection con = dao.con;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sqldelect = "DROP TABLE IF EXISTS "+spaderTableName+"";
			stmt.executeUpdate(sqldelect);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally{

			try {
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
