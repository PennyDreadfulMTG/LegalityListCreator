package utility;

import java.util.ArrayList;

public class SetOperator {

	public static ArrayList<String> aWithoutB(ArrayList<String> a, ArrayList<String> b){
		ArrayList<String> toReturn = new ArrayList<String>();
		
		for (String element : a){
			if (!(b.contains(element)))
				toReturn.add(element);
		}
		
		return toReturn;
	}
}
