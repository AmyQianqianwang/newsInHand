package com.newsinhand.fileoperate;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class SaveImage {
	public void saveFile(Bitmap bm, String ALBUM_PATH, String fileName)
			throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
			System.out.println("�����ļ���");
		}
		File myCaptureFile = new File(ALBUM_PATH + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	public Bitmap setImage(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// ������Ҫ�Ĵ�С
		int newWidth = 155;
		int newHeight = 120;
		// �������ű���
	

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ȡ����Ҫ���ŵ�matrix����
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// �õ��µ�ͼƬ
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return bitmap;
	}
}
