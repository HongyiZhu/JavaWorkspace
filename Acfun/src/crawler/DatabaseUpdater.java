package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import database.ISHCDataOperator;

public class DatabaseUpdater {
	ISHCDataOperator op;
	String root = "E:/emoticon_research/";	
	
	DatabaseUpdater() {
		op = new ISHCDataOperator();
		upd();
	}
	
	private void upd(){
		File input = new File(root + "89.txt");
		String id;
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			while ((id = br.readLine()) != null) {
				System.out.println(id);
				BigInteger base = new BigInteger("30400000000000000");
				BigInteger bot = new BigInteger(id).multiply(new BigInteger("100000")).add(base);
				BigInteger top = new BigInteger(id).multiply(new BigInteger("100000")).add(new BigInteger("100000")).add(base);
				op.update("UPDATE `danmaku_health` SET `category`= '2' WHERE `id` >='" + bot + "' AND `id` < '" + top + "'");
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			op.printTerminatePoint();
			op.disconnect();
		}
	}
	
	
	public static void main(String[] args) {
		DatabaseUpdater du = new DatabaseUpdater();
	}

}
