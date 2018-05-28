package plotter;

import java.util.HashMap;

public class Runner {
	public enum State {
	    CONF,
	    SIMS,
	}
	
	private final HashMap<String, Pen> pens = new HashMap<>();
	private final HashMap<String, Motor> motors = new HashMap<>();
	private State status = State.CONF;
	private double time; // simulator time
	private double simTime = 0.1;  // simulator period
	private double logTime = 1.0;  // logging period
	
	public void doStart() {
		if (checkConf("START")) {
			status = State.SIMS;
			if (logTime < simTime) { // senceless
				logTime = simTime;   // set to minimal possible
			}
			time = 0.0;
			motors.values().forEach(m -> m.start());
			pens.values().forEach(p -> p.start());
		}
	}

	public void doStop() {
		if (checkSim("STOP")) {
			motors.values().forEach(m -> m.stop());
			pens.values().forEach(p -> p.stop());
			System.out.println("Done !");
			System.exit(0);
		}
	}
	
	public void doAttach(String motor, String axis, String pen) {
		if (checkConf("ATTACH")) {
			pens.get(pen).attach(motors.get(motor), axis);
		}
	}

	public void createPen(String s) {
		if (checkConf("CREATE PEN"))
			pens.put(s.toUpperCase(), new Pen(s));
	}
	
	public Pen getPen(String s) {
		return pens.get(s.toUpperCase());
	}
	
	public void createMotor(String s) {
		if (checkConf("CREATE MOTOR"))
			motors.put(s.toUpperCase(), new Motor(s));
	}
	
	public Motor getMotor(String s) {
		return motors.get(s.toUpperCase());
	}
	
	public void doWait(int seconds) {
		checkSim("WAIT");
		// .SECONDS.sleep(1); // no need
		Double targetSimTime = time + seconds;
		Double nextLogTime = time + logTime; 
		while (time < targetSimTime) {
			time = Math.round((time + simTime) * 1000.0d) / 1000.0d; // round up to 1/1000
			motors.values().forEach(m -> m.step(time, simTime));
			if ((Math.abs(time - nextLogTime) < 0.001) || (time > nextLogTime)) {
				pens.values().forEach(p -> p.write(time));
				nextLogTime = time + logTime;
			}
		}
	}

	public void setPen(String name, boolean state) {
		pens.get(name).setPen(state, time);
	}

	public void setSim(Double value) {
		if (checkConf("SET SIM TIME")) {
			simTime = value;
		}
	}

	public void setLog(Double value) {
		if (checkConf("SET LOG TIME")) {
			logTime = value;
		}		
	}
	
	public void setMotorSpeed(String name, Double value) {
		motors.get(name).setMaxSpeed(value);
	}

	public void setMotorAccel(String name, Double value) {
		motors.get(name).setMaxAccel(value);		
	}

	public void setMotorPos(String name, Double value) {
		if (checkSim("SET MOTOR TARGET POSITION")) {
			motors.get(name).setTarget(value);
		}		
	}

	public void setMotorNeedStatus(String name, boolean value) {
		motors.get(name).setNeedStatus(value);
	}

	//***************************************************************
	//  UTILITY METHODS
	//***************************************************************
	private boolean checkConf(String s) {
		if (status != State.CONF) {
			Plotter.log("Runner", "Operation " + s + " can run in CONF mode only. Ignored.");
			return false;
		}
		return true;
	}

	private boolean checkSim(String s) {
		if (status != State.SIMS) {
			Plotter.log("Runner", "Operation " + s + " can run in SIMS mode only. Ignored.");
			return false;
		}
		return true;
	}

}
