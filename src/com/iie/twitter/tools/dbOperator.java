package com.iie.twitter.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class dbOperator {
	private static Connection conn = null;  
	static PreparedStatement statement = null;  
	  
	    
	 // connect to MySQL  
    public static Connection getDBConnection() {  
        String url = "jdbc:mysql://localhost:3306/http_facebook140506?characterEncoding=UTF-8";  
        String username = "root";  
        String password = "hadoop"; // ���������������������ݿ�   
        try {   
            Class.forName("com.mysql.jdbc.Driver" );   
            conn = DriverManager.getConnection( url,username, password );   
            }  
        //����������������쳣  
         catch ( ClassNotFoundException cnfex ) {  
             System.err.println(  
             "װ�� JDBC/ODBC ��������ʧ�ܡ�" );  
             cnfex.printStackTrace();   
         }   
         //�����������ݿ��쳣  
         catch ( SQLException sqlex ) {  
             System.err.println( "�޷��������ݿ�" );  
             sqlex.printStackTrace();   
         }
		return conn;  
    }  
	  
    // disconnect to MySQL  
    public static void deconnSQL() {  
        try {  
            if (conn != null)  
                conn.close();  
        } catch (Exception e) {  
            System.out.println("�ر����ݿ����� ��");  
            e.printStackTrace();  
        }  
    }  
	  
    // execute selection language  
    public static ResultSet selectSQL(String sql) {  
        ResultSet rs = null;  
        try {  
            statement = conn.prepareStatement(sql);  
            rs = statement.executeQuery(sql);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return rs;  
    }  
	  
    // execute insertion language  
    public static boolean insertSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("�������ݿ�ʱ����");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("����ʱ����");  
            e.printStackTrace();  
        }  
        return false;  
    }
    
    private static java.sql.Timestamp getCurrentTimeStamp() {
    	 
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
 
	}
 
    
    
    //execute delete language  
    boolean deleteSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("�������ݿ�ʱ����");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("����ʱ����");  
            e.printStackTrace();  
        }  
        return false;  
    }  
    
    //execute update language  
    public static boolean updateSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("�������ݿ�ʱ����");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("����ʱ����");  
            e.printStackTrace();  
        }  
        return false;  
    }

}
