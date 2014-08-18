/**
 * 
 */
package com.iie.twitter.robot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import spader.dao.SpaderDao;

import com.iie.util.TxtReader;

/**
 * @author Gingber
 *
 */
public class KeyUser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		List<String> keyusers = TxtReader.loadVectorFromFile(new File("task/file/keyuser.txt"), "utf-8");
		SpaderDao.insertToKeyUserTable(keyusers);
		
	}

}
