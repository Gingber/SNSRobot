package com.iie.twitter.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import com.iie.twitter.plantform.LogSys;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class MulityInsertDataBase {
	private String ip="";
	private String user,password;
	private String databaseName;
	private final String encode="utf-8";
	private Connection connection;
	private PreparedStatement messageps=null;
	private PreparedStatement messagdetailps=null;
	
	
	private PreparedStatement userprofile=null;
	
	private PreparedStatement messagereteet=null;
	
	
	public MulityInsertDataBase(){
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.dbaddressIP")){
				String res = t.substring(t.indexOf('=') + 1);
				this.ip= res;
			}			
			if (t.startsWith("http.dbusername")) {
				String res = t.substring(t.indexOf('=') + 1);
				this.user = res;
			} else if (t.startsWith("http.dbpassword")) {
				String res = t.substring(t.indexOf('=') + 1);
				this.password=res;
			} else if (t.startsWith("http.databasename")) {
				String res = t.substring(t.indexOf('=') + 1);
				this.databaseName=res;
			}
		}		
	}
	
	public Connection getConnection(){
		
		try {
			if(connection!=null&&!connection.isClosed()){
				return connection;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?useUnicode=true&continueBatchOnError=true&characterEncoding=" + encode, user,
					password);
			connection.setAutoCommit(false);
		} catch (Exception e) {
			LogSys.nodeLogger.error("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		LogSys.nodeLogger.debug("Success to connect to SQLServer  [IP:]"+ip+" [DBName:"+databaseName+"]");
		
		return connection;
	}
	
	private void checkBatch(int[] updateCounts) throws AllHasInsertedException{
		int OKRows=0,NoInfoRows=0,FailRows=0;
		for(int i=0;i<updateCounts.length;i++){
			if (updateCounts[i] >= 0) {
				OKRows++;
		      } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
		        NoInfoRows++;
		      } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
		    	//System.out.println("["+i+"]Failed to execute; updateCount=Statement.EXECUTE_FAILED");
		        FailRows++;
		      }
		}
		System.out.println(String.format("Success:%d NoInfo:%d Failed:%d",OKRows,NoInfoRows,FailRows));
		if(FailRows==updateCounts.length){
			throw new AllHasInsertedException("所有的数据都插入过了");
		}
	}
	
	public void getDatafromprofile(){
		Connection con=this.getConnection();
		try{
			Statement sta=con.createStatement();
			ResultSet rs=sta.executeQuery("select profile_image from user_profile limit 0,1");
			rs.next();
			InputStream ins=rs.getBinaryStream(1);
			
			File f=new File("Output/Twitter/take_picture_fromdatabase.jpg");
			if(!f.exists())
				f.createNewFile();
			FileOutputStream fos=new FileOutputStream(f);
			byte[] buffer=new byte[1000];
			int length=0;
			while((length=ins.read(buffer))>0){
				fos.write(buffer, 0, length);
			}
			fos.close();
			ins.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	
	
	
}
