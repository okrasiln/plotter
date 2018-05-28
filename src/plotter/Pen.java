package plotter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Pen {
	
	private final String name;
	private final File logFile;
	private FileOutputStream fos; 
	private boolean isOn = true;
	private Motor mX = null;
	private Motor mY = null;
	
	public Pen(String name) {
		this.name = name;
		logFile = new File("./" + name + ".log");
	}

	public void setPen(boolean state, Double time) {
		isOn = state;
		if (state == false && time > 0) {
			writeLog(time);
		}
	}
	
	public void attach(Motor motor, String axis) {
		if ('X' == axis.toUpperCase().charAt(0)) {
			mX = motor;
		} else {
			mY = motor;
		}
	}
	
	public void start() {
		try {
			fos = new FileOutputStream(logFile);
		} catch (IOException e) {
			Plotter.log("Pen " + name, "Creating Log file: " + e.getMessage());			
		}
		writeLog(0.0);
	}
	
	public void stop() {
		try {
			fos.close();
		} catch (IOException e) {}
	}
	
	public void write(Double time) {
		if (isOn) writeLog(time);
	}
	
	//**************************************************************
	// Utility methods
	//**************************************************************
	
	private void writeLog(Double time) {
		String x = isOn ? (mX == null ? "?" : Plotter.format(mX.getPos())) : "-"; 
		String y = isOn ? (mY == null ? "?" : Plotter.format(mY.getPos())) : "-";
		String s = String.format( "%s; %s; %s\n", Plotter.format(time), x, y);
		try {
			fos.write(s.getBytes());
		}catch (IOException e) {
			Plotter.log("Pen " + name, "Writing Log file: " + e.getMessage());
		}
	}
}
