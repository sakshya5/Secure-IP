package rou14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class ServerThread3 extends Thread {

    public Socket client = null;
    public String nick = null;
    public static List<ServerThread3> allClients = new ArrayList<ServerThread3>();
    public List<ServerThread3> connClients = new ArrayList<ServerThread3>();
    public List<ServerThread3> dconClients = new ArrayList<ServerThread3>();
    public BufferedReader is;
    public PrintWriter os;
    public int writeKey;
    public int readKey;

    public ServerThread3(Socket client) {

        this.client = client;
    }

    public void run() {

        String str = "";

        try {

            System.out.println("Connected to client ->  " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
            is = new BufferedReader(new InputStreamReader(client.getInputStream()));
            os = new PrintWriter(client.getOutputStream(), true);

            nick = is.readLine();
            writeKey = is.read();
            is.readLine();
            
            readKey = (int )(Math.random() * 10 + 1);
            str = Integer.toString(readKey);
            encrypt(str.toCharArray(), writeKey);
            os.println(str);
            
            allClients.add(this);            
            
            for (ServerThread3 ce : allClients) {

                if (!this.nick.equals(ce.nick)) {

                    PrintWriter cos = new PrintWriter(ce.client.getOutputStream(), true);
                    str = this.nick + " is online!";
                    encrypt(str.toCharArray(), ce.readKey);
                    cos.println(str);
                    ce.dconClients.add(this);
                    this.dconClients.add(ce);

                }
            }            
            
            if(allClients.size() > 1){
                str = "Following People are Online:";
                encrypt(str.toCharArray(), this.readKey);
                os.println(str);
                os.flush();

                for (ServerThread3 ce : allClients) {
                    if (!this.nick.equals(ce.nick)) {
                        str = ce.nick;
                        encrypt(str.toCharArray(), readKey);
                        os.println(str);
                        os.flush();
                    }
                }
            }else{
                
                str = "No one is online!";
                encrypt(str.toCharArray(), this.readKey);
                os.println(str);                
            }

            String echostr;

            while (true) {

                str = is.readLine();
                decrypt(str.toCharArray(), this.writeKey);

                if (str.equals("connect")) {

                    if (dconClients.size() > 0) {

                        str = "With whom do you want to connect?";
                        encrypt(str.toCharArray(), this.readKey);
                        os.println(str);
                        os.flush();

                        for (ServerThread3 ce : dconClients) {
                            str = ce.nick;
                            encrypt(str.toCharArray(), this.readKey);
                            os.println(str);
                            os.flush();
                        }

                        str = is.readLine();
                        decrypt(str.toCharArray(), this.writeKey);

                        for (ServerThread3 ce : allClients) {

                            if (ce.nick.equals(str)) {

                                this.connClients.add(ce);
                                ce.connClients.add(this);
                                this.dconClients.remove(ce);
                                ce.dconClients.remove(this);
                                str = "Connected to " + ce.nick;
                                encrypt(str.toCharArray(), this.readKey);
                                os.println(str);
                                os.flush();
                                PrintWriter uskaOut = new PrintWriter(ce.client.getOutputStream(), true);
                                str = "Connected to " + this.nick;
                                encrypt(str.toCharArray(), ce.readKey);
                                uskaOut.println(str);
                            }
                        }

                    } else {
                        str = "No other is online.";
                        encrypt(str.toCharArray(), this.readKey);
                        os.println(str);
                        os.flush();
                    }

                } else if (str.equals("exit")) {

                    for (ServerThread3 ce : allClients) {
                        if(!this.nick.equals(ce.nick)){
                            PrintWriter uskoOut = new PrintWriter(ce.client.getOutputStream());
                            str = this.nick + " went offline.";
                            encrypt(str.toCharArray(), ce.readKey);
                            uskoOut.println(str);
                        }
                    }

                    for (ServerThread3 ce : connClients) {
                            this.connClients.remove(ce);
                            ce.connClients.remove(this);
                    }

                    for (ServerThread3 ce : dconClients) {
                        this.dconClients.remove(ce);
                        ce.dconClients.remove(this);

                    }

                    allClients.remove(this);

                } else {
                    String searchNick = str.substring(str.length() - 4, str.length());

                    int flag = 0;
                    if (str.startsWith("bye")) {

                            for (ServerThread3 ce : this.connClients) {

                                if (ce.nick.equals(searchNick)) {

                                    str = "Disconnected from " + ce.nick;
                                    encrypt(str.toCharArray(), this.readKey);
                                    os.println(str);
                                    os.flush();
                                    ce.dconClients.add(this);
                                    ce.connClients.remove(this);
                                    PrintWriter uskaOut = new PrintWriter(ce.client.getOutputStream(), true);
                                    str = "Disconnected from " + this.nick;
                                    encrypt(str.toCharArray(), ce.readKey);
                                    uskaOut.println(str);
                                    this.connClients.remove(ce);
                                    this.dconClients.add(ce);
                                    break;
                                }
                            }
                    } else {

                        flag = 0;
                        for (ServerThread3 ce : connClients) {

                            if (searchNick.equals(ce.nick)) {
                    
                                PrintWriter cos = new PrintWriter(ce.client.getOutputStream(), true);
                                echostr = this.nick + " : " + str;
                                encrypt(echostr.toCharArray(), ce.readKey);
                                cos.println(echostr);
                                echostr = this.nick + " : " + str;
                                encrypt(echostr.toCharArray(), readKey);
                                os.println(echostr);
                                os.flush();
                                flag = 1;
                            }
                        }

                        if (flag == 0) {

                            str = "Not connected to " + str;
                            encrypt(str.toCharArray(), this.readKey);
                            os.println(str);
                            os.flush();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encrypt(char[] str, int key) {

        for (int i = 0; i < str.length; i++) {
            str[i] += key;
        }
    }

    public void decrypt(char[] str, int key) {

        for (int i = 0; i < str.length; i++) {
            str[i] -= key;
        }
    }
}
