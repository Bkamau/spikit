package fi.benson.spikit.notepad;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import fi.benson.spikit.R;
import fi.benson.spikit.R.id;
import fi.benson.spikit.R.layout;
import fi.benson.spikit.R.menu;

public class Notepad extends Activity {
	
	DBAdapter myDb;
	ListView lv;
	Dialog dialog;
	String t, d;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notepad);
		
		lv = (ListView)findViewById(R.id.listView1);
		
		openDB();
		displayRecordSet();
		onListClick();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		closeDB();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		displayRecordSet();
	}
	
	//open the database
	public void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}
	
	// close the database
	private void closeDB() {
		myDb.close();
	}
	/*
	public void onClick_AddRecord(String t, String d) {
		// Add it to the DB and re-draw the ListView
		t = "The Title";
		d = "Some notes to self";
		myDb.insertRow(t, d);
		
	}
	*/
	
	// Delete all records in the database
	public void onClick_ClearAll(View v) {
		myDb.deleteAll();
		displayRecordSet();
		Toast.makeText(this, "Items DELETED", Toast.LENGTH_SHORT).show();
		}

	// Display an entire recordset to the screen.
	@SuppressWarnings("deprecation")                          
	private void displayRecordSet() {
		Cursor cursor = myDb.getAllRows();                     //create the cursor to query database
		
		startManagingCursor(cursor);
		
		String [] fromFieldData = new String[]{                //get the values from the database
				DBAdapter.KEY_TITLE,
			
				DBAdapter.KEY_DETAILS,
				DBAdapter.KEY_ROWID
				};
	    int[] toViewIDs = new int[]{                           // assign the values to fields in a list adapter layout
	    		R.id.textViewTitle,
	    		R.id.textViewDetails,
	    		R.id.textViewID
	    		};
		
		SimpleCursorAdapter  myCursorAdapter = new SimpleCursorAdapter(    //create the adapter for the listview
		
				this,
				R.layout.item_layout,
				cursor,
				fromFieldData,
				toViewIDs
				
				
				);
		lv.setAdapter(myCursorAdapter);                                   //set the listview adapter
	}
	
	
	
	// Set on long lick to delete a database row from the listview
	private void onListClick() {
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				Cursor cursor = myDb.getRow(id);
				if (cursor.moveToFirst()){
					long iddb = cursor.getLong(myDb.COL_ROWID);
					myDb.deleteRow(iddb);
				}
				displayRecordSet();
				
				return false;
			}
		
		});
		
	}
	
	 // Create the menus on Main Activity
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {                        
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);                                // Inflate the menus
		return true;
	}
	
	

	
	//Run a dialog only the first time the app is started
	/*
	public void onCreate(){
	    boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
	    if (firstrun){
	    	showCustomDialog();
	        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
	            .edit()
	            .putBoolean("firstrun", false)
	            .commit();
	    }
	}
	*/
	
	
	
}

