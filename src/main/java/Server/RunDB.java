package Server;

import java.sql.*;

import static Xml.ReadXml.GetParam;

public class RunDB {
    public Statement l_rStatDB;
    private Connection l_rCon;

    public RunDB(){
        try{
            Class.forName(GetParam("driver"));
            l_rCon = DriverManager.getConnection(GetParam("db_url"), GetParam("user_name"), GetParam("pass"));
            System.out.println("Есть конект к БД!");

            l_rStatDB = l_rCon.createStatement();

        }catch (ClassNotFoundException | SQLException e){
            System.out.println("Не удалось подключиться к БД!");
        }
    }

    public void AddNewMess(String p_sFrom, String p_sTo, String p_sMess, String p_sRead){
        try{
            PreparedStatement rPs = l_rCon.prepareStatement("INSERT INTO messages VALUES (?, ?, ?, ?, LOCALTIMESTAMP)");
            rPs.setString(1, p_sFrom);
            rPs.setString(2, p_sTo);
            rPs.setString(3, p_sMess);
            rPs.setString(4, p_sRead);
            rPs.executeUpdate();
        }catch (SQLException e){
            System.out.println("Запись в БД не добавилась!");
        }
    }

    public ResultSet PrintMessNotActive(String p_sTo){
        ResultSet rRes = null;
        try {
            PreparedStatement rPs = l_rCon.prepareStatement("SELECT * FROM messages WHERE to_user = ? AND read = '0' ORDER BY time");
            rPs.setString(1, p_sTo);
            rRes = rPs.executeQuery();
        } catch (SQLException e) {
            System.out.println("Не удалось прочесть БД!");
        }

        return rRes;
    }

    public void UpdateNotActive(String p_sToUser) throws SQLException{
        PreparedStatement rPs = l_rCon.prepareStatement("UPDATE messages SET read = '1' WHERE to_user = ? AND read = '0'");
        rPs.setString(1, p_sToUser);
        rPs.executeUpdate();
    }

    public void Disconnect() throws SQLException {
        l_rCon.close();
        System.out.println("Дисконект БД!");
    }
}
