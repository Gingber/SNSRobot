package spader.dao;



import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

import com.iie.twitter.tools.BasePath;
import com.iie.twitter.tools.ReadTxtFile;


public class DbConnection {
	
	private static String ip = "127.0.0.1";
	private static String user = "";
	private static String password = "";
	private static String databaseName = "http_twitter";
	private static String encode = "utf-8";
	
	public void InitConfig() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.dbaddressIP")){
				String res = t.substring(t.indexOf('=') + 1);
				ip = res;
			}
			
			if (t.startsWith("http.dbusername")) {
				String res = t.substring(t.indexOf('=') + 1);
				user = res;
			} else if (t.startsWith("http.dbpassword")) {
				String res = t.substring(t.indexOf('=') + 1);
				password = res;
			} else if (t.startsWith("http.databasename")) {
				String res = t.substring(t.indexOf('=') + 1);
				databaseName = res;
			}
		}
		
	}
	
	public Connection getCon() {
		try {
			InitConfig();
	        Connection con = null;  //���������������ݿ��Connection����  
	        try {  
	            Class.forName("com.mysql.jdbc.Driver");// ����Mysql��������  
	              
	            con = DriverManager.getConnection("jdbc:mysql://" + ip
						+ ":3306/" + databaseName
						+ "?useUnicode=true&continueBatchOnError=true&characterEncoding=" + encode, user,
						password);// ������������  
	              
	        } catch (Exception e) { 
	        	e.printStackTrace();
	            System.out.println("���ݿ�����ʧ��" + e.getMessage());  
	        }  
	        return con; //���������������ݿ�����  
		} catch (Exception ne) {
			ne.printStackTrace();
			return null;
		}
	}
}
