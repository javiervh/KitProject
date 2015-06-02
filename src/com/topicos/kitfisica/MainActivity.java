package com.topicos.kitfisica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void lanzarGameVector(View view){
    	Intent i=new Intent(this, VectorGame.class);
    	startActivity(i);
    }
    public void lanzarAcercaDe(View view){
    	Intent i=new Intent(this, AcercaDe.class);
    	startActivity(i);
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	
    	if (id == R.id.acercaDe) {
    		lanzarAcercaDe(null);
    		return true;
    	}
    	
    	return true; 
    }
}
