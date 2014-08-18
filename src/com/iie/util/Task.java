/**
 * 
 */
package com.iie.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Gingber
 *
 */
public class Task {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DBOperator.getDBConnection();
		
		ArrayList<String> arrUser = new ArrayList<String>();
		try {
			arrUser = TxtReader.loadVectorFromFile(new File("d:/keyuser2013.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		int successNum = 0, failNum = 0;
		for(int i = 0; i < arrUser.size(); i++) {
			String sql = "insert into key_user2013(user_id) values('" + arrUser.get(i) + "')"; 
			if(DBOperator.insertSQL(sql)) {
				//System.out.println("成功插入用户:\t" + arrUser.get(i));
				String correctUser = arrUser.get(i);
				sb.append(correctUser);
				sb.append("\n");
				successNum++;
			} else {
				//System.out.println("失败插入用户:\t" + arrUser.get(i));
				//System.out.println(arrUser.get(i));
				failNum++;
			}
		}
		System.out.println("successNum = " + successNum + "\t" + "failNum = " + failNum);
		System.out.println(sb.toString());

	}

}
