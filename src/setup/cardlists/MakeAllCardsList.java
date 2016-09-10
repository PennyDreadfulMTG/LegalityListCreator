package setup.cardlists;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utility.*;

public class MakeAllCardsList {
	
	//This code is meant to be run using some kind of scheduler.

	public static void main(String[] args) throws Exception{
		
		final int TIMES_CHECKED = 168; //Hours in a week
		//final int TIMES_CHECKED = 1; //For testing

		//Create a list of set abbreviations from the provided file
		ArrayList<String> setAbbr = FileConverter.readToArrayList("MTGO Set Abbreviations");
		int count = FileConverter.readToInt("count.txt");
		
		//In each loop, get a snapshot of all legal card and put it in a file, while also using a "count" file to track how many times you did this
		count++;
		FileConverter.writeFile(getLegalSnapshot(setAbbr), "Run "+count+".txt");
		FileConverter.writeFile(count, "count.txt");
		
		//On the last run only, after all lists have been created:
		if (count == TIMES_CHECKED){
			
		//Makes a map of "card names -> # of times it was at 0.01 tix"
		Map<String, Integer> timesLegalMap = new HashMap<String, Integer>();
		
		//Updates the map with each file that was previously written.
		for (int c = 1; c <= TIMES_CHECKED; c++){
			timesLegalMap = updateMap(FileConverter.readToArrayList("Run "+c+".txt"), timesLegalMap);
		}
		
		//Add all cards that were 0.01 tix 50% or more of the time to an array
		ArrayList<String> legalCards = new ArrayList<String>();
		for (String card: timesLegalMap.keySet()){
			if (timesLegalMap.get(card) >= TIMES_CHECKED/2)
				legalCards.add(card);
		}
		
		//Print the arrays out as usable, readable files
		FileConverter.writeFile(legalCards, "Weeklong Legal Cards.txt");
		System.out.println("File written!");
		}
	}
	
	//Updates the map with the snapshot passed to it.
	private static Map<String, Integer> updateMap(ArrayList<String> snapshot, Map<String, Integer> map){
		for (String card : snapshot){
			if (map.containsKey(card))
				map.put(card, map.get(card)+1);
			else{
				map.put(card,1);
			}
		}
		
		return map;
	}
	
	public static ArrayList<String> getLegalSnapshot(ArrayList<String> setCodes) throws Exception{
		//Create an array to hold and eventually return all the cards found to be at 0.01 tix
		ArrayList<String> legalCards = new ArrayList<String>();
		
		//Add the land-station basics
		legalCards.add("Plains");
		legalCards.add("Island");
		legalCards.add("Swamp");
		legalCards.add("Mountain");
		legalCards.add("Forest");
		
		//Cycle through the "set pages" for every set available on MTGO
		for (String str : setCodes){
			URL thisUrl = new URL("https://www.mtggoldfish.com/index/"+str+"#online");
			
			//Reads the webpage into an array of strings.
			ArrayList<String> webpage = FileConverter.readToArrayList(thisUrl);
			
			ArrayList<String> cardNames = getCardNames(webpage);
			List<Double> cardPrices = getCardPrices(webpage);
			for (int c = 0; c < cardPrices.size(); c++){
				
				//Check for all cards that are 0.01 tix and not already on the list and add them to the list
				if(cardPrices.get(c) == 0.01 && !(legalCards.contains(cardNames.get(c))))
					legalCards.add(cardNames.get(c));
			}
			System.out.println("Legal cards from "+str+" found!");
		}
		System.out.println("Snapshot complete!");
		return legalCards;
	}
	
	public static ArrayList<String> getCardNames(ArrayList<String> html) throws Exception{
		//Create an array to hold all card names and eventually return
		ArrayList<String> cards = new ArrayList<String>();
		
		for (String str : html){
			if (str.length() > 17 && str.substring(0, 17).equals("<td class='card'>")){ //Markers that this line of HTML is a table row
				//Get the locations of the second ">" and the third "<", the card's name is located between them
				int secondClose = str.indexOf('>',str.indexOf('>')+1);
				int thirdOpen = str.indexOf('<',str.indexOf('<',str.indexOf('<')+1)+1);
				String thisCard = str.substring(secondClose+1,thirdOpen);
				
				//Lots of complicated formatting stuff. Don't worry about it.
				String cardname = formatEnglishName(thisCard).replace("&#39;", "'").replace("Lim-Dul","Lim-Dûl");
				
				//MTGGoldfish doesn't include the accent, but it should, so I add it.
				switch (cardname){
				case "Seance": cardname = "Séance";
					break;
				case "Dandan": cardname = "Dandân";
					break;
				case "Khabal Ghoul": cardname = "Khabál Ghoul";
					break;
				case "Junun Efreet": cardname = "Junún Efreet";
					break;
				case "Ghazban Ogre": cardname = "Ghazbán Ogre";
					break;
				case "Ifh-Biff Efreet": cardname = "Ifh-Bíff Efreet";
					break;
				case "Ring of Ma'ruf": cardname = "Ring of Ma'rûf";
					break;
				}
				cards.add(cardname);
				}
	
		}
		return cards;
	}

	
	public static List<Double> getCardPrices(ArrayList<String> html) throws Exception{
		List<Double> prices = new ArrayList<>();
		int linesSinceMarker = -1;
		
		for (String str: html){
			
			if (linesSinceMarker > -1){
				linesSinceMarker ++;
			}
			if (str.length() > 17 && str.substring(0, 17).equals("<td class='card'>")){ //Looks for a marker line
				linesSinceMarker = 0;
			}
			
			//The third line after the marker has the card price
			if (linesSinceMarker == 4){
				prices.add(Double.parseDouble(str));
				linesSinceMarker = -1;
			}
		}
		return prices;
	}
	
	public static String formatEnglishName(String name){
		String formatted = "";
		for (int c = 0; c < name.length(); c++){
			char letter = name.charAt(c);
			
			//Some cards with multiple promo printings have (second promo) or something in parens, this gets rid of that so we don't get duplicates
			if(letter == '('){
				formatted = formatted.trim();
				break;
			}
			else
				formatted += letter;
		}
		return formatted;
	}
	
}