package com.iie.twitter.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class MYPropertyTool {

	/**
	 * @param args
	 */
	static Properties pro;
	public static void  Init(String filePath){
		pro=new Properties();
		try {
			pro.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.getLogger("文件不存在");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.getLogger("文件读写异常");
			e.printStackTrace();
		}		
	}
	public static Properties getPro(){
		return pro;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
