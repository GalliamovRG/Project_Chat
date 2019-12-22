package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;

public class Server {
    public int iServerPort;
    public ServerSocket rServSocket;
    public HashMap<String, Readers> arrReaders;

    public RunDB l_rDb;

    public Server(){
        iServerPort = 1778;

        try {
            rServSocket = new ServerSocket(iServerPort);
            System.out.println("Конект к порту " + iServerPort);
        }
        catch (IOException e){
            System.out.println("Нет конекта к порту " + iServerPort);
        }

        arrReaders = new HashMap<String, Readers>();

        l_rDb = new RunDB();
    }

    public void AddSocket(Readers p_rRead, String p_sName){
        arrReaders.put(p_sName, p_rRead);
    }

    public void AddNewMess(String p_sFrom, String p_sTo, String p_sMess, char p_sRead){
        l_rDb.AddNewMess(p_sFrom, p_sTo, p_sMess, String.valueOf(p_sRead));
    }

    public void PrintMessNotActive(String p_sTo) throws SQLException {
        ResultSet rRes = l_rDb.PrintMessNotActive(p_sTo);

        while (rRes.next()){
            for (String sIdx : arrReaders.keySet()){
                Readers rRead = arrReaders.get(sIdx);
                String sUserFrom = rRes.getString(1);
                String sText = rRes.getString(3);
                if (p_sTo.equals(rRead.GetName())) {
                    rRead.l_rPrinter.println(sUserFrom + " : " + sText);
                }
            }
        }
        l_rDb.UpdateNotActive(p_sTo);
    }

    public void NotActivePrint(String p_sToUser) throws SQLException {
        ResultSet rRes = l_rDb.PrintMessNotActive(p_sToUser);
        while (rRes.next()){
            for (String sIdx : arrReaders.keySet()){
                Readers rRead = arrReaders.get(sIdx);
                String sUserFrom = rRes.getString(1);
                String sText = rRes.getString(3);
                if (p_sToUser.equals(rRead.GetName())) {
                    rRead.l_rPrinter.println(sUserFrom + " : " + sText);
                }
            }
        }

    }

    public static void main(String[] args) throws SQLException {
        Server rServ = new Server();

        try {
            while (true){
                Socket fromClientSocket = rServ.rServSocket.accept();

                Readers rRead = new Readers(fromClientSocket, rServ);

                rRead.start();
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        rServ.l_rDb.Disconnect();
    }
}