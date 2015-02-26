package tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtility {
	public static BufferedWriter newBufferedWriter(String filepath) throws IOException
	{
		String path = filepath.substring(0,filepath.lastIndexOf("\\"));
		File folder = new File(path);
		if(!folder.exists())
			folder.mkdirs();
		return new BufferedWriter( new FileWriter(filepath) );
	}
}
