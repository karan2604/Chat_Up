/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package function;

import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author asus
 */
public class Method {
    
    private static JTextArea txt;
    private static ArrayList<ClientHandler> clientslist;
    private static int clientID;
    private static int fileID;

    public static int getFileID() {
        return fileID;
    }

    public static void setFileID(int fileID) {
        Method.fileID = fileID;
    }

    public static JTextArea getTxt() {
        return txt;
    }

    public static void setTxt(JTextArea atxt) {
        txt = atxt;
    }

    public static ArrayList<ClientHandler> getClientslist() {
        return clientslist;
    }

    public static void setClientslist(ArrayList<ClientHandler> aclients) {
        clientslist = aclients;
        clientID = 1;
        fileID = 1;
    }

    public int getClientID() {
        return clientID;
        
    }

    public void setClientID(int aclientID) {
        clientID = aclientID;
    }
    
    public static int addclients(ClientHandler clienthandler)
    {
        clientslist.add(clienthandler);
        return clientID++;
    }
    
    
    
    
    
}
