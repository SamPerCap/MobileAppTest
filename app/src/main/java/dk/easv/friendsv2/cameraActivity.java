package dk.easv.friendsv2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;


public class cameraActivity extends AppCompatActivity {

    public static String TAG = MainActivity.TAG;
    private static int CAMERA_REQUEST_CODE = 4;
    CheckBox cbSound;
    Switch swSwitchCamera;
    Button btntakePhoto;
    TextureView tvCameraPreview;
    CameraManager _cameraManager;
    CameraDevice _cameraDevice;
    int cameraFacing;
    TextureView.SurfaceTextureListener surfaceTextureListener;
    String cameraId;
    CameraCaptureSession cameraCaptureSession;
    HandlerThread backgroundThread;
    Handler backgroundHandler;
    private Size previewSize;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            cameraActivity.this._cameraDevice = cameraDevice;
            createPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            cameraActivity.this._cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            cameraActivity.this._cameraDevice = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_2);
        locateItems();

        btntakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTakePhotoClicked();
            }
        });
        setup();
    }

    private void btnTakePhotoClicked() {
        Log.d(TAG, "Take photo button clicked");
        FileOutputStream outputPhoto = null;
        try {
            File f = getOutputMediaFile();
            outputPhoto = new FileOutputStream(f);
            tvCameraPreview.getBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
            Log.d(TAG, "Photo taken - size: " + f.length());
            Log.d(TAG, "     Location: " + f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputPhoto != null) {
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getOutputMediaFile() {
        //Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            String postfix = "jpg";
            String prefix = "IMG";

            File mediaFile = new File(mediaStorageDir.getPath() +
                    File.separator + prefix +
                    "_" + timeStamp + "." + postfix);

            return mediaFile;
        }
        Log.d(TAG, "Permission for writing NOT granted");
        return null;
    }

    private void locateItems() {
        Log.d(TAG, "Locating items");
        cbSound = findViewById(R.id.chkBoxSound);
        swSwitchCamera = findViewById(R.id.swCameraSelect);
        btntakePhoto = findViewById(R.id.btn_take_photo);
        tvCameraPreview = findViewById(R.id.camera_preview);
    }

    private void setup() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);

        _cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };
    }

    private void setUpCamera() {
        try {
            for (String cameraId : _cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        _cameraManager.getCameraCharacteristics(cameraId);
                Log.d(TAG, "Looking for back camera, current cameraId=" + cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                _cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
                Log.d(TAG, "Camera with id= " + cameraId + " open.");

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createPreviewSession() {
        Log.d(TAG,"Creating a preview");
        try {
            SurfaceTexture surfaceTexture = tvCameraPreview.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            final CaptureRequest.Builder captureRequestBuilder = _cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            _cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (_cameraDevice == null) {
                                return;
                            }

                            try {
                                CaptureRequest captureRequest = captureRequestBuilder.build();
                                cameraActivity.this.cameraCaptureSession = cameraCaptureSession;
                                cameraActivity.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
