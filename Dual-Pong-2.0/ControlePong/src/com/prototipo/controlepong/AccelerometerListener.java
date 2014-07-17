package com.prototipo.controlepong;

public interface AccelerometerListener {
	
	public enum State {UP, DOWN, STOP};
    
    public void onAccelerationChanged(float x, float y, float z);
  
    public void onShake(State estado);
  
}
