package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

public class ViewfinderResultPointCallback implements ResultPointCallback {
    private final ViewfinderView viewfinderView;

    public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
//        LogUtil.trace("ReslutPoint::" + point);

        viewfinderView.addPossibleResultPoint(point);
    }

}