package org.kleemann.diceprobabilities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private PoolDicePile pd12;
	private PoolDicePile pd10;
	private PoolDicePile pd8;
	private PoolDicePile pd6;
	private PoolDicePile pd4;
	private PoolDicePile pd1;
	
	private CurrentDicePile cd12;
	private CurrentDicePile cd10;
	private CurrentDicePile cd8;
	private CurrentDicePile cd6;
	private CurrentDicePile cd4;
	private CurrentDicePile cd1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		Context c = getApplicationContext();
		
		// TODO: not sure if this is where I should create ui elements that are not defined by xml
		ViewGroup container = (ViewGroup)findViewById(R.id.dice_pool);
		pd12 = new PoolDicePile(12,c,container);
		pd10 = new PoolDicePile(10,c,container);
		pd8 = new PoolDicePile(8,c,container);
		pd6 = new PoolDicePile(6,c,container);
		pd4 = new PoolDicePile(4,c,container);
		pd1 = new PoolDicePile(1,c,container);
		
		ViewGroup container2 = (ViewGroup)findViewById(R.id.dice_current);
		cd12 = new CurrentDicePile(12,c,container2);
		pd12.setIncrementer(cd12);
		cd10 = new CurrentDicePile(10,c,container2);
		pd10.setIncrementer(cd10);
		cd8 = new CurrentDicePile(8,c,container2);
		pd8.setIncrementer(cd8);
		cd6 = new CurrentDicePile(6,c,container2);
		pd6.setIncrementer(cd6);
		cd4 = new CurrentDicePile(4,c,container2);
		pd4.setIncrementer(cd4);
		cd1 = new CurrentDicePile(1,c,container2);
		pd1.setIncrementer(cd1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
