/**
 * 
 */
package com.telecom.media.camera.live;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import android.view.SurfaceView;

import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.RecorderHelper;

/**
 * @author June Cheng
 * @date 2015年10月10日 下午11:17:23
 */
public class LiveHelper {
	private static final String	TAG	= "LiveHelper";
	private Context				mContext;
	private SurfaceView			mSurfaceView;

	private CameraHelper		mCameraHelper;

	private RecorderHelper		mRecorderHelper;
	private LocalSocket			receiver, sender;
	private LocalServerSocket	lss;

	public LiveHelper(Context context, SurfaceView surfaceView) {
		this.mContext = context;
		mCameraHelper = CameraHelper.getInstance();
		this.mSurfaceView = surfaceView;
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
			//			Socket receiver = new Socket("192.168.1.5", 8089);
			//			ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(receiver);
			mRecorderHelper.prepare(sender.getFileDescriptor());
		} catch (Exception e) {
			e.printStackTrace();
		}

		startVideoRecording();
	}

	private void InitLocalSocket() {
		try {
			lss = new LocalServerSocket("H264");
			receiver = new LocalSocket();

			receiver.connect(new LocalSocketAddress("H264"));
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(50000);

			sender = lss.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(50000);

		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return;
		}
	}

	Thread	t;

	private void startVideoRecording() {
		t = new Thread() {
			public void run() {
				int frame_size = 20000;
				byte[] buffer = new byte[1024 * 64];
				int num, number = 0;
				InputStream fis = null;
				try {
					fis = receiver.getInputStream();
				} catch (IOException e1) {
					return;
				}
				try {
					Socket socket = new Socket("192.168.1.5", 8089);
					PrintWriter os = new PrintWriter(socket.getOutputStream());
					os.write("i am here !");
					os.flush();
					Log.d(TAG, "i am here !");
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				number = 0;
				while (true) {
					Log.d(TAG, "thread is running");
					try {
						byte[] buff = new byte[1024];
						int count = -1;
						while ((count = fis.read(buff)) != -1) {
							Log.d(TAG, new String(buff, 0, count));
						}
					} catch (Exception e) {
						System.out.println("exception break");
					}
				}
			}
		};
		t.start();
	}
}
