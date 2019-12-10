package com.by100.util;


import android.os.Environment;

public class AppConfig {
	/**
	 * �����ļ��ĸ�·�� Ŀǰ�����ⲿ�洢����
	 */
	public static final String BasePath=Environment.getExternalStorageDirectory().getAbsolutePath();
	/**
	 * ���ݿ��ļ�������
	 */
	private static final String DBDirectoryName = "wltlib";
	/**
	 * ��ʱ֤���ļ���
	 */
	public static final String DBDirectoryNameL = "clog";
	
	/**
	 * ���ļ��е�·��
	 */
	public static final String RootFile = BasePath+"/"+DBDirectoryName+"/";
	/**
	 * ���ļ��е�·��
	 */
	public static final String RootFileL = BasePath+"/"+DBDirectoryNameL+"/";
	
	public static final String WITLIB = RootFile+"base.dat";
	
	public static final String LIC = RootFile+ "license.lic";

}
