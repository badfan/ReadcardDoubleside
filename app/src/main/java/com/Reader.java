/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.by100.util.AppConfig;
import com.by100.util.CopyFileToSD;
import com.by100.util.NationDeal;
import com.ivsign.android.IDCReader.IDCReaderSDK;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.sample.Application;
import android_serialport_api.sample.R;

public abstract class Reader {
    public static final String TAG = "IDCardReader";
    private Context ct;
    private static Reader reader;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    protected InputStream mInputStream;
    protected ReadThread mReadThread;
    protected int tempFlag = -1;
    byte[] buffer = new byte[5000];
    private boolean isRun = true;

    protected void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        PortConfig.closeSerialPort();
        mSerialPort = null;
        isRun = false;
    }


    public Reader(Context ct) {
        this.ct = ct;
        CopyFileToSD cFileToSD = new CopyFileToSD();
        cFileToSD.initDB(ct);
        initPort();
    }

    public Context getContext() {
        return ct;
    }

    private void initPort() {
        try {
            mSerialPort = PortConfig.getSerialPort("/dev/ttyS1", 115200);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            Log.e(TAG, "initport成功");
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }
    }

    public void startRead() {
        /* Create a receiving thread */
        if (mReadThread == null) {
            mReadThread = new ReadThread();
            mReadThread.start();
            Log.e(TAG, "读卡开启");
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {

            super.run();
            while (!isInterrupted() && isRun) {
                int size;
                try {
                    if (mInputStream == null) {
                        Log.e(TAG, "mInputStream == null");
                        Thread.sleep(2000);
                        initPort();
                        continue;
                    }
                    if (tempFlag == -1) {
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            onDataReceived(buffer, size);
                            Thread.sleep(500);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public abstract void onDataReceived(byte[] buffer, int size);


    private void DisplayError(int resourceId) {
        Log.e("readCard", ct.getResources().getString(resourceId));
    }

}
