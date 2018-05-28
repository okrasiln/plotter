package plotter;


public class Motor {
	private final String name;
	private boolean needStatus = false;
	private Double maxSpeed = 1.0;  
	private Double maxAccel = 1.0;  
	private Double targetPos = 0.0;  
	private Double p = 0.0;  
	private Double v = 0.0;  
	private Double a = 0.0;  
	private Double t = 0.0;  

	public Motor(String name) {
		this.name = name;
	}
	
	public void setMaxSpeed(Double value) {
		maxSpeed = value;
	}

	public void setMaxAccel(Double value) {
		maxAccel = value;
	}

	public void setTarget(Double value) {
		targetPos = value;
	}

	public void setNeedStatus(boolean value) {
		needStatus = value;
	}

	public double getPos() {
		return p;
	}
	
	public void step(Double time, Double dT) {
		t = time; 
		a = calcAcc(p, targetPos, maxAccel, dT);
		p = p + v * dT + a * dT * dT;
		
		v = v + a * dT;
		if (v > maxSpeed) { // Limit max speed
			v = maxSpeed;
		} else if (v < -maxSpeed) {
			v = -maxSpeed;			
		}
		if (needStatus) {
			writeStatus();
		}
	}
	
	public void start() {} // does nothing for now
	
	public void stop() {} // does nothing for now
	
	//****************************************
	//    Calculates new acceleration value
	//****************************************
	
	private Double calcAcc(Double p, Double tp, Double aMax, Double dT) {
		Double _p = p + v * dT; // new position w/o acceleration
		Double _a = aMax * dT * dT; // acceleration addendum
		Double pP = Math.abs(_p + _a - tp); // distance to target point with +acc
		Double pN = Math.abs(_p - _a - tp); // the same, with -acc
		Double p0 = Math.abs(_p - tp);      // the same, without acc  

		if(p0 < pP && p0 < pN){
		    return 0.0; // zero acc
		}else if(pP < pN && pP < p0){
		    return aMax;  // +acc
		}else{
		    return -aMax; // -acc
		}
	}
	
	private void writeStatus() {
		System.out.println(String.format("Motor %s T=%s P=%s V=%s A=%s PT=%s", 
			name, Plotter.format(t), Plotter.format(p), Plotter.format(v), 
			Plotter.format(a), Plotter.format(targetPos) ));
	}
}
