package spader.util;

public class TWEncodeURL {

	private String url;
	
	public TWEncodeURL(String url){
		this.url = url;
	}
	
	public String getMessageID(){
		String[] strs = url.split("/");				
		String message_id = strs[strs.length-1];
		return message_id;
	}
	
	public String getUserID(){
		String[] strs = url.split("/");
		String user_id = "@"+strs[3]+" ";
		return user_id;
	}
	
	public static void main(String[] args) {
		

	}


}
