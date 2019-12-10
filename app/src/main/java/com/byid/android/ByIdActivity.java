package com.byid.android;


import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android_serialport_api.sample.PowerOperate;
import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortActivity;
import android_serialport_api.sample.SerialPortPreferences;
import android_serialport_api.sample.dianyuan;

import com.IDCardReader;
import com.ReaderListener;
import com.by100.util.AppConfig;
import com.by100.util.CopyFileToSD;
import com.by100.util.NationDeal;
import com.ivsign.android.IDCReader.IDCReaderSDK;


public class ByIdActivity extends SerialPortActivity {
    int Readflage = -99;
    int datalen;
    boolean isRun = true;
    public static boolean isOpen = false;
    boolean isPlay;
    byte[] cmd_SAM = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE};
    byte[] cmd_find = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22};
    byte[] cmd_selt = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21};
    byte[] cmd_read = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32};
    byte[] cmd_sleep = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02};
    byte[] cmd_weak = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03};
    byte[] recData = new byte[5000];

    byte[] myData = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x05, 0x0A};
    byte[] myData_b = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x09, 0x0A};

    byte[] tempData = new byte[5000];
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String[] decodeInfo = new String[10];
    TextView ett;
    ImageView image;
    Button btread;
    MediaPlayer player;
    SharedPreferences prefs;
    private Button btreadd;
    public static boolean isopent = false;
    public static boolean source = false;
    public static boolean jie = true;
    public static boolean fingerprint = false;
    int flag = 0;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ett = (TextView) findViewById(R.id.textView1);
        IDCardReader.newInstance(this).setReaderListener(new ReaderListener() {
            @Override
            public void onDataReceived(String[] decodeInfo, Bitmap size) {
                ett.setText("姓名：" + decodeInfo[0] + "\n" + "性别："
                        + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                        + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                        + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                        + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                        + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                        + "无指纹" + "\n");
            }
        }).startRead();
//		init();
    }

    private void init() {
        CopyFileToSD cFileToSD = new CopyFileToSD();
        cFileToSD.initDB(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        player = MediaPlayer.create(this, R.raw.success);

        Button btconn = (Button) findViewById(R.id.btconn);

        btread = (Button) findViewById(R.id.btread);
        //btread.setVisibility(View.INVISIBLE);

        ett = (TextView) findViewById(R.id.textView1);


        image = (ImageView) findViewById(R.id.imageView1);
        btconn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                Intent inten = new Intent(ByIdActivity.this, SerialPortPreferences.class);
                inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(inten);

            }
        });
        btread.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (isOpen == false & source == true & SerialPortPreferences.switching == true) {

                    isOpen = true;
                    source = true;
                    Toast.makeText(getApplicationContext(), "开始读卡", 0)
                            .show();
                    btread.setText("停止");
                    return;

                }
                if (isOpen == false & source == false & SerialPortPreferences.switching == false) {

                    //PowerOperate.enableRIFID_Module_5Volt();
                    source = true;
                    Toast.makeText(getApplicationContext(), "开始读卡", 0)
                            .show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {


                            try {
                                // btread.performClick();
                                btread.setText("停止");

                                isOpen = true;
                                return;
                            } catch (Exception e) {
                                // TODO: handle exception
                            }


                        }
                    }, 1500);


                } else {
                    if (isOpen == true & source == true & SerialPortPreferences.switching == false) {
                        btread.setText("读卡");

                        source = false;
                        isOpen = false;
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {


                                try {
                                    // btread.performClick();
                                    //PowerOperate.disableRIFID_Module_5Volt();
                                    Toast.makeText(getApplicationContext(), "已经关闭", 0)
                                            .show();
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }


                            }
                        }, 1500);
                    } else {
                        if (isOpen == true & source == true & SerialPortPreferences.switching == true) {

                            isOpen = false;
                            source = false;
                            SerialPortPreferences.switching = false;
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {


                                    try {
                                        //btread.performClick();
                                        PowerOperate.disableRIFID_Module_5Volt();
                                        Toast.makeText(getApplicationContext(), "已经关闭", 0)
                                                .show();

                                        btread = (Button) findViewById(R.id.btread);
                                        btread.setText("读卡");
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }


                                }
                            }, 500);
                        }

                    }
                }
                if (isOpen == false & source == false & SerialPortPreferences.switching == true) {

                    PowerOperate.enableRIFID_Module_5Volt();
                    source = true;
                    Toast.makeText(getApplicationContext(), "开始读卡", 0)
                            .show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {


                            try {
                                // btread.performClick();
                                btread.setText("停止");

                                isOpen = true;

                            } catch (Exception e) {
                                // TODO: handle exception
                            }


                        }
                    }, 1500);
                }


            }
        });
        new Thread(new ThreadRun()).start();
    }

    private class ThreadRun implements Runnable {

        @Override
        public void run() {
            while (isRun) {
                try {
                    Thread.sleep(0);

                    if (isOpen)
                        ReadCard();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }


    private void ReadCard() {


        if ((mInputStream == null) || (mInputStream == null)) {
//			Log.e("readcard","连接异常");
            Readflage = -2;// 连接异常
            return;
        }


        if (mInputStream != null) {

            if (isOpen == true & source == true) {
                try {
                    //读取串口数据到tempData数组中
                    //Thread.sleep(600);
                    Log.e("readcard", "读取数据");
                    datalen = mInputStream.read(tempData);
                    Log.e("readcard", "读取到数据" + datalen);


                } catch (Exception e) {
                    // TODO: handle exception
                }


            }
        }

        //把tempData数据存放到recData中，并且计数
        for (int i = 0; i < datalen; i++, flag++) {

            recData[flag] = tempData[i];
        }
        Log.e("readcard", "读据");

        //判断串口读取到的数据是无指纹1297字节或是有指纹数据2321字节，主要是预防串口读取数据时出现数据丢失
        try {
            if (flag == 1297 || flag == 2321) {
                Log.e("readcard", "读据kkk");
                int not = 0;
                int exist = 0;
                //无指纹数据整合
                for (int i = 0; i < 7; i++) {

                    if (recData[i] == myData[i]) {
                        not++;
                    }


                }


                //有指纹数据整合

                for (int i = 0; i < 7; i++) {

                    if (recData[i] == myData_b[i]) {
                        exist++;
                    }


                }

                //判断串口读取到的数据，如果是有指纹的身份证数据或是无指纹的身份证数据就解析
                if (not == 7 || exist == 7) {
                    not = 0;
                    exist = 0;

                    byte[] newData = new byte[1384];
                    //把recData数组中的数据截取存放到newData，首先存放前14个字节，然后跳过两个字节，再存放1281个字节
                    //注意截取后的身份证数据是1295个字节，所以newData一定是1295个字节。
                    for (int data = 0; data < 14; data++) {
                        newData[data] = recData[data];
                    }
                    for (int data = 14; data < 1281; data++) {
                        newData[data] = recData[2 + data];

                    }


                    if (flag == 2321) {
                        fingerprint = true;
                    }

                    flag = 0;


                    if (newData[9] == -112) {

                        byte[] dataBuf = new byte[256];
                        for (int i = 0; i < 256; i++) {
                            dataBuf[i] = newData[14 + i];
                        }
                        try {
                            String TmpStr = new String(dataBuf, "UTF16-LE");
                            TmpStr = new String(TmpStr.getBytes("UTF-8"));

                            decodeInfo[0] = TmpStr.substring(0, 15);
                            decodeInfo[1] = TmpStr.substring(15, 16);
                            decodeInfo[2] = TmpStr.substring(16, 18);
                            decodeInfo[3] = TmpStr.substring(18, 26);
                            decodeInfo[4] = TmpStr.substring(26, 61);
                            decodeInfo[5] = TmpStr.substring(61, 79);
                            decodeInfo[6] = TmpStr.substring(79, 94);
                            decodeInfo[7] = TmpStr.substring(94, 102);
                            decodeInfo[8] = TmpStr.substring(102, 110);
                            decodeInfo[9] = TmpStr.substring(110, 128);
                        } catch (Exception e) {
                            // TODO: handle exception


                        }
                        if (decodeInfo[1].equals("1"))
                            decodeInfo[1] = "男";
                        else
                            decodeInfo[1] = "女";
                        try {
                            int code = Integer.parseInt(decodeInfo[2]
                                    .toString());
                            decodeInfo[2] = NationDeal.decodeNation(code);
                        } catch (Exception e) {
                            decodeInfo[2] = "";
                        }

                        // 照片解码
                        try {

                            int ret = IDCReaderSDK.Init();
                            if (ret == 0) {
                                byte[] datawlt = new byte[1384];
                                byte[] byLicData = {(byte) 0x05,
                                        (byte) 0x00, (byte) 0x01,
                                        (byte) 0x00, (byte) 0x5B,
                                        (byte) 0x03, (byte) 0x33,
                                        (byte) 0x01, (byte) 0x5A,
                                        (byte) 0xB3, (byte) 0x1E,
                                        (byte) 0x00};

                                for (int i = 0; i < 1295; i++) {
                                    datawlt[i] = newData[i];

                                }

                                int t = IDCReaderSDK.unpack(datawlt, byLicData);

                                if (t == 1) {
                                    Readflage = 1;// 读卡成功
                                } else {
                                    Readflage = 6;// 照片解码异常
                                }
                            } else {
                                Readflage = 6;// 照片解码异常
                            }
                        } catch (Exception e) {
                            Readflage = 6;// 照片解码异常
                        }
                        handler.sendEmptyMessage(0);


                    }
                }


            } else {

                if (flag > 2321) {
                    flag = 0;
                }
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    private void sendMessage(byte[] outS) {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(outS);
                mOutputStream.write('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        // TODO Auto-generated method stub
        datalen = size;
        if (tempFlag == -1)

            recData = buffer;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what != 0) {
                return;
            }
            try {
                if (Readflage > 0) {
                    if (fingerprint == true) {
                        fingerprint = false;
                        ett.setText("姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "有指纹" + "\n");
                    } else {
                        ett.setText("姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "无指纹" + "\n");

                    }
                    if (Readflage == 1) {
                        FileInputStream fis = new FileInputStream(
                                Environment.getExternalStorageDirectory()
                                        + "/wltlib/zp.bmp");
                        Bitmap bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
                        image.setImageBitmap(bmp);

                    } else {
                        ett.append("照片解码失败，请检查路径"
                                + AppConfig.RootFile);
                        image.setImageBitmap(BitmapFactory.decodeResource(
                                getResources(), R.drawable.face));
                    }
                    if (isPlay)
                        player.start();

                } else {
                    image.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.face));
                    if (Readflage == -2) {
                        ett.setText("连接异常");
                    }
                    if (Readflage == -3) {
                        ett.setText("无卡或卡片已读过");
                    }
                    if (Readflage == -4) {
                        ett.setText("无卡或卡片已读过");
                    }
                    if (Readflage == -5) {
                        ett.setText("读卡失败");
                    }
                    if (Readflage == -99) {
                        ett.setText("操作异常");
                    }
                }
                Thread.sleep(0);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                ett.setText("读取数据异常！");
                image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.face));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                ett.setText("读取数据异常！");
                image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.face));
            }
        }

    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (prefs != null)
            isPlay = prefs.getBoolean("checkbox", false);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRun = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOpen == true & source == true & SerialPortPreferences.switching == true) {

            isOpen = false;
            source = false;
            SerialPortPreferences.switching = false;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    try {
                        //btread.performClick();
                        PowerOperate.disableRIFID_Module_5Volt();


                        btread = (Button) findViewById(R.id.btread);
                        btread.setText("读卡");
                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                }
            }, 500);
        } else {
            if (isOpen == true & source == true & SerialPortPreferences.switching == false) {

                isOpen = false;
                source = false;
                SerialPortPreferences.switching = false;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {


                        try {
                            //btread.performClick();
                            //PowerOperate.disableRIFID_Module_5Volt();


                            btread = (Button) findViewById(R.id.btread);
                            btread.setText("读卡");
                        } catch (Exception e) {
                            // TODO: handle exception
                        }


                    }
                }, 500);


            }

        }
        if (source == true & isOpen == false) {

            //PowerOperate.disableRIFID_Module_5Volt();
            btread = (Button) findViewById(R.id.btread);
            btread.setText("读卡");

        }

    }


}