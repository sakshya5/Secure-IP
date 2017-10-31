package rou14;

import java.io.BufferedReader;
import java.io.IOException;


public class ClientRead extends Thread {
	
    BufferedReader is = null;
    int key;

    public ClientRead(BufferedReader is, int key) {

                super();
		this.is = is;   
                this.key = key;
    }
	    
    public void run() {
                
		
			try {
                            
                            String str = is.readLine();
                            decrypt(str.toCharArray());
                            key = Integer.parseInt(str);
    
                            while(!Thread.currentThread().isInterrupted()){
			
                                String msg = is.readLine();
                                decrypt(msg.toCharArray());
				System.out.println(msg);
				
                            }
			}catch (IOException e) {

				e.printStackTrace();
			}finally {

                            try {

					is.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}

		super.run();
	}
    
    public void decrypt(char[] str) {

        for (int i = 0; i < str.length; i++) {
            str[i] -= this.key;
        }
    }		
}
