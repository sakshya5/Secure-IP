package rou14;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {

	public static void main(String[] args) {

		ServerSocket echoserver = null;
		ServerThread3 sthread = null;
		int port = 7777;

		try {
			echoserver = new ServerSocket(port);
			while(true){

				sthread = new ServerThread3(echoserver.accept());
				sthread.start();				
			}
		} catch (Exception e) {

		}
	}

}