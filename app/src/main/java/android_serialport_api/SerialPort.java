
package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.LogUtil;

public class SerialPort {

	private static final String TAG = SerialPort.class.getSimpleName();

	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int bits, char event, int stop, int flags) throws SecurityException, IOException {
		try {
			System.loadLibrary("serialport");
		}catch (Exception e){

			return;
		}

		LogUtil.trace("device.canRead()=" + device.canRead()
				+ "; device.canWrite()=" + device.canWrite());

		if (!device.canRead() || !device.canWrite()) {
			try {
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
				su.getOutputStream().write(cmd.getBytes());

				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		mFd = open(device.getAbsolutePath(), baudrate, bits, event, stop, flags);
		LogUtil.d(TAG, "open device success!!");

		if (mFd == null) {
			LogUtil.d(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	public SerialPort(File device, int baudrate, int bits, char event, int stop, int flags, int mode) throws SecurityException, IOException {
		System.loadLibrary("serialport");
		LogUtil.trace("device.canRead()=" + device.canRead()
				+ "; device.canWrite()=" + device.canWrite());

		if (!device.canRead() || !device.canWrite()) {
			try {
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());

				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, bits, event, stop, flags);
		LogUtil.d(TAG, "open device success!!");
		if (mFd == null) {
			LogUtil.d(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);

	}

	public SerialPort(File device, int baudrate, int flags)
			throws SecurityException, IOException {
		System.loadLibrary("serial_port");
		LogUtil.trace("device.canRead()=" + device.canRead()
				+ "; device.canWrite()=" + device.canWrite());

		// 判断设备文件是否能读写
		if (!device.canRead() || !device.canWrite()) {
			try {
				// TODO java.io.IOException: Error running exec(). Command:
				// [/system/bin/su] Working Directory: null Environment: null
				Process su = Runtime.getRuntime().exec("/system/bin/su");
				LogUtil.trace("test for runtime exception...");

				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				LogUtil.trace("device.getAbsolutePath()::"
						+ device.getAbsolutePath());

				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				LogUtil.trace("runtime exception...");

				e.printStackTrace();
				throw new SecurityException();
			}
		}

		LogUtil.trace(device.getAbsolutePath() + "; " + baudrate + "; " + flags);
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			LogUtil.d(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	public SerialPort(boolean isSu, String path, File device, int baudrate,
					  int flags) throws SecurityException, IOException {
		System.loadLibrary("serial_port");
		LogUtil.trace("device.canRead()=" + device.canRead()
				+ "; device.canWrite()=" + device.canWrite());

		if (!device.canRead() || !device.canWrite()) {
			try {
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());

				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		LogUtil.d(TAG, "open device!!");
		if (mFd == null) {
			LogUtil.d(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	private native static FileDescriptor open(String path, int baudrate,
											  int flags);

	private native static FileDescriptor open(String path, int baudrate,
											  int bits, char event, int stop, int flags);

	public native void close();
}
