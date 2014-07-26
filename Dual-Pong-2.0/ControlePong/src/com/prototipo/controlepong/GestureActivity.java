package com.prototipo.controlepong;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class GestureActivity extends Activity {
	
	//Atributos de conexão
	String message[]; //IP e Player, respectivamente
	Connection connection;
	Handler handler;
	//Atributos de controle gestual
	int lastY = 0, lastDerivate = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture);
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
		getMenuInflater().inflate(R.menu.gesture, menu);
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
	public boolean onTouchEvent(MotionEvent event) {		
	    int y = (int)event.getY();
	    switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	lastY = y;	        	
	        	break;
	        case MotionEvent.ACTION_MOVE:
	        	int derivate = Derivate(y);
	        	
	        	if(derivate != lastDerivate)
	        	{
	        		if(derivate > 0)
	        			SendCommand("UP");		        	
		        	if(derivate < 0)		       
		        		SendCommand("DOWN");
		        	if(derivate == 0)//PODE SER REMOVIDO?		        		   
			        	SendCommand("STOP");
		        	lastDerivate = derivate;
	        	}
	        	break;
	        case MotionEvent.ACTION_UP:	   
	        	SendCommand("STOP");
	        	lastY = 0;
	        	lastDerivate = 0;
	        
	    }
	return false;
	}

	private int Derivate(int current)
	{	
		if(current > lastY)			
			return -1;
		else{
			if(current < lastY)
				return 1;
			else
				return 0;
		}
	}
	
	private void SendCommand(String command)
	{
		String datagram = "<pong><com>MOVE</com><action>"+ command +"</action><player>" + message[1] + "</player></pong>";
		try {	   			
	   		connection.SendUdp(handler, datagram , 10405, message[0]);	   			
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

}
