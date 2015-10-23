/**
 * 
 */
package com.telecom.media.camera.live;

import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.RecorderHelper;
import com.telecom.media.camera.live.source.ByMediaRecorder;
import com.telecom.media.camera.live.source.ByPreviewCallback;
import com.telecom.media.camera.live.source.StreamSource;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

/**
 * @author June Cheng
 * @date 2015年10月10日 下午11:17:23
 */
public class LiveHelper {
	private static final String	TAG					= "LiveHelper";
	private static final String	LOCAL_SOCKET_ADDR	= "com.telecom.media.live-";
	private Context				mContext;
	private SurfaceView			mSurfaceView;

	private CameraHelper mCameraHelper;

	private RecorderHelper mRecorderHelper;

	private Thread mSendThread;

	private StreamSource mStreamSource;

	public LiveHelper(Context context, SurfaceView surfaceView) {
		this.mContext = context;
		mCameraHelper = CameraHelper.getInstance();
		this.mSurfaceView = surfaceView;
	}

	/** 录制视频到文件 */
	public void startRecordVideo() {
		if (!mCameraHelper.isPreview()) {
			Log.d(TAG, " 当前照相不在预览中 ");
			return;
		}

		if (mRecorderHelper == null) {
			Log.d(TAG, "strat record -> " + mSurfaceView.getHolder().getSurface().toString());
			mRecorderHelper = new RecorderHelper(mCameraHelper.getCamera(), mSurfaceView.getHolder().getSurface());
		}
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaavideo.3gp";
		try {
			mRecorderHelper.prepare(path);
			mRecorderHelper.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始直播
	 */
	public void startLive() {
		Log.d(TAG, " start live record ");
		if (mStreamSource == null) {
			//			mStreamSource = new ByMediaRecorder(mSurfaceView);
			mStreamSource = new ByPreviewCallback();
			mStreamSource.start();
		}
	}

	public void stopRecord() {
		if (mStreamSource != null) {
			mStreamSource.stop();
			mStreamSource = null;
		}

		if (mRecorderHelper != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					mRecorderHelper.release();
					mRecorderHelper = null;
				}
			}).start();
		}
	}

}
