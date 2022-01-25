package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class CameraConfigurationManager {
    private static final String TAG = "CameraConfigurationManager";

    private static final int TEN_DESIRED_ZOOM = 27;
    @SuppressWarnings("unused")
    private static final int DESIRED_SHARPNESS = 30;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private final Context mContext;
    private int mPreviewFormat;
    private String mPreviewFormatString;

    private Point mScreenResolution;
    private Point mCameraResolution;

    public CameraConfigurationManager(Context context) {
        this.mContext = context;
    }

    /**
     * 获取屏幕参数，设置Camera参数
     *
     * @param camera
     */
    @SuppressWarnings("deprecation")
    public void getFromCameraParameters(Camera camera) {
//        LogUtil.trace();
//
        Camera.Parameters cameraParameters = camera.getParameters();
        mPreviewFormat = cameraParameters.getPreviewFormat();
        mPreviewFormatString = cameraParameters.get("preview-format");
        // Default preview format: 17/yuv420sp
//        LogUtil.d(TAG, "Default preview format: " + mPreviewFormat + '/' + mPreviewFormatString);

        WindowManager manager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        mScreenResolution = new Point(display.getWidth(), display.getHeight());
        // 实际屏幕分辨率：1024*600，实际系统获取到的是：1024*552
//        LogUtil.d(TAG, "Screen resolution: " + mScreenResolution);

        mCameraResolution = getCameraResolution(cameraParameters,
                mScreenResolution);
        // Screen resolution: Point(1024, 552) cameraResolution: Point(800, 600)
//        LogUtil.d(TAG, "Screen resolution: " + mScreenResolution + " cameraResolution: " + mCameraResolution);
    }

    /**
     * Sets the camera up to take preview images which are used for both preview
     * and decoding. We detect the preview format here so that
     * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
     * In the future we may want to force YUV420SP as it's the smallest, and the
     * planar Y can be used for barcode scanning without a copy in some cases.
     */
    public void setDesiredCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        // TODO 连续对焦
        parameters
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewSize(mCameraResolution.x, mCameraResolution.y);

        setFlash(parameters);
        setZoom(parameters);
        // 设置预览图像的方向
        camera.setDisplayOrientation(0);
        camera.setParameters(parameters);
    }

    public Point getCameraResolution() {
        return mCameraResolution;
    }

    public Point getScreenResolution() {
        return mScreenResolution;
    }

    public int getPreviewFormat() {
        return mPreviewFormat;
    }

    public String getPreviewFormatString() {
        return mPreviewFormatString;
    }

    private void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod(
                    "setDisplayOrientation", new Class[] {
                            int.class
                    });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] {
                        angle
                });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setSharpness(Camera.Parameters parameters) {
        int desiredSharpness = DESIRED_SHARPNESS;
        String maxSharpnessString = parameters.get("sharpness-max");
        if (maxSharpnessString != null) {
            try {
                int maxSharpness = Integer.parseInt(maxSharpnessString);
                if (desiredSharpness > maxSharpness) {
                    desiredSharpness = maxSharpness;
                }
            } catch (NumberFormatException nfe) {
//                LogUtil.w(TAG, "Bad sharpness-max: " + maxSharpnessString);
            }
        }
        parameters.set("sharpness", desiredSharpness);
    }

    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
//        LogUtil.trace();

        String previewSizeValueString = parameters.get("preview-size-values");
        if (previewSizeValueString == null) {
//            LogUtil.trace("previewSizeValueString is null...");
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;
        if (previewSizeValueString != null) {
            /**
             * preview-size-values parameter:
             * 176x144,320x240,352x288,640x480,720
             * x480,800x600,1280x720,1920x1080
             * ,3264x2448,3264x2448,1632x1224,1632
             * x1224,1632x1224,1632x1224,1632x1224
             */
//            LogUtil.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString,
                    screenResolution);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the
            // screen may not be.
            cameraResolution = new Point((screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(
            CharSequence previewSizeValueString, Point screenResolution) {
//        LogUtil.trace();

        int bestX = 0;
        int bestY = 0;
        // 0x7FFFFFFF --> 2147483647
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            previewSize = previewSize.trim();
//            LogUtil.d(TAG, "previewSize:" + previewSize);

            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
//                LogUtil.d(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;
            try {
                // 176
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                // 144
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
//                LogUtil.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x)
                    + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                // 递归：获取屏幕分辨率间最小差距
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }

            // TODO 是否能够增强性能？
            /*if (newX == 320) {
                break;
            }*/

        }

        if (bestX > 0 && bestY > 0) {
//            LogUtil.trace("return best...for camera resolution..." + bestX + "*" + bestY);
            return new Point(bestX, bestY);
        }

//        LogUtil.trace("return null...for camera resolution...");
        return null;
    }

    private void setFlash(Camera.Parameters parameters) {
        // FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
        // And this is a hack-hack to work around a different value on the
        // Behold II
        // Restrict Behold II check to Cupcake, per Samsung's advice
        // if (Build.MODEL.contains("Behold II") &&
        // CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
        if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) {
            parameters.set("flash-value", 1);
        } else {
            parameters.set("flash-value", 2);
        }
        // This is the standard setting to turn the flash off that all devices
        // should honor.
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Camera.Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");

        if (zoomSupportedString != null
                && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;
        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double
                        .parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
//                LogUtil.d(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters
                .get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
//                LogUtil.d(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString,
                    tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString
                        .trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    private static int findBestMotZoomValue(CharSequence stringValues,
                                            int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom
                    - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

}
