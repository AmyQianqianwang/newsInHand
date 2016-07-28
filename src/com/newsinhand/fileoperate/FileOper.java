package com.newsinhand.fileoperate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
public class FileOper {

	private String SDPATH = null;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileOper() {
		// ��õ�ǰ�ⲿ�洢�豸SD����Ŀ¼
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	// �����ļ�
	public File createFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	// ����Ŀ¼
	public File createDir(String fileName) throws IOException {
		File dir = new File(SDPATH + fileName);
		dir.mkdir();
		return dir;
	}

	// �ж��ļ��Ƿ����
	public boolean isExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public File writeToSDPATHFromInput(String path, String fileName,
			InputStream inputstream) {
		File file = null;
		OutputStream outputstream = null;
		try {
			createDir(path);
			file = createFile(path + fileName);
			outputstream = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			// ���������е����������뵽buffer�л��棬Ȼ���������д���ļ���
			do {
				int length = (inputstream.read(buffer));
				if (length != -1) {
					outputstream.write(buffer, 0, length);
				} else {
					break;
				}
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}
	public void deleteFile(File file) {

		if (file.exists()) { // �ж��ļ��Ƿ����
			if (file.isFile()) { // �ж��Ƿ����ļ�
				file.delete(); // delete()���� ��Ӧ��֪�� ��ɾ������˼;
			} else if (file.isDirectory()) { // �����������һ��Ŀ¼
				File files[] = file.listFiles(); // ����Ŀ¼�����е��ļ� files[];
				for (int i = 0; i < files.length; i++) { // ����Ŀ¼�����е��ļ�
					this.deleteFile(files[i]); // ��ÿ���ļ� ������������е���
				}
			}
			file.delete();
		} 
	}
	
}
