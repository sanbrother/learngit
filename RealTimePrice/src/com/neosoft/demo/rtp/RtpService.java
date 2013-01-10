package com.neosoft.demo.rtp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class RtpService extends Service {
	private static final String TAG = "RtpService";
	private static final long interval = 300000L;
	private boolean stopFlag = false;
	private Thread t;

	public IBinder onBind(Intent paramIntent) {
		return null;
	}

	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
	}

	public void onDestroy() {
		this.stopFlag = true;
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand");
		
		PriceHistoryDbHelper dbHelper = new PriceHistoryDbHelper(this);
		
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final SQLiteDatabase db2 = dbHelper.getReadableDatabase();

		Runnable rn = new Runnable() {
			public void run() {
				try {					
					while (!stopFlag) {						
						SSLContext sc = SSLContext.getInstance("SSL");
						sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());

						URL console = new URL("https://kds2.kitco.com/getQuote?symbol=AG&apikey=9bnteWVi2kT13528d100c608fn0TlbC6");
						HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
						conn.setSSLSocketFactory(sc.getSocketFactory());
						conn.setHostnameVerifier(new TrustAnyHostnameVerifier());

						conn.setDoInput(true);
						conn.setDoOutput(false);
						
						conn.connect();
						System.out.println(conn.getResponseCode());
						InputStream ins=conn.getInputStream();
						
						String priceRecord = inStream2String(ins);
						Log.i(TAG, priceRecord);
						
						conn.disconnect();
						
						ContentValues cv = new ContentValues();
			            cv.put(PriceHistoryDbHelper.COLUMN_PRICE_RECORD, priceRecord);
			            
			            db.insert(PriceHistoryDbHelper.TABLE_NAME_PRICE_HISTORY, null, cv);
						
						Thread.sleep(5000);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("RTP", e.toString());
				}
			}
		};

		Thread t = new Thread(rn);
		t.start();

		return super.onStartCommand(intent, flags, startId);
	}

	private String inStream2String(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
			Log.i(TAG, "msg len = " + len);
		}
		return new String(baos.toByteArray());
	}
}