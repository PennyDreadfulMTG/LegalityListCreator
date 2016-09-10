package utility;

import java.util.ArrayList;
import java.io.*;
import java.net.URL;

public class FileConverter {

	//Convert a file to an array of strings, with each line being an element,
	public static ArrayList<String> readToArrayList(String filename) throws IOException{
		ArrayList<String> toReturn = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = br.readLine();
		
		//BufferedReaders return null at the end of a file, so this cycles through the whole file.
		while (line != null){
			toReturn.add(line);
			line = br.readLine();
		}
		br.close();
		return toReturn;
	}
	
	public static ArrayList<String> readToArrayList(URL url) throws IOException{
		ArrayList<String> toReturn = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = br.readLine();
		
		//BufferedReaders return null at the end of a file, so this cycles through the whole file.
		while (line != null){
			toReturn.add(line);
			line = br.readLine();
		}
		br.close();
		return toReturn;
	}
	
	public static int readToInt(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		int num = Integer.parseInt(br.readLine());
		br.close();
		return num;
	}
	
	//Write an array of strings to a file, with each element on its own line.
	public static void writeFile(ArrayList<String> toWrite, String filename) throws IOException{
		PrintWriter pw = new PrintWriter(filename);
		for (String str : toWrite){
			pw.println(str);
		}
		pw.close();
	}
	
	public static void writeFile(int toWrite, String filename) throws IOException{
		PrintWriter pw = new PrintWriter(filename);
		pw.println(toWrite);
		pw.close();
	}
}
