package setup.cardlists;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.*;

public class MakeAllCardsList {

	//This code is meant to be run using some kind of scheduler.

	public static void main(String[] args) throws Exception{

		final int TIMES_CHECKED = 168; //Hours in a week
		//final int TIMES_CHECKED = 1; //For testing

		//Create a list of set abbreviations from the provided file, and check how many times the program has been run so far.
		List<String> setAbbr = FileConverter.readToList("MTGO Set Abbreviations");
		int count = FileConverter.readToInt("count.txt");

		//Increase the count of how many times this program has been run, and update the text file to reflect that.
		count++;
		FileConverter.writeFile(count, "count.txt");

		if (count <= TIMES_CHECKED){
			//In each valid run, get a snapshot of all legal cards and put it in a file
			FileConverter.writeFile(getLegalSnapshot(setAbbr), "Run "+count+".txt");
		} else if (count == TIMES_CHECKED){ //On the last run only, after all lists have been created:

			FileConverter.writeFile(getLegalSnapshot(setAbbr), "Run "+count+".txt");

			//Makes a map of "card names -> # of times it was at 0.01 tix"
			Map<String, Integer> timesLegalMap = new HashMap<>();

			//Updates the map with each file that was previously written.
			for (int c = 1; c <= TIMES_CHECKED; c++){
				updateMap(FileConverter.readToList("Run "+c+".txt"), timesLegalMap);
			}

			//Add all cards that were 0.01 tix 50% or more of the time to an array
			List<String> legalCards = new ArrayList<>();
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
	private static void updateMap(List<String> snapshot, Map<String, Integer> map){
		for (String card : snapshot){
			map.merge(card, 1, (val, one) -> val + one);
		}
	}

	public static Set<String> getLegalSnapshot(List<String> setCodes) throws Exception{
		//Create an array to hold and eventually return all the cards found to be at 0.01 tix
		Set<String> legalCards = new HashSet<>();

		//Cycle through the "set pages" for every set available on MTGO
		for (String str : setCodes){
			URL thisUrl = new URL("https://www.mtggoldfish.com/index/"+str+"#online");

			//Reads the webpage into an array of strings.
			List<String> webpage = FileConverter.readToList(thisUrl);

			Map<String, Double> cards = getCardNamesAndPrices(webpage);

			cards.entrySet().stream() //
				.filter(e -> e.getValue() == 0.01) //
				.map(Map.Entry::getKey) //
				.forEach(legalCards::add);	
			
			System.out.println("Legal cards from "+str+" found!");
		}

		//Add the land-station basics
		legalCards.add("Plains");
		legalCards.add("Island");
		legalCards.add("Swamp");
		legalCards.add("Mountain");
		legalCards.add("Forest");

		System.out.println("Snapshot complete!");
		return legalCards;
	}

	public static Map<String, Double> getCardNamesAndPrices(List<String> html)  {
		Map<String, Double> cards = new HashMap<>();
		int linesSinceMarker = -1;
		String lastCardFound = null;

		for (String str : html){

			linesSinceMarker++;

			if (str.length() > 17 && str.substring(0, 17).equals("<td class='card'>")){ //Looks for a marker line

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

				// Retain knowledge of the last card we found
				lastCardFound = cardname;

				// Then start counting lines until we hit the proper one.
				linesSinceMarker = 0;
			}

			// If we've found a card, then the third line after the marker has the card price
			if (lastCardFound != null && linesSinceMarker == 4){
				cards.put(lastCardFound, Double.parseDouble(str));
				
				lastCardFound = null;
				linesSinceMarker = -1;
			}
		}

		return cards;
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
