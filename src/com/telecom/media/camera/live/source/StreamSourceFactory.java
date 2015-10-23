/**
 * 
 */
package com.telecom.media.camera.live.source;

/**
 * @author June Cheng
 * @date 2015年10月23日 下午5:09:24
 */
public class StreamSourceFactory {
	public static final int	STREAM_MEDIA_RECORDER	= 1;
	public static final int	STREAM_PREVIEW_FRAME	= 2;

	public StreamSource create(int type) {
		if (type == STREAM_MEDIA_RECORDER) {
			return new ByMediaRecorder(null);
		} else if (type == STREAM_PREVIEW_FRAME) {
			return new ByPreviewCallback();
		}
		return null;
	}
}
