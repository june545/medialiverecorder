/**
 * 
 */
package com.telecom.media.camera.live.source;

import android.view.SurfaceView;

/**
 * @author June Cheng
 * @date 2015年10月23日 下午5:09:24
 */
public class StreamSourceFactory {
	public static final int	STREAM_MEDIA_RECORDER	= 1;
	public static final int	STREAM_PREVIEW_FRAME	= 2;

	public static StreamSource create(int type, SurfaceView surfaceView) {
		if (type == STREAM_MEDIA_RECORDER) {
			return new ByMediaRecorder(surfaceView);
		} else if (type == STREAM_PREVIEW_FRAME) {
			return new ByPreviewCallback();
		}
		return null;
	}
}
