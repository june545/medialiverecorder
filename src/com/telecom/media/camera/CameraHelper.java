/**
 * 
 */
package com.telecom.media.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.CamcorderProfile;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

/**
 * @author June Cheng
 * @date 2015年10月10日 下午11:17:55
 */
public class CameraHelper implements SurfaceHolder.Callback {
	private static final String	TAG	= "CameraHelper";
	private static CameraHelper	sInstance;
	private Context				mContext;
	private Camera				mCamera;
	private int					mCameraId;
	private SurfaceHolder		mSurfaceHolder;
	private boolean				isPreview;

	public static CameraHelper getInstance() {
		if (sInstance == null) {
			sInstance = new CameraHelper();
		}
		return sInstance;
	}

	public void init(Context context, SurfaceHolder surfaceHolder) {
		this.mContext = context;
		this.mSurfaceHolder = surfaceHolder;
		mSurfaceHolder.addCallback(this);
	}

	public void initCamera() {
		Log.d(TAG, "init camera");
		if (mCamera == null) {
			return;
		} else {
			// stop preview before making changes
			if (isPreview) {
				mCamera.stopPreview();
				isPreview = false;
			}
			// ignore: tried to stop a non-existent preview

			// >>>set preview size and make any resize, rotate or reformatting
			// >>>changes here

			// start preview with new settings
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				setCameraDisplayOrientation();
				//				Parameters parameters = mCamera.getParameters();
				//				parameters.set("orientation", "landscape");
				//				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				//				mCamera.setParameters(parameters);

				setCameraParameters();
			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
				e.printStackTrace();
			}

			startPreview();
		}
	}

	private void setCameraParameters() {
		Parameters mParameters = mCamera.getParameters();
		CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		mParameters.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
		//	mParameters.setPreviewFrameRate(camcorderProfile.videoFrameRate);

		// Set flash mode.
		//		String flashMode = mPreferences.getString(CameraSettings.KEY_VIDEOCAMERA_FLASH_MODE, getString(R.string.pref_camera_video_flashmode_default));
		//		List<String> supportedFlash = mParameters.getSupportedFlashModes();
		//		if (isSupported(flashMode, supportedFlash)) {
		//			mParameters.setFlashMode(flashMode);
		//		} else {
		//			flashMode = mParameters.getFlashMode();
		//			if (flashMode == null) {
		//				flashMode = getString(R.string.pref_camera_flashmode_no_flash);
		//			}
		//		}

		// Set white balance parameter.
		//		String whiteBalance = mPreferences.getString(CameraSettings.KEY_WHITE_BALANCE, getString(R.string.pref_camera_whitebalance_default));
		//		if (isSupported(whiteBalance, mParameters.getSupportedWhiteBalance())) {
		//			mParameters.setWhiteBalance(whiteBalance);
		//		} else {
		//			whiteBalance = mParameters.getWhiteBalance();
		//			if (whiteBalance == null) {
		//				whiteBalance = Parameters.WHITE_BALANCE_AUTO;
		//			}
		//		}

		// Set color effect parameter.
		//		String colorEffect = mPreferences.getString(CameraSettings.KEY_COLOR_EFFECT, getString(R.string.pref_camera_coloreffect_default));
		//		if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
		//			mParameters.setColorEffect(colorEffect);
		//		}

		mParameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
		mCamera.setParameters(mParameters);
		// Keep preview size up to date.
	}

	public void startPreview() {
		if (mCamera != null) {
			mCamera.startPreview();
			Log.d(TAG, "the camera starts previewing");
			isPreview = true;
		}
		set();
	}

	private void set() {
		if (isPreview) {
			mCamera.setPreviewCallback(new PreviewCallback() {

				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					//					StringBuffer sb = new StringBuffer();
					//					for (byte b : data) {
					//						sb.append(new String(data));
					//					}
					//					Log.d(TAG, "onPreviewFrame" + ByteUtil.getHexString(data));
				}
			});
			mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {

				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onPreviewFrame" + ByteUtil.getHexString(data));
				}
			});
		}
	}

	public Camera getCamera() {
		return mCamera;
	}

	public void setCameraDisplayOrientation() {
		if (mContext == null)
			return;

		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(mCameraId, cameraInfo);

			int degrees = getDeviceOrientation(mContext);
			int result;
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (cameraInfo.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (cameraInfo.orientation - degrees + 360) % 360;
			}
			Log.d(TAG, " setDisplayOrientation " + result);
			mCamera.setDisplayOrientation(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDeviceOrientation(Context context) {
		int orientation = 0;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Log.d(TAG, "wm.getDefaultDisplay().getRotation() -> " + wm.getDefaultDisplay().getRotation());
		switch (wm.getDefaultDisplay().getRotation()) {
		case Surface.ROTATION_90:
			orientation = 90;
			break;
		case Surface.ROTATION_180:
			orientation = 180;
			break;
		case Surface.ROTATION_270:
			orientation = 270;
			break;
		case Surface.ROTATION_0:
		default:
			orientation = 0;
			break;
		}
		return orientation;
	}

	public void releaseCamera() {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		mCameraId = -1;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (holder == null) {
			return;
		}
		if (mCamera == null) {
			mCamera = CameraUtil.getBackCamera();
			mCameraId = 0;
			Log.d(TAG, "打开摄像头");
			holder.setKeepScreenOn(true);
		}
		initCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallbackWithBuffer(null);
			mCamera.release();
			mCamera = null;
			isPreview = false;
		}
		mSurfaceHolder = null;
		//		mSurface = null;
	}
}
