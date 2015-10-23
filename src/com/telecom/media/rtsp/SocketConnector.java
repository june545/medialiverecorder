package com.telecom.media.rtsp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class SocketConnector {
	private static ThreadPoolExecutor	executor;
	private static Socket				socket;
	static PrintWriter					os;
	static {
		if (executor == null) {
			executor = new ThreadPoolExecutor(2, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3), new ThreadPoolExecutor.DiscardOldestPolicy());
		}
	}

	public static void send(final String s) {
		if (socket == null) {
			try {
				socket = new Socket("192.168.18.37", 8089);
				socket.setSoTimeout(3000);
				os = new PrintWriter(socket.getOutputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					if (os != null) {
						os.write("data " + s);
//						os.flush();
//						Log.d("SocketConnector", "sending data");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		executor.execute(r);

	}
}
