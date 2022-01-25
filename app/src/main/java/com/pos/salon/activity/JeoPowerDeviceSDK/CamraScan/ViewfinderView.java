package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.pos.salon.R;

import java.util.Collection;
import java.util.HashSet;

public class ViewfinderView extends View {

    @SuppressWarnings("unused")
    private static final int[] SCANNER_ALPHA = {
            0, 64, 128, 192, 255, 192, 128, 64
    };
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private static final int SPEEN_DISTANCE = 5;
    private int slideTop;
    @SuppressWarnings("unused")
    private int slideBottom;
    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    @SuppressWarnings("unused")
    private static final int MIDDLE_LINE_PADDING = 5;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final int MIDDLE_LINE_WIDTH = 6;
    /**
     * 四个绿色边角对应的宽度
     */
    @SuppressWarnings("unused")
    private static final int CORNER_WIDTH = 10;
    /**
     * 四个绿色边角对应的长度
     */
    @SuppressWarnings("unused")
    private int screenRate;

    private static float density;
    private final Paint paint;
    private Bitmap resultBitmap;
    @SuppressWarnings("unused")
    private final int maskColor;
    @SuppressWarnings("unused")
    private final int resultColor;
    private final int frameColor;
    @SuppressWarnings("unused")
    private final int laserColor;
    private final int resultPointColor;
    @SuppressWarnings("unused")
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private boolean mIsFirst;
    private Rect mFrameRect;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        LogUtil.trace();

        // density 1.0
        density = context.getResources().getDisplayMetrics().density;
        // 将像素转换成dp
        screenRate = (int) (20 * density);

        paint = new Paint();
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);

        Resources resources = getResources();

        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        // 外框
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 0 0 1024 552
        mFrameRect = CameraManager.get().getFramingRect();
        if (mFrameRect == null) {
            return;
        }

        // 1024 552
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 计算初始化中间线滑动的最上边和最下边
        if (!mIsFirst) {
            mIsFirst = true;
            slideTop = mFrameRect.top + 75;
            slideBottom = mFrameRect.bottom - 80;
            // slideTop=75; slideBottom=472
//            LogUtil.trace("slideTop=" + slideTop + "; slideBottom=" + slideBottom);
        }

        // 外面半透明的画面
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, mFrameRect.top, paint);
        canvas.drawRect(0, mFrameRect.top, mFrameRect.left,
                mFrameRect.bottom + 1, paint);
        canvas.drawRect(mFrameRect.right + 1, mFrameRect.top, width,
                mFrameRect.bottom + 1, paint);
        canvas.drawRect(0, mFrameRect.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            // 0 0
            canvas.drawBitmap(resultBitmap, mFrameRect.left, mFrameRect.top,
                    paint);
        } else {
            // Draw a two pixel solid black border inside the framing rect
            paint.setColor(frameColor);
            // 0 0 1025 2
            canvas.drawRect(mFrameRect.left, mFrameRect.top,
                    mFrameRect.right + 1, mFrameRect.top + 2, paint);
            // 0 2 2 551
            canvas.drawRect(mFrameRect.left, mFrameRect.top + 2,
                    mFrameRect.left + 2, mFrameRect.bottom - 1, paint);
            // 1023 0 1025 551
            canvas.drawRect(mFrameRect.right - 1, mFrameRect.top,
                    mFrameRect.right + 1, mFrameRect.bottom - 1, paint);
            // 0 551 1025 553
            canvas.drawRect(mFrameRect.left, mFrameRect.bottom - 1,
                    mFrameRect.right + 1, mFrameRect.bottom + 1, paint);

            paint.setColor(Color.GREEN);
            // 左上册
            // left:183.0; top:95.0; right:170.0; bottom:60.0 对应15#和16#点坐标
            canvas.drawRect(mFrameRect.left + 170, mFrameRect.top + 60,
                    mFrameRect.left + 183, mFrameRect.top + 95, paint);
            // left:200.0; top:75.0; right:180.0; bottom:60.0 对应13#和14#点坐标
            canvas.drawRect(mFrameRect.left + 180, mFrameRect.top + 60,
                    mFrameRect.left + 200, mFrameRect.top + 75, paint);
            // 右上侧
            // left:824.0; top:75.0; right:844.0; bottom:60.0 对应11#和12#点坐标
            canvas.drawRect(mFrameRect.right - 200, mFrameRect.top + 60,
                    mFrameRect.right - 180, mFrameRect.top + 75, paint);
            // left:841.0; top:95.0; right:854.0; bottom:60.0 对应9#和10#点坐标
            canvas.drawRect(mFrameRect.right - 183, mFrameRect.top + 60,
                    mFrameRect.right - 170, mFrameRect.top + 95, paint);
            // 左下侧
            // left:203.0; top:477.0; right:180.0; bottom:492.0 对应7#和8#点坐标
            canvas.drawRect(mFrameRect.left + 180, mFrameRect.bottom - 75,
                    mFrameRect.left + 203, mFrameRect.bottom - 60, paint);
            // left:183.0; top:457.0; right:170.0; bottom:492.0 对应5#和6#点坐标
            canvas.drawRect(mFrameRect.left + 170, mFrameRect.bottom - 95,
                    mFrameRect.left + 183, mFrameRect.bottom - 60, paint);
            // 右下侧
            // left:841.0; top:457.0; right:854.0; bottom:492.0 对应3#和4#点坐标
            canvas.drawRect(mFrameRect.right - 183, mFrameRect.bottom - 95,
                    mFrameRect.right - 170, mFrameRect.bottom - 60, paint);
            // left:821.0; top:477.0; right:844.0; bottom:492.0 对应1#和2#点坐标
            canvas.drawRect(mFrameRect.right - 203, mFrameRect.bottom - 75,
                    mFrameRect.right - 180, mFrameRect.bottom - 60, paint);

            // 绘制扫描线
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= mFrameRect.bottom - 80) {
                slideTop = mFrameRect.top + 75;
            }
            canvas.drawRect(mFrameRect.left + 190, slideTop - MIDDLE_LINE_WIDTH
                    / 2, mFrameRect.right - 190, slideTop + MIDDLE_LINE_WIDTH
                    / 20, paint);

            // 绘制扫描识别点
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);

                // TODO foreach cast to
                // java.util.ConcurrentModificationException
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(mFrameRect.left + point.getX(),
                            mFrameRect.top + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(mFrameRect.left + point.getX(),
                            mFrameRect.top + point.getY(), 3.0f, paint);
                }
            }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, mFrameRect.left,
                    mFrameRect.top, mFrameRect.right, mFrameRect.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    /**
     * 使用LogUtil打印Rect的坐标点
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void printRectPoint(float left, float top, float right, float bottom) {
//        LogUtil.trace("left:" + left + "; top:" + top + "; right:" + right + "; bottom:" + bottom);
    }

}

