package utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
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
	
	public static void writeNameCountMapToFile(Collection<Map.Entry<String, Integer> > cardCounts, String filename) throws IOException {
		PrintWriter pw = new PrintWriter(filename);
		for (Map.Entry<String, Integer> entry : cardCounts)
		{
			pw.println(entry.getKey() + "\t" + entry.getValue());
		};
		pw.close();
	}
	
	public static Map<String, Integer> readToNameCountMap(String filename) throws IOException {
		Map<String, Integer> cardCounts = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = br.readLine();
		
		//BufferedReaders return null at the end of a file, so this cycles through the whole file.
		while (line != null)
		{
			StringTokenizer st = new StringTokenizer(line, "\t");
			if (st.countTokens() >= 2)
			{
				String name = st.nextToken();
				String count_str = st.nextToken();
				int count = 0;
				try
				{
					count = Integer.parseInt(count_str);
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("could not read count for " + name + " from " + count_str);
				}
				cardCounts.put(name, count);
			}
			line = br.readLine();
		}
		br.close();
		return cardCounts;
	}
	

}
