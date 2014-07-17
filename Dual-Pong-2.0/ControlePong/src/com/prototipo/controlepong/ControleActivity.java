package com.prototipo.controlepong;

import java.io.IOException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class ControleActivity extends Activity {
	//Atributos de conexão
	String message[]; //IP e Player, respectivamente
	Connection connection;
	Handler handler;
	
	//Atributos visuais
	ImageButton botao1, botao2;
	
	//Controle de envio depacotes
	int habilitaEnvio = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controle);
		// Show the Up button in the action bar.
		setupActionBar();
		connection = new Connection(getApplicationContext());
		message = new String[2];
		Intent intent = getIntent();
	    
	    message[0] = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_IP);
	    message[1] = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_PLAYER);
	    
	    botao1 = (ImageButton)findViewById(R.id.imageButtonup);
        botao2 = (ImageButton)findViewById(R.id.imageButtondown);
        
        botao1.setOnTouchListener(MyOnTouchListener);        
        botao2.setOnTouchListener(MyOnTouchListener);
        
        //Gerenciador de threads de conexão
        handler = new Handler() {           
            public void handleMessage(Message msg) {
            	if (msg.what == 1) {
            		              
                } 
            	else if (msg.what == 2) 
            	{
            		
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
		getMenuInflater().inflate(R.menu.controle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void SendUdpCommand(String comando)
	{    	
	   	String datagram = "<pong><com>MOVE</com><action>"+ comando +"</action><player>" + message[1] + "</player></pong>";
	   	try {	   			
	   			connection.SendUdp(handler, datagram , 10405, message[0]);	   			
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
    OnTouchListener MyOnTouchListener = new OnTouchListener()
    {
	   @Override
	   public boolean onTouch(View view, MotionEvent event) {
	    // TODO Auto-generated method stub
	    
	    switch(event.getAction() & MotionEvent.ACTION_MASK)
	    {
		     case MotionEvent.ACTION_DOWN:
		    	 SendUdpCommand(view.getTag().toString());
		    	 habilitaEnvio++;
		    	 return true;
		     
		   	 case MotionEvent.ACTION_POINTER_DOWN:
		     //A non-primary pointer has gone down.
		    	//Toast.makeText(getApplicationContext(), "Pointer Down", Toast.LENGTH_SHORT).show();
		     break;
		      
		     case MotionEvent.ACTION_MOVE:
		    	 //SendUdpCommand(view.getTag().toString());
		     break;
		     
		     case MotionEvent.ACTION_UP:
		    	 habilitaEnvio--;
		    	 if(habilitaEnvio == 0)
		    		 SendUdpCommand("STOP");
		     break;
		     
		     case MotionEvent.ACTION_POINTER_UP:
		    	
		     break;
	    }	    
	    return true;
	    
	   }
	     
	}; 

}

