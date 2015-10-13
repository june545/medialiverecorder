/**
 * 
 */
package com.telecom.media.camera.live;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;

/**
 * @author June Cheng
 * @date 2015年10月13日 下午4:55:20
 */
public class PreviewCallbackImpl implements PreviewCallback {

	private String ipname = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.Camera.PreviewCallback#onPreviewFrame(byte[],
	 * android.hardware.Camera)
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Size size = camera.getParameters().getPreviewSize();
		try {
			// 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
			YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
			if (image != null) {
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
				outstream.flush();
				// 启用线程将图像数据发送出去
				Thread th = new MyThread(outstream, ipname);
				th.start();
			}
		} catch (Exception ex) {
			Log.e("Sys", "Error:" + ex.getMessage());
		}
	}

	class MyThread extends Thread {
		private byte					byteBuffer[]	= new byte[1024];
		private OutputStream			outsocket;
		private ByteArrayOutputStream	myoutputstream;
		private String					ipname;

		public MyThread(ByteArrayOutputStream myoutputstream, String ipname) {
			this.myoutputstream = myoutputstream;
			this.ipname = ipname;
			try {
				myoutputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// 将图像数据通过Socket发送出去
				Socket tempSocket = new Socket(ipname, 8089);
				outsocket = tempSocket.getOutputStream();
				ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
				int amount;
				while ((amount = inputstream.read(byteBuffer)) != -1) {
					outsocket.write(byteBuffer, 0, amount);
				}
				myoutputstream.flush();
				myoutputstream.close();
				tempSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
