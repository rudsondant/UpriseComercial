package com.prototipo.controlepong;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AccelerometerActivity extends Activity implements AccelerometerListener {
	
	//Atributos de conexão
	String message[]; //IP e Player, respectivamente
	Connection connection;
	Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accelerometer);
		// Show the Up button in the action bar.
		setupActionBar();
		
		connection = new Connection(getApplicationContext());
		message = new String[2];
		Intent intent = getIntent();
	    
	    message[0] = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_IP);
	    message[1] = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_PLAYER);
        
        //Gerenciador de threads de conexão
        handler = new Handler() {           
            public void handleMessage(Message msg) {
            	if (msg.what == 1) {
            		              
                }            	
            }
        };
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accelerometer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onShake(State estado) {			  	
		String datagram = "<pong><com>MOVE</com><action>"+ estado +"</action><player>" + message[1] + "</player></pong>";
		try {	   			
		   		connection.SendUdp(handler, datagram , 10405, message[0]);	   			
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
	}
	
	@Override
    public void onResume() {
            super.onResume();
             
            //Check device supported Accelerometer senssor or not
            if (AccelerometerManager.isSupported(this)) {
                 
                //Start Accelerometer Listening
                AccelerometerManager.startListening(this);
            }
    }
	
	 @Override
	    public void onStop() {
	            super.onStop();
	             
	            //Check device supported Accelerometer senssor or not
	            if (AccelerometerManager.isListening()) {
	                 
	                //Start Accelerometer Listening
	                AccelerometerManager.stopListening();
	            }
	            
	    }
	     
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	         
	        //Check device supported Accelerometer senssor or not
	        if (AccelerometerManager.isListening()) {
	             
	            //Start Accelerometer Listening
	            AccelerometerManager.stopListening();
	        }
	             
	    }
	    
	    

}
