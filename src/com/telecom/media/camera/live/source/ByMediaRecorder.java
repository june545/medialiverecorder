package com.telecom.media.camera.live.source;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Random;

import com.telecom.media.camera.ByteUtil;
import com.telecom.media.camera.CameraHelper;
import com.telecom.media.camera.RecorderHelper;
import com.telecom.media.rtsp.SocketConnector;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import android.view.SurfaceView;

/**
 * 
 * @author June Cheng
 * @date 2015年10月23日 下午4:56:10
 */
public class ByMediaRecorder extends StreamSource {
	private final String		TAG					= "ByMediaRecorder";
	private static final String	LOCAL_SOCKET_ADDR	= "com.telecom.media.live-";
	private RecorderHelper		mRecorderHelper;
	private LocalSocket			receiver, sender;
	private LocalServerSocket	lss;
	private int					mSocketId;

	private Thread mSendThread;

	private SurfaceView mSurfaceView;

	public ByMediaRecorder(SurfaceView surfaceView) {
		this.mSurfaceView = surfaceView;
	}

	private void initLocalSocket() {
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

	@Override
	public void start() {
		Log.d(TAG, "start");
		if (mRecorderHelper == null) {
			mRecorderHelper = new RecorderHelper(CameraHelper.getInstance().getCamera(),
					mSurfaceView.getHolder().getSurface());

			initLocalSocket();

			try {
				mRecorderHelper.prepare(receiver.getFileDescriptor());
				mRecorderHelper.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mSendThread = new AudioCaptureAndSendThread();
			mSendThread.start();
		}
	}

	@Override
	public void stop() {
		Log.d(TAG, "stop");
		if (mSendThread != null) {
			((AudioCaptureAndSendThread) mSendThread).cancel();
			mSendThread.interrupt();
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

	private class AudioCaptureAndSendThread extends Thread {
		private boolean threadEnable = true;

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

			// This will skip the MPEG4 header if this step fails we can't stream anything :(
			try {
				byte buffer[] = new byte[4];
				// Skip all atoms preceding mdat atom
				while (!Thread.interrupted()) {
					while (dataInput.read() != 'm')
						;
					dataInput.read(buffer, 0, 3);
					if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't')
						break;
				}
			} catch (IOException e) {
				Log.e(TAG, "Couldn't skip mp4 header :/");
				throw e;
			}

			while (threadEnable) {
				try {
					//					Log.d(TAG, " is running ");
					byte[] buff = new byte[1024];
					int count = -1;

					count = dataInput.read(buff);
					if (count != -1) {
						String s = ByteUtil.getHexString(buff);
						//						Log.d(TAG, " data " + s.toUpperCase());

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
