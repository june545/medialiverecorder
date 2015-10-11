package com.telecom.media.camera;

/**
 * 视频参数配置文件
 * 
 * @author June Cheng
 * @date 2015年9月8日 下午11:58:17
 */
public class VideoProfile {
	// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
	public int	fileFormat			= 1;

	// 设置录制的视频编码h263 h264
	public int	videoCodec			= 2;
	public int	audioCodec			= 3;

	public int	videoFrameWidth		= 640;
	public int	videoFrameHeight	= 480;

	// 设置录制的视频帧率
	public int	videoFrameRate		= 25;

	/**
	 * 采样率(单位：赫兹),不能随意设置-_-#，否则报错；采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
	 */
	public int	audioSampleRate		= 22050;

	public int	audioBitRate		= 64000;
	public int	videoBitRate		= 1000000;

	public int	audioChannels		= 2;
	public int	duration			= 30;
	public int	quality				= 1;

}
