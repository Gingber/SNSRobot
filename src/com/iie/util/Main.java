/**
 * 
 */
package com.iie.util;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gingber
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ParseException, IOException, SQLException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = sdf.parse("2013-04-30 23:59:59");
    	Date date2 = sdf.parse("2013-06-11 00:00:00");

		// replace with your start date string 
		DBOperator.getDBConnection();
		
		for(int i = 0; i < 6300; i++) {
			int begin = i*10000;
			int end = (i+1)*10000;
			String sql =  "SELECT * FROM message limit " + begin + "," + end;
			ResultSet res = DBOperator.selectSQL(sql);
			StringBuilder sb = new StringBuilder();
			
			while (res.next()) {        
				String title = res.getString("title");
				String create_time = res.getString("create_time");
				Date date = sdf.parse(create_time);
				if(title.contains("六四") && date.after(date1) && date.before(date2)) {
					String messageId = res.getString("message_id"); 
					String userId = res.getString("user_id"); 
					String type = res.getString("is_retweet"); 
					//System.out.println("   "+ messageId + " " + userId);
					sb.append(messageId);
					sb.append("\t");
					sb.append(userId);
					sb.append("\t");
					sb.append(title);
					sb.append("\t");
					sb.append(type);
					sb.append("\t");
					sb.append(create_time);
					sb.append("\n");
				}	
			}
			// 释放结果集  
	        if (res != null) {  
	            try {  
	                res.close();  
	            } catch (SQLException e) {  
	                // TODO Auto-generated catch block  
	                e.printStackTrace();  
	            }  
	        }  
			System.out.println("[" + begin + "," + end+ "]");
			TxtWriter.saveToFile(sb.toString(), new File("f:/twitter_04_14.txt"), "utf-8");
			
			
		}
		
		
		
	}

}
