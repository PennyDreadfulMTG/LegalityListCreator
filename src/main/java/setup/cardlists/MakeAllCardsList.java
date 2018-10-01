package setup.cardlists;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import analysis.ChangeAnalyzer;
import utility.*;

public class MakeAllCardsList {

	//This code is meant to be run using some kind of scheduler.
	private static boolean test = false;

	public static void main(String[] args) throws Exception {

		final int TIMES_CHECKED = (test ? 1 : 168); // 168 hours per week

		//Create a list of set abbreviations from the provided file, and check how many times the program has been run so far.
		List<String> setAbbr;

		if (test) {
			if (!new File("MTGO_Set_Abbreviations_test").exists())
			{
				setAbbr = FileConverter.readToList(MakeAllCardsList.class.getClassLoader().getResource("MTGO_Set_Abbreviations_test"));
			}
			else {
				setAbbr = FileConverter.readToList("MTGO_Set_Abbreviations_test");
			}
		} else {
			if (!new File("MTGO_Set_Abbreviations").exists())
			{
				setAbbr = FileConverter.readToList(MakeAllCardsList.class.getClassLoader().getResource("MTGO_Set_Abbreviations"));
			}
			else {
				setAbbr = FileConverter.readToList("MTGO_Set_Abbreviations");
			}
		}

		if (!new File("Count.txt").exists()) {
			FileConverter.writeFile(0, "Count.txt");
		}
		int count = FileConverter.readToInt("Count.txt");

		//Increase the count of how many times this program has been run, and update the text file to reflect that.
		count++;

		if (count <= TIMES_CHECKED) {
			//In each valid run, get a snapshot of all legal cards and put it in a file
			FileConverter.writeFile(getLegalSnapshot(setAbbr), "Run_"+String.format("%03d", count)+".txt");
		}
		if (count == TIMES_CHECKED) { //On the last run only, after all lists have been created:

			//Makes a map of "card names -> # of times it was at 0.01 tix"
			Map<String, Integer> timesLegalMap = new HashMap<>();

			//Updates the map with each file that was previously written.
			for (int c = 1; c <= TIMES_CHECKED; c++) {
				updateMap(FileConverter.readToList("Run_" + String.format("%03d", c) + ".txt"), timesLegalMap);
			}

			//Add all cards that were 0.01 tix 50% or more of the time to an array
			List<String> legalCards = new ArrayList<>();
			for (String card : timesLegalMap.keySet()) {
				if (timesLegalMap.get(card) >= TIMES_CHECKED / 2)
					legalCards.add(card);
			}

			//Add to an array all cards that were equal or below $1 50% or more of the time
			List<String> dollarLegalCards = new ArrayList<>();
			for (String card : timesLegalMap.keySet()) {
				if (timesLegalMap.get(card) >= TIMES_CHECKED / 2)
					dollarLegalCards.add(card);
			}

			//Print the arrays out as usable, readable files
			legalCards.retainAll(dollarLegalCards);
			FileConverter.writeFile(legalCards, "legal_cards.txt");
			System.out.println("File written!");
			System.out.println("Starting Analysis...");
			ChangeAnalyzer.main(new String[0]);

		}
		FileConverter.writeFile(count, "Count.txt");
	}

	//Updates the map with the snapshot passed to it.
	private static void updateMap(List<String> snapshot, Map<String, Integer> map) {
		for (String card : snapshot) {
			map.merge(card, 1, (val, one) -> val + one);
		}
	}

	private static Set<String> getLegalSnapshot(List<String> setCodes) throws Exception{
		//Create an array to hold and eventually return all the cards found to be at 0.01 tix
		Set<String> legalCards = new HashSet<>();

		//Cycle through the "set pages" for every set available on MTGO
		for (String str : setCodes) {
			URL mtgoUrl = new URL("https://www.mtggoldfish.com/index/" + str + "#online");
			URL paperUrl = new URL("https://www.mtggoldfish.com/index/" + str + "#paper");

			//Reads the webpage into an array of strings.
			List<String> mtgoWebpage = FileConverter.readToList(mtgoUrl);
			List<String> paperWebpage = FileConverter.readToList(paperUrl);

			Map<String, Double> mtgoCards = getCardNamesAndPrices(mtgoWebpage);
			Map<String, Double> paperCards = getCardNamesAndPrices(paperWebpage);

			mtgoCards.entrySet().stream() //
				.filter(e -> e.getValue() == 0.01) //
				.map(Map.Entry::getKey) //
				.forEach(legalCards::add);

			Set<String> paperLegalCards = new HashSet<>();
			paperCards.entrySet().stream() //
				.filter(e -> e.getValue() <= 1) //
				.map(Map.Entry::getKey) //
				.forEach(paperLegalCards::add);
			legalCards.retainAll(paperLegalCards);

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

	private static Map<String, Double> getCardNamesAndPrices(List<String> html)  {
		Map<String, Double> cards = new HashMap<>();
		int linesSinceMarker = -1;
		String lastCardFound = null;

		for (String str : html) {

			linesSinceMarker++;

			if (str.length() > 17 && str.substring(0, 17).equals("<td class='card'>") &&
					((str.indexOf("#online\">") > -1) || (str.indexOf("#paper\">") > -1))) { //Looks for a marker line

				//Get the locations of the second ">" and the third "<", the card's name is located between them
				int secondClose = str.indexOf('>',str.indexOf('>')+1);
				int thirdOpen = str.indexOf('<',str.indexOf('<',str.indexOf('<')+1)+1);
				String thisCard = str.substring(secondClose+1,thirdOpen);

				//Lots of complicated formatting stuff. Don't worry about it.
				String cardname = formatEnglishName(thisCard).replace("&#39;", "'").replace("Lim-Dul","Lim-Dûl").replace("Jotun","Jötun");

				//MTGGoldfish doesn't include the accent, but it should, so I add it.
				switch (cardname) {
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
			if (lastCardFound != null && linesSinceMarker == 4) {
				cards.put(lastCardFound, Double.parseDouble(str));

				lastCardFound = null;
				linesSinceMarker = -1;
			}
		}

		return cards;
	}

	private static String formatEnglishName(String name) {
		String formatted = "";
		for (int c = 0; c < name.length(); c++) {
			char letter = name.charAt(c);

			//Some cards with multiple promo printings have (second promo) or something in parens, this gets rid of that so we don't get duplicates
			if (letter == '(') {
				formatted = formatted.trim();
				break;
			}
			else
				formatted += letter;
		}
		return formatted;
	}

}
