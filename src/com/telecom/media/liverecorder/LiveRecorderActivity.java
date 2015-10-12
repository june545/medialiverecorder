package com.telecom.media.liverecorder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.live.LiveHelper;
import com.telecom.media.demo.liverecorder.R;

/**
 * 
 * @author June Cheng
 * @date 2015年10月10日 下午9:37:00
 */
public class LiveRecorderActivity extends Activity {
	private final String	TAG	= "LiveRecorderActivity";

	private SurfaceView		mSurfaceView;
	private Button			mStartLiveBtn;
	private Button			mStopLiveBtn;

	private CameraHelper	mCameraHelper;
	private LiveHelper		mLiveHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_recorder);
		initView();
		initCamera();
		initLiveRecorder();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		if (mCameraHelper != null) {
			mCameraHelper.releaseCamera();
		}
	}

	private void initView() {
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mStartLiveBtn = (Button) findViewById(R.id.btn_start_live_recorder);
		mStopLiveBtn = (Button) findViewById(R.id.btn_stop_live_recorder);

		mStartLiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "start live video", Toast.LENGTH_SHORT).show();
				mLiveHelper.startLiveRecord();
				//				mLiveHelper.startRecordVideo();
			}
		});
		mStopLiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLiveHelper != null) {
					mLiveHelper.stopRecord();
				}
			}
		});
	}

	private void initCamera() {
		mCameraHelper = CameraHelper.getInstance();
		mCameraHelper.init(this, mSurfaceView.getHolder());

	}

	private void initLiveRecorder() {
		mLiveHelper = new LiveHelper(this, mSurfaceView);
	}
}
