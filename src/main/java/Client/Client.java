package Client;

import Xml.ReadXml;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static Xml.ReadXml.GetParam;

public class Client extends Thread{
    private String l_sNameUser;
    private int l_iPortServer;
    private Socket l_rSocket;

    private BufferedReader l_rBr;
    private PrintWriter l_rPw;

    public Client(String p_sNameUser) throws ParserConfigurationException, SAXException, IOException {
        ReadXml rXml = new ReadXml("config.xml");

        l_sNameUser = p_sNameUser;
        l_iPortServer = Integer.valueOf(GetParam("port"));

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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException  {

        String str;

        System.out.println("Старт клиента");

        Scanner rScan = new Scanner(System.in);
        System.out.println("Введите ник:");
        str = rScan.nextLine();

        Client rCl = new Client(str);

        rCl.start();

        while (true){
            StringBuilder sMessage = new StringBuilder();

            sMessage.append("message #").append(rCl.GetName()).append("&");

            System.out.println("Ник получателя:");

            str = rScan.nextLine();
            if (str.equals("exit")){
                break;
            }

            sMessage.append(str).append("#");

            System.out.println("Ввод сообщения:");
            sMessage.append(rScan.nextLine());

            rCl.SendMess(sMessage.toString());
        }

        rCl.Close();
    }
}