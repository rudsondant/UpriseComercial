package com.prototipo.controlepong;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends Activity {
	//Variáveis globais
	Connection connection;
	Handler handler;
	String remoteIp, playerNumber;	
	
	public final static String EXTRA_MESSAGE_IP = "com.prototipo.pongcontrole.MESSAGE1";
	public final static String EXTRA_MESSAGE_PLAYER = "com.prototipo.pongcontrole.MESSAGE2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		connection = new Connection(getApplicationContext());
		
		//Rever tratamento de respostas no futuro 
        handler = new Handler() {           
            public void handleMessage(Message msg) {
            	if (msg.what == 1) {
                    Toast.makeText(getApplicationContext(), "Buscando servidor...", Toast.LENGTH_SHORT).show();                    
                } 
            	else if (msg.what == 2) 
            	{
            		String packet[] = new String[2]; 
            		packet = (String[]) msg.obj;
                	remoteIp = packet[0].replace("/", "");   
                	try {
						playerNumber = SelectPlayer(packet[1]);
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
                	
                	Toast.makeText(getApplicationContext(),"Servidor encontrado em: " + remoteIp + " Player: " + playerNumber, Toast.LENGTH_SHORT).show();
                	
                	//Chamando a activity do controle (botão ou acelerômetro)
                	RadioButton radioGesture = (RadioButton) findViewById(R.id.gesture_radio);
                	RadioButton radioAccelerometer = (RadioButton) findViewById(R.id.accelerometer_radio); 
                	Intent intent;
                	
                	if(radioGesture.isChecked())
                	  	intent = new Intent(MainActivity.this, GestureActivity.class);	
                	else
                	{
                		if(radioAccelerometer.isChecked())
                			intent = new Intent(MainActivity.this, AccelerometerActivity.class);
                		else
                			intent = new Intent(MainActivity.this, ControleActivity.class);
                	}               		                	
                	
                	intent.putExtra(EXTRA_MESSAGE_IP, remoteIp);
                	intent.putExtra(EXTRA_MESSAGE_PLAYER, playerNumber);
                    startActivity(intent);
                }
            }
        };
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_exit:
	        	System.exit(0);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void SearchForServer(View view)
	{		
		try 
		{
			connection.SendBroadcastRequest(handler, "<pong><com>SEARCH</com></pong>", 10405);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String SelectPlayer(String packet) throws XmlPullParserException, IOException
	{
		String player = null;
		XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
		XmlPullParser myParser = xmlFactoryObject.newPullParser();			
		myParser.setInput(new StringReader(packet));
		String text = null;
		
        int event = myParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
        	String name = myParser.getName();
        	
	        	switch (event)
	        	{
		            case XmlPullParser.START_TAG:
		            break;
		            case XmlPullParser.TEXT:
		            text = myParser.getText();
		            break;
	
		            case XmlPullParser.END_TAG:
		            	if(name.equals("player"))
		            	{		            		
		            		player = text;
		            	}
	               	break;
               }		 
	        	
               event = myParser.next(); 
           }
		
		return player;
	}
}
