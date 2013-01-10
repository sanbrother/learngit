package com.neosoft.demo.rtp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClickButtonGet(View v) {
//		Runnable rn = new Runnable() {
//			public void run() {
//				// Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//				HttpClient client = new DefaultHttpClient();
//				HttpGet get = new HttpGet(
//						"https://kds2.kitco.com/getQuote?symbol=AG&apikey=9bnteWVi2kT13528d100c608fn0TlbC6");
//				HttpResponse response;
//				try {
//					response = client.execute(get);
//					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						InputStream is = response.getEntity().getContent();
//						String result = inStream2String(is);
//						Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//						Assert.assertEquals(result, "GET_SUCCESS");
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					Log.e("RTP", e.toString());
//				}
//			}
//		};
//		
//		Thread t = new Thread(rn);
//		t.start();
		
		Intent localIntent = new Intent(this, RtpService.class);
	    startService(localIntent);
	}
	
	public void onClickButtonStop(View v) {
		this.stopService(new Intent(this, RtpService.class));
	}

	private String inStream2String(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toByteArray());
	}
}
