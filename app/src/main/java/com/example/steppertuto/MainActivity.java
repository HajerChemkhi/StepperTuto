package com.example.steppertuto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Button pauseButton;
    private Sensor stepCounterSensor;
    private SensorManager sensorManager;


    private ProgressBar progressBar;
    private boolean isPaused= false;
    private TextView timeText;
    private TextView distanceText;
    private TextView stepsText;
    private TextView StepCountTarget;


    private long timePaused = 0;
    private float stepsLenghtInMeter = 0.762f ;
    private long startTime;
    private int stepCountTarget = 5000;
    private int stepCount = 0;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {

        @Override
        public void run() {
            long milis= System.currentTimeMillis() - startTime;
            int seconds = (int)(milis/1000);
            int min = seconds/60;
            seconds = seconds % 60;
            timeText.setText(String.format(Locale.getDefault(), "Time: %02d:%02d",min,seconds));
            timeHandler.postDelayed(this,1000);
        }
    };



    @Override
    protected void onStop() {
        super.onStop();
        if (stepCounterSensor != null){
            sensorManager.unregisterListener(this);
            timeHandler.removeCallbacks(timeRunnable);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (stepCounterSensor != null){
            sensorManager.registerListener(this, stepCounterSensor,SensorManager.SENSOR_DELAY_NORMAL);

            timeHandler.postDelayed(timeRunnable,0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        stepsText= findViewById(R.id.stepsText);
        distanceText=findViewById(R.id.distanceText);
        timeText=findViewById(R.id.timeText);
        pauseButton=findViewById(R.id.pauseButton);
        StepCountTarget=findViewById(R.id.StepCountTarget);
        progressBar=findViewById(R.id.progressBar);


        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTarget);

        if (stepCounterSensor == null) {
            StepCountTarget.setText("Step counter: not available");
        } else {
            progressBar.setMax(stepCountTarget);
            StepCountTarget.setText("Step Goal: " + stepCountTarget);
        }}




    @Override
    public void onSensorChanged(SensorEvent sensorEvent){

        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER ){
            stepCount = (int) sensorEvent.values[0];
            stepsText.setText("Step Count : "+stepCount);
            progressBar.setProgress(stepCount);

            if(stepCount >= stepCountTarget){
                StepCountTarget.setText("Step Goal Achieved");
            }


            float distanceInKm = stepCount * stepsLenghtInMeter / 1000;
            distanceText.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKm));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPauseButtonClicked (View view){
        if(isPaused){
            isPaused = false ;
            pauseButton.setText("Paused");
            startTime= System.currentTimeMillis()-timePaused;
            timeHandler.postDelayed(timeRunnable,0);

        }else {
            isPaused=true;
            pauseButton.setText("Resume");


            timeHandler.removeCallbacks(timeRunnable);
            timePaused=System.currentTimeMillis()-startTime;
        }
    }
}