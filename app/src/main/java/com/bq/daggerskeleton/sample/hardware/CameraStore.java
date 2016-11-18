package com.bq.daggerskeleton.sample.hardware;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.bq.daggerskeleton.flux.Dispatcher;
import com.bq.daggerskeleton.flux.Store;
import com.bq.daggerskeleton.sample.app.App;
import com.bq.daggerskeleton.sample.app.AppScope;
import com.bq.daggerskeleton.sample.preview.PreviewSurfaceBufferCalculatedAction;
import com.bq.daggerskeleton.sample.preview.PreviewSurfaceDestroyedAction;
import com.bq.daggerskeleton.sample.preview.PreviewSurfaceReadyAction;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import durdinapps.rxcamera2.RxCameraManager;
import timber.log.Timber;


@AppScope
public class CameraStore extends Store<CameraState> {

    private final RxCameraManager cameraManager;
    private final Handler backgroundHandler;

    private final Semaphore cameraLock = new Semaphore(1);
    private static final int CAMERA_LOCK_TIMEOUT = 3000; //3s

    @Override protected CameraState initialState() {
        return new CameraState();
    }

    @Inject CameraStore(App app, Handler backgroundHandler) {
        this.cameraManager = new RxCameraManager((CameraManager) app.getSystemService(Context.CAMERA_SERVICE));
        this.backgroundHandler = backgroundHandler;

        Dispatcher.subscribe(CameraPermissionChanged.class, permissionChanged -> {
            CameraState newState = new CameraState(state());
            newState.canOpenCamera = permissionChanged.granted;
            setState(newState);
        });

        Dispatcher.subscribe(OpenCameraAction.class, a -> {
            if (state().cameraDevice == null) setState(openCamera(state()));
        });

        Dispatcher.subscribe(CloseCameraAction.class, a -> setState(closeCamera(state())));

        Dispatcher.subscribe(PreviewSurfaceDestroyedAction.class, a -> {
            CameraState newState = new CameraState(state());
            if (newState.previewTexture != null) {
                //Nothing to do here, surface auto releases, don't call release on it
                newState.previewTexture = null;
            }
            setState(newState);
        });

        Dispatcher.subscribe(PreviewSurfaceReadyAction.class, a -> {
            CameraState newState = new CameraState(state());
            newState.previewTexture = a.surfaceTexture;
            newState.previewSurface = new Surface(a.surfaceTexture);
            setState(newState);
        });

        Dispatcher.subscribe(PreviewSurfaceBufferCalculatedAction.class, a -> {
            CameraState newState = new CameraState(state());
            newState.previewSize = a.size;
            setState(newState);
        });

        Dispatcher.subscribe(CameraOpenedAction.class, a -> {
            CameraState newState = new CameraState(state());
            newState.cameraDevice = a.camera;
            setState(newState);
        });
    }

    private CameraState openCamera(CameraState state) {
        if (!state().canOpenCamera) return state;
        CameraState newState = new CameraState(state);

        try {
            if (!cameraLock.tryAcquire(CAMERA_LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                // STOPSHIP: 16/11/2016
                // TODO: 16/11/2016 Move to an error status and notify user properly
                throw new IllegalStateException("Failed to open camera in time");
            }
            Timber.v("Opening camera");

            populateCameraMap(newState.availableCameras);
            newState.selectedCamera = selectDefaultCamera(newState.availableCameras);

            cameraManager.openCamera(newState.selectedCamera,backgroundHandler)
                    .doOnTerminate(cameraLock::release)
                    .subscribe(openCameraEvent -> {
                        switch (openCameraEvent.eventType){
                            case OPENED:
                                cameraLock.release();
                                Dispatcher.dispatchOnUi(new CameraOpenedAction(openCameraEvent.cameraDevice));
                                break;
                            case DISCONNECTED:
                                Timber.d("Camera disconnected: %s", openCameraEvent.cameraDevice.getCameraDevice().getId());
                                break;
                            case ERROR:
                                break;
                        }
                    });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newState;
    }

    private CameraState closeCamera(CameraState state) {
        CameraState newState = new CameraState(state);
        try {
            if (!cameraLock.tryAcquire(CAMERA_LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                // STOPSHIP: 16/11/2016
                // TODO: 16/11/2016 Move to an error status and notify user properly
                throw new IllegalStateException("Failed to close camera in time");
            }

            if (newState.cameraDevice != null) {
                //TODO handle exceptions
                newState.cameraDevice.close().subscribe();
                newState.cameraDevice = null;
            }

        } catch (InterruptedException e) {
            e.printStackTrace(); //Should never happen
        } finally {
            cameraLock.release();
        }
        return newState;
    }


    private void populateCameraMap(Map<String, CameraCharacteristics> cameraMap) {
        try {
            for (String cameraId : cameraManager.getCameraManager().getCameraIdList()) {
                cameraMap.put(cameraId, cameraManager.getCameraManager().getCameraCharacteristics(cameraId));
            }
        } catch (CameraAccessException e) {
            e.printStackTrace(); //Permission denied, should not happen
        }
    }

    @NonNull private String selectDefaultCamera(Map<String, CameraCharacteristics> cameras) {
        for (String cameraId : cameras.keySet()) {
            CameraCharacteristics characteristics = cameras.get(cameraId);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return cameraId;
            }
        }
        return cameras.keySet().iterator().next();
    }

    @Module
    public static class CameraModule {
        @Provides @AppScope @IntoSet
        static Store<?> provideCameraStoreToSet(CameraStore store) {
            return store;
        }
    }
}
