package tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GeneralUtility {
	public static String currTime()
	{
		return new SimpleDateFormat("[yyyy.MM.dd,HH:mm:ss]").format(Calendar.getInstance().getTime());
	}
	
	public long DJBHash(String str) {
		long hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}
		return hash;
	}
}
