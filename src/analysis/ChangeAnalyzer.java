package analysis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import utility.FileConverter;
import utility.SetOperator;

public class ChangeAnalyzer {

	//File holding the old legality list
	final static String OLD_FILE_PATH = "EMN_legal_cards.txt";
	//File holding the new one
	final static String FILE_PATH = "legal_cards.txt";
	
	public static void main(String[] args) throws IOException{
		//Read the lists to arrays so we can use them
		ArrayList<String> oldList = FileConverter.readToArrayList(OLD_FILE_PATH);
		ArrayList<String> newList = FileConverter.readToArrayList(FILE_PATH);
		
		//Create a printwriter to spit out all this info for us.
		PrintWriter pw = new PrintWriter("stats.txt");

		//Find all cards that weren't legal last rotation.
		ArrayList<String> entering = SetOperator.aWithoutB(newList, oldList);
		pw.println("Entering Cards:");
		for (String card: entering){
			pw.println(card);
		}
		
		//Find all cards that have been priced out of the format.
		ArrayList<String> leaving = SetOperator.aWithoutB(oldList, newList);
		pw.println("\nLeaving Cards:");
		for (String card: leaving){
			pw.println(card);
		}
		
		//Find the total change in how many cards are legal (should normally be positive, given that newly printed cards should enter the format)
		pw.println("\nNet change of "+(newList.size()-oldList.size())+" cards.");
		
		//Calculate the percent of cards from the old list that aren't legal anymore, and format it as a percentage to two decimal points.
		String percentLeaving = String.format("%3.2f",(double)(leaving.size())/(double)(oldList.size())*100.0);
		//Calculate the percentage of cards now legal that weren't legal before, and format it as a percentage to two decimal points.
		String percentEntering = String.format("%3.2f", (double)(entering.size())/(double)(newList.size())*100.0);
		
		//Print out the previously calculated values
		pw.println(percentLeaving+"% of cards rotated out.");
		pw.println(percentEntering+"% of cards are new.");
		
		//Close the printwriter.
		pw.close();
	}
}
