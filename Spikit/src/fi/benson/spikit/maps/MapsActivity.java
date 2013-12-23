package fi.benson.spikit.maps;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import fi.benson.spikit.R;

@SuppressLint("ShowToast")
public class MapsActivity extends FragmentActivity {
         private static final int SPLASH_TIME       = 4 * 1000;// 5 seconds
        private static final int HYBRID_CODE        = 1234;
        private static final int MY_DATA_CHECK_CODE = 1322;
        private static final int SATELLITE_CODE     = 1235;
        private static final int TERRAIN_CODE       = 1236;
        private TextToSpeech mapsToSpeech;
    GoogleMap myMap;
        
        

        Location location;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_maps);
                
                
                myMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
         myMap.setMyLocationEnabled(true);
                
                
                
                if (myMap == null) {
                    Toast.makeText(this, "Google Maps not available", 
                        Toast.LENGTH_LONG).show();
                
                }
                
                try {
                    new Handler().postDelayed(new Runnable() {
             
                        public void run() {
                                Toast.makeText(MapsActivity.this, "Spikit Maps", Toast.LENGTH_LONG).show();
                                
                                
                                
                               // mapTypeRecognition();
                            
                        }
                         
                         
                    }, SPLASH_TIME);
                     
                    new Handler().postDelayed(new Runnable() {
                          public void run() {
                                 } 
                            }, SPLASH_TIME);
                    } catch(Exception e){}
                  
                
                }
                


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                  // First Menu Button
        menu.add("HYBRID")
                .setOnMenuItemClickListener(this.hybrid)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
 
        // Second Menu Button
        menu.add("SATELLITE")
                .setOnMenuItemClickListener(this.setellite)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
 
        // Third Menu Button
        menu.add("TERRAIN")
                .setOnMenuItemClickListener(this.terrain)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
        }
        OnMenuItemClickListener hybrid = new OnMenuItemClickListener() {
                
                
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                         
                        return false;
                }
        };
        OnMenuItemClickListener setellite = new OnMenuItemClickListener() {
                
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        return false;
                }
        };
        OnMenuItemClickListener terrain = new OnMenuItemClickListener() {
                
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                        myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        return false;
                }
        };
        private void mapTypeRecognition()
        {
                Intent hybridRecognitionintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                hybridRecognitionintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                hybridRecognitionintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                hybridRecognitionintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "...Give Title...");
                startActivityForResult(hybridRecognitionintent, HYBRID_CODE);
        }




        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                switch (requestCode) {
                case HYBRID_CODE:
                        ArrayList<String> hybridPredictions = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String wholeWord = hybridPredictions.get(0);
                        if(wholeWord.contains("hybrid")){
                                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        }
                        
                        break;

                default:
                        break;
                }
                
                
                
                super.onActivityResult(requestCode, resultCode, data);
        }
        
        


        
        

        
}