package com.cp2y.cube.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

public class MediaUtil {

	public static String getPathFromUri(Context context, Uri uri) {
		String[] pojo = { MediaStore.Images.Media.DATA };
		String path = null;
		Cursor cursor = context.getContentResolver().query(uri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			path = cursor.getString(columnIndex);
			try {
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			} catch (Exception e) {
				Log.e("", "error:" + e);
			}
		}
		return path;
	}
	
}
