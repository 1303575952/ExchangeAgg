package com.zdx.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class FileHandler {
	//private static final Logger logger = LogManager.getLogger(DataFormat.class);

	public static int mkdir(String destDir){
		File dir = new File(destDir);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			if (!dir.exists() || !dir.isDirectory()){
				//logger.error("创建目录失败，请检查是否存在同名文件");
				return 0;
			}
		}
		return 1;
	}

	public static void writeFile(String filePath, String fileContent) {
		try {
			FileWriter fw = new FileWriter(filePath);  
			PrintWriter out = new PrintWriter(fw);  
			out.write(fileContent); 
			out.println();  
			fw.close();  
			out.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getArrayListFromFile(InputStream is){
		//InputStream is = DataFormat.class.getClass().getResourceAsStream("/exchange_name");
		ArrayList<String> exnames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String y ="";
		try {
			while((y = br.readLine())!=null){
				exnames.add(y);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exnames;
	}
}
