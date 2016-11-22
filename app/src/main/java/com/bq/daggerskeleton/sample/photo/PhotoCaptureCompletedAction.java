package com.bq.daggerskeleton.sample.photo;

import android.hardware.camera2.TotalCaptureResult;

import com.bq.daggerskeleton.flux.Action;

@SuppressWarnings("javadoctype")
public class PhotoCaptureCompletedAction implements Action {
    public final TotalCaptureResult result;

    public PhotoCaptureCompletedAction(TotalCaptureResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "CaptureCompletedAction{" +
                "result=" + result +
                '}';
    }
}
