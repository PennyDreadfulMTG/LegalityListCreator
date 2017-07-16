package utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.nio.charset.Charset;
import java.io.*;
import java.net.URL;

public class FileConverter {

	//Convert a file to an array of strings, with each line being an element,
	public static List<String> readToList(String filename) throws IOException{
		List<String> toReturn = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {

			//BufferedReaders return null at the end of a file, so this cycles through the whole file.
			for (String line  = br.readLine(); line != null; line = br.readLine()){
				toReturn.add(line);
			}
		}
		return toReturn;
	}

	public static List<String> readToList(URL url) throws IOException{
		List<String> toReturn = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {

			// BufferedReaders return null at the end of a file, so this cycles through the whole file.
			for (String line  = br.readLine(); line != null; line = br.readLine()){
				toReturn.add(line);
			}
		}
		return toReturn;
	}

	public static int readToInt(String filename) throws IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
			return Integer.parseInt(br.readLine());
		}
	}

	//Write an array of strings to a file, with each element on its own line.
	public static void writeFile(Collection<String> toWrite, String filename) throws IOException{
		try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), Charset.forName("UTF-8")))) {
			for (String str : toWrite){
				pw.println(str);
			}
		}
	}

	public static void writeFile(int toWrite, String filename) throws IOException{
		try(PrintWriter pw = new PrintWriter(filename)) {
			pw.println(toWrite);
		}
	}
}
