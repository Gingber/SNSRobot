package test;

public class RemainderTest {

	public static void main(String[] args) {

		String url = "https://twitter.com/SarahKSilverman/status/470773154751979521";
		String[] strs = url.split("/");
		for(String s:strs){
			System.out.println(s);
		}

		System.out.println(strs[strs.length-1]);
	}

}
