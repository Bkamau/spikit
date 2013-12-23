package fi.benson.spikit.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fi.benson.spikit.R;
import fi.benson.spikit.Spikit;
import fi.benson.spikit.R.anim;
import fi.benson.spikit.R.id;
import fi.benson.spikit.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SpikitCamera extends Activity implements SurfaceHolder.Callback {

	private Camera mCamera;
	TextView timer ;
	private SurfaceView srfView;
	private PackageManager pm;
	private Boolean hasCamera = false;
	 Animation myAnimation;

	 
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
	
		timer = (TextView) findViewById(R.id.textView1);
		timer.setTextSize(72);
	     myAnimation = AnimationUtils.loadAnimation(this, R.anim.button_nim);
		srfView = (SurfaceView) findViewById(R.id.surfaceView);
		
		pm = this.getPackageManager();
		
		if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			hasCamera = true;
			SurfaceHolder holder = srfView.getHolder();
			holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder.setFixedSize(600, 400);
            
		}else{
			
		}
		
		if(hasCamera){
			new CountDownTimer(6000, 1000) {

			     public void onTick(long millisUntilFinished) {
			    	 timer.startAnimation(myAnimation);
			         timer.setText("" +  millisUntilFinished / 1000);
			     }

			     public void onFinish() {
			    	 takePicture();
			    	 timer.setText("");
			    	
			    	 
			     }
			  }.start();
			
			
		}
		 
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 mCamera= Camera.open();
	     mCamera.setDisplayOrientation(90);
	     try {
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	     } catch (IOException e) {
	            e.printStackTrace();
	     }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		 mCamera.stopPreview();
	     mCamera.release();
	     mCamera = null;
	}

	private void takePicture(){
		
		mCamera.takePicture(_shutterCallBack, _rawCallBack, _jpgCallBack);
	}
	
	
	ShutterCallback _shutterCallBack = new ShutterCallback(){
		@Override
		public void onShutter() {
			
		}
	};
	
	
	PictureCallback _rawCallBack=new PictureCallback(){
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			
		}
	};
	
	
	PictureCallback _jpgCallBack = new PictureCallback(){
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File theDir = new File("/sdcard/dcim/spikit");
			  if (!theDir.exists()) 
			   theDir.mkdir();
			
			FileOutputStream foutStream = null;
			try {
				foutStream = new FileOutputStream("/sdcard/DCIM/Spikit/spikit.jpg");
				foutStream.write(data);
				foutStream.close();
				
				returnToSpikit();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
	};
	public void returnToSpikit(){
		Intent bts = new Intent(this, Spikit.class);
		startActivity(bts);
	}
	
	
}
