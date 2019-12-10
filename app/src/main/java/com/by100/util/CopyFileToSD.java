package com.by100.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;
import android_serialport_api.sample.R;

public class CopyFileToSD {
	/**
	 * ��ʼ���豸���ݿ�
	 * 
	 * @param context
	 * @return
	 */
	public void initDB(Context context) {
		File file = new File(AppConfig.WITLIB);
		File file1 = new File(AppConfig.LIC);
		File file2 = new File(AppConfig.RootFile);//��ʱ�ļ���
		if(!file2.exists()){
			file2.mkdir();
		}
		//����ļ��Ƿ���ڣ����򿽱�
		if (!file.exists()) {
			InitDeviceB(context);
		}
		if(!file1.exists()){
			InitDeviceL(context);
		}
	}
	/**
	 * �������ݿ�
	 * 
	 * @param context
	 * @return
	 */
	private boolean InitDeviceB(Context context) {
		InputStream input = null;
		OutputStream output = null;
		// ���·��

		// ����Դ�ж�ȡ���ݿ���
		input = context.getResources().openRawResource(R.raw.base);

		try {
			output = new FileOutputStream(AppConfig.WITLIB);

			// �����������
			byte[] buffer = new byte[2048];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			return true;
		} catch (FileNotFoundException e) {
			Log.e("123", e.getMessage());
		} catch (IOException e) {
			Log.e("123", e.getMessage());
		} finally {
			// �ر������
			try {
				output.flush();
				output.close();
				input.close();
			} catch (IOException e) {
			}

		}

		return false;

	}
	/**
	 * �������ݿ�
	 * 
	 * @param context
	 * @return
	 */
	private boolean InitDeviceL(Context context) {
		InputStream input = null;
		OutputStream output = null;
		// ���·��

		// ����Դ�ж�ȡ���ݿ���
		input = context.getResources().openRawResource(R.raw.license);

		try {
			output = new FileOutputStream(AppConfig.LIC);

			// �����������
			byte[] buffer = new byte[2048];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			return true;
		} catch (FileNotFoundException e) {
			Log.e("123", e.getMessage());
		} catch (IOException e) {
			Log.e("123", e.getMessage());
		} finally {
			// �ر������
			try {
				output.flush();
				output.close();
				input.close();
			} catch (IOException e) {
			}

		}

		return false;

	}
}
