//���ݿ������ļ�

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
	 * ����mysql����
	 */
	public void reginster() {
		try {
			Class.forName(driver); // ����MYSQL JDBC��������

		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
	}

	/**
	 * �������ݿ�
	 * 
	 * @return ���Ӷ���
	 */

	public Connection conDB(){
		LogSys.nodeLogger.info("ConDB ��ʼ��");
		Connection connect = null;
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?useUnicode=true&continueBatchOnError=true&characterEncoding=" + encode, user,
					password);
			// ����URLΪ jdbc:mysql//��������ַ/���ݿ���
			// �����2�������ֱ��ǵ�½�û���������
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
		LogSys.nodeLogger.info("������һ�����ӣ��ܹ����������ǣ���"+connectionCount);
		Connection connect = null;
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?rewriteBatchedStatements=true&continueBatchOnError=true&useUnicode=true&characterEncoding=" + encode, user,
					password);
			// ����URLΪ jdbc:mysql//��������ַ/���ݿ���
			// �����2�������ֱ��ǵ�½�û���������
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
