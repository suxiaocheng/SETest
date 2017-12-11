package com.desay.openmobile;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by uidq0655 on 2017/12/5.
 */

public class Tmc200 {
    private final static String TAG = "TMC200_JAVA";

    public native boolean open();

    public native boolean close();

    public native byte[] transmit(byte[] command);

    public native byte[] reset();

    public native byte[] getATR();

    static {
        System.loadLibrary("tmc200");   //defaultConfig.ndk.moduleName
    }
}
