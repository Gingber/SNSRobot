package com.iie.twitter.tools;

import java.util.Vector;

public class SimpleXmlAnalyser {
	String str;
	public static void main(String[] args){
		SimpleXmlAnalyser sm=new SimpleXmlAnalyser("<uname>1111</uname><uname>222</uname>");
		sm.getValueByTag("uname");
	}
	
	
	public SimpleXmlAnalyser(String t){
		this.str=t;
	}
	public String getFirstValueByTag(String tag){
		String frontTag="<"+tag+">";
		String backTag="</"+tag+">";
		if(str.indexOf(tag)==-1){
			return null;
		}else{
			int t=str.indexOf(frontTag);
			int q=str.indexOf(backTag);
			int infoStart=t+frontTag.length();
			String res=str.substring(infoStart,q);
			return res;
		}
	}
	public String[] getValueByTag(String tag){
		String frontTag="<"+tag+">";
		String backTag="</"+tag+">";
		Vector<String> allist=new Vector<String>(5);
		String backup=str;
		if(str.indexOf(tag)==-1){
			return null;
		}else{
			while(str.indexOf(frontTag)!=-1){
				int t=str.indexOf(frontTag);
				int q=str.indexOf(backTag);
				int infoStart=t+frontTag.length();
				String res=str.substring(infoStart,q);
				str=str.substring(q+backTag.length());
				allist.add(res);
			}			
		}
		int size=allist.size();
		String[] res=new String[size];
		for(int i=0;i<allist.size();i++){
			res[i]=allist.elementAt(i);
		}	
		str=backup;
		return res.clone();
	}

}
