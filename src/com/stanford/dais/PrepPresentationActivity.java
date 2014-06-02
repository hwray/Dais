package com.stanford.dais; 
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.Build;

import com.firebase.client.*;



public class PrepPresentationActivity extends Activity {

	private Globals g; 
	
	private View mMainView; 
	private TextView mTitleView; 
	private TextView mHeadingView; 

	private TextView mLeftHeadingView; 
	private TextView mRightHeadingView; 
	
	private boolean mLiveFeedbackMode; 

	private static final double MUMBLE_DIFFERENCE = 3;
	private static final int MUMBLE_TIME_THRESHOLD = 30; 
    private static final float GAZE_TIME_THRESHOLD = 100.0f; 
    private static final float GAZE_PITCH_THRESHOLD = 10.0f;
    
    // The sampling rate for the audio recorder.
    private static final int SAMPLING_RATE = 44100;
    
    private static final int NUM_BACKGROUND_CALIBRATION_SAMPLES = 50; 
	private static final int NUM_SPEECH_CALIBRATION_SAMPLES = 50; 

	private ImageView mUpArrow;
	private ImageView mRightArrow;
	private ImageView mLeftArrow;
    
    private OrientationManager mOrientationManager;        
    private boolean mInterference; 
    
    private SensorManager mSensorManager; 
    private LocationManager mLocationManager; 
    
    private StepDetector mStepDetector; 
    
    private GestureDetector mGestureDetector;
    
    private int mBufferSize;
    private short[] mAudioBuffer;
    private String mDecibelFormat;
    
    private CalibrationThread mCalibrationThread; 
    
    private SpeechThread mSpeechThread;
    
    private GazeThread mGazeThread;  
    
    private Handler uiHandler;
  

    /* FIREBASE GLOBALS */
     Firebase connection;
     

	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
        	if (g.pres.mLeftHeading == 0 || g.pres.mRightHeading == 0) {
            	mHeadingView.setText("" + mOrientationManager.getHeading());
        	}
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
        	// Do nothing
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
            mInterference = orientationManager.hasInterference();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_presentation);
        
        this.g = (Globals) getApplication(); 
        
        g.clearGlobals(); 
        g.pres.reset(); 
        
        mInterference = false; 
        
        mMainView = (View) findViewById(R.id.prep_presentation_container); 
        mHeadingView = (TextView) findViewById(R.id.compass_heading); 
        mTitleView = (TextView) findViewById(R.id.instructions_and_feedback); 
        mLeftHeadingView = (TextView) findViewById(R.id.left_heading); 
        mRightHeadingView = (TextView) findViewById(R.id.right_heading); 
        
        mLiveFeedbackMode = getIntent().getExtras().getBoolean("liveFeedbackMode");

        /* Arrow Images */
        mUpArrow = (ImageView) findViewById(R.id.up_arrow);
        mLeftArrow = (ImageView) findViewById(R.id.left_arrow);
        mRightArrow = (ImageView) findViewById(R.id.right_arrow); 

        mUpArrow.setVisibility(View.GONE);
        mLeftArrow.setVisibility(View.GONE);
        mRightArrow.setVisibility(View.GONE);

        mGestureDetector = createGestureDetector(this); 
        
        mSensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mOrientationManager = new OrientationManager(mSensorManager, mLocationManager);
        
        mOrientationManager.addOnChangedListener(mCompassListener);
        mOrientationManager.start();
        
        mStepDetector = new StepDetector(); 
        mStepDetector.setSensitivity(2.96f);
        mStepDetector.addStepListener(new StepListener() {
        	public void onStep() {
        		g.pres.numSteps++; 
        		// mHeadingView.setText("Steps: " + g.pres.numSteps);
        	}
        	
        	public void passValue() {
        		
        	}
        }); 
        
        Sensor mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*| 
                Sensor.TYPE_MAGNETIC_FIELD | 
                Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        
        // Compute the minimum required audio buffer size and allocate the buffer.
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioBuffer = new short[mBufferSize / 2];

        mDecibelFormat = getResources().getString(R.string.decibel_format);
        
        
        initHandler(); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        g.pres.reset(); 
        
        mSpeechThread = new SpeechThread();
        
        mGazeThread = new GazeThread();         
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCalibrationThread != null) {
        	mCalibrationThread.stopRunning(); 
        	mCalibrationThread = null; 
        }
        
        if (mSpeechThread != null) {
            mSpeechThread.stopRunning();
            mSpeechThread = null;
        }
        
        if (mGazeThread != null) {
            mGazeThread.stopRunning();
            mGazeThread = null;
        }
    }
    
    
    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
    
    private GestureDetector createGestureDetector(Context context) {    	
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
        	 @Override
             public boolean onGesture(Gesture gesture) {
             	
                 if (gesture == Gesture.TAP) {
                     // mAudioManager.playSoundEffect(Sounds.TAP);
                 	if (g.pres.mLeftHeading == 0) {
                 		g.pres.mLeftHeading = mOrientationManager.getHeading(); 
                 		
                 		if (g.pres.mLeftHeading < 0) {
                 			g.pres.mLeftHeading += 360; 
                 		}
                 		
                 		mLeftHeadingView.setText("" + g.pres.mLeftHeading); 
                 		mTitleView.setText("Look at right side of room and tap"); 
                 	} else if (g.pres.mRightHeading == 0) {
                 		g.pres.mRightHeading = mOrientationManager.getHeading(); 
                 		
                 		mRightHeadingView.setText("" + g.pres.mRightHeading);
                 		
                 		if (g.pres.mRightHeading < 0) {
                 			g.pres.mRightHeading += 360; 
                 		}
                 		
                 		if (g.pres.mRightHeading < g.pres.mLeftHeading) {
                 			g.pres.mRightHeading += 360; 
                 		}
                 		
                 		g.pres.mCenterHeading = (g.pres.mLeftHeading + g.pres.mRightHeading) / 2; 
                 		
                 		mTitleView.setText("Tap to record background volume"); 
                 		mHeadingView.setText("");    
                 	} else if (g.pres.mFloorVolume == 0 && mCalibrationThread == null) {
                 		mTitleView.setText("Calibrating room volume..."); 
                 		mCalibrationThread = new CalibrationThread("floor"); 
                 		mCalibrationThread.start(); 
                 	} else if (g.pres.mSpeechVolume == 0 && mCalibrationThread == null) {
                 		mTitleView.setText("Calibrating speech volume..."); 
                 		mCalibrationThread = new CalibrationThread("speech"); 
                 		mCalibrationThread.start(); 
                 	} else if (mCalibrationThread != null) {
                 		return true; 
                 	} else {
                 		mTitleView.setText("");
                 		mLeftHeadingView.setText(""); 
                 		mRightHeadingView.setText(""); 
                 		mHeadingView.setText(""); 
                 		mGazeThread.start(); 
                 		mSpeechThread.start(); 
                 	}
                    return true;
                 } else if (gesture == Gesture.TWO_TAP) {
                     // do something on two finger tap
                     return true;
                 } else if (gesture == Gesture.SWIPE_RIGHT) {
                     // do something on right (forward) swipe
                     return true;
                 } else if (gesture == Gesture.SWIPE_LEFT) {
                     // do something on left (backwards) swipe
                     return true;
                 } else if (gesture == Gesture.SWIPE_DOWN) {
                	 if (mCalibrationThread != null) 
                		 mCalibrationThread.stopRunning(); 
                	 if (mSpeechThread != null)
                		 mSpeechThread.stopRunning(); 
                	 if (mGazeThread != null)
                		 mGazeThread.stopRunning(); 
                     g.pres.pushOnline();
                     // g.pres.reset();
                 }
                 return false;
        	 }
        });
        return gestureDetector;
    }
    
    public void initHandler() {
    	uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mUpArrow.setVisibility(View.GONE);
		        mLeftArrow.setVisibility(View.GONE);
		        mRightArrow.setVisibility(View.GONE);
		        
				if (msg.what == 0) {
					if (mLiveFeedbackMode)
						mHeadingView.setText("Magnetic interference");
				} else if (msg.what == 1) {
					if (mLiveFeedbackMode){
				        mUpArrow.setVisibility(View.VISIBLE);
				        //mTitleView.setText("Look up!");
					}
				} else if (msg.what == 2) {
					if (mLiveFeedbackMode){
				        mRightArrow.setVisibility(View.VISIBLE);
				        //mTitleView.setText("Look right!");
					}
				} else if (msg.what == 3) {
					if (mLiveFeedbackMode){
				        mLeftArrow.setVisibility(View.VISIBLE);
				        //mTitleView.setText("Look left!");
					}
				} else if (msg.what == 4) {
					if (mLiveFeedbackMode)
						mTitleView.setText("Face forward!"); 
				} else if (msg.what == 5) {
					mTitleView.setText(""); 
				} else if (msg.what == 6) {
					mHeadingView.setText("Room: " + String.format(mDecibelFormat, g.pres.mFloorVolume));
					mTitleView.setText("Tap and speak at desired volume"); 
				} else if (msg.what == 7) {
					mHeadingView.setText("Speech: " + String.format(mDecibelFormat, g.pres.mSpeechVolume));
					mTitleView.setText("Calibration complete. Tap to start."); 
				} else if (msg.what == 8) {
					if (mLiveFeedbackMode)
						mTitleView.setText("Speak up!"); 
				}
			}
    	}; 
    }
    
    public void sendUIMessage(int msg) {
		Message message = uiHandler.obtainMessage(); 
		message.what = msg; 
		uiHandler.sendMessage(message);
	}
    
    /**
     * A background thread that checks the compass heading of the presenter's gaze, 
     * in order to track visual spread and give live feedback on where to look. 
     */
    private class GazeThread extends Thread {
    	
        private boolean mShouldContinue = true;

        @Override
        public void run() {
            while (shouldContinue()) {
            	try {
					if (mInterference) {
						sendUIMessage(0); 
						//Thread.sleep(100); 
						//continue; 
					}
					
		        	if (mOrientationManager.getPitch() > GAZE_PITCH_THRESHOLD) {
		        		sendUIMessage(1);
		        	} else {
		        		float heading = mOrientationManager.getHeading(); 
		        		
		        		if (g.pres.mRightHeading > 360) {
		        			heading += 360; 
		        		}
		        		
		        		g.pres.headings.add(heading); 
		        		
		        		if (heading > g.pres.mLeftHeading && heading < g.pres.mCenterHeading) {
		        			if (g.pres.mGazeSide != 0) {
			        			g.pres.mGazeSide = 0; 
		        				g.pres.mGazeTime = 0; 
		        			}
		        			g.pres.mGazeTime += 1; 
		        			
		        			if (g.pres.mGazeTime > GAZE_TIME_THRESHOLD) {
		        				sendUIMessage(2);
		        			} else {
		        				sendUIMessage(5); 
		        			}
		        			
		        			g.pres.mLeftTime += 1; 
		        		} 
		        		else if (heading > g.pres.mCenterHeading && heading < g.pres.mRightHeading) {
		        			if (g.pres.mGazeSide != 1) {
			        			g.pres.mGazeSide = 1; 
			        			g.pres.mGazeTime = 0; 
		        			}
		        			g.pres.mGazeTime += 1; 
		        			
		        			if (g.pres.mGazeTime > GAZE_TIME_THRESHOLD) {
		        				sendUIMessage(3); 
		        			} else {
		        				sendUIMessage(5); 
		        			}
		        			
		        			g.pres.mRightTime += 1; 
		        		} 
		        		else {
		        			sendUIMessage(4); 
		        		}
		        	}
		        	
					Thread.sleep(100);
				} catch (Exception e) { e.printStackTrace(); }
            }
        }

        /**
         * Gets a value indicating whether the thread should continue running.
         *
         * @return true if the thread should continue running or false if it should stop
         */
        private synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        /** Notifies the thread that it should stop running at the next opportunity. */
        public synchronized void stopRunning() {
            mShouldContinue = false;
        }
    }
    
    
    /**
     * A background thread that calibrates for the presenter's desired speech volume
     * by recording roughly 8-10 seconds of audio from the microphone. 
     */
    private class CalibrationThread extends Thread {

    	private String type; 
    	
        private boolean mShouldContinue = true;
        
        private double mAverageDecibels; 
        
        private ArrayList<Double> mDecibelReadings; 
        
        public CalibrationThread(String type) {
        	this.type = type; 
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            
            mDecibelReadings = new ArrayList<Double>(); 

            AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLING_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            record.startRecording();

            int limit = 0; 
            if (type.equals("floor")) {
            	limit = NUM_BACKGROUND_CALIBRATION_SAMPLES; 
            } else if (type.equals("speech")) {
            	limit = NUM_SPEECH_CALIBRATION_SAMPLES; 
            }
            
            for (int i = 0; i < limit; i++) {
                record.read(mAudioBuffer, 0, mBufferSize / 2);
                computeDecibelLevel();
            }
            
            record.stop();
            record.release();
            
            mAverageDecibels /= (double)mDecibelReadings.size(); 
            
            if (type.equals("floor")) {
            	g.pres.mFloorVolume = mAverageDecibels; 
    			sendUIMessage(6); 
            } else if (type.equals("speech")) {
                g.pres.mSpeechVolume = mAverageDecibels; 
                g.pres.mMumbleVolume = g.pres.mSpeechVolume - MUMBLE_DIFFERENCE; 
    			sendUIMessage(7); 
            }
            			
			mCalibrationThread = null; 
        }

        /**
         * Gets a value indicating whether the thread should continue running.
         *
         * @return true if the thread should continue running or false if it should stop
         */
        private synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        /** Notifies the thread that it should stop running at the next opportunity. */
        public synchronized void stopRunning() {
            mShouldContinue = false;
        }

        /**
         * Computes the decibel level of the current sound buffer. 
         */
        private void computeDecibelLevel() {
            // Compute the root-mean-squared of the sound buffer and then apply the formula for
            // computing the decibel level, 20 * log_10(rms). This is an uncalibrated calculation
            // that assumes no noise in the samples; with 16-bit recording, it can range from
            // -90 dB to 0 dB.
            double sum = 0;

            for (short rawSample : mAudioBuffer) {
                double sample = rawSample / 32768.0;
                sum += sample * sample;
            }

            double rms = Math.sqrt(sum / mAudioBuffer.length);
            final double db = 20 * Math.log10(rms);
            
            mDecibelReadings.add(db); 
            
            mAverageDecibels += db; 

            // Update the text view on the main thread.
            mHeadingView.post(new Runnable() {
                @Override
                public void run() {
                    mHeadingView.setText(String.format(mDecibelFormat, db));
                }
            });
        }
    }
    
    
    /**
     * A background thread that receives audio from the microphone 
     * and gives the presenter feedback to "Speak up!" when they mumble. 
     */
    private class SpeechThread extends Thread {

        private boolean mShouldContinue = true;
        
        private boolean isMumbling = false; 
        
        private int mNumMumbleSamples = 0; 

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

            AudioRecord record = new AudioRecord(AudioSource.MIC, SAMPLING_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            record.startRecording();

            while (shouldContinue()) {
                record.read(mAudioBuffer, 0, mBufferSize / 2);
                checkDecibelLevel();
            }

            record.stop();
            record.release();
        }

        /**
         * Gets a value indicating whether the thread should continue running.
         *
         * @return true if the thread should continue running or false if it should stop
         */
        private synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        /** Notifies the thread that it should stop running at the next opportunity. */
        public synchronized void stopRunning() {
            mShouldContinue = false;
        }

        /**
         * Computes the decibel level of the current sound buffer and
	     * sends a message to provide UI feedback if the presenter is mumbling. 
         */
        private void checkDecibelLevel() {
            double sum = 0;

            for (short rawSample : mAudioBuffer) {
                double sample = rawSample / 32768.0;
                sum += sample * sample;
            }

            double rms = Math.sqrt(sum / mAudioBuffer.length);
            final double db = 20 * Math.log10(rms);
            
            g.pres.decibels.add(db); 
            
            if (db > (g.pres.mFloorVolume) && db < g.pres.mMumbleVolume) {
            	mNumMumbleSamples++; 
            	
            } else {
            	mNumMumbleSamples = 0; 
            	sendUIMessage(5); 
            }
            
            if (mNumMumbleSamples > MUMBLE_TIME_THRESHOLD) {
            	sendUIMessage(8); 
            }
        }
    }
}