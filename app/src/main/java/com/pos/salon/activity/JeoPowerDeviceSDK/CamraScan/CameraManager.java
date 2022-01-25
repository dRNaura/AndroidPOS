package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.google.zxing.ResultPoint;

import java.io.IOException;

public class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1024;
    private static final int MAX_FRAME_HEIGHT = 600;

    static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException nfe) {
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
        // 22
//        LogUtil.trace("SDK_INT::" + SDK_INT);
    }

    @SuppressWarnings("unused")
    private final Context mContext;
    private static CameraManager mCameraManager;
    private final CameraConfigurationManager mCameraConfigurationManager;
    private Camera mCamera;

    private Rect mFramingRect;
    private Rect mFramingRectInPreview;
    private boolean mIsInitialized;
    private boolean mIsPreview;
    private final boolean mIsUseOneShotPreviewCallback;

    private final PreviewCallback mPreviewCallback;
    private final AutoFocusCallback mAutoFocusCallback;

    public static void init(Context context) {
        if (mCameraManager == null) {
            mCameraManager = new CameraManager(context);
        }
    }

    private CameraManager(Context context) {
//        LogUtil.trace();

        this.mContext = context;
        this.mCameraConfigurationManager = new CameraConfigurationManager(
                context);
        mIsUseOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3;
        // true
//        LogUtil.trace("useOneShotPreviewCallback::" + mIsUseOneShotPreviewCallback);

        // Camera预览后，捕获图像后的接口回调
        mPreviewCallback = new PreviewCallback(mCameraConfigurationManager,
                mIsUseOneShotPreviewCallback);
        mAutoFocusCallback = new AutoFocusCallback();
    }

    public static CameraManager get() {
        return mCameraManager;
    }

    /**
     * 打开Camera驱动，初始化硬件参数
     *
     * @param holder Camera预览的钩子对象，实现实时预览
     * @throws IOException
     */
    public void openDriver(SurfaceHolder holder) throws IOException {
//        LogUtil.trace();

        if (mCamera == null) {
            mCamera = Camera.open();

            if (mCamera == null) {
                throw new IOException();
            } else {
//                LogUtil.d(TAG, "CameraManager  camera: " + mCamera);
            }

            mCamera.setPreviewDisplay(holder);
            // false
//            LogUtil.d(TAG, "CameraManager  openDriver initialized: " + mIsInitialized);
            if (!mIsInitialized) {
                mIsInitialized = true;
                mCameraConfigurationManager.getFromCameraParameters(mCamera);
            }

            mCameraConfigurationManager.setDesiredCameraParameters(mCamera);
            // TODO 是不是此处不应该取消自动聚焦？如果要实现连续的自动对焦，要在startPreview()之后调用
            mCamera.cancelAutoFocus();
        }
    }

    /**
     * 初始化Camera硬件参数
     */
    public void initDriver() {
        if (!mIsInitialized) {
            mIsInitialized = true;
            mCameraConfigurationManager.getFromCameraParameters(mCamera);
        }
        mCameraConfigurationManager.setDesiredCameraParameters(mCamera);
        // TODO mCamera.cancelAutoFocus();再次被执行
        mCamera.cancelAutoFocus();
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (mCamera != null) {
            stopPreview();
            FlashlightManager.disableFlashlight();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Camera获取预览图像，并显示
     */
    public void startPreview() {
//        LogUtil.d(TAG, "startPreview::mIsPreview=" + mIsPreview);

        if (mCamera != null) {
//            LogUtil.trace("camera is not null...");
        }

        if (!mIsPreview) {
//            LogUtil.trace("previewing is false...");
        }

        if (mCamera != null && !mIsPreview) {
//            LogUtil.d(TAG, "startPreview::camera.startPreview()...");
            mCamera.startPreview();
            mIsPreview = true;
        }
    }

    /**
     * 停止Camera预览
     */
    public void stopPreview() {
        if (mCamera != null && mIsPreview) {
            if (!mIsUseOneShotPreviewCallback) {
                mCamera.setPreviewCallback(null);
            }
            mCamera.stopPreview();
            mPreviewCallback.setHandler(null, 0);
            mAutoFocusCallback.setHandler(null, 0);
            mIsPreview = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data
     * will arrive as byte[] in the message.obj field, with width and height
     * encoded as message.arg1 and message.arg2, respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (mCamera != null && mIsPreview) {
            mPreviewCallback.setHandler(handler, message);

            // true
//            LogUtil.trace("mUseOneShotPreviewCallback::" + mIsUseOneShotPreviewCallback);
            if (mIsUseOneShotPreviewCallback) {
                // 请求获取一帧图像，并回调mPreviewCallback的onPreviewFrame()
                mCamera.setOneShotPreviewCallback(mPreviewCallback);
            } else {
                mCamera.setPreviewCallback(mPreviewCallback);
            }
        }
    }

    /**
     * Camera执行一次自动聚焦
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (mCamera != null && mIsPreview) {
            mAutoFocusCallback.setHandler(handler, message);
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }

    /**
     * Calculates the framing Rect which the UI should draw to show the user
     * where to place the barcode. This target helps with alignment as well as
     * forces the user to hold the device far enough away to ensure the image
     * will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    public Rect getFramingRect() {
        // 762C项目：1024*552
        Point screenResolution = mCameraConfigurationManager
                .getScreenResolution();

        if (mFramingRect == null) {
            if (mCamera == null) {
                return null;
            }

            int width = screenResolution.x;
            if (width < MIN_FRAME_WIDTH) {
                width = MIN_FRAME_WIDTH;
            } else if (width > MAX_FRAME_WIDTH) {
                width = MAX_FRAME_WIDTH;
            }

            int height = screenResolution.y;
            if (height < MIN_FRAME_HEIGHT) {
                height = MIN_FRAME_HEIGHT;
            } else if (height > MAX_FRAME_HEIGHT) {
                height = MAX_FRAME_HEIGHT;
            }

            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;

            // 0 0
//            LogUtil.trace("leftOffset:" + leftOffset + "; topOffset:" + topOffset);
            // 0 0 1024 552
            mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width,
                    topOffset + height);
        }

        return mFramingRect;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview
     * frame, not UI / screen.
     */
    public Rect getFramingRectInPreview() {
        if (mFramingRectInPreview == null) {
            // 0 0 1024 552
            Rect rect = new Rect(getFramingRect());
            // 800 600
            Point cameraResolution = mCameraConfigurationManager
                    .getCameraResolution();

            // 1024 552
            Point screenResolution = mCameraConfigurationManager
                    .getScreenResolution();

            // add by zhangfeng 20170707 ZXing竖屏扫描
            /*
             * rect.left = rect.left * cameraResolution.y / screenResolution.x;
             * rect.right = rect.right * cameraResolution.y /
             * screenResolution.x; rect.top = rect.top * cameraResolution.x /
             * screenResolution.y; rect.bottom = rect.bottom *
             * cameraResolution.x / screenResolution.y;
             */
            // end add by zhangfeng 20170707 ZXing竖屏扫描

            // add by zhangfeng 20170707 ZXing横屏扫描
            // 根据相机分辨率和屏幕分辨率的比例对屏幕中央聚焦框进行调整
            rect.left = rect.left * cameraResolution.x / screenResolution.x;
            rect.right = rect.right * cameraResolution.x / screenResolution.x;
            rect.top = rect.top * cameraResolution.y / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            // end add by zhangfeng 20170707 ZXing横屏扫描

            mFramingRectInPreview = rect;
        }

        // Rect(0, 0, 800, 600)
//        LogUtil.trace("mFramingRectInPreview:" + mFramingRectInPreview);
        return mFramingRectInPreview;
    }

    /**
     * Converts the result points from still resolution coordinates to screen
     * coordinates.
     *
     * @param points The points returned by the Reader subclass through
     *            Result.getResultPoints().
     * @return An array of Points scaled to the size of the framing rect and
     *         offset appropriately so they can be drawn in screen coordinates.
     */

    public Point[] convertResultPoints(ResultPoint[] points) {
        Rect frame = getFramingRectInPreview();
        int count = points.length;
        Point[] output = new Point[count];
        for (int x = 0; x < count; x++) {
            output[x] = new Point();
            output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
            output[x].y = frame.top + (int) (points[x].getY() + 0.5f);
        }
        return output;
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on
     * the format of the preview buffers, as described by Camera.Parameters.
     *
     * @param data A preview frame.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
                                                         int width, int height) {
        // (0, 0, 800, 600)
        Rect rect = getFramingRectInPreview();
        // 17
        int previewFormat = mCameraConfigurationManager.getPreviewFormat();
        // yuv420sp
        String previewFormatString = mCameraConfigurationManager
                .getPreviewFormatString();

        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to
            // support. In theory, it's the only one we should ever care about.
            case PixelFormat.YCbCr_420_SP:
                // This format has never been seen in the wild, but is
                // compatible as
                // we only care about the Y channel, so allow it.
            case PixelFormat.YCbCr_422_SP:
//                LogUtil.trace("PixelFormat.YCbCr_420_SP and PixelFormat.YCbCr_422_SP");
                return new PlanarYUVLuminanceSource(data, width, height,
                        rect.left, rect.top, rect.width(), rect.height());
            default:
                // The Samsung Moment incorrectly uses this variant instead of
                // the
                // 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can
                // read
                // it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height,
                            rect.left, rect.top, rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: "
                + previewFormat + '/' + previewFormatString);
    }

    public void setIsPreviewing(boolean isPreviewing) {
        this.mIsPreview = isPreviewing;
    }

    public boolean getIsPreviewing() {
        return this.mIsPreview;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin,
                                                   int hardMax) {
        int dim = 5 * resolution / 8; // Target 5/8 of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

}
