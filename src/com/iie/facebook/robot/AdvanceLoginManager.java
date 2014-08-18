package com.iie.facebook.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.iie.facebook.tools.DbOperation;
import com.iie.twitter.plantform.LogSys;

import java.util.*;
public class AdvanceLoginManager {
	class CookieItem{
		public List<Cookie> cookieList;
		public String username;
		public String userid;
	}
	
	class logonItem {
		public boolean flag;
		public String userid;
		public String fbdtsg;
	}
	
	
	DefaultHttpClient httpclient;
	DbOperation dbo;
	logonItem lgItem = new logonItem();
	
	public AdvanceLoginManager(DefaultHttpClient _httpclient){
		httpclient=_httpclient;
		FacebookLoginCookieStore mycookiestore = new FacebookLoginCookieStore();
		httpclient.setCookieStore(mycookiestore);
		dbo=new DbOperation();
	}
	
	private boolean getAvailableCookie(CookieItem item, int accountId){
		//ByteArrayInputStream in = new ByteArrayInputStream(data);
	    //ObjectInputStream is = new ObjectInputStream(in);
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			sta = con.createStatement();
			ResultSet rs=sta.executeQuery("select cookie,username,userid from fb_account where status='using' and id=" + accountId);
			if(rs.next()){
				InputStream is = rs.getBlob("cookie").getBinaryStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				item.cookieList= (List<Cookie>) ois.readObject(); 
				item.username=rs.getString(2);
				item.userid = rs.getString(3);
				rs.close();
				sta.close();
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	private List<String[]> GetAvailableCount(int accountId){
		List<String[]> res=new ArrayList<String[]>();
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			sta = con.createStatement();
			ResultSet rs=sta.executeQuery("select username,userpassword,userid from fb_account where id=" + accountId);
			while(rs.next()){
				String[] t=new String[3];
				t[0]=rs.getString(1);
				t[1]=rs.getString(2);
				t[2] = rs.getString(3);
				res.add(t);
			}
			rs.close();
			sta.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public logonItem trylogin(int accountId){
		//
		//先把表锁起来
		//
		CookieItem item=new CookieItem();
		String username="";
		boolean find=this.getAvailableCookie(item, accountId);
		if(find){
			System.out.println("发现能使用的账户:\t"+item.username);
			try {
		    	FacebookLoginCookieStore mycookiestore = new FacebookLoginCookieStore();
				mycookiestore.resume(item.cookieList);
				httpclient.setCookieStore(mycookiestore);
				if(checkLoginStatus()){
					LogSys.nodeLogger.info("登陆成功 with User:\t"+ item.username);
					LogSys.nodeLogger.info("Success Login  OK");
					lgItem.flag = true;
					lgItem.fbdtsg = this.getFbdtsg(httpclient);
					lgItem.userid = item.userid;
					return lgItem;
				}else{//证明当前账户已经失效啦
					markStatus(item.username,"frozen");
					//MaskAsNotAvailable(item.username);
					System.out.println("刚刚发现的账户无法进行恢复会话:\t"+item.username);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String[]> list=this.GetAvailableCount(accountId);
		boolean success=false;
		for(int i=0;i<list.size();i++){
			String[] nameandpass=list.get(i);
			if(forceLogin(nameandpass)){//如果登陆成功的话
				//记得保存当前的Cookie信息啊
				FacebookLoginCookieStore mycookiestore = (FacebookLoginCookieStore) httpclient.getCookieStore();
				List<Cookie> cookie=mycookiestore.savetodb();
				if(cookie==null||cookie.size()==0){
					System.out.println("大小错误啊");
				}
				SaveCookieToDB(nameandpass[0],cookie);
				success=true;
				break;
			}else{//标记当前账号失效啦
				MaskAsNotAvailable(nameandpass[0]);
			}
			
		}
		
		//
		//将表权限释放掉。
		//
		if(!success){
			LogSys.clientLogger.error("注意，当前账户 id = " + accountId + " 无法正常使用");
			//System.exit(-1);
		}
		return lgItem;

	}
	
	public boolean forceLogin(String[] loginInfo){
		boolean logined=false;
		String user = null, pass = null, userid = null;
		user = loginInfo[0];
		pass = loginInfo[1];
		userid = loginInfo[2];
		try{
			LogSys.nodeLogger.debug("准备进行用户登录操作");
			HttpGet httpget = new HttpGet("https://www.facebook.com/");
			HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	        String content=SaveToHtml(entity,"Output/Facebook/LogBefore.html");
	        String token=null;
	        if(content!=null)
	        	token=this.getToken(content);
	        System.out.println("Token："+token);	        
	        System.out.println("--------------");
	        LogSys.nodeLogger.debug("Login form get: " + response.getStatusLine());
	        EntityUtils.consume(entity);	
	        LogSys.nodeLogger.debug("Initial set of cookies:");
	        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	        	LogSys.nodeLogger.debug("None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	            	LogSys.nodeLogger.debug("- " + cookies.get(i).toString());
	            }
	        }	        

            HttpPost httpost = new HttpPost("https://www.facebook.com/login.php?login_attempt=1");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("email", user));
            nvps.add(new BasicNameValuePair("pass", pass));          
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
          
            response = httpclient.execute(httpost);
            entity = response.getEntity();
            SaveToHtml(entity,"Output/Facebook/LogAfter.html");
            LogSys.nodeLogger.debug("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);
            LogSys.nodeLogger.debug("Post logon cookies:");
            httpclient.getCookieSpecs();
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
            	LogSys.nodeLogger.debug("None");
            	logined=false;
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                	LogSys.nodeLogger.info("- " + cookies.get(i).toString());
                	if(cookies.get(i).getName().equals("xs")){
                		LogSys.nodeLogger.info("Success To Login To Facebook");
                		logined=true;
                	}
                }
                if(logined){
                    LogSys.nodeLogger.info("Success To Save Cookies");
                }else{
                	LogSys.nodeLogger.info("Fail To Login To Facebook RM the datFile");
                }
            }
            
		}catch(org.apache.http.conn.ConnectionPoolTimeoutException ex){
			ex.printStackTrace();
			return false;
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("登录失败with User"+user+" Password"+pass);
			return false;
		}
		
		if(checkLoginStatus()){
			LogSys.nodeLogger.info("登陆成功with User"+user+" Password"+pass);
			lgItem.flag = true;
			lgItem.fbdtsg = this.getFbdtsg(httpclient);
			lgItem.userid = userid;
			return true;
		}else{
			LogSys.nodeLogger.info("登录失败with User"+user+" Password"+pass);
			lgItem.flag = false;
//			lgItem.fbdtsg = this.getFbdtsg(httpclient);
//			lgItem.userid = userid;
			return false;
		}
	}
	
	private String getToken(String html) {
		Document doc=Jsoup.parse(html, "/");
		Elements elemets = doc.getElementsByAttributeValue("name","authenticity_token");
		String res=null;
		if(elemets.size()>0){
			Element ele=elemets.first();
			res=ele.attr("value");
		}		
		return res;
	}
	
	private String getFbdtsg(DefaultHttpClient httpclient) {
		
		String fbdtsg = null;
		HttpGet request = new HttpGet("https://www.facebook.com/home.php");
		HttpResponse homeResponse;
		StringBuilder html = new StringBuilder();
		try {
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
    		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			homeResponse = httpclient.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(homeResponse.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				html.append(line);
			} 
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Document doc = Jsoup.parse(html.toString());
		Element elem = doc.select("input[name=fb_dtsg]").first();
		fbdtsg = elem.attr("value"); // fb_dtsg = AQE5pcm4XG2-
		
		return fbdtsg;
	}
	
	public boolean markStatus(String username,String status){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update fb_account set status=?, health=false where username=?");
			pst.setString(1, status);
			pst.setString(2, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean markAccountStatus(String userId,String status){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update fb_account set status=?, health=false where userid=?");
			pst.setString(1, status);
			pst.setString(2, userId);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean MaskAsNotAvailable(String username){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update fb_account set status='frozen', health=false where username=?");
			pst.setString(1, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private boolean SaveCookieToDB(String username,List<Cookie> cookie){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update fb_account set status='using',count=1,health=true,cookie=? where username=?");
			pst.setObject(1, (Object)cookie);
			pst.setString(2, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	

	private boolean checkLoginStatus(){
		///%s/following/users?%sinclude_available_features=1&include_entities=1&is_forward=true
		HttpGet httpget = new HttpGet("https://www.facebook.com/amir.aizat.7");
		try {
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
    		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpResponse response = httpclient.execute(httpget);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			if(HttpStatus.SC_OK==stateCode){
				String res=SaveToHtml(response.getEntity(),"Output/FaceBook/CheckLogin.html");
				if(res.contains("We gotta check... are you human?")||res.contains("Not Logged In")||
								res.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")
								){
					return false;
				}else{
					return true;
				}
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				return false;
			}else{
				return false;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}
	
	private String SaveToHtml(HttpEntity entity,String fileName){       
        try{
        	 BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
             BufferedWriter bw=new BufferedWriter(new FileWriter(fileName));
        	String t="";
        	StringBuffer sb=new StringBuffer();
        	while((t=br.readLine())!=null){
        		bw.write(t+"\n\r");
        		sb.append(t+"\n\r");
        	}
        	bw.close();
        	br.close();
        	return sb.toString();
        }catch (Exception ex){
        	ex.printStackTrace();
        	return null;
        }
        
	}

}
