package com.prototipo.controlepong;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerManager {
	
	 private static Context aContext = null;
	 enum state {UP, DOWN, STOP};
	 
	 /** Accuracy configuration */
	 private static float threshold  = 8.5f, sense = 15;
	 
	 //Detecção de direção
	 private static float lastValue = 0;
	 private static byte motionPerforming = 0, lastDerivate = 0;
	 
	 private static Sensor sensor;
	 private static SensorManager sensorManager;
	 private static AccelerometerListener listener;
	  
	 /** indicates whether or not Accelerometer Sensor is supported */
	 private static Boolean supported;
	 /** indicates whether or not Accelerometer Sensor is running */
	 private static boolean running = false;
	
	 //Retorna o estado do listener
	 public static boolean isListening() {
	      return running;
	 }
	 
	 public static void stopListening() {
	      running = false;
	      try {
	          if (sensorManager != null && sensorEventListener != null)
	              sensorManager.unregisterListener(sensorEventListener);
	          
	      } catch (Exception e) {}
	 }
	 
	public static boolean isSupported(Context context) {
	        aContext = context;
	        if (supported == null) {
	            if (aContext != null) {	                 
	                 
	                sensorManager = (SensorManager) aContext.
	                getSystemService(Context.SENSOR_SERVICE);
	                 
	                // Get all sensors in device
	                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);	                 
	                supported = new Boolean(sensors.size() > 0);     
	                 
	            } else {
	                supported = Boolean.FALSE;
	            }
	        }
	      return supported;
	  }
	 
	 public static void configure(int threshold, float derivate) {
	        AccelerometerManager.threshold = threshold;
	 }
	 
	 public static void startListening( AccelerometerListener accelerometerListener )
	 {
	         
	        sensorManager = (SensorManager) aContext.
	                getSystemService(Context.SENSOR_SERVICE);
	         
	        // Take all sensors in device
	        List<Sensor> sensors = sensorManager.getSensorList(
	                Sensor.TYPE_ACCELEROMETER);
	         
	        if (sensors.size() > 0) {
	             
	            sensor = sensors.get(0);
	             
	            // Register Accelerometer Listener
	            running = sensorManager.registerListener(
	                    sensorEventListener, sensor,
	                    SensorManager.SENSOR_DELAY_GAME);
	             
	            listener = accelerometerListener;
	        }        
	         
	}
	 
	 private static SensorEventListener sensorEventListener =
		        new SensorEventListener() {	
		 
		        private float x = 0;
		 
		        private byte derivate = 0;
		        
		        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		  
		        public void onSensorChanged(SensorEvent event) {
		  
		            x = (float) event.values[0];
		            
		            //Calculando a derivada
		            if(Math.abs(x - lastValue) > sense)
		            {
		            	if(x < lastValue)		            	
		            		derivate = 1;	  
		            	else if(x > lastValue)
		            		derivate = -1;
		            	else
		            		derivate = 0;
		            	
		            	lastValue = x;
		            }		            
		            
		            //Detectando sentido do movimento
		            if(derivate > 0 && derivate != lastDerivate)
		            {
		            	listener.onShake(com.prototipo.controlepong.AccelerometerListener.State.UP);	            	
		            }		
		            	
		            if(derivate < 0 && derivate != lastDerivate)
		            {
		            	listener.onShake(com.prototipo.controlepong.AccelerometerListener.State.DOWN);
		            }	            
		            
		            if(derivate == 0 && derivate != lastDerivate)
		            {
		            	listener.onShake(com.prototipo.controlepong.AccelerometerListener.State.STOP);
		            }	
		        }
		  
		    };
}

