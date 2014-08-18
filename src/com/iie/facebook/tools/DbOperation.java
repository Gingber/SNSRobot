//数据库配置文件

package com.iie.facebook.tools;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.iie.twitter.plantform.LogSys;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DbOperation {
	
	

	private static String ip = "127.0.0.1";

	private static String driver = "com.mysql.jdbc.Driver";

	private static String user = "";

	private static String password = "";

	private static String databaseName = "http_twitter";

	private static String encode = "utf-8";
	private static int patchCount=0;
	private Connection connect = null;
	Statement stmt = null;
	public static int connectionCount;
	
	public DbOperation() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.dbaddressIP")){
				String res = t.substring(t.indexOf('=') + 1);
				DbOperation.ip = res;
			}
			
			if (t.startsWith("http.dbusername")) {
				String res = t.substring(t.indexOf('=') + 1);
				DbOperation.user = res;
			} else if (t.startsWith("http.dbpassword")) {
				String res = t.substring(t.indexOf('=') + 1);
				DbOperation.password = res;
			} else if (t.startsWith("http.databasename")) {
				String res = t.substring(t.indexOf('=') + 1);
				DbOperation.databaseName = res;
			}
		}
		this.reginster();
		this.conDB();
	}

	/**
	 * 加载mysql驱动
	 */
	public void reginster() {
		try {
			Class.forName(driver); // 加载MYSQL JDBC驱动程序

		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
	}

	/**
	 * 连接数据库
	 * 
	 * @return 连接对象
	 */

	public Connection conDB(){
		LogSys.nodeLogger.info("ConDB 初始化");
		Connection connect = null;
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?useUnicode=true&continueBatchOnError=true&characterEncoding=" + encode, user,
					password);
			// 连接URL为 jdbc:mysql//服务器地址/数据库名
			// 后面的2个参数分别是登陆用户名和密码
			System.out.println("Success connect Mysql server!");

		} catch (Exception e) {
			System.out.print("Fail to access DataBase!");
			e.printStackTrace();
			System.exit(-1);
			
		}
		this.connect = connect;
		createStmt();
		return connect;
	}

	public void createStmt() {
		try {
			stmt = this.connect.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Connection conDB(String ip, String databaseName, String user,String password){
		connectionCount++;
		LogSys.nodeLogger.info("创建了一个连接，总共的连接数是？？"+connectionCount);
		Connection connect = null;
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?rewriteBatchedStatements=true&continueBatchOnError=true&useUnicode=true&characterEncoding=" + encode, user,
					password);
			// 连接URL为 jdbc:mysql//服务器地址/数据库名
			// 后面的2个参数分别是登陆用户名和密码
			System.out.println("Success connect Mysql server!");
			stmt = this.connect.createStatement();
		} catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		}
		this.connect = connect;
		return connect;
	}
	
	public boolean insertRightNow(){
		try {
			if(this.connect==null||this.connect.isClosed()){
				this.connect=conDB();
			}
			if(stmt==null||stmt.isClosed()){
				stmt=this.connect.createStatement();
			}
			stmt.executeBatch();			
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	

	public void close() {
		if (this.stmt != null) {
			try {
				this.stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.stmt = null;
		}
		if (this.connect != null) {
			try {
				connect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connect = null;
		}

	}

	
	public void closeConn() {
		// TODO Auto-generated method stub
		this.close();
	}

	public void closeStmt() {
		// TODO Auto-generated method stub
		this.close();
	}
	
	public Connection GetConnection(){
		try {
			if(this.connect!=null&&(!this.connect.isClosed())){
				return this.connect;
			}else{
				return this.conDB();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.conDB();
		}
	}
}
