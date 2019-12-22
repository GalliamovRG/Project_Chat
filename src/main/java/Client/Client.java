package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    private String l_sNameUser;
    private int l_iPortServer;
    private Socket l_rSocket;

    private BufferedReader l_rBr;
    private PrintWriter l_rPw;

    public Client(String p_sNameUser){
        l_sNameUser = p_sNameUser;
        l_iPortServer = 1778;

        try {
            l_rSocket = new Socket("127.0.0.1", l_iPortServer);

            l_rBr = new BufferedReader(new InputStreamReader(l_rSocket.getInputStream()));

            l_rPw = new PrintWriter(l_rSocket.getOutputStream(), true);
        }
        catch (IOException e){
            e.printStackTrace(System.out);
        }

        l_rPw.println("connect " + l_sNameUser);
    }

    @Override
    public void run() {
        String str;
        try {
            while((str = l_rBr.readLine()) != null) {
                if (str.equals("disconect")){
                    break;
                }

                System.out.println(str);
            }
        }
        catch (IOException e){
            System.out.println("Сервер не доступен!");
        }
    }


    public String GetName(){
        return l_sNameUser;
    }

    public void SendMess(String sMess){
        l_rPw.println(sMess);
    }

    public void Close() throws IOException {
        l_rPw.println("disconnect");

        l_rBr.close();
        l_rPw.close();
        l_rSocket.close();
    }

    public static void main(String[] args) throws IOException {

        String str;

        System.out.println("Старт клиента");

        String sMessage;
        Scanner rScan = new Scanner(System.in);
        System.out.println("Введите ник:");
        str = rScan.nextLine();

        Client rCl = new Client(str);

        rCl.start();

        while (true){
            sMessage = "message #" + rCl.GetName() + "&";

            System.out.println("Ник получателя:");

            str = rScan.nextLine();
            if (str.equals("exit")){
                break;
            }

            sMessage = sMessage + str + "#";

            System.out.println("Ввод сообщения:");
            sMessage = sMessage + rScan.nextLine();

            rCl.SendMess(sMessage);
        }

        rCl.Close();
    }
}