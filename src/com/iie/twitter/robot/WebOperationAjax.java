/**
 * 
 */
package com.iie.twitter.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import spader.bean.Task;

import com.iie.twitter.plantform.LogSys;
import com.iie.twitter.tools.TwitterClientManager;

/**
 * @author Gingber
 *
 */
public class WebOperationAjax {
	
	private static DefaultHttpClient httpclient;
	private static ExecutorService service = Executors.newCachedThreadPool();
	
	private static String baseUrl = "https://twitter.com/i/tweet/create";
	private static String retweetUrl = "https://twitter.com/i/tweet/retweet";
	
	public void setHttpclient(DefaultHttpClient _httpclient){
		httpclient = _httpclient;
	}

	// comment function
	public static String openLink(DefaultHttpClient httpclient, Task task, int count) throws RuntimeException, ClientProtocolException, IOException{
		if(count<=2){
			LogSys.nodeLogger.debug("The Retry["+count+"] OpenLink with Address:" + baseUrl);
			String res = null;
			String newTweet = null, tweetId = null;
			switch(task.ownType) {
				case post: {
					newTweet = task.getContent();
					res = doPost(httpclient, newTweet, task.getAccountId());
					break;
				}
				case comment: {
					tweetId = task.getTargetString();
					newTweet = task.getContent();
					res = doComment(httpclient, tweetId, newTweet, task.getAccountId());
					break;
				}
				case retweet: {
					tweetId = task.getTargetString(); 
					res = doRetweet(httpclient, tweetId, task.getAccountId());
					break;
				}
				default: {
					LogSys.nodeLogger.error("未知的TaskType数据类型 exit");
					break;
				}	
			}
				
			if(res != null && res.length() >= 1){
				return res;
			}else{
				return openLink(httpclient, task, count+1);
			}
		}else{
			return null;
		}
	}

	private  static String doPost(DefaultHttpClient httpclient, String content, int accountId) throws ClientProtocolException, IOException {
		
		HttpPost httppost = null;
		int questIndex = baseUrl.indexOf('?');
		int qIndex = baseUrl.indexOf('q');
		if(questIndex != -1 && qIndex != -1){
			try {
				String path = baseUrl.substring(0, baseUrl.indexOf('?'));
				String query = baseUrl.substring(baseUrl.indexOf('?') + 1, baseUrl.length());				
				httppost = new HttpPost(baseUrl);
			} catch(Exception ex){
				ex.printStackTrace();
				System.exit(0);	
			}			
		}else{
			 httppost = new HttpPost(baseUrl);
		}
		
		String authToken = getAuthenticityToken(httpclient);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("authenticity_token", authToken));
        params.add(new BasicNameValuePair("place_id", ""));
        params.add(new BasicNameValuePair("status", content));
        params.add(new BasicNameValuePair("tagged_users", ""));
        
        httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   
      
        httppost.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
        httppost.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
        httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		HttpHost targetHost = new HttpHost("twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);	
       
		StringBuffer sb=new StringBuffer();
		HttpResponse response = null;
		try {
			response = httpclient.execute(targetHost, httppost, localcontext);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			boolean needReLogin=false;
			if(HttpStatus.SC_OK==stateCode){
				BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine+"\r\n");
					//判断是否需要重新登录到Twitter
					if(inputLine.contains("Sign in to Twitter")||inputLine.contains("<form action=\"https://twitter.com/sessions\"")){
						LogSys.nodeLogger.error("需要重新登录");
						needReLogin=true;
					}
				}
				in.close();				
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				Header[] headers=response.getHeaders("location");
				if(headers!=null&&headers.length>0){
					String redirectLocation = headers[0].getValue();
					String redirectAddress;
					if(redirectLocation!=null&&redirectLocation.isEmpty()==false){
						redirectAddress=redirectLocation;
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
						
					}else{
						redirectAddress="/";
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
					}
				}				
			}
			
			//执行后过滤，发现网页的异常情况,需要重新登录操作，则进行重新登录
			if(needReLogin){				
				AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
				twlogin.trylogin(accountId);
				return null;
			}
			
						
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LogSys.nodeLogger.debug("ClientProtocalException e");
		}catch(HttpHostConnectException ex){
			return null;
		}catch(org.apache.http.ConnectionClosedException ex){
			LogSys.nodeLogger.error("文件不正常关闭");
			ex.printStackTrace();
			return sb.toString();
		}
		catch (java.net.SocketTimeoutException x){
			LogSys.nodeLogger.debug("读取文件超时");
			httppost.abort();
			return null;			
		}catch (RuntimeException ex) {
			httppost.abort();
	         throw ex;
	    }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}finally{
			if(httppost!=null){
				httppost.abort();
			}
			if(response!=null){
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					System.err.println("response 销毁失败");
					e.printStackTrace();
				}
			}else{
				return null;
			}
		}	
		
		return sb.toString();           
		
	}
	
	private  static String doComment(DefaultHttpClient httpclient, String tweetId, String content, int accountId) throws ClientProtocolException, IOException {
		
		HttpPost httppost = null;
		int questIndex = baseUrl.indexOf('?');
		int qIndex = baseUrl.indexOf('q');
		if(questIndex != -1 && qIndex != -1){
			try {
				String path = baseUrl.substring(0, baseUrl.indexOf('?'));
				String query = baseUrl.substring(baseUrl.indexOf('?') + 1, baseUrl.length());				
				httppost = new HttpPost(baseUrl);
			} catch(Exception ex){
				ex.printStackTrace();
				System.exit(0);	
			}			
		}else{
			 httppost = new HttpPost(baseUrl);
		}
		
		String authToken = getAuthenticityToken(httpclient);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("authenticity_token", authToken));
        params.add(new BasicNameValuePair("in_reply_to_status_id", tweetId));
        params.add(new BasicNameValuePair("place_id", ""));
        params.add(new BasicNameValuePair("status", content));
        params.add(new BasicNameValuePair("tagged_users", ""));
        
        httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   
      
        httppost.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
        httppost.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
        httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		HttpHost targetHost = new HttpHost("twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);	
       
		StringBuffer sb=new StringBuffer();
		HttpResponse response = null;
		try {
			response = httpclient.execute(targetHost, httppost, localcontext);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			boolean needReLogin=false;
			if(HttpStatus.SC_OK==stateCode){
				BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine+"\r\n");
					//判断是否需要重新登录到Twitter
					if(inputLine.contains("Sign in to Twitter")||inputLine.contains("<form action=\"https://twitter.com/sessions\"")){
						LogSys.nodeLogger.error("需要重新登录");
						needReLogin=true;
					}
				}
				in.close();				
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				Header[] headers=response.getHeaders("location");
				if(headers!=null&&headers.length>0){
					String redirectLocation = headers[0].getValue();
					String redirectAddress;
					if(redirectLocation!=null&&redirectLocation.isEmpty()==false){
						redirectAddress=redirectLocation;
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
						
					}else{
						redirectAddress="/";
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
					}
				}				
			}
			
			//执行后过滤，发现网页的异常情况,需要重新登录操作，则进行重新登录
			if(needReLogin){				
				AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
				twlogin.trylogin(accountId);
				return null;
			}
			
						
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LogSys.nodeLogger.debug("ClientProtocalException e");
		}catch(HttpHostConnectException ex){
			return null;
		}catch(org.apache.http.ConnectionClosedException ex){
			LogSys.nodeLogger.error("文件不正常关闭");
			ex.printStackTrace();
			return sb.toString();
		}
		catch (java.net.SocketTimeoutException x){
			LogSys.nodeLogger.debug("读取文件超时");
			httppost.abort();
			return null;			
		}catch (RuntimeException ex) {
			httppost.abort();
	         throw ex;
	    }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}finally{
			if(httppost!=null){
				httppost.abort();
			}
			if(response!=null){
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					System.err.println("response 销毁失败");
					e.printStackTrace();
				}
			}else{
				return null;
			}
		}	
		
		return sb.toString();           
		
	}
	

	private  static String doRetweet(DefaultHttpClient httpclient, String tweetId, int accountId) throws ClientProtocolException, IOException {
		
		HttpPost httppost = null;
		int questIndex = retweetUrl.indexOf('?');
		int qIndex = retweetUrl.indexOf('q');
		if(questIndex != -1 && qIndex != -1){
			try {
				String path = retweetUrl.substring(0, retweetUrl.indexOf('?'));
				String query = retweetUrl.substring(retweetUrl.indexOf('?') + 1, retweetUrl.length());				
				httppost = new HttpPost(retweetUrl);
			} catch(Exception ex){
				ex.printStackTrace();
				System.exit(0);	
			}			
		}else{
			 httppost = new HttpPost(retweetUrl);
		}
		
		String authToken = getAuthenticityToken(httpclient);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("authenticity_token", authToken));
        params.add(new BasicNameValuePair("id", tweetId));
        
        httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   
      
        httppost.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
        httppost.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
        httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		HttpHost targetHost = new HttpHost("twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);	
       
		StringBuffer sb=new StringBuffer();
		HttpResponse response = null;
		try {
			response = httpclient.execute(targetHost, httppost, localcontext);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			boolean needReLogin=false;
			if(HttpStatus.SC_OK==stateCode){
				BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine+"\r\n");
					//判断是否需要重新登录到Twitter
					if(inputLine.contains("Sign in to Twitter")||inputLine.contains("<form action=\"https://twitter.com/sessions\"")){
						LogSys.nodeLogger.error("需要重新登录");
						needReLogin=true;
					}
				}
				in.close();				
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				Header[] headers=response.getHeaders("location");
				if(headers!=null&&headers.length>0){
					String redirectLocation = headers[0].getValue();
					String redirectAddress;
					if(redirectLocation!=null&&redirectLocation.isEmpty()==false){
						redirectAddress=redirectLocation;
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
						
					}else{
						redirectAddress="/";
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
					}
				}				
			}
			
			//执行后过滤，发现网页的异常情况,需要重新登录操作，则进行重新登录
			if(needReLogin){				
				AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
				twlogin.trylogin(accountId);
				return null;
			}
			
						
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LogSys.nodeLogger.debug("ClientProtocalException e");
		}catch(HttpHostConnectException ex){
			return null;
		}catch(org.apache.http.ConnectionClosedException ex){
			LogSys.nodeLogger.error("文件不正常关闭");
			ex.printStackTrace();
			return sb.toString();
		}
		catch (java.net.SocketTimeoutException x){
			LogSys.nodeLogger.debug("读取文件超时");
			httppost.abort();
			return null;			
		}catch (RuntimeException ex) {
			httppost.abort();
	         throw ex;
	    }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}finally{
			if(httppost!=null){
				httppost.abort();
			}
			if(response!=null){
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					System.err.println("response 销毁失败");
					e.printStackTrace();
				}
			}else{
				return null;
			}
		}	
		
		return sb.toString();           
		
	}
	
	public static String getAuthenticityToken(DefaultHttpClient httpclient) {
	
		String authToken = null;
		// 额外增加部分
		HttpGet request = new HttpGet("https://twitter.com/");
		HttpResponse homeResponse;
		StringBuilder html = new StringBuilder();
		try {
			homeResponse = httpclient.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader
			  (new InputStreamReader(homeResponse.getEntity().getContent()));
			    
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
		Element elem = doc.select("input[name=authenticity_token]").first();
		authToken = elem.attr("value"); // fb_dtsg = AQE5pcm4XG2-
		
		return authToken;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.180", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest=new AdvanceLoginManager(httpclient);
		//lgtest.trylogin();
	
	}

}
