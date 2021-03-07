/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class Database {
        
    private static Connection connection=null;
    private static PreparedStatement prestat;
    private static boolean login_find_result;
    private static String chatmsg=null;
    private static String chattime=null;

    public static String getChattime() {
        return chattime;
    }

    public static void setChattime(String chattime) {
        Database.chattime = chattime;
    }

    public static String getChatmsg() {
        return chatmsg;
    }

    public static void setChatmsg(String chatmsg) {
        Database.chatmsg = chatmsg;
    }

    public static boolean isLogin_find_result() {
        return login_find_result;
    }

    public static void setLogin_find_result(boolean login_find_result) {
        Database.login_find_result = login_find_result;
    }
    
    Database() throws ClassNotFoundException, SQLException
    {
        connection();
    }
    
    public static void connection() throws ClassNotFoundException, SQLException
    {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url="jdbc:mysql://localhost:3306/chat_up_database";
            connection=DriverManager.getConnection(url,"root","karan12345");
            String q="create table if not exists registration(username varchar(20) primary key,password varchar(50),msg varchar(5000),timeuser varchar(50));";
            prestat=connection.prepareStatement(q);
            prestat.execute();
            
        
    }
    
    public static void insert_sign_up(String username,String password) throws SQLException
    {
            String q="insert into registration values(?,?,?,?)";
            prestat=connection.prepareStatement(q);
            prestat.setString(1,username);
            prestat.setString(2,password);
            prestat.setString(3,"");
            prestat.setString(4,"");
            prestat.execute();
        
    }
    
    public static void update_msg(String msg,String time,String uname)
    {
        try {
            if(connection==null)
                connection();
            
            String q="select msg from registration where username="+'"'+uname+'"'+";";
            prestat=connection.prepareStatement(q);
            ResultSet result=prestat.executeQuery();
            
            String prev="";
            while(result.next())
                prev=result.getString("msg");
            
            if(prev.length()>=8000)     //deleting too much data 
                prev="";
            
            q="update registration set"+" msg="+'"'+prev+msg+'"'+",timeuser="+'"'+time+'"'+" where username="+'"'+uname+'"'+";";
            

            //System.out.println(q);
            prestat=connection.prepareStatement(q);
            prestat.execute();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void login_find(String uname,String pwd) throws ClassNotFoundException, SQLException
    {
        if(connection==null)
        {
            connection();
        }
        String q="select * from registration where username="+'"'+uname+'"'+";";
        //System.out.println(q);
        prestat=connection.prepareStatement(q);
        ResultSet result=prestat.executeQuery();
        
        while(result.next())
        {
            if(result.getString("username").equals(uname)&&result.getString("password").equals(pwd))
                login_find_result=true;
            else 
                login_find_result=false;
        }
    }
    
    public static void get_msg_from_savechat(String uname)
    {
        try {
            if(connection==null)
                connection();
            
            String q="select * from registration where username="+'"'+uname+'"'+";";
            //System.out.println(q);
            prestat=connection.prepareStatement(q);
            ResultSet result=prestat.executeQuery();
            
            while(result.next())
            {
                setChatmsg(result.getString("msg"));
                setChattime(result.getString("timeuser"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
