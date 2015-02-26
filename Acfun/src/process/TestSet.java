package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import dao.TestSetDAO;
import database.ISHCDataOperator;

public class TestSet {

	public static Map<BigDecimal, Emoticon> testSet = new HashMap<BigDecimal, Emoticon> ();
	public static Map<String, Emoticon> emoticon = new HashMap<String, Emoticon> ();
	public static Map<String, Emoticon> triplet = new HashMap<String, Emoticon> ();
	
	public static List<String> emoticonList = new ArrayList<String> ();
	public static List<String> tripletList = new ArrayList<String> ();
	
//	public static void insertIntoDatabase(String path) {
//		File newOne = new File(path);
//		BufferedReader br = null;
//		String line = null;
//		ISHCDataOperator op = new ISHCDataOperator();
//		
//		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(newOne), "UTF-8"));
//			while ((line = br.readLine()) != null) {
//				if (line.length() <= 1)
//					continue;
//				
//				TestSetDAO ts = new TestSetDAO();
//				String[] items = line.split("\t");
//				if (items.length == 3) {
//					// 不含表情符号
//					ts.ID = new BigInteger(items[0]);
//					ts.type = Integer.parseInt(items[1]);
//					ts.content = items[2];
//					op.insert(ts);
//				} else if (items.length == 4) {
//					// 含表情符号
//					char ch = items[0].charAt(0);
//					switch (ch) {
//					case 'h':
//						ts.happiness++;
//						break;
//					case 's':
//						ts.sadness++;
//						break;
//					case 'f':
//						ts.fear++;
//						break;
//					case 'a':
//						ts.anger++;
//						break;
//					case 'd':
//						ts.disgust++;
//						break;
//					case 'u':
//						ts.surprise++;
//						break;
//					case 'l':
//						ts.love++;
//						break;
//					}
//					ts.ID = new BigInteger(items[1]);
//					ts.type = Integer.parseInt(items[2]);
//					ts.content = items[3];
//					ts.occurrence = ts.happiness + ts.sadness + ts.fear + ts.anger
//							+ ts.disgust + ts.surprise + ts.love;
//					op.insert(ts);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			op.emptyCache();
//			op.disconnect();
//		}
//	}
//	
	public static void test() {
		ISHCDataOperator op = new ISHCDataOperator();

		PrintStream out = System.out;
		
		ResultSet rs = op.query("SELECT * FROM test_set LIMIT 0, 10000");
		try {
			while (rs.next()) {
				Emoticon e = new Emoticon();
				e.content = rs.getString(2);
				e.happiness = rs.getInt(4);
				e.sadness = rs.getInt(5);
				e.fear = rs.getInt(6);
				e.disgust = rs.getInt(7);
				e.anger = rs.getInt(8);
				e.surprise = rs.getInt(9);
				e.love = rs.getInt(10);
				e.occurrence = rs.getInt(11);
				testSet.put(rs.getBigDecimal(1), e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int total = 0;
		int truePositive = 0;
		int falsePositive = 0;
		int trueNegative = 0;
		int falseNegative = 0;
		int classificationHit = 0;
		
		for (BigDecimal s : testSet.keySet()) {
			Emoticon testee = testSet.get(s);
			total++;
			boolean found = false;
			// 首先检测是否存在表情符号
			Emoticon emoticonExtracted = null;
			for (String ss : emoticonList) {
				if (testee.content.contains(ss)) {
					emoticonExtracted = emoticon.get(ss);
					found = true;
					break;
				}
			}
			if (!found) {
				for (String ss : tripletList) {
					if (testee.content.contains(ss)) {
						emoticonExtracted = triplet.get(ss);
						found = true;
						break;
					}
				}
			}
			
			if (found && testee.occurrence > 0) {
				truePositive++;
			} else if (found && testee.occurrence == 0) {
				falsePositive++;
				out.println("@@False Positive: {" + emoticonExtracted.content + "} in test set item {"
						+ testee.content + "}");
			} else if (!found && testee.occurrence > 0) {
				falseNegative++;
				out.println("@@False Negative: failed to recongize emoticons in test set item {"
						+ testee.content + "}");
			} else if (!found && testee.occurrence == 0) {
				trueNegative++;
			}
			
			if (found) {
//				int emoticonExtractedJudge = Grading.judge(emoticonExtracted);
//				int testeeJudge = Grading.judge(testee);
//				if ((emoticonExtractedJudge & testeeJudge) > 0) {
//					classificationHit++;					
////					out.println("##Succeeded to classify {" + emoticonExtracted.content + "}:" + emoticonExtractedJudge
////							+ " whereas testee {" + testee.content + "}:" + testeeJudge);
//				} else {
////					out.println("!!Failed to classify {" + emoticonExtracted.content + "}:" + emoticonExtractedJudge
////							+ " whereas testee {" + testee.content + "}:" + testeeJudge);
//				}
			}
		}
		
		out.println("=============Stats=============");
		out.println("Total: " + total);
		out.println("truePositive: " + truePositive);
		out.println("trueNegative: " + trueNegative);
		out.println("falsePositive: " + falsePositive);
		out.println("falseNegative: " + falseNegative);
		out.println("classificationHit: " + classificationHit);
		
		op.disconnect();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		insertIntoDatabase("d:/Platina/Desktop/without_emoticon.txt");
//		insertIntoDatabase("d:/Platina/Desktop/emoticon_.txt");
		
		NewPartsSeparator.initEmoticon();
		emoticon = NewPartsSeparator.emoticon;
		emoticonList = NewPartsSeparator.emoticonList;
		NewPartsSeparator.initTriplet();
		triplet = NewPartsSeparator.triplet;
		tripletList = NewPartsSeparator.tripletList;
		
		test();
	}

}
