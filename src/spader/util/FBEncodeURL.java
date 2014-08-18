package spader.util;

public class FBEncodeURL {

	private String url;
	public FBEncodeURL(String url){
		this.url = url;
	}
	
	public String getMessageID(){
		
		String message_id = "";
		
		if(url.contains("https://www.facebook.com/photo.php?fbid=")) {
			String[] strs = url.split("&");				
			message_id = strs[0].split("fbid=")[1];
		} else if(url.contains("?story_fbid")) {
			String[] strs = url.split("&");				
			message_id = strs[0].split("story_fbid=")[1];
		} else if(url.contains("&story_fbid")) {
			String[] strs = url.split("&");				
			message_id = strs[1].split("story_fbid=")[1];
		} else if(url.contains("posts")) {
			String[] strs = url.split("/");				
			message_id = strs[strs.length-1];
		} else if(url.contains("photo.php?v=")) {
			String[] strs = url.split("v=");				
			message_id = strs[strs.length-1];
		} else if(url.contains("/?type=1")) {
			String[] strs = url.split("/");				
			message_id = strs[strs.length-2];
		}
		
//		System.out.println(message_id);

		return message_id;
	}
	
	public String getUserID(){
		String user_id = "";
		return user_id;
	}
	
	
	public static void main(String[] args) {
		String url1 = "https://www.facebook.com/photo.php?fbid=321138158041874&set=a.314105602078463.1073741826.100004370777606&type=1&source=11";

		String url2 = "https://www.facebook.com/permalink.php?story_fbid=1450708801834310&id=100006856112624";
		
		String url3 = "https://www.facebook.com/eun.hee.167527/posts/320482344774122";
		
		String url4 = "https://www.facebook.com/photo.php?v=10152629754289238";
		
		String url5= "https://www.facebook.com/permalink.php?id=659939304058109&story_fbid=1412439095674353";
		
		String url6 = "https://www.facebook.com/WhiteHouse/photos/a.158628314237.115142.63811549237/10152666684514238/?type=1";
		
		FBEncodeURL fbUrl = new FBEncodeURL(url6);
		fbUrl.getMessageID();
		
	}

}
