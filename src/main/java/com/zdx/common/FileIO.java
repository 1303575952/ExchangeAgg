package com.zdx.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileIO {
	public static String ReadFile(String path){
		String laststr="";
		File file=new File(path);// 打开文件  
		BufferedReader reader=null;
		try{
			FileInputStream in = new FileInputStream(file);
			reader=new BufferedReader(new InputStreamReader(in,"UTF-8"));// 读取文件  
			String tempString=null;
			while((tempString=reader.readLine())!=null){
				laststr=laststr+tempString;
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException el){
				}  
			}  
		}
		return laststr;
	}

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
}
