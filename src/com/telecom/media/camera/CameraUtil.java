package com.telecom.media.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public class CameraUtil {
	private static final String	TAG	= "CameraUtil";

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Log.d(TAG, "获取camera");
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}
	
	public static int cameraId = -1;
	
	/**
	 * @return front camera
	 */
	public static Camera getFrontCamera() {
		CameraInfo cameraInfo = new CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				return Camera.open(i);
			}
		}
		return null;
	}

	/**
	 * @return back camera
	 */
	public static Camera getBackCamera() {
		CameraInfo cameraInfo = new CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				return Camera.open(i);
			}
		}
		return null;
	}

	/** Check if this device has a camera */
	public boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

}
