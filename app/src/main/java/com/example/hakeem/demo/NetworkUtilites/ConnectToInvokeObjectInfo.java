package com.example.hakeem.demo.NetworkUtilites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.hakeem.demo.AudioPlayerActivity;
import com.example.hakeem.demo.GoogleVisionScanningActivity;
import com.example.hakeem.demo.utilities.Variables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by hakeem on 1/25/18.
 */

public class ConnectToInvokeObjectInfo extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = ConnectToInvokeObjectInfo.class.getName();

    @SuppressLint("StaticFieldLeak")
    private Context context;


    public ConnectToInvokeObjectInfo(Context context1) {
        context = context1;
    }

    @Override
    protected String doInBackground(String... params) {
        String operationType = params[0];

        String connectionResult = "";

        if (operationType.equals(Variables.selectAudioFilePathOperation)) {

            String statueName = params[1];
            String language = params[2];
            connectionResult = audioFilePathConnection(statueName, language);
            Log.e(LOG_TAG, "doInBackground connectionResult " + connectionResult);
//            try {
//                String statueNameKey = "statueName=";
//                String languageKey = "&language=";
//                String selecturl = "http://192.168.1.3/guidak_files/select_audio_file_path_with_POST.php";
//                String connectionParameters = statueNameKey + URLEncoder.encode(params[1], "UTF-8")
//                        + languageKey + URLEncoder.encode(params[2], "UTF-8");
//
//                byte[] paramtersbyt = connectionParameters.getBytes("UTF-8");
//
//                URL SelectAudioFilePathUrl = new URL(selecturl);
//
//
//                HttpURLConnection filePathConnection = (HttpURLConnection) SelectAudioFilePathUrl.openConnection();
//
//                filePathConnection.setRequestMethod("POST");
//                filePathConnection.setDoInput(true);
//                filePathConnection.setDoOutput(true);
//                Log.e(LOG_TAG, "1");
//
//                filePathConnection.getOutputStream().write(paramtersbyt);
//                filePathConnection.getOutputStream().flush();
//                filePathConnection.getOutputStream().close();
//
//                Log.e(LOG_TAG, "2");
//
//                InputStreamReader resultStreamReader = new InputStreamReader(filePathConnection.getInputStream());
//                Log.e(LOG_TAG, "3");
//
//                BufferedReader resultReader = new BufferedReader(resultStreamReader);
//                Log.e(LOG_TAG, "4");
//
//                connectionResult = resultReader.readLine();
//                resultReader.close();
//                resultStreamReader.close();
//                filePathConnection.disconnect();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        Log.e(LOG_TAG, "file path is " + connectionResult);
//        return connectionResult;
//        }
        }

        return connectionResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e(LOG_TAG, "onPostExecute " + s);

        GoogleVisionScanningActivity.fa.finish();

        if(s.equals("no")){


            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("Scan Result");
            builder.setMessage("unknown QR");
            AlertDialog alert1 = builder.create();
            alert1.show();

        }else if(null == s){

            Log.e(LOG_TAG, "no connection result is " + s);
        }
        else{

            Variables.audioFilePath = s;
            Variables.completeAudioFilePath = Variables.serverUrl + Variables.audioFilePath;
            Intent intent = new Intent(context, AudioPlayerActivity.class);
            context.startActivity(intent);
        }
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    private String audioFilePathConnection(String statueName, String language) {

        String connectionResult = null;

        try {
            String statueNameKey = "statueName=";
            String languageKey = "&language=";

            String selectUrl = Variables.serverUrl + "guidak_files/select_audio_file_path_with_POST.php";

            Log.e(LOG_TAG, "ppp " +statueName + "---" + language);

            String connectionParameters = statueNameKey + URLEncoder.encode(statueName, "UTF-8") + languageKey + URLEncoder.encode(language, "UTF-8");

            Log.e(LOG_TAG, "ppp " +connectionParameters);

            byte[] parametersByt = connectionParameters.getBytes("UTF-8");

            URL SelectAudioFilePathUrl = new URL(selectUrl);


            HttpURLConnection filePathConnection = (HttpURLConnection) SelectAudioFilePathUrl.openConnection();

            filePathConnection.setRequestMethod("POST");
            filePathConnection.setDoInput(true);
            filePathConnection.setDoOutput(true);
            Log.e(LOG_TAG, "1");

            filePathConnection.getOutputStream().write(parametersByt);
            filePathConnection.getOutputStream().flush();
            filePathConnection.getOutputStream().close();

            Log.e(LOG_TAG, "2");

            InputStreamReader resultStreamReader = new InputStreamReader(filePathConnection.getInputStream());
            Log.e(LOG_TAG, "3");

            BufferedReader resultReader = new BufferedReader(resultStreamReader);
            Log.e(LOG_TAG, "4");

            connectionResult = resultReader.readLine();
            Log.e(LOG_TAG, "res -" + connectionParameters);
            resultReader.close();
            resultStreamReader.close();
            filePathConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.e(LOG_TAG, "file path  fom method is " + connectionResult);
        return connectionResult;

    }

}


