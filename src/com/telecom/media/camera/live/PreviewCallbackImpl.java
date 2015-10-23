/**
 * 
 */
package com.telecom.media.camera.live;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.telecom.media.camera.live.frame.FrameStreamer;

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
	private final String TAG = "PreviewCallbackImpl";

	private String ipname = "192.168.18.37";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.Camera.PreviewCallback#onPreviewFrame(byte[],
	 * android.hardware.Camera)
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.d(TAG, "onPreviewFrame");
		Size size = camera.getParameters().getPreviewSize();
		try {
			// 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
			YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
			if (image != null) {
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
				outstream.flush();
				// 启用线程将图像数据发送出去
				(new FrameStreamer(new ByteArrayInputStream(outstream.toByteArray()), ipname, 8089)).start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("Sys", "Error:" + ex.getMessage());
		}
	}

}
