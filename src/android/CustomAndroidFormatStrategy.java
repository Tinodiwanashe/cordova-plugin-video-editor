package org.apache.cordova.videoeditor;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import net.ypresto.androidtranscoder.format.MediaFormatStrategy;
import net.ypresto.androidtranscoder.format.OutputFormatUnavailableException;

/**
 * Created by ehmm on 02.05.2016.
 *
 *
 */
public class CustomAndroidFormatStrategy implements MediaFormatStrategy {

    private static final String TAG = "CustomFormatStrategy";
    private static final int DEFAULT_BITRATE = 8000000;
    private static final int DEFAULT_FRAMERATE = 30;
    private static final int DEFAULT_WIDTH = 0; // 0 means use original
    private static final int DEFAULT_HEIGHT = 0; // 0 means use original
    private static final int IFRAME_INTERVAL = 3; // GOP interval in seconds
    private static final String VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC; // H.264
    // Minimum dimensions to prevent invalid outputs
    private static final int MIN_DIMENSION = 16; // Must be multiple of 16 for most codecs
    private final int mBitRate;
    private final int mFrameRate;
    private final int width;
    private final int height;

    public CustomAndroidFormatStrategy() {
        this.mBitRate = DEFAULT_BITRATE;
        this.mFrameRate = DEFAULT_FRAMERATE;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    public CustomAndroidFormatStrategy(final int bitRate, final int frameRate, final int width, final int height) {
        this.mBitRate = bitRate;
        this.mFrameRate = frameRate;
        this.width = width;
        this.height = height;
    }

    public MediaFormat createVideoOutputFormat(MediaFormat inputFormat) {
        int inWidth = inputFormat.getInteger(MediaFormat.KEY_WIDTH);
        int inHeight = inputFormat.getInteger(MediaFormat.KEY_HEIGHT);
        int inLonger, inShorter, outWidth, outHeight, outLonger;
        double aspectRatio;

        if (this.width >= this.height) {
            outLonger = this.width;
        } else {
            outLonger = this.height;
        }

        if (inWidth >= inHeight) {
            inLonger = inWidth;
            inShorter = inHeight;

        } else {
            inLonger = inHeight;
            inShorter = inWidth;

        }

        if (inLonger > outLonger && outLonger > 0) {
            if (inWidth >= inHeight) {
                aspectRatio = (double) inLonger / (double) inShorter;
                outWidth = outLonger;
                outHeight = Double.valueOf(outWidth / aspectRatio).intValue();

            } else {
                aspectRatio = (double) inLonger / (double) inShorter;
                outHeight = outLonger;
                outWidth = Double.valueOf(outHeight / aspectRatio).intValue();
            }
        } else {
            outWidth = inWidth;
            outHeight = inHeight;
        }

        MediaFormat format = MediaFormat.createVideoFormat("video/avc", outWidth, outHeight);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 3);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);

        // Set color format (required for surface encoding)
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        
        // Add profile and level for better compatibility (H.264 Baseline Profile)
        // This ensures broad device compatibility
        outputFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        
        // Set bitrate mode to VBR (Variable Bitrate) for better quality
        outputFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
        
        return format;

    }

    public MediaFormat createAudioOutputFormat(MediaFormat inputFormat) {
        return null;
    }

}
