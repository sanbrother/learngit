package com.neosoft.demo.lottery;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.random.rjgodoy.trng.MH_SecureRandom;
import org.random.rjgodoy.trng.RjgodoyProvider;

public class LotteryMain {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		//IMPORTANT: system properties must be set before requesting the SecureRandom instance!

		//Sets the user information (for the HTTP User-Agent header)
		System.setProperty(MH_SecureRandom.USER,"user@example.org");

		//How many minutes will we wait befor aborting the connection?
		System.setProperty(MH_SecureRandom.TIMEOUT,"2");

		//Use TLS for connecting the server.
		System.setProperty(MH_SecureRandom.SSL_PROTOCOL,"TLSv1");
		//System.setProperty(MH_SecureRandom.SSL_PROVIDER,"SunJSSE");

		//Uncomment this to verify the server certificate
		//System.setProperty(MH_SecureRandom.CERTFILE,"www_random_org.cer");

		//Configure how many HTTP redirects will be followed
		//Normally, there shouldn't be any redirect.
		System.setProperty(MH_SecureRandom.MAX_REDIRECTS,"5");


		//register the provider
		Security.addProvider(new RjgodoyProvider());

		//creates a SecureRandom object using the MH_TRNG algorithm (which access www.random.org)
		SecureRandom srandom = SecureRandom.getInstance("MH_TRNG");

		System.err.flush(); Thread.yield();
		System.out.println("Now, ask for some random bits");
		
		List<Integer> redBalls = new ArrayList<Integer>();
		List<Integer> blueBalls = new ArrayList<Integer>();
		
		for (int i = 0; i < 33; i++) {
			redBalls.add(i + 1);
		}
		
		for (int i = 0; i < 16; i++) {
			blueBalls.add(i + 1);
		}
		
		int count = 0;
		
		List<Integer> selectedRedBalls = new ArrayList<Integer>();

		while (!redBalls.isEmpty()) {
			Integer i = redBalls.remove(Math.abs(srandom.nextInt()) % redBalls.size());
			
			selectedRedBalls.add(i);
			
			count++;
			
			if (count >= 6) {
				break;
			}
		}
		
		Comparator<Integer> valueComp = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {				
				return o1.compareTo(o2);
			}
		};
		
		
		Collections.sort(selectedRedBalls, valueComp);
		
		for (Integer i : selectedRedBalls) {
			System.out.println("red " + i);
		}
		
		
		Integer i = blueBalls.remove(Math.abs(srandom.nextInt()) % blueBalls.size());
		System.out.println("blue " + i);

	}

}
