package spader.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import spader.bean.SpaderBean;
import spader.bean.TaskBean;

public class SpaderProductMethod {
	/**
	 * 
	 * @param t
	 *            ������
	 * @param accountIDlist
	 *            �˺��б�
	 * @return List<SpiderBean> ���ɵ���ľ������� null ��������û�е������ʱ����null
	 */
	public List<SpaderBean> productLike_FB(TaskBean t, List<Integer> accountIDlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int likes = t.getLike();

		if (likes == 0) {
			return null;
		}
		
		if(likes <= accountIDlist.size()) {
			System.out.println("like = " + likes);
			for(int i = 0; i < likes; i++) {
				
				SpaderBean sb = new SpaderBean();
				sb.setWebsite(t.getWebsite());
				sb.setMessage_id(t.getMessage_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("like");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setStatus("created");
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
			}	
		} else {
			System.out.println("[like > " + accountIDlist.size() + "] only like = " + accountIDlist.size());
			for(int i = 0; i < accountIDlist.size(); i++) {
				SpaderBean sb = new SpaderBean();
				sb.setWebsite(t.getWebsite());
				sb.setMessage_id(t.getMessage_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("like");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setStatus("created");
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
			}
		}
		
		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	/**
	 * 
	 * @param t
	 *            ������
	 * @param accountIDlist
	 *            �˺��б�
	 * @return List<SpiderBean> ���ɵ���ľ������� null ��������û�е������ʱ����null
	 */
	public List<SpaderBean> productLike_GL(TaskBean t, List<Integer> accountIDlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int likes = t.getLike();

		if (likes == 0) {
			return null;
		}
		
		if(likes <= accountIDlist.size()) {
			System.out.println("like = " + likes);
			for(int i = 0; i < likes; i++) {
				
				SpaderBean sb = new SpaderBean();
				sb.setWebsite(t.getWebsite());
				sb.setMessage_id(t.getMessage_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("like");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setStatus("created");
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
			}	
		} else {
			System.out.println("[like > " + accountIDlist.size() + "] only like = " + accountIDlist.size());
			for(int i = 0; i < accountIDlist.size(); i++) {
				SpaderBean sb = new SpaderBean();
				sb.setWebsite(t.getWebsite());
				sb.setMessage_id(t.getMessage_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("like");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setStatus("created");
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
			}
		}

		System.out.println("����**����**�������");

		return spiderlist;
	}

	public List<SpaderBean> productComment_Tw(TaskBean t,
		List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int comments = t.getComment();

		if (comments == 0) {
			return null;
		}
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		
		for(int i = 0; i < comments; i++) {

			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("comment");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(t.getUser_id() + contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}	

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	
	public List<SpaderBean> productComment_FB(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int comments = t.getComment();

		if (comments == 0) {
			return null;
		}
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		
		for(int i = 0; i < comments; i++) {

			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("comment");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}	

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	public List<SpaderBean> productComment_GL(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int comments = t.getComment();
		
		if (comments == 0) {
			return null;
		}
		
		System.out.println("comment = " + comments);
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}
		
		Collections.shuffle(numbers);
		
		for(int i = 0; i < comments; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("comment");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}	
		
		System.out.println("����**����**�������");

		return spiderlist;
	}

	public List<SpaderBean> productRetweet_Tw(TaskBean t,
			List<Integer> accountIDlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int retweets = t.getRetweet();

		if (retweets == 0) {
			return null;
		}
		
		for(int i = 0; i < retweets; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("retweet");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}	

		System.out.println("����**ת��**�������");

		return spiderlist;
	}

	
	public List<SpaderBean> productRetweet_FB(TaskBean t,
			List<Integer> accountIDlist ,List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int retweets = t.getRetweet();

		if (retweets == 0) {
			return null;
		}
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		
		for(int i = 0; i < retweets; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setUser_id(t.getUser_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("retweet");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}	
		
		System.out.println("����**ת��**�������");

		return spiderlist;
	}
	
	
	
	public List<SpaderBean> productRetweet_GL(TaskBean t,
			List<Integer> accountIDlist ,List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int retweets = t.getRetweet();

		if (retweets == 0) {
			return null;
		}

		// ������С���˺��������ȡ�˺�
		if (accountIDlist.size() > retweets) {
			int[] ram = new int[retweets];
			int[] samID = random(ram, accountIDlist.size());
			int j = 0;
			for (int i : samID) {
				if(j==contentlist.size()){
					j=-1;
				}
				SpaderBean sb = new SpaderBean();
				sb.setWebsite(t.getWebsite());
				sb.setMessage_id(t.getMessage_id());
				sb.setUser_id(t.getUser_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("retweet");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setContent(contentlist.get(j));
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
				j++;
			}
		} else {// ������ ���ڵ��� �˺�����ֻ�ܰ��˺�������
			for (int j=0,i=0;i<accountIDlist.size();i++) {
			
				if(j==contentlist.size()){
					j=-1;
				}
				SpaderBean sb = new SpaderBean();
				sb.setMessage_id(t.getMessage_id());
				sb.setWebsite(t.getWebsite());
				sb.setUser_id(t.getUser_id());
				sb.setMessage_url(t.getMessage_url());
				sb.setVerb("retweet");
				sb.setAccount_id(accountIDlist.get(i));
				sb.setContent(contentlist.get(j));
				sb.setStatus("created");
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				sb.setCreate_time(ts);
				spiderlist.add(sb);
				j++;
			}

		}

		System.out.println("����**ת��**�������");

		return spiderlist;
	}
	
	
	public List<SpaderBean> productPost_Tw(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int posts = t.getPost();

		if (posts == 0) {
			return null;
		}
		System.out.println("contentlist.size()  " + contentlist.size());
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		
		for(int i = 0; i < posts; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("post");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	public List<SpaderBean> productPost_FB(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int posts = t.getPost();

		if (posts == 0) {
			return null;
		}
		System.out.println("contentlist.size()  " + contentlist.size());
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		
		for(int i = 0; i < posts; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("post");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	
	public List<SpaderBean> productPost_GL(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int posts = t.getPost();

		if (posts == 0) {
			return null;
		}
		
		System.out.println("post = " + posts);
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < contentlist.size(); i++) { 
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		

		for(int i = 0; i < posts; i++) {
			
			SpaderBean sb = new SpaderBean();
			sb.setWebsite(t.getWebsite());
			sb.setMessage_id(t.getMessage_id());
			sb.setMessage_url(t.getMessage_url());
			sb.setVerb("post");
			sb.setAccount_id(accountIDlist.get(i%accountIDlist.size()));
			sb.setContent(contentlist.get(numbers.get(i%contentlist.size())));
			sb.setStatus("created");
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.setCreate_time(ts);
			spiderlist.add(sb);

		}

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	
	private int kul_count=0;
	private int atusernumber = 1;
	public List<SpaderBean> productPostAT(TaskBean t,
			List<Integer> accountIDlist, List<String> contentlist,List<String> keyuserlist) {
		List<SpaderBean> spiderlist = new ArrayList<SpaderBean>();
		int posts = t.getPost();

		if (posts == 0) {
			return null;
		}

		if ((accountIDlist.size() * contentlist.size()) > posts) {
			int remainder = posts % accountIDlist.size();// ����
			int rs = posts / accountIDlist.size();// ������
			int count = 0;// �����ļ���
			for (int id : accountIDlist) {
				count++;
				// ���ز��б��� ȡ�������
				if (count <= remainder) {
					
					int[] ram = new int[rs + 1];
					int[] samID = random(ram, contentlist.size());
					for (int i : samID) {
						SpaderBean sb = new SpaderBean();
						sb.setWebsite(t.getWebsite());
						sb.setMessage_url("");
						sb.setVerb("post");
						sb.setAccount_id(id);
						
						String atstr = getATUser(keyuserlist);
						
						sb.setContent(contentlist.get(i)+atstr);
						sb.setStatus("created");
						Timestamp ts = new Timestamp(System.currentTimeMillis());
						sb.setCreate_time(ts);
						spiderlist.add(sb);
					}

				} else {
					int[] ram = new int[rs];
					int[] samID = random(ram, contentlist.size());
					for (int i : samID) {
						SpaderBean sb = new SpaderBean();
						sb.setWebsite(t.getWebsite());
						sb.setMessage_url("");
						sb.setVerb("post");
						sb.setAccount_id(id);
						
						String atstr = getATUser(keyuserlist);
						
						sb.setContent(contentlist.get(i)+atstr);
						sb.setStatus("created");
						Timestamp ts = new Timestamp(System.currentTimeMillis());
						sb.setCreate_time(ts);
						spiderlist.add(sb);
					}
				}
			}
		} else {// ������ ���ڵ��� ���֧�ֶ� ���˺������ز����˻�����ֻ�ܰ����֧�ֶȷ���
			for (int id : accountIDlist) {
				for (String s : contentlist) {
					SpaderBean sb = new SpaderBean();
					sb.setWebsite(t.getWebsite());
					sb.setMessage_url("");
					sb.setVerb("post");
					sb.setAccount_id(id);
					sb.setStatus("created");
					
					String atstr = getATUser(keyuserlist);
					
					sb.setContent(s+atstr);
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					sb.setCreate_time(ts);
					spiderlist.add(sb);
				}
			}

		}// ����

		System.out.println("����**����**�������");

		return spiderlist;
	}
	
	
	public String getATUser(List<String> keyuserlist){
		String result = "";
		if((kul_count+atusernumber)<=keyuserlist.size()){
			for(int i = 0;i<atusernumber;i++){
				result+="@"+keyuserlist.get(kul_count+i)+" ";				
			}
			kul_count = kul_count+atusernumber;
		}else{
			int remaind = keyuserlist.size()-kul_count;//�б�ʣ����
			
			for(int i = 0;i<remaind;i++){
				result+="@"+keyuserlist.get(kul_count+i)+" ";
			}
			kul_count=0;
			for(int i = 0;i<atusernumber-remaind;i++){
				result+="@"+keyuserlist.get(kul_count+i)+" ";
			}
			kul_count = kul_count+atusernumber-remaind;
		}
		
		return result;
	}
	

	public int[] random(int[] ram, int idlistsize) {

		int r = 0;
		int count = 0;
		int flag = 0;
		while (count < ram.length) {
			Random random = new Random();
			r = Math.abs(random.nextInt()) % (idlistsize);
			
			for (int i = 0; i < count; i++) {
				if (ram[i] == r) {
					flag = 1;
					break;
				} else {
					flag = 0;
				}
			}
			if (flag == 0) {
				ram[count] = r;
				// System.out.println(r);
				count++;
			}
		}

		return ram;
	}

	public static void main(String[] args) {
		SpaderProductMethod sp = new SpaderProductMethod();
		// int[] ram = new int[10];
		// int[] sam = sp.random(ram,20);
		// for(int i : sam){
		// System.out.println(i);
		// }

		// TaskBean t,List<Integer> accountIDlist

		List<Integer> id = new ArrayList<Integer>();
		id.add(1);
		id.add(13);
		id.add(15);

		TaskBean t = new TaskBean();
		t.setMessage_url("aaa");
		t.setPost(3);

		List<SpaderBean> sb = sp.productLike_FB(t, id);
		if (sb == null) {
			System.out.println("û������");
		} else {
			for (SpaderBean s : sb) {
				System.out.println(s.getAccount_id());
			}
		}

	}

}
