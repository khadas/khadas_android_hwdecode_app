package com.khadas.khadashwdecoderdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button_decode;

    private EditText editText_fps;
    private EditText editText_width;
    private EditText editText_height;
    private EditText editText_format;

    private String fps;
    private String width;
    private String height;
    private String format;
    private String video_file;

    private int mfileIndex = 0;

    private static final String TAG = "MainActivity";

    private KhadasHwDecodeManager khadashwhecodemanager = new KhadasHwDecodeManager();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView)findViewById(R.id.title);
        textView.setText("hardware decorder");


        button_decode = (Button) findViewById(R.id.button_decode);
        editText_fps = (EditText) findViewById(R.id.edit_fps);
        editText_width = (EditText) findViewById(R.id.edit_width);
        editText_height = (EditText) findViewById(R.id.edit_height);
        editText_format = (EditText) findViewById(R.id.edit_format);

        final Spinner mChannel = (Spinner) findViewById(R.id.choose_channel);
        ArrayAdapter<CharSequence> channelAdapter;
        channelAdapter = ArrayAdapter.createFromResource(this,
                R.array.hwdecode_video_list, android.R.layout.simple_spinner_item);
        channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChannel.setAdapter(channelAdapter);

        mChannel.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
            boolean mInit = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                       long id) {
                if (!mInit) {
                    mfileIndex = position;
                    //mWifiConfig.apBand = mfileIndex;
                    Log.i(TAG, "config on mfileIndex : " + mfileIndex );
                } else {
                    mInit = false;
                    mChannel.setSelection(mfileIndex);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }
        );

        button_decode.setOnClickListener(this);

        copyVideoFile(this,"bbb_avc_640x360_768kbps_30fps.h264");
        copyVideoFile(this,"bbb_hevc_640x360_1600kbps_30fps.hevc");
        copyVideoFile(this,"bbb_mpeg4_352x288_512kbps_30fps.m4v");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_decode:
                onClickDecode();
                break;
            default:
                break;
        }
    }

    public static String exec(String command) {

        Process process = null;
        BufferedReader reader = null;
        InputStreamReader is = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            is = new InputStreamReader(process.getInputStream());
            reader = new BufferedReader(is);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (is != null) {
                    is.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyVideoFile(Context context, String tab_name) {
        InputStream in = null;
        FileOutputStream out = null;
        Log.d(TAG, "copyVideoFile enter");
        /**data/data/路径*/
        String path = "/data";
        File file = new File(path + "/" + tab_name);
        exec("chmod 777 /data");

        try {
            //创建文件夹
            File file_ = new File(path);
            if (!file_.exists()) {
                Log.d(TAG, "copyVideoFile mkdirs ");
                file_.mkdirs();
            }

            if (file.exists())//删除已经存在的
                Log.d(TAG, "copyVideoFile deleteOnExit ");
            file.deleteOnExit();


            if (!file.exists()) {
                Log.d(TAG, "copyVideoFile !file.exists ");
                file.createNewFile();
            }

            AssetManager assmgr = context.getApplicationContext().getAssets();

            in = assmgr.open(tab_name);

            out = new FileOutputStream(file);
            int length = -1;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void onClickDecode() {
        //处理逻辑
        new Thread(){
            @Override
            public void run() {
                super.run();
                    try {
                        fps = editText_fps.getText().toString();
                        width = editText_fps.getText().toString();
                        height = editText_fps.getText().toString();
                        format = editText_format.getText().toString();
                        switch (mfileIndex) {
                            case 0:
                                video_file = "/data/bbb_avc_640x360_768kbps_30fps.h264";
                                break;
                            case 1:
                                video_file = "/data/bbb_hevc_640x360_1600kbps_30fps.hevc";
                                break;
                            case 2:
                                video_file = "/data/bbb_mpeg4_352x288_512kbps_30fps.m4v";
                                break;
                        }
                        exec("chmod 777 data");
                        exec("chmod 666 /sys/class/graphics/fb0/osd_display_debug");
                        exec("chmod 666 /sys/class/graphics/fb0/blank");
                        exec("chmod 666 /sys/class/video/video_global_output");


                        khadashwhecodemanager.hardware_decode(video_file, Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(fps), 2, Integer.parseInt(format));
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


            }
        }.start();
    }

}


