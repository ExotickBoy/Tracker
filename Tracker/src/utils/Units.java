package utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.function.BiFunction;

public class Units {
	
	private static final String VALUE_4_LONG = "tera";
	private static final String VALUE_3_LONG = "giga";
	private static final String VALUE_2_LONG = "mega";
	private static final String VALUE_1_LONG = "kilo";
	private static final String VALUE_0_LONG = "";
	
	private static final String VALUE_4_SHORT = "T";
	private static final String VALUE_3_SHORT = "G";
	private static final String VALUE_2_SHORT = "M";
	private static final String VALUE_1_SHORT = "k";
	private static final String VALUE_0_SHORT = "";
	
	private static final String KILO_GRAM_LONG = "grams";
	private static final String METRE_LONG = "metres";
	private static final String METRE_PER_SECOND_LONG = "metres per second";
	private static final String METRE_PER_SECOND_PER_SECOND_LONG = "metres per second squared";
	
	private static final String KILO_GRAM_SHORT = "g";
	private static final String METRE_SHORT = "m";
	private static final String METRE_PER_SECOND_SHORT = "m/s";
	private static final String METRE_PER_SECOND_PER_SECOND_SHORT = "m/s²";
	
	private static final String VALUE_4_KILO_GRAM_EXCEPTION_LONG = "tonnes";
	private static final String VALUE_4_KILO_GRAM_EXCEPTION_SHORT = "t";
	
	private static final String FORMAT_STRING = "#,##0.#";
	
	private static final int DEFAULT_STEP = 1000;
	private static final String ILLEGAL_ARGUMENT_MESSAGE = "Must either be Units.SHORT or Units.LONG";
	
	private static final String[] SHORT_PREFIXSE = new String[] { VALUE_0_SHORT, VALUE_1_SHORT, VALUE_2_SHORT, VALUE_3_SHORT, VALUE_4_SHORT };
	private static final String[] LONG_PREFIXSE = new String[] { VALUE_0_LONG, VALUE_1_LONG, VALUE_2_LONG, VALUE_3_LONG, VALUE_4_LONG };
	
	public static final int SHORT = 0;
	public static final int LONG = 1;
	
	public static final Units KILO_GRAM = new Units(KILO_GRAM_SHORT, KILO_GRAM_LONG) {
		{
			
			amountToShort.put(2, (amount, digitGroups) -> {
				
				return new DecimalFormat(FORMAT_STRING).format(amount / Math.pow(stepSize, digitGroups)) + " " + VALUE_4_KILO_GRAM_EXCEPTION_SHORT;
				
			});
			amountToLong.put(2, (amount, digitGroups) -> {
				
				return new DecimalFormat(FORMAT_STRING).format(amount / Math.pow(stepSize, digitGroups)) + " " + VALUE_4_KILO_GRAM_EXCEPTION_LONG;
				
			});
		}
		
		public String formatUnit(double amount, int length) {
			return super.formatUnit(amount * 1000, length);
		}
	};
	public static final Units METRE = new Units(METRE_SHORT, METRE_LONG);
	public static final Units METRE_PER_SECOND = new Units(METRE_PER_SECOND_SHORT, METRE_PER_SECOND_LONG);
	public static final Units METRE_PER_SECOND_PER_SECOND = new Units(METRE_PER_SECOND_PER_SECOND_SHORT, METRE_PER_SECOND_PER_SECOND_LONG);
	
	String shortSuffix;
	String longSuffix;
	
	HashMap<Integer, String> shortPrefixes = new HashMap<>();
	HashMap<Integer, String> longPrefixes = new HashMap<>();
	
	HashMap<Integer, BiFunction<Double, Integer, String>> amountToShort = new HashMap<>();
	HashMap<Integer, BiFunction<Double, Integer, String>> amountToLong = new HashMap<>();
	
	int stepSize;
	
	private Units(String shortSuffix, String longSuffix, int stepSize) {
		
		this.shortSuffix = shortSuffix;
		this.longSuffix = longSuffix;
		this.stepSize = stepSize;
		
		for (int i = 0; i < LONG_PREFIXSE.length; i++) {
			shortPrefixes.put(i, SHORT_PREFIXSE[i]);
			amountToShort.put(i, (amount, digitGroups) -> {
				
				return new DecimalFormat(FORMAT_STRING).format(amount / Math.pow(stepSize, digitGroups)) + " " + shortPrefixes.get(digitGroups) + shortSuffix;
				
			});
			longPrefixes.put(i, LONG_PREFIXSE[i]);
			amountToLong.put(i, (amount, digitGroups) -> {
				
				return new DecimalFormat(FORMAT_STRING).format(amount / Math.pow(stepSize, digitGroups)) + " " + longPrefixes.get(digitGroups) + longSuffix;
				
			});
		}
	}
	
	public Units(String suffix, String longSuffix) {
		
		this(suffix, longSuffix, DEFAULT_STEP);
		
	}
	
	public String formatUnit(double amount, int length) {
		
		int digitGroups = (int) (Math.log10(amount) / Math.log10(stepSize));
		if (digitGroups > SHORT_PREFIXSE.length) {
			digitGroups = SHORT_PREFIXSE.length;
		}
		if (amount == 0) {
			digitGroups = 0;
		}
		
		if (length == SHORT) {
			
			if (amountToShort.containsKey(digitGroups)) {
				
				return amountToShort.get(digitGroups).apply(amount, digitGroups);
				
			} else {
				
				return amountToShort.get(0).apply(amount, digitGroups);
				
			}
			
		} else if (length == LONG) {
			
			if (amountToLong.containsKey(digitGroups)) {
				
				return amountToLong.get(digitGroups).apply(amount, digitGroups);
				
			} else {
				
				return amountToLong.get(0).apply(amount, digitGroups);
				
			}
			
		} else {
			
			throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE);
			
		}
		
	}
	
	public String formatShortUnit(double amount) {
		
		return formatUnit(amount, SHORT);
		
	}
	
	public String formatLongUnit(double amount) {
		
		return formatUnit(amount, LONG);
		
	}
	
}
