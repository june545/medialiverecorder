package com.telecom.media.liverecorder;

import android.app.Activity;
import android.os.Bundle;
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

	private SurfaceView		mSurfaceView;
	private Button			mStartLiveBtn;

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

	private void initView() {
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mStartLiveBtn = (Button) findViewById(R.id.btn_start_live_recorder);

		mStartLiveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "start living", Toast.LENGTH_SHORT).show();
				mLiveHelper.startLiveRecord();
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
