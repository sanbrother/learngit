package com.neosoft.demo.rtp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger {
	public static final String TAG = "FileLogger";

	private String logFilePath;
	private File logFile;

	private PrintWriter writer;

	public FileLogger(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public synchronized void open() throws IOException {
		this.logFile = new File(this.logFilePath);
		
		if (null != this.logFile) {
			if (!this.logFile.exists()) {
				if (!this.logFile.createNewFile()) {

				}
			}

			FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);

			if (fileOutputStream != null) {
				writer = new PrintWriter(fileOutputStream);
			}
		}
	}

	public synchronized void close() {
		if (null != this.writer) {
			this.writer.close();
		}
	}

	public void trace(String msg) {
		if (null != this.writer) {
			this.writer.println(msg);
			this.writer.flush();
		}
	}
}
