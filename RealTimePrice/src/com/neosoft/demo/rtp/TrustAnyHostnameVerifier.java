package com.neosoft.demo.rtp;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class TrustAnyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String paramString, SSLSession paramSSLSession) {
		return true;
	}
}