package io.neurolab.utilities;

import java.util.HashMap;

public class NeuroUtils {
	
	public static HashMap<String,Float> getHexLookUpTable(float startVal, float endVal, int val) {
		
		HashMap<String,Float> lookUpTable = new HashMap<String, Float>();
		
		float range = Math.abs(startVal - endVal); 
		
		float step = range / (float)val;
		float curVal = startVal;
				
		for (int i = 0; i < val+1; i++) {
		    String hex = Integer.toHexString(i);
		    while (hex.length()<3)
		    	hex="0"+hex;
		    lookUpTable.put(hex, curVal);
		    curVal += step;
		}
		return lookUpTable;
	}
	
	public static long parseUnsignedHex(String text) {
		try {
			return Long.parseLong(text, 16);
		}
		catch (NumberFormatException e) {
			System.err.println("tried to interpret a non-valid hexadecimal value! (string: '" + text + "')");
		}
		return 0l;
	}

	public static HashMap<String, Float> getBrainduinoDefaultLookupTable() {
		int val = 1023;
		float startVal = -100f;
		float endVal = 100f;
		HashMap<String,Float> lookUpTable = getHexLookUpTable(startVal, endVal, val);
		return lookUpTable;
	}

}
