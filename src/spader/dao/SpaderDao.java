package spader.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;




import spader.bean.SpaderBean;
import spader.bean.Task;
import spader.bean.Task.TaskType;

public class SpaderDao {
	private static String spaderTableName = "spadertask";

	private String accountTableName = "crawlaccount";
	public List<Integer> getAccountID(){
		List<Integer> accountIDlist = new ArrayList<Integer>();
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
			PreparedStatement pst=con.prepareStatement("select id from "+accountTableName+"");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				accountIDlist.add(rs.getInt("id"));
			}
			System.out.println("得到账号成功");
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
			PreparedStatement pst=con.prepareStatement("select message_id, verb, account_id, content from "+spaderTableName+"");
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				Task task = new Task();
				if(rs.getString("verb").equals("post")) {
					task.setOwnType(TaskType.post);
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					//task.set
					tasklist.add(task);
				} else if(rs.getString("verb").equals("comment")) {
					task.setOwnType(TaskType.comment);
					task.setTargetString(rs.getString("message_id"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				} else if(rs.getString("verb").equals("retweet")) {
					task.setOwnType(TaskType.retweet);
					task.setTargetString(rs.getString("message_id"));
					task.setContent(rs.getString("content"));
					task.setAccountId(rs.getInt("account_id"));
					tasklist.add(task);
				}
				
			}
			System.out.println("得到账号成功");
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
				createSpiderTable(spaderTableName);
			}
			
			con.setAutoCommit(false);
			PreparedStatement pst=con.prepareStatement("insert into "+spaderTableName+""
					+ "(website,message_url,message_id varchar(255),verb,account_id, status, content, create_time) values(?,?,?,?,?,?,?,?)");
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
			System.out.println("插入成功");
			con.setAutoCommit(true);
			System.out.println("提交成功");
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
	
	public static void insertToKeyUserTable(List<String> keyusers){
		DAO dao = new DAO();
		Connection con = dao.con;
		try {
		
			con.setAutoCommit(false);
			PreparedStatement pst=con.prepareStatement("insert into message_visit "
					+ "(message_id) values(?)");
			for(int i = 0; i < keyusers.size(); i++) {
				pst.setString(1, keyusers.get(i));
				pst.addBatch();
			}
			pst.executeBatch();
			System.out.println("插入成功");
			con.setAutoCommit(true);
			System.out.println("提交成功");
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
	
	public static void updateToSpaderTable(String tableName, int id, String status) throws SQLException {
		DAO dao = new DAO();
		Connection con = dao.con;
		PreparedStatement pst = null;
		String sql = "UPDATE " + tableName + " SET status = ?, finish_time = ?"
				+ " WHERE id = ?";
		java.sql.Timestamp time = new Timestamp(System.currentTimeMillis());
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, status);
			pst.setTimestamp(2, time);
			pst.setInt(3, id);
			// execute update SQL stetement
			pst.executeUpdate();		
			System.out.println("Record is updated to " + tableName + " table!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally{
			if (pst != null) {
				pst.close();
			}
			
			if (con != null) {
				con.close();
			}
		}
	}
	
	public static void updateAccountTable(String tableName, String status, int id) throws SQLException {
		DAO dao = new DAO();
		Connection con = dao.con;
		PreparedStatement pst = null;
		String sql = "UPDATE " + tableName + " SET status = ?, health = ?"
				+ " WHERE id = ?";
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, status);
			pst.setInt(2, 0);
			pst.setInt(3, id);
			pst.executeUpdate();		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally{
			if (pst != null) {
				pst.close();
			}
			
			if (con != null) {
				con.close();
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
	
	public void createSpiderTable(String tablename){
		DAO dao = new DAO();
		Connection con = dao.con;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sql = "create table "+tablename+" (`id` int(10) NOT NULL AUTO_INCREMENT,website varchar(255),message_url varchar(255),"
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
}
