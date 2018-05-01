package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {
	 private MediaRecorder mRecorder = null;
	 private MediaPlayer   mPlayer = null;
	 public String mFileName = "";

	public void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("AudioRecorder", "prepare() failed");
		}
	}

	public void stopPlaying() {
		if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
	}
	public String getFileDirPath()
	{
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Kwality");
		if (!(file.exists() && file.isDirectory())) {
			file.mkdirs();
        }		return file.getAbsolutePath()+"/";
	}

	public void deleteAudioFile(String fileName)
	{
		String selectedFilePath = getFileDirPath() + fileName;
		File file = new File(selectedFilePath);
		file.delete();
	}
}
