package fi.benson.spikit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import fi.benson.spikit.camera.SpikitCamera;
import fi.benson.spikit.maps.MapsActivity;
import fi.benson.spikit.notepad.DBAdapter;
import fi.benson.spikit.notepad.Notepad;

/**
 * 
 * @author Benson Kamau
 * Spikit App
 * Utilising both speech recognition and TTS
 *
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class Spikit extends Activity {

	DBAdapter myDb = new DBAdapter(this);
	Notepad   myNP;
	
	private static final int REQUEST_CODE      = 1234;
	private static final int NOTEPAD_CODE      = 1235;
	private static final int TITLE_CODE        = 1236;
	private static final int BODY_CODE         = 1237;
	private static final int MY_DATA_CHECK_CODE= 1230; 
	
	private TextToSpeech mTextToSpeech;
	
    ImageButton speakButton;
    Animation hyperspaceJumpAnimation;
    String t, d;
    String[] mapTypes;
 
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spikit);

		 speakButton = (ImageButton) findViewById(R.id.imageButton1);
		 
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.button_nim);
	    hyperspaceJumpAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);

		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();	
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0)
		{
			Toast.makeText(getApplicationContext(),"Voice Engine not Installed", Toast.LENGTH_LONG).show();
		}


		// Fire off an intent to check if a TTS engine is installed
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	
		openDB();
	}

	/**
	 * Handle the action of the button being clicked
	 */
		public void speakButtonClicked(View v)
	{	
		speakButton.startAnimation(hyperspaceJumpAnimation);
		Recognition(REQUEST_CODE);
	}
	

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	
	private void Recognition(int code)
	{
		Intent noteRecognitionintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		noteRecognitionintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		noteRecognitionintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "... listening...");
		startActivityForResult(noteRecognitionintent, code);
	}
	
	
	/**
	 * open the database
	 */
	public void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}
	
	/**
	 * close the database
	 */
	private void closeDB() {
		myDb.close();
	}
	
	/**
	 *  Add it to the DB and re-draw the ListView
	 */
	public void onClick_AddRecord() {
	
		myDb.insertRow(t, d);
		
	}

	
	/**
	 * Handle the results from the voice recognition activity.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
			
		switch (requestCode) {
		case REQUEST_CODE:
			            					
			//get the list of predictions
						
			ArrayList<String> listOfPredictions = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
						
			String wholeWord = listOfPredictions.get(0);
						
			// remove the word google and search the rest of the text 
		if(wholeWord.contains("Google")|| wholeWord.contains("google")){
							
			// we replace the word google with ""
			wholeWord = wholeWord.replace("Google", "");
							
			Toast.makeText(getApplicationContext(),"Googling "+ wholeWord, Toast.LENGTH_LONG).show();
			mTextToSpeech.speak("opening google to find " +wholeWord, TextToSpeech.QUEUE_FLUSH, null);
							
			//create and search the url using the rest of the word(s) in a web browser
			String url = "https://www.google.fi/search?q=" + wholeWord;
			Intent webSearch = new Intent(Intent.ACTION_VIEW);
			webSearch.setData(Uri.parse(url));
			startActivity(webSearch);
						
				// is called when the user wants to use notepad
		}else if(wholeWord.contains("open facebook") || wholeWord.contains("start facebook")){
							
			// we replace the word google with ""
			Intent intent = new Intent("android.intent.category.LAUNCHER");
			intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
			startActivity(intent);
			mTextToSpeech.speak("Opening facebook" , TextToSpeech.QUEUE_FLUSH, null);
							
		}else if(wholeWord.contains("note")|| wholeWord.contains("Note")){
			String  notePadChoise = "Would you like to add a note";	
			noteInteract(notePadChoise,NOTEPAD_CODE);
		
		}else if (wholeWord.contains("map")|| wholeWord.contains("mop")){
				mapclick();
				mTextToSpeech.speak("Opening spikit maps", TextToSpeech.QUEUE_FLUSH, null);
		}else if(wholeWord.contains("camera")|| wholeWord.contains("take picture")){
				mTextToSpeech.speak("starting spikit camera", TextToSpeech.QUEUE_FLUSH, null);
				startCamera();
							
		}else{
				Toast.makeText(getApplicationContext(), wholeWord, Toast.LENGTH_LONG).show();
							mTextToSpeech.speak(wholeWord, TextToSpeech.QUEUE_FLUSH, null);
			 }	
			
		break;
		/*
		 * respond to the users request
		 * if yes ask for the title of the note
		 * if no, show the items stored in the notepad
		 */
		case NOTEPAD_CODE:
			ArrayList<String> notepadAnswer = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String theAnswer = notepadAnswer.get(0);
			
			if(theAnswer.contains("yes")){
				String  noteTitle = "Give a title";	
				noteInteract(noteTitle,TITLE_CODE );
				
				
			}else{
				mTextToSpeech.speak("Ok, here are your notes" , TextToSpeech.QUEUE_FLUSH, null);
				Intent noteIntent = new Intent(this, Notepad.class);
				startActivity(noteIntent);
				
			}
		
		break;
		
		//get the title and ask for the note details
		case TITLE_CODE:
			ArrayList<String> titleAnswer = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String theTitle = titleAnswer.get(0);
			
			t = theTitle;
			
			String  noteTitle = "Give the note to save";	
			noteInteract(noteTitle, BODY_CODE );
		break;
		
		// get the entire note  and save title and note to database
		case BODY_CODE:
			ArrayList<String> bodyAnswer = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String theBody = bodyAnswer.get(0);
			
			d = theBody;
			mTextToSpeech.speak("Thank you, your note has been saved " , TextToSpeech.QUEUE_FLUSH, null);
			
			onClick_AddRecord();
		break;
		
		default:
			if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				mTextToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {

					@Override
					public void onInit(int status) {
						// TODO Auto-generated method stub
						mTextToSpeech.setLanguage(Locale.UK);
						mTextToSpeech.speak("Hello! Welcome to spikit ", TextToSpeech.QUEUE_FLUSH, null);
					}
				});
			}

			else
			{
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		
		super.onActivityResult(requestCode, resultCode, data);
			break;
		}
}
	public void mapclick(){
		Intent mapi = new Intent(this , MapsActivity.class);
		startActivity(mapi);
	}
	public void startCamera(){
		Intent cam = new Intent(this, SpikitCamera.class);
		startActivity(cam);
		
	}

	// Implement an on utteranceProgressListener
	public void noteInteract(String question, final int RecCode ){
		
			
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"titleId");

		mTextToSpeech.speak(question,TextToSpeech.QUEUE_ADD, params);
		
		mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			
			@Override
			public void onStart(String utteranceId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(String utteranceId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDone(String utteranceId) {
				Recognition(RecCode);
				
			}
		});
		
	}

	/**
	 * shutdown the tts engine.... once everything is done... so that other applications can use it
	 * 
	 */
	@Override
	public void onDestroy()
	{
		closeDB();
	
		if (mTextToSpeech != null)
		{
			mTextToSpeech.stop();
			mTextToSpeech.shutdown();
		}
		
		super.onDestroy();
	}
	
	


}