package com;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.by100.util.AppConfig;
import com.by100.util.NationDeal;
import com.ivsign.android.IDCReader.IDCReaderSDK;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import android_serialport_api.sample.PowerOperate;
import android_serialport_api.sample.R;


public class IDCardReader extends Reader {

    private static IDCardReader reader;
    int Readflage = -99;

    byte[] cmd_SAM = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE};
    byte[] cmd_find = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22};
    byte[] cmd_selt = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21};
    byte[] cmd_read = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32};
    byte[] cmd_sleep = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02};
    byte[] cmd_weak = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03};
    byte[] recData = new byte[5000];

    byte[] myData = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x05, 0x0A};
    byte[] myData_b = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x09, 0x0A};

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String[] decodeInfo = new String[10];
    MediaPlayer player;
    SharedPreferences prefs;
    public static boolean fingerprint = false;
    int flag = 0;
    boolean isRun = true;
    boolean isPlay = true;


    private Handler outsideHandler;
    private ReaderListener mReaderListener;

    private IDCardReader(Context ct) {
        super(ct);
        player = MediaPlayer.create(ct, R.raw.success);
    }

    public static IDCardReader newInstance(Context ct) {
        if (reader == null) {
            reader = new IDCardReader(ct);
        }
        return reader;
    }

    public IDCardReader setReaderListener(ReaderListener readerListener) {
        mReaderListener = readerListener;
        return reader;
    }

    @Override
    public void onDataReceived(byte[] tempData, int datalen) {

        if (mInputStream == null) {
//			Log.e("readcard","连接异常");
            Readflage = -2;// 连接异常
            return;
        }


//        Log.e(TAG, "读数据size=" + datalen);
        //把tempData数据存放到recData中，并且计数
        for (int i = 0; i < datalen; i++, flag++) {

            recData[flag] = tempData[i];
        }

        //判断串口读取到的数据是无指纹1297字节或是有指纹数据2321字节，主要是预防串口读取数据时出现数据丢失
        try {
            if (flag == 1297 || flag == 2321) {
                Log.e("readcard", "读到正确数据");
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
                        if (outsideHandler != null)
                            outsideHandler.sendEmptyMessage(0);
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
                        Log.e(TAG, "姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "有指纹" + "\n");
                    } else {
                        Log.e(TAG, "姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "无指纹" + "\n");

                    }
                    Bitmap bmp = null;
                    if (Readflage == 1) {
                        FileInputStream fis = new FileInputStream(
                                Environment.getExternalStorageDirectory()
                                        + "/wltlib/zp.bmp");
                        bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
//                        image.setImageBitmap(bmp);

                    } else {
                        Log.e(TAG, "照片解码失败，请检查路径"
                                + AppConfig.RootFile);
//                        image.setImageBitmap(BitmapFactory.decodeResource(
//                                getContext().getResources(), R.drawable.face));
                    }
                    if (mReaderListener != null) {
                        mReaderListener.onDataReceived(decodeInfo, bmp);
                    }
                    if (isPlay)
                        player.start();

                } else {
//                    image.setImageBitmap(BitmapFactory.decodeResource(
//                            getContext().getResources(), R.drawable.face));
                    if (Readflage == -2) {
                        Log.e(TAG, "连接异常");
                    }
                    if (Readflage == -3) {
                        Log.e(TAG, "无卡或卡片已读过");
                    }
                    if (Readflage == -4) {
                        Log.e(TAG, "无卡或卡片已读过");
                    }
                    if (Readflage == -5) {
                        Log.e(TAG, "读卡失败");
                    }
                    if (Readflage == -99) {
                        Log.e(TAG, "操作异常");
                    }
                }
                Thread.sleep(0);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "读取数据异常！");
//                image.setImageBitmap(BitmapFactory.decodeResource(
//                        getContext().getResources(), R.drawable.face));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "读取数据异常！");
//                image.setImageBitmap(BitmapFactory.decodeResource(
//                        getContext().getResources(), R.drawable.face));
            }
        }

    };


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        PowerOperate.disableRIFID_Module_5Volt();
        isRun = false;
    }

}