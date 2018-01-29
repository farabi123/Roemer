package abr.auto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;

public class Main_activity extends Activity implements IOIOLooperProvider, SensorEventListener        // implements IOIOLooperProvider: from IOIOActivity
{
	private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(this, this);			// from IOIOActivity
	private TextView irCenterText;
	private ToggleButton btnStartStop;

	//variables for compass
	private SensorManager mSensorManager;
	private Sensor mCompass, mAccelerometer;
	float[] mAcc;
	//variables for logging
	private Sensor mGyroscope;
	private Sensor mGravityS;
	float[] mGravity;
	float[] mGyro;
	float[] mGeomagnetic;

	IOIO_thread m_ioio_thread;
	private TextView resultTEXT;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		resultTEXT = (TextView) findViewById(R.id.command);
		irCenterText = (TextView) findViewById(R.id.irCenter);

		btnStartStop = (ToggleButton) findViewById(R.id.buttonStartStop);

		//set up compass
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mCompass= mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mGravityS = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		helper_.create();		// from IOIOActivity

		// enableUi(false);

	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	// Called whenever the value of a sensor changes
	// Should be called whenever a sensor changes.
	// Good time to get IR values and send move commands to the robot

	@Override
	public final void onSensorChanged(SensorEvent event) {
		if(m_ioio_thread != null) {
			setText(String.format("%.3f", m_ioio_thread.getIrCenterReading()), irCenterText);
		}

	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnStartStop.setEnabled(enable);
			}
		});
	}

	//set the text of any text view in this application
	public void setText(final String str, final TextView tv)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv.setText(str);
			}
		});
	}


	/****************************************************** functions from IOIOActivity *********************************************************************************/


	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) 
	{
		if(m_ioio_thread == null && connectionType.matches("ioio.lib.android.bluetooth.BluetoothIOIOConnection"))
		{
			// enableUi(true);
			m_ioio_thread = new IOIO_thread();
			return m_ioio_thread;
		}
		else
		{
			return null;
		}
	}

	//Called whenever activity resumes from pause
	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_NORMAL);
		helper_.start();		// from IOIOActivity

	}

	//Called when activity pauses
	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		// Log.d("robo", "onPause");
	}

	//Called when activity restarts. onCreate() will then be called
	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy()
	{
		helper_.destroy();
		super.onDestroy();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		helper_.start();
	}

	@Override
	protected void onStop()
	{
		helper_.stop();
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0)
		{
			helper_.restart();
		}
	}
}
