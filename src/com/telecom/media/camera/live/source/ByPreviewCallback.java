package com.telecom.media.camera.live.source;

import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.live.PreviewCallbackImpl;

import android.util.Log;

/**
 * 
 * @author June Cheng
 * @date 2015年10月23日 下午4:56:35
 */
public class ByPreviewCallback extends StreamSource {
	private final String TAG = "ByPreviewCallback";

	@Override
	public void start() {
		Log.d(TAG, "setCallback()");
		CameraHelper.getInstance().getCamera().setPreviewCallback(new PreviewCallbackImpl());
	}

	@Override
	public void stop() {
		CameraHelper.getInstance().getCamera().setPreviewCallback(null);
	}

}
