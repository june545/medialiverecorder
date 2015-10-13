/**
 * 
 */
package com.telecom.media.camera.live;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Random;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

import com.telecom.media.camera.ByteUtil;
import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.RecorderHelper;
import com.telecom.media.rtsp.SocketConnector;

/**
 * @author June Cheng
 * @date 2015年10月10日 下午11:17:23
 */
public class LiveHelper {
	private static final String	TAG					= "LiveHelper";
	private static final String	LOCAL_SOCKET_ADDR	= "com.telecom.media.live-";
	private Context				mContext;
	private SurfaceView			mSurfaceView;

	private CameraHelper		mCameraHelper;

	private RecorderHelper		mRecorderHelper;
	private LocalSocket			receiver, sender;
	private LocalServerSocket	lss;
	private int					mSocketId;

	private Thread				mSendThread;

	public LiveHelper(Context context, SurfaceView surfaceView) {
		this.mContext = context;
		mCameraHelper = CameraHelper.getInstance();
		this.mSurfaceView = surfaceView;
	}

	/** 录制视频到文件 */
	public void startRecordVideo() {
		if (!mCameraHelper.isPreview()) {
			Log.d(TAG, " 当前照相不在预览中 ");
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
	public void startLiveRecord() {
		Log.d(TAG, " start live record ");
		if (mRecorderHelper == null) {
			mRecorderHelper = new RecorderHelper(mCameraHelper.getCamera(), mSurfaceView.getHolder().getSurface());
		}

		InitLocalSocket();

		try {
			mRecorderHelper.prepare(receiver.getFileDescriptor());
			mRecorderHelper.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mSendThread = new AudioCaptureAndSendThread();
		mSendThread.start();
	}

	public void stopRecord() {
		if (mSendThread != null) {
			((AudioCaptureAndSendThread) mSendThread).cancel();
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
		closeSockets();
	}

	public void addCallback() {
		Log.d(TAG, "setCallback()");
//		mCameraHelper.getCamera().addCallbackBuffer(new byte[1024 * 64]);
//		mCameraHelper.getCamera().setPreviewCallback(new PreviewCallback() {
//
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera) {
//				Log.d(TAG, "onPreviewFrame");
//			}
//		});
		mCameraHelper.getCamera().setPreviewCallback(new PreviewCallbackImpl());
	}

	private void InitLocalSocket() {
		Log.d(TAG, "init local socket");
		try {
			for (int i = 0; i < 10; i++) {
				try {
					mSocketId = new Random().nextInt();
					lss = new LocalServerSocket(LOCAL_SOCKET_ADDR + mSocketId);
					break;
				} catch (IOException e1) {
				}
			}

			receiver = new LocalSocket();
			receiver.connect(new LocalSocketAddress(LOCAL_SOCKET_ADDR + mSocketId));
			receiver.setReceiveBufferSize(8 * 1024);
			receiver.setSendBufferSize(8 * 1024);
			receiver.setSoTimeout(3000);

			sender = lss.accept();
			sender.setReceiveBufferSize(8 * 1024);
			sender.setSendBufferSize(8 * 1024);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return;
		}
	}

	private class AudioCaptureAndSendThread extends Thread {
		private boolean	threadEnable	= true;

		public void run() {
			try {
				readStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void readStream() throws Exception {
			DatagramSocket udpSocket = new DatagramSocket();
			DataInputStream dataInput = new DataInputStream(sender.getInputStream());
			while (threadEnable) {
				try {
					Log.d(TAG, " is running ");
					byte[] buff = new byte[1024];
					int count = -1;

					count = dataInput.read(buff);
					if (count != -1) {
						String s = ByteUtil.getHexString(buff);
						Log.d(TAG, " data " + s.toUpperCase());

						SocketConnector.send(s);
					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, " xxxxxx ");
				}
			}
		}

		public void cancel() {
			threadEnable = false;
		}
	}

	public void closeSockets() {
		Log.d(TAG, "release start");
		Log.d(TAG, "release xxxxx");

		try {
			receiver.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			sender.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lss.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "release e");
	}

}
