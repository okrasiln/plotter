/**
 * 
 */
package plotter;
/**
 * Syntax analyse of input commands
 */
public class Reader {
	private final Runner runner = new Runner();
	private int count = 0;
	
	public void parse(String s) {
		if (s == null || s.trim().isEmpty()) {
			return; // ignore empty lines
		}
		count++; // operators counter
		System.out.println(count + "\t" + s);
		String[] words = s.split(" ");
		switch (words[0].toUpperCase()) {
		case "START":
			runner.doStart();
			break;
		case "STOP":
			runner.doStop();
			break;
		case "CREATE":
			processCreate(words);
			break;
		case "SET":
			processSet(words, s);
			break;
		case "ATTACH":
			processAttach(words, s);
			break;
		case "WAIT":
			processWait(words);
			break;
		default:
			System.err.println("Bad keyword: " + s); 
		}
	}
	
	private void processWait(String[] words) {
		if (words.length < 2) {
			log("No parameter for " + words[0]);
		} else {
			try {
				runner.doWait(Integer.parseInt(words[1]));
			} catch (NumberFormatException e) {
				log("Not-integer argument: " + words[0] + " " + words[1]);
			}
		}
	}
	
	private void processCreate(String[] words) {
		if (words.length < 3) {
			log("Not enougth parameters for " + words[0]);
		} else {
			switch (words[1].toUpperCase()) {
			case "MOTOR":
				if (runner.getMotor(words[2]) == null) {
					runner.createMotor(words[2]);
				} else {
					log("Duplicate Motor name: " + words[2]);
				}
				break;
			case "PEN":
				if (runner.getPen(words[2]) == null) {
					runner.createPen(words[2]);
				} else {
					log("Duplicate Pen name: " + words[2]);
				}
				break;
			default:
				log("Bad parameter: " + words[0] + " " + words[1]); 
			}
		}	
	}

	private void processSet(String[] words, String s) {
		switch (words[1].toUpperCase()) {
		case "SIM":
		case "LOG":
			processSetSimLog(words, s);
			break;
		case "MOTOR":
			processSetMotor(words, s);
			break;
		case "PEN":
			if (words.length < 4) {
				log("Not enougth parameters for " + s);
			} else if (runner.getPen(words[2]) == null) {
				log("Unknown Pen: " + words[2] + " in " + s);
			} else if ("ON".equals(words[3].toUpperCase())) { 
				runner.setPen(words[2], true);
			} else if ("OFF".equals(words[3].toUpperCase())){
				runner.setPen(words[2], false);
			} else {
				log("Bad parameter: " + words[3] + " in " + s);
			}
			break;
		default:
			log("Bad parameter: " + words[0] + " " + words[1] + " in " + s); 
		}
	}

	private void processAttach(String[] words, String s) {
		if (words.length < 6) {
			log("Not enougth  parameters for " + words[0]);
		} else if (! ("WITH".equals(words[2].toUpperCase()) && "OF".equals(words[4].toUpperCase()))) {
			log("Bad syntax: " + s);
		} else if (runner.getMotor(words[1]) == null) {
			log("Unknown Motor: " + words[1] + " in " + s);
		} else if (! ("X".equals(words[3].toUpperCase()) || "Y".equals(words[3].toUpperCase()))) {
			log("Unknown Axis: " + words[3] + " in " + s);
		} else if (runner.getPen(words[5]) == null) {
			log("Unknown Pen: " + words[5] + " in " + s);
		} else { // OK !
			runner.doAttach(words[1], words[3], words[5]);
		}
	}
	
	private void processSetSimLog(String[] words, String s) {
		if (words.length < 3) {
			log("Not enougth parameters for " + s);
		} else if (! words[2].startsWith("dT=")) {
			log("Bad 3rd parameter: " + words[2] + " in " + s);
		} else try {
			Double value = Double.parseDouble(words[2].substring(3));
			if ("SIM".equals(words[1].toUpperCase())) {
				runner.setSim(value);
			} else {
				runner.setLog(value);
			}
		} catch (NumberFormatException e) {
			log("Bad digital value: " + words[2] + " in " + s);			
		}
	}

	private void processSetMotor(String[] words, String s) {
		if (words.length < 4) {
			log("Not enougth parameters for " + s);
		} else if (runner.getMotor(words[2]) == null) {
			log("Unknown Motor: " + words[2] + " in " + s);
		} else {
			int n = words[3].indexOf("=");
			if (n < 0) {
				log("Bad 4th parameter: " + words[3] + " in " + s);
			} else try {
				Double v = Double.parseDouble(words[3].substring(n + 1));
				switch (words[3].substring(0, n).toUpperCase()) {
				case "S": 
					runner.setMotorSpeed(words[2], v); 
					break;
				case "A": 
					runner.setMotorAccel(words[2], v); 
					break;
				case "P": 
					runner.setMotorPos(words[2], v); 
					break;
				case "W": 
					runner.setMotorNeedStatus(words[2], Math.round(v) != 0); 
					break;
				default: log("Unknown setMotor parameter: " + words[3] + " in " + s);
				}
			} catch (NumberFormatException e) {
				log("Bad digital value: " + words[3] + " in " + s);							
			}
		} 
	}
	
	private void log(String s) {
		Plotter.log("Reader line " + count, s);
	}
}
