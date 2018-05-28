/**
 * 
 */
package plotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 */
public class Plotter {
	private static final NumberFormat NF = new DecimalFormat("###.#");

	public static void main(String[] args) {
		System.out.println("BEGIN");
		Reader reader = new Reader(); // holds parsing logics
		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			br.lines().forEach(s -> reader.parse(s));
		} catch (IOException e) {
			log("Main", e.getMessage());
		}	
	}

	//********************************************
	//
	//********************************************
	
	public static String format(Double d) {
		String s = NF.format((double) Math.round(d * 10.0) / 10.0);
		return s.endsWith(".0") ? s.substring(0, s.length() - 2) + "  " : s;
	}
	
	public static void log(String who, String s) {
		System.err.println(String.format("[%s] %s", who, s));
	}
}
