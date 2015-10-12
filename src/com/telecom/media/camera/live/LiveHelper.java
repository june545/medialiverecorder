/**
 * 
 */
package com.telecom.media.camera.live;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
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

	private CameraHelper mCameraHelper;

	private RecorderHelper		mRecorderHelper;
	private LocalSocket			receiver, sender;
	private LocalServerSocket	lss;
	private boolean				threadEnable	= true;
	Socket						socket;

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
			mRecorderHelper.prepare(sender.getFileDescriptor());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// startVideoRecording();
		new Thread(new AudioCaptureAndSendThread()).start();
	}

	private void InitLocalSocket() {
		try {
			final int bufSize = 1024;

			lss = new LocalServerSocket("H264");

			receiver = new LocalSocket();
			receiver.connect(new LocalSocketAddress("H264"));
			receiver.setReceiveBufferSize(bufSize);
			receiver.setSendBufferSize(bufSize);

			sender = lss.accept();
			sender.setReceiveBufferSize(bufSize);
			sender.setSendBufferSize(bufSize);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return;
		}
	}

	Thread t;

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
				while (true) {
					Log.d(TAG, "thread is running");
					try {
						byte[] buff = new byte[1024];
						int count = -1;
						while ((count = fis.read(buff)) != -1) {
							Log.d(TAG, "count " + count);
							Log.d(TAG, new String(buff, 0, count));
						}
						Log.d(TAG, "end count " + count);
					} catch (Exception e) {
						Log.d(TAG, "exception break");
					}
				}
			}
		};
		t.start();
	}

	private class AudioCaptureAndSendThread implements Runnable {
		public void run() {
			try {
				sendAmrAudio();
			} catch (Exception e) {
				Log.e(TAG, "sendAmrAudio() 出错");
			}
		}

		private void sendAmrAudio() throws Exception {
			DatagramSocket udpSocket = new DatagramSocket();
			DataInputStream dataInput = new DataInputStream(receiver.getInputStream());

			PrintWriter os = null;
			try {
				socket = new Socket("192.168.1.5", 8089);
				os = new PrintWriter(socket.getOutputStream());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				os.write("i am here ! " + System.currentTimeMillis());
				Log.d(TAG, "i am here ! " + System.currentTimeMillis());
			} catch (Exception e2) {
				e2.printStackTrace();
				Log.d(TAG, " ddddd ");
			}

			while (threadEnable) {
				try {
					Log.d(TAG, " is running ");
					byte[] buff = new byte[1024];
					int count = -1;
					// String s = dataInput.readLine();
					// Log.d(TAG, "readline " + s);
					while ((count = dataInput.read(buff)) != -1) {
						Log.d(TAG, "count " + count);
						Log.d(TAG, new String(buff, 0, count));
					}
					Log.d(TAG, "end count " + count);

				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, " xxxxxx ");
				}
			}
		}
	}

	public void release() {
		threadEnable = false;
		if (mRecorderHelper != null) {
			mRecorderHelper.release();
		}
		if (receiver != null) {
			try {
				receiver.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (sender != null) {
			try {
				sender.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (lss != null) {
			try {
				lss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
