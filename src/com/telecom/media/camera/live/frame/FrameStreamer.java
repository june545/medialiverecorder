/**
 * 
 */
package com.telecom.media.camera.live.frame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author June Cheng
 * @date 2015年10月23日 下午3:36:35
 */
public class FrameStreamer implements Runnable {
	private Thread			t;
	private byte[]			mBuffer	= new byte[1024 * 8];
	private String			host;
	private int				port;
	private Socket			mSocket;
	private InputStream		mInputStream;
	private OutputStream	mOutputStream;

	public FrameStreamer(InputStream in, String host, int port) {
		this.host = host;
		this.port = port;
		this.mInputStream = in;
	}

	@Override
	public void run() {
		try {
			try {
				mSocket = new Socket(host, port);
				mOutputStream = mSocket.getOutputStream();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int amount;
			while ((amount = mInputStream.read(mBuffer)) != -1) {
				mOutputStream.write(mBuffer, 0, amount);
			}
			mOutputStream.flush();
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}

	public void stop() {
		try {
			mOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
