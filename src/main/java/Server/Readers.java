package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class Readers extends Thread{
    private Socket l_rLocalSocket;
    private String l_sNameUser;
    public PrintWriter l_rPrinter;
    private Server l_rServ;

    public Readers(Socket p_rSocetClient, Server p_rServ){
        l_rLocalSocket = p_rSocetClient;
        l_rServ = p_rServ;
    }

    public String GetName(){
        return l_sNameUser;
    }

    @Override
    public void run() {
        try {
            l_rPrinter = new PrintWriter(l_rLocalSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(l_rLocalSocket.getInputStream()));

            String str;

            while ((str = br.readLine()) != null){
                System.out.println("Сообщение: " + str);

                if (str.indexOf("disconnect") == 0){
                    System.out.println("Отключен пользователь " + l_sNameUser);

                    l_rServ.arrReaders.remove(l_sNameUser);
                    for (String sIdx : l_rServ.arrReaders.keySet()){
                        Readers rRead = l_rServ.arrReaders.get(sIdx);
                        if (!l_sNameUser.equals(rRead.GetName())) {
                            rRead.l_rPrinter.println("Пользователь " + l_sNameUser + " вышел из сети");
                        }
                    }
                    break;
                }

                if (str.indexOf("connect") == 0){
                    l_sNameUser = str.substring(8);
                    System.out.println("Подключен пользователь " + l_sNameUser);
                    l_rServ.AddSocket(this, l_sNameUser);

                    for (String sIdx : l_rServ.arrReaders.keySet()){
                        Readers rRead = l_rServ.arrReaders.get(sIdx);
                        if (!l_sNameUser.equals(rRead.GetName())) {
                            rRead.l_rPrinter.println("В сети появился пользователь " + l_sNameUser);
                            l_rPrinter.println("Пользователь " + rRead.GetName() + " в сети");
                        }
                    }

                    l_rServ.PrintMessNotActive(l_sNameUser);
                }

                if (str.indexOf("message") == 0){
                    int iBeg = str.indexOf("#") + 1;
                    int iEnd = str.indexOf("&", iBeg);
                    String sUserFrom = str.substring(iBeg, iEnd);
                    iBeg = str.indexOf("&") + 1;
                    iEnd = str.indexOf("#", iBeg);
                    String sUserTo = str.substring(iBeg, iEnd);
                    String sText = str.substring(iEnd + 1);
                    System.out.println("Сообщение на сервер от : " + sUserFrom + " пользователю " + sUserTo + " Сообщение: " + sText);
                    char sRead = '0';
                    for (String sIdx : l_rServ.arrReaders.keySet()){
                        Readers rRead = l_rServ.arrReaders.get(sIdx);
                        if (sUserTo.equals(rRead.GetName())) {
                            rRead.l_rPrinter.println(sUserFrom + " : " + sText);
                            sRead = '1';
                        }
                    }
                    l_rServ.AddNewMess(sUserFrom, sUserTo, sText, sRead);
                }
            }
            l_rPrinter.close();
            br.close();
            l_rLocalSocket.close();
        }
        catch (IOException | SQLException e){
            System.out.println("Отключен пользователь " + l_sNameUser);

            l_rServ.arrReaders.remove(l_sNameUser);
            for (String sIdx : l_rServ.arrReaders.keySet()){
                Readers rRead = l_rServ.arrReaders.get(sIdx);
                if (!l_sNameUser.equals(rRead.GetName())) {
                    rRead.l_rPrinter.println("Пользователь " + l_sNameUser + " вышел из сети");
                }
            }
        }
    }
}