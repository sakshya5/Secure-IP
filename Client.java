package rou14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		
		Socket echoclient = null;
		BufferedReader is = null;
		PrintWriter os = null;
		BufferedReader stdin = null;
		ClientRead cr = null;
                int key;
                
		try {
			int port = 7777;
			echoclient = new Socket("localhost", port);
			System.out.println("Connected to Chat room.");
			is = new BufferedReader(new InputStreamReader(echoclient.getInputStream()));
			os = new PrintWriter(echoclient.getOutputStream(), true);
			stdin = new BufferedReader(new InputStreamReader(System.in));
			String str = null;

			System.out.println("What do I call you?? (4 letters)");
			String nick = stdin.readLine();

                        key = (int )(Math.random() * 10 + 1);                                                

			os.println(nick.substring(nick.length()-4, nick.length()));
                        os.print(key); 
                        os.println("e");                        

			cr = new ClientRead(is, key);
			cr.start();

			while(true){
				str = stdin.readLine();	
                                encrypt(str.toCharArray(), key);      
                                os.println(str);
                                os.flush();
				if(str.equalsIgnoreCase("exit")){
					System.out.println("Connection to server terminated.");
					cr.interrupt();
					break;
				}                                  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				echoclient.close();
				os.close();
				stdin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();  
			}
		}
	}
        
        public static void encrypt(char[] str, int key) {

        for (int i = 0; i < str.length; i++) {
            str[i] += key;
        }
    }
}
