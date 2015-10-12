package com.telecom.media.camera;

import java.io.FileDescriptor;
import java.io.IOException;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.util.Log;
import android.view.Surface;

/**
 * 录制视频
 * 
 * @author June Cheng
 * @date 2015年9月8日 下午11:32:01
 */
public class RecorderHelper implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
	private final String	TAG	= "CameraRecorder";
	private MediaRecorder	mMediaRecorder;
	private Camera			mCamera;
	private Surface			mSurface;

	/** 视频参数，如视频画质、音频采样率等 */
	private VideoProfile mProfile = new VideoProfile();

	private String			mPath;
	private FileDescriptor	mFD;

	private int orientationDegrees;

	/**
	 * 
	 * @param camera
	 *            instream
	 * @param surface
	 *            preview stream
	 */
	public RecorderHelper(Camera camera, Surface surface) {
		this.mCamera = camera;
		this.mSurface = surface;
	}

	public RecorderHelper(Camera camera, Surface surface, String path) {
		this.mCamera = camera;
		this.mSurface = surface;
		this.mPath = path;
	}

	public RecorderHelper(Camera camera, Surface surface, FileDescriptor fd) {
		this.mCamera = camera;
		this.mSurface = surface;
		this.mFD = fd;
	}

	public void setOrientationDegrees(int orientationDegrees) {
		this.orientationDegrees = orientationDegrees;
	}

	/**
	 * set a custom profile to recorder
	 * 
	 * @param profile
	 */
	public void setVideoProfile(VideoProfile profile) {
		this.mProfile = profile;
	}

	/**
	 * Prepares the recorder to begin capturing and encoding data.
	 * 
	 * @param surface
	 * @param mCamera
	 * @param path
	 * @return
	 */
	public boolean prepare(String path) {
		this.mPath = path;
		return prepare();
	}

	public boolean prepare(FileDescriptor fileDescriptor) {
		this.mFD = fileDescriptor;
		return prepare();
	}

	private boolean prepare() {
		mMediaRecorder = new MediaRecorder();
		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		camcorderProfile.fileFormat = mProfile.fileFormat;
		camcorderProfile.audioCodec = mProfile.audioCodec;
		camcorderProfile.videoCodec = mProfile.videoCodec;
		camcorderProfile.videoFrameWidth = mProfile.videoFrameWidth;
		camcorderProfile.videoFrameHeight = mProfile.videoFrameHeight;
		camcorderProfile.videoFrameRate = mProfile.videoFrameRate;
		camcorderProfile.audioSampleRate = mProfile.audioSampleRate;
		camcorderProfile.audioBitRate = mProfile.audioBitRate;
		camcorderProfile.videoBitRate = mProfile.videoBitRate;
		mMediaRecorder.setProfile(camcorderProfile);
		// mMediaRecorder.setMaxDuration(1000 * 30);

		// Step 4: Set output file
		if (mPath != null) {
			mMediaRecorder.setOutputFile(mPath);
		} else {
			mMediaRecorder.setOutputFile(mFD);
			Log.d(TAG, "record to filedescriptor");
		}
		// mMediaRecorder.setMaxFileSize(1024 * 1024 * 10);

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mSurface);

		Log.d(TAG, "视频录制时的输入视频方向 ： " + orientationDegrees);
		if (orientationDegrees != 0) {
			mMediaRecorder.setOrientationHint(orientationDegrees);
		}

		mMediaRecorder.setOnInfoListener(this);
		mMediaRecorder.setOnErrorListener(this);

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
			return true;
		} catch (IllegalStateException e) {
			Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
			release();
		} catch (IOException e) {
			Log.i(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			release();
		}
		return false;
	}

	/**
	 * Begins capturing and encoding data to the file specified with
	 * setOutputFile(). Call this after prepare().
	 */
	public void start() {
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					Log.e(TAG, "what=" + what + ", extra=" + extra);
				}
			});
			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {
					Log.e(TAG, "onInfo what=" + what + ", extra=" + extra);
				}
			});
			mMediaRecorder.start();
		}
	}

	/**
	 * Stops recording. Call this after start().
	 */
	public void stop() {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
		}
	}

	/**
	 * 释放MediaRecorder
	 */
	public void release() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onError  what=" + what + ", extra=" + extra);
		switch (what) {
		case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
			break;
		default:

		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onInfo  what=" + what + ", extra=" + extra);
	}
}
