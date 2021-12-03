package com.khadas.khadashwdecoderdemo;

import android.content.Context;
import android.util.Log;

public class KhadasHwDecodeManager {
    private static final String TAG = "KhadasHwDecode";
    private static final boolean DEBUG = true;
    private Context mContext = null;

    static {
        System.loadLibrary("khadas_hwdecode_jni");
    }

    private native static int native_hardware_decode(String file_name,int width,int height,int fps,int mode,int format);

    public static int hardware_decode(String file_name,int width,int height,int fps,int mode,int format) {

        if (DEBUG)
            Log.d(TAG, "------native_hardware_decode begin " );
        return native_hardware_decode(file_name,width,height,fps,mode,format);
    }

}

