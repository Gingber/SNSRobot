/**
 * 
 */
package com.iie.facebook.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jsoup.select.Elements;

import com.iie.facebook.robot.AdvanceLoginManager.logonItem;
import com.iie.twitter.plantform.LogSys;

import spader.bean.Task;

/**
 * @author Gingber
 *
 */
public class WebOperationAjax {
	
	private static DefaultHttpClient httpclient;
	private static ExecutorService service = Executors.newCachedThreadPool();
	
	private static String ajaxCommentUrl = "/ajax/ufi/add_comment.php";
	private static String ajaxLiketUrl = "https://www.facebook.com/ajax/ufi/like.php";
	private static String ajaxSharerUrl = "/ajax/sharer/submit/";
	private static String ajaxTimeLineUrl = "/ajax/updatestatus.php?__av=";
	
	
	
	public void setHttpclient(DefaultHttpClient _httpclient){
		httpclient = _httpclient;
	}

	// comment function
	public static String openLink(DefaultHttpClient httpclient, Task task, logonItem lgitem, int count) throws RuntimeException, ClientProtocolException, IOException{
		if(count<=2){
			LogSys.nodeLogger.debug("The Retry["+count+"] OpenLink with Address:" + ajaxCommentUrl);
			String res = null;
			String fbId = null, content = null;
			switch(task.ownType) {
				case post: {
					content = task.getResultString();
					res = doPost(httpclient, content, lgitem);
					break;
				}
				case comment: {
					fbId = task.getTargetString(); 
					content = task.getContent();
					res = doComment(httpclient, fbId, content, lgitem);
					break;
				}
				case retweet: {
					fbId = task.getTargetString();
					content = task.getContent();
					res = doShare(httpclient, fbId, content, lgitem);
					break;
				}
				case like: {
					fbId = task.getTargetString(); 
					res = doLike(httpclient, fbId, lgitem);
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
				return openLink(httpclient, task, lgitem, count+1);
			}
		}else{
			return null;
		}
	}

	private  static String doPost(DefaultHttpClient httpclient, String content, logonItem lgitem) throws ClientProtocolException, IOException {
		
		StringBuffer sb = new StringBuffer();
        try {  
        	 HttpPost httpost = new HttpPost(ajaxTimeLineUrl + lgitem.userid);
        	
        	 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
             nvps.add(new BasicNameValuePair("composer_session_id", "63a95205-dfdc-43fc-a0f1-f1bb42ab6958"));
             nvps.add(new BasicNameValuePair("fb_dtsg", lgitem.fbdtsg));
             nvps.add(new BasicNameValuePair("xhpc_context", "profile")); 
             nvps.add(new BasicNameValuePair("xhpc_ismeta", "1"));
             nvps.add(new BasicNameValuePair("xhpc_timeline", "1"));
             nvps.add(new BasicNameValuePair("xhpc_composerid", "u_0_27"));
             nvps.add(new BasicNameValuePair("xhpc_targetid", lgitem.userid));  
             nvps.add(new BasicNameValuePair("clp", "{\"cl_impid\":\"bced9f4f\",\"clearcounter\":0,\"elementid\":\"u_0_2n\",\"version\":\"x\",\"parent_fbid\":100004527931783}"));             
             
             nvps.add(new BasicNameValuePair("xhpc_message_text", content));             
             nvps.add(new BasicNameValuePair("xhpc_message", content)); 
             
             nvps.add(new BasicNameValuePair("backdated_date[year]", ""));             
             nvps.add(new BasicNameValuePair("backdated_date[month]", "")); 
             nvps.add(new BasicNameValuePair("backdated_date[day]:", ""));      
             nvps.add(new BasicNameValuePair("backdated_date[hour]", ""));             
             nvps.add(new BasicNameValuePair("backdated_date[minute]", "")); 
             
             nvps.add(new BasicNameValuePair("is_explicit_place", ""));   
             nvps.add(new BasicNameValuePair("composertags_place", ""));             
             nvps.add(new BasicNameValuePair("composertags_place_name", "")); 
             nvps.add(new BasicNameValuePair("tagger_session_id", "1401004597"));  
             nvps.add(new BasicNameValuePair("action_type_id[0]", ""));   
             nvps.add(new BasicNameValuePair("object_str[0]", ""));             
             nvps.add(new BasicNameValuePair("object_id[0]", "")); 
             nvps.add(new BasicNameValuePair("og_location_id[0]", ""));  

             nvps.add(new BasicNameValuePair("hide_object_attachment", "0"));
             nvps.add(new BasicNameValuePair("og_suggestion_mechanism", ""));
             nvps.add(new BasicNameValuePair("composertags_city", ""));
             nvps.add(new BasicNameValuePair("disable_location_sharing", "false"));
             nvps.add(new BasicNameValuePair("composer_predicted_city", ""));
             nvps.add(new BasicNameValuePair("audience[0][value]", "80"));
             nvps.add(new BasicNameValuePair("nctr[_mod]", "pagelet_timeline_recent"));
            
             nvps.add(new BasicNameValuePair("__user", lgitem.userid));
             nvps.add(new BasicNameValuePair("__a", "1"));
             nvps.add(new BasicNameValuePair("__dyn", "7n8ahyj2qm9udDgDxyG8EipEtCxO4pbGA8AGGzQAjFDxCm6pWGczo"));
             nvps.add(new BasicNameValuePair("__req", "j"));
           
             nvps.add(new BasicNameValuePair("ttstamp", "265817177120105687152109107119")); // 26581709778728382577212187
             nvps.add(new BasicNameValuePair("__rev", "1243192"));
             
             httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

             HttpHost targetHost = new HttpHost("facebook.com", 443, "https");
             httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
     		 httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
     		 BasicHttpContext localcontext = new BasicHttpContext();
             HttpResponse response = httpclient.execute(targetHost, httpost, localcontext);
             
             StatusLine state = response.getStatusLine();
             int stateCode=state.getStatusCode();
             boolean needReLogin=false;
             if(HttpStatus.SC_OK==stateCode){
            	 BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
            	 String inputLine = null;
            	 while ((inputLine = in.readLine()) != null) {
            		 sb.append(inputLine+"\r\n");
            		 //判断是否需要重新登录到Twitter
            		 if(inputLine.contains("Not Logged In")||inputLine.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")){
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
//             if(needReLogin) {				
//            	 AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
//            	 twlogin.trylogin();
//            	 return null;
//             }
          
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
         return sb.toString();   
	}


	private  static String doComment(DefaultHttpClient httpclient, String target_fbid, String content, logonItem lgitem) throws ClientProtocolException, IOException {
		
		StringBuffer sb = new StringBuffer();
        try {

        	 HttpPost httpost = new HttpPost(ajaxCommentUrl);
        	
        	 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
             nvps.add(new BasicNameValuePair("ft_ent_identifier", target_fbid));
             nvps.add(new BasicNameValuePair("comment_text", content));
             nvps.add(new BasicNameValuePair("source", "0"));
             nvps.add(new BasicNameValuePair("client_id", "1400639368189:1648344894"));
             nvps.add(new BasicNameValuePair("reply_fbid", ""));
             nvps.add(new BasicNameValuePair("parent_comment_id", ""));
             
             nvps.add(new BasicNameValuePair("timeline_log_data", "AQARgzhOgSLdm1dcbKTARsyToNj4Wx5OR_xPUDH9uosyhVPaqpFMqTiueWbHw192qaixiTX1Am3XCIIzjrWknBqBi7NXg6zX-5NZBMxk7GYPQJIwzQnvQqhBdZmSujslHHEN1628M43mi66agYRODE2GY27ft6AYJezQHsFjXBcJDEnT2rNwZRlReiZoiNcEUKmeZX-yoOiRwG0Qs5XO6k8s8ntIBaWzTxVNhwIVAcR8LNIbgL1cX7p5nx8Lvz12sU-dRDC0WpEC-AUoF-fZD-6oqbiQeCicKBsz3vHVibEL_0IfDXrA6HujEPk-GSvDH63HJyb9oIFz72_wxAo1OQSuACoQxCdKGKVS6rH_aiFXvc5S1MhbZTG_d5FGli2o-VQAzkVt-qrqneI0lBW52rTtK7kifDb7eVxf4XfhF1u1_g"));
             nvps.add(new BasicNameValuePair("rootid", "u_0_37"));
             String clp = "{\"cl_impid\":\"69af99ba\",\"clearcounter\":0,\"elementid\":\"js_5\",\"version\":\"x\",\"parent_fbid\":\"100008360472119;\"}";
             nvps.add(new BasicNameValuePair("clp", clp));
             
             nvps.add(new BasicNameValuePair("ft[tn]", "[]"));
             nvps.add(new BasicNameValuePair("nctr[_mod]", "pagelet_timeline_recent"));
             
             nvps.add(new BasicNameValuePair("__av", lgitem.userid));
             nvps.add(new BasicNameValuePair("__user", lgitem.userid));
             nvps.add(new BasicNameValuePair("__a", "1"));
             nvps.add(new BasicNameValuePair("__dyn", "7n8ahyj2qm9udDgDxyG8EipEtV8sx6iWF3qGEZ94WpUpBxCdz8"));
             nvps.add(new BasicNameValuePair("__req", "j"));
             nvps.add(new BasicNameValuePair("fb_dtsg", lgitem.fbdtsg));
             nvps.add(new BasicNameValuePair("ttstamp", "2658172104571039570554512053")); // 26581709778728382577212187
             nvps.add(new BasicNameValuePair("__rev", "1243192"));
             
             httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

             HttpHost targetHost = new HttpHost("facebook.com", 443, "https");
             httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
     		 httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
     		 BasicHttpContext localcontext = new BasicHttpContext();
             HttpResponse response = httpclient.execute(targetHost, httpost, localcontext);
             
             StatusLine state = response.getStatusLine();
             int stateCode=state.getStatusCode();
             boolean needReLogin=false;
             if(HttpStatus.SC_OK==stateCode){
            	 BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
            	 String inputLine = null;
            	 while ((inputLine = in.readLine()) != null) {
            		 sb.append(inputLine+"\r\n");
            		 //判断是否需要重新登录到Twitter
            		 if(inputLine.contains("Not Logged In")||inputLine.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")){
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
//             if(needReLogin) {				
//            	 AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
//            	 twlogin.trylogin();
//            	 return null;
//             }
          
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
         return sb.toString();   
	}


	private  static String doLike(DefaultHttpClient httpclient, String target_fbid, logonItem lgitem) throws ClientProtocolException, IOException {
		
		StringBuffer sb = new StringBuffer();
        try {  
        	 HttpPost httpost = new HttpPost(ajaxLiketUrl);
        	
        	 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        	 nvps.add(new BasicNameValuePair("like_action", "true"));
             nvps.add(new BasicNameValuePair("ft_ent_identifier", target_fbid));
             nvps.add(new BasicNameValuePair("source", "0"));
             nvps.add(new BasicNameValuePair("client_id", "1400999909245:2724766365"));
             nvps.add(new BasicNameValuePair("rootid", "u_0_2l"));
             nvps.add(new BasicNameValuePair("giftoccasion", ""));
             nvps.add(new BasicNameValuePair("ft[tn]", ">=]"));
             nvps.add(new BasicNameValuePair("ft[type]", "20"));
             nvps.add(new BasicNameValuePair("nctr[_mod]", "pagelet_timeline_recent"));
             nvps.add(new BasicNameValuePair("__av", lgitem.userid));
             nvps.add(new BasicNameValuePair("__user", lgitem.userid));
             nvps.add(new BasicNameValuePair("__a", "1"));
             nvps.add(new BasicNameValuePair("__dyn", "7n8ahyj2qm9udDgDxyG8EipEtCxO4pbGA8AGGzQAjFDxCm6pWGczo"));
             nvps.add(new BasicNameValuePair("__req", "o"));
             nvps.add(new BasicNameValuePair("fb_dtsg", lgitem.fbdtsg));
             nvps.add(new BasicNameValuePair("ttstamp", "265816911811575497195988270")); // 26581709778728382577212187
             nvps.add(new BasicNameValuePair("__rev", "1262776"));
             
             httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
             
             HttpHost targetHost = new HttpHost("facebook.com", 443, "https");
             httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
     		 httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
     		 BasicHttpContext localcontext = new BasicHttpContext();
             HttpResponse response = httpclient.execute(targetHost, httpost, localcontext);
             
             StatusLine state = response.getStatusLine();
             int stateCode=state.getStatusCode();
             boolean needReLogin=false;
             if(HttpStatus.SC_OK==stateCode){
            	 BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
            	 String inputLine = null;
            	 while ((inputLine = in.readLine()) != null) {
            		 sb.append(inputLine+"\r\n");
            		 //判断是否需要重新登录到Twitter
            		 if(inputLine.contains("Not Logged In")||inputLine.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")){
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
//             if(needReLogin) {				
//            	 AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
//            	 twlogin.trylogin();
//            	 return null;
//             }
          
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
         return sb.toString();   
	}

	private  static String doShare(DefaultHttpClient httpclient, String url, String content, logonItem lgitem) throws ClientProtocolException, IOException {
		
		StringBuffer sb = new StringBuffer();
        try {  
        	 
        	 ArrayList<String> paramsList = getAttachParams(httpclient, url);
        	 
        	
        	 HttpPost httpost = new HttpPost(ajaxSharerUrl);
        	
        	 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        	 nvps.add(new BasicNameValuePair("fb_dtsg", lgitem.fbdtsg));
        	 nvps.add(new BasicNameValuePair("ad_params", ""));
             nvps.add(new BasicNameValuePair("pubcontent_params", "{\"sbj_type\":null}"));
             nvps.add(new BasicNameValuePair("mode", "self"));
             nvps.add(new BasicNameValuePair("friendTarget", ""));
             nvps.add(new BasicNameValuePair("groupTarget", ""));
             nvps.add(new BasicNameValuePair("message_text", content));
             nvps.add(new BasicNameValuePair("message", content)); 
             
             /*
              * 参数的设定取决于：class="share_action_link" href="/ajax/sharer/?s=2&amp;appid=2305272732&amp;p%5B0%5D=100008168318377&amp;p%5B1%5D=1073741880&amp;profile_id=100008168318377&amp;share_source_type=unknown"
              * 
              * attachment[params][0]: 用户ID 
              * attachment[params][1]: 消息ID
              * attachment[type]: 22
              * 
              * attachment[params][0]: 用户ID
              * attachment[params][1]: 消息编号
              * attachment[type]: 2
              * 
              */
             nvps.add(new BasicNameValuePair("attachment[type]", paramsList.get(0)));
             nvps.add(new BasicNameValuePair("appid", paramsList.get(1)));
             //System.out.println("attachment[params][0] = " + paramsList.get(2));
             nvps.add(new BasicNameValuePair("attachment[params][0]", paramsList.get(2))); // 消息的发布者ID user_id=  100001933006724
             
             //System.out.println("attachment[params][1] = " + paramsList.get(3));
             nvps.add(new BasicNameValuePair("attachment[params][1]", paramsList.get(3)));  //消息编号fbid
             nvps.add(new BasicNameValuePair("share_source_type", paramsList.get(4)));
             nvps.add(new BasicNameValuePair("src", "i")); 
             nvps.add(new BasicNameValuePair("parent_fbid", ""));
             nvps.add(new BasicNameValuePair("ogid", ""));
             nvps.add(new BasicNameValuePair("audience[0][value]", "80"));
             nvps.add(new BasicNameValuePair("__user", lgitem.userid));
             nvps.add(new BasicNameValuePair("__a", "1"));
             nvps.add(new BasicNameValuePair("__dyn", "7n8ahyj2qm9udDgDxyG8EipEtV8sx6iWF3qGEZ94WpUpBxCdz8S"));
             nvps.add(new BasicNameValuePair("__req", "9"));
             nvps.add(new BasicNameValuePair("ttstamp", "265817012080122120981221074572")); // 26581709778728382577212187
             nvps.add(new BasicNameValuePair("__rev", "1262776"));
             
             httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
             
             HttpHost targetHost = new HttpHost("facebook.com", 443, "https");
             httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
     		 httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
     		 BasicHttpContext localcontext = new BasicHttpContext();
             HttpResponse response = httpclient.execute(targetHost, httpost, localcontext);
             
             StatusLine state = response.getStatusLine();
             int stateCode=state.getStatusCode();
             boolean needReLogin=false;
             if(HttpStatus.SC_OK==stateCode){
            	 BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
            	 String inputLine = null;
            	 while ((inputLine = in.readLine()) != null) {
            		 sb.append(inputLine+"\r\n");
            		 //判断是否需要重新登录到Twitter
            		 if(inputLine.contains("Not Logged In")||inputLine.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")){
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
//             if(needReLogin) {				
//            	 AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
//            	 twlogin.trylogin();
//            	 return null;
//             }
          
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
         return sb.toString();   
	}

	
	public static String getFbAuthenticityToken(DefaultHttpClient httpclient) {
		
		String fbdtsg = null;
		// 额外增加部分
		HttpGet request = new HttpGet("https://www.facebook.com/home.php");
		HttpResponse homeResponse;
		StringBuilder html = new StringBuilder();
		try {
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
	
	private static ArrayList<String> getAttachParams(DefaultHttpClient httpclient, String linkAddress) {
		String content = null;
		URI uri=null;
		HttpGet httpget=null;
		int questIndex=linkAddress.indexOf('?');
		int qIndex=linkAddress.indexOf('q');
		if(questIndex!=-1&&qIndex!=-1){
			try {
				String path=linkAddress.substring(0,linkAddress.indexOf('?'));
				String query=linkAddress.substring(linkAddress.indexOf('?')+1,linkAddress.length());				
				uri=new URI(null,null,path,query,null);
				LogSys.nodeLogger.info("尝试打开网页address："+uri.toString());
				httpget= new HttpGet(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				System.out.println("语法错误");			
				e.printStackTrace();
			}catch(Exception ex){
				ex.printStackTrace();
				System.exit(0);
				
			}			
		}else{
			httpget = new HttpGet(linkAddress);
		}
		
		httpget.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
		httpget.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
		httpget.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		HttpHost targetHost = new HttpHost("facebook.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);		
		StringBuffer sb=new StringBuffer();
		HttpResponse response = null;
		try {
			response = httpclient.execute(targetHost, httpget, localcontext);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			boolean needReLogin=false;
			if(HttpStatus.SC_OK==stateCode){
				BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine+"\r\n");
					//判断是否需要重新登录到Twitter
					if(inputLine.contains("Not Logged In")||inputLine.contains("goURI(\"https:\\/\\/www.facebook.com\\/login.php")){
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
//			if(needReLogin){				
//				AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
//				twlogin.trylogin();
//				return null;
//			}
			
						
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LogSys.nodeLogger.debug("ClientProtocalException e");
		} catch(HttpHostConnectException ex){
			return null;
		} catch (java.net.SocketTimeoutException x){
			LogSys.nodeLogger.debug("读取文件超时");
			httpget.abort();
			return null;			
		} catch (RuntimeException ex) {
	         httpget.abort();
	         throw ex;
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} finally {
			if(httpget!=null){
				httpget.abort();
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

		StringBuilder line = new StringBuilder();
		
		if(linkAddress.contains("story_fbid") || linkAddress.contains("posts")) {
			String pattern = "<a class=\"share_action_link\" href=\"(.*?)\" rel=\"dialog";
			   
		    // Create a Pattern object
		    Pattern r = Pattern.compile(pattern);

		    // Now create matcher object. 
		    Matcher m = r.matcher(sb.toString());
		    while (m.find( )) {
//		    	System.out.println(m.group(1));
		        line.append(m.group(1));
		    }
		} else {
			String pattern = "<a rel=\"dialog\" href=\"(.*?)\" role=\"button";
			   
		    // Create a Pattern object
		    Pattern r = Pattern.compile(pattern);

		    // Now create matcher object. 
		    Matcher m = r.matcher(sb.toString());
		    while (m.find( )) {
//		    	System.out.println(m.group(1));
		        line.append(m.group(1));
		    }
			
		}
			
		String url = null;
		ArrayList<String> paramsList = new ArrayList<String>();
		try {
			url = URLDecoder.decode(line.toString(), "GBK");
//			System.out.println(url);
			String[] arrayUrl = url.split("&amp;");
			
//			System.out.println(arrayUrl[0]);
//			System.out.println(arrayUrl[1]);
//			System.out.println(arrayUrl[2]);
//			System.out.println(arrayUrl[3]);
//			System.out.println(arrayUrl[4]);
			
			paramsList.add(arrayUrl[0].split("s=")[1]);
			paramsList.add(arrayUrl[1].split("=")[1]);
			paramsList.add(arrayUrl[2].split("=")[1]);
			paramsList.add(arrayUrl[3].split("=")[1]);
			paramsList.add(arrayUrl[4].split("=")[1]);
			
//			System.out.println(paramsList.get(0));
//			System.out.println(paramsList.get(1));
//			System.out.println(paramsList.get(2));
//			System.out.println(paramsList.get(3));
//			System.out.println(paramsList.get(4));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return paramsList;     
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
	
	}

}
