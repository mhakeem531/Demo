package com.example.hakeem.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.hakeem.demo.NetworkUtilites.ConnectToInvokeObjectInfo;
import com.example.hakeem.demo.utilities.Variables;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class GoogleVisionScanningActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    public static final String TAG = GoogleVisionScanningActivity.class.getSimpleName();
//    SurfaceView cameraView;
//    BarcodeDetector barcode;
//    CameraSource cameraSource;
//    SurfaceHolder holder;

    private String statueName;
    private String fileLanguage;

    public static Activity fa;
    BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_vision_scanner2);

        fa = this;
//        cameraView = findViewById(R.id.cameraView);
//        cameraView.setZOrderMediaOverlay(true);
//        holder = cameraView.getHolder();
//        barcode = new BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.QR_CODE)
//                .build();
//        if(!barcode.isOperational()){
//            Toast.makeText(getApplicationContext(), "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();
//            this.finish();
//        }
//        cameraSource = new CameraSource.Builder(this, barcode)
//                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setRequestedFps(24)
//                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(1920,1024)
//                .build();
//        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try{
//                    if(ContextCompat.checkSelfPermission(GoogleVisionScanningActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                        cameraSource.start(cameraView.getHolder());
//                    }
//                }
//                catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                cameraSource.stop();
//
//            }
//        });
//        barcode.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes =  detections.getDetectedItems();
//
//                Log.e("yup" , "1");
//                if(barcodes.size() > 0){
//                    Log.e("yup" , "2");
//                    Intent intent = new Intent();
//                    intent.putExtra("barcode", barcodes.valueAt(0));
//                    Log.e("yup" , barcodes.valueAt(0).toString());
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        });
        String key = getResources().getString(R.string.pref_langs_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Variables.audioFileLanguage = prefs.getString(key, null);

        this.fileLanguage = prefs.getString(key, null);

        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);


    }

    @Override
    public void onScanned(Barcode barcode) {
        // playing barcode reader beep sound
        barcodeReader.playBeep();

        // ticket details activity by passing barcode
//        Intent intent = new Intent(GoogleVisionScanningActivity.this, AudioPlayerActivity.class);
//        intent.putExtra("code", barcode.displayValue);
//        startActivity(intent);

        Log.e("result isssssss " , barcode.displayValue);
        Variables.statueName = barcode.displayValue;
        this.statueName = barcode.displayValue;
        FetchStatueAudioFilePath();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {

    }


    public void FetchStatueAudioFilePath(){

        ConnectToInvokeObjectInfo invokeAudioFilePath = new ConnectToInvokeObjectInfo(this);
        invokeAudioFilePath.execute(Variables.selectAudioFilePathOperation, this.statueName, this.fileLanguage);

    }

}
