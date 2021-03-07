/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import message.Message;

/**
 *
 * @author asus
 */
public class ClientHandler extends Thread{

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int ID;
    private String username;
    private String time;
    private ImageIcon profile;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ImageIcon getProfile() {
        return profile;
    }

    public void setProfile(ImageIcon profile) {
        this.profile = profile;
    }
    
    
    public ClientHandler(Socket socket) {
        this.socket=socket;
        execute();
    }
    
    private void execute()
    {
        this.start();
    }
    
    public void run()
    {
        
        try {
            in=new ObjectInputStream(socket.getInputStream());
            out=new ObjectOutputStream(socket.getOutputStream());
            ID=Method.addclients(this);
            while(true)
            {
                
                Message ms=(Message)in.readObject();
                String status=ms.getStatus();
               // System.out.println(status);
                if(status.equals("New"))
                {
                   // System.out.println(ms.getName());
                    username=ms.getName().split("!")[0];
                    time=ms.getName().split("!")[1];
                    profile=ms.getImage();
                    Method.getTxt().append("New Client name: "+username+" has been connected.\n");
                    
                    //list all friend send to new client login 
                    for(ClientHandler clienthandler:Method.getClientslist())
                    {
                        ms=new Message();
                        ms.setStatus("New");
                        ms.setID(clienthandler.getID());
                        ms.setName(clienthandler.getUsername() + "!" + clienthandler.getTime());
                        ms.setImage(clienthandler.getProfile());
                        out.writeObject(ms);
                        out.flush();
                    }
                    
                    //  send new client to old client
                    for (ClientHandler clienthandler: Method.getClientslist()) {
                        if (clienthandler!= this) {
                            ms = new Message();
                            ms.setStatus("New");
                            ms.setName(username + "!" + time);
                            ms.setID(ID);
                            ms.setImage(profile);
                            clienthandler.getOut().writeObject(ms);
                            clienthandler.getOut().flush();
                        }
                    }
                }
                else if(status.equals("File"))
                {
                    int fileID = Method.getFileID();
                    String fileN = ms.getName();
                    SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyhhmmssaa");
                    String fileName = fileID + "!" + df.format(new Date()) + "!" + ms.getName().split("!")[0];
                    Method.getTxt().append(fileName);
                    FileOutputStream output = new FileOutputStream(new File("data/" + fileName));
                    output.write(ms.getData());
                    output.close();
                    Method.setFileID(fileID + 1);
                    ms = new Message();
                    ms.setStatus("File");
                    ms.setName(fileID + "!" + fileN);
                    ms.setImage((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(new File("data/" + fileName)));
                    ms.setID(ID);
                    for (ClientHandler client : Method.getClientslist()) {
                        client.getOut().writeObject(ms);
                        client.getOut().flush();
                    }
                }
                else if (status.equals("download")) {
                    sendFile(ms);
                }
                else
                {
                    for(ClientHandler clienthandler:Method.getClientslist())
                    {
                        clienthandler.getOut().writeObject(ms);
                        clienthandler.getOut().flush();
                    }
                }
                        
                
                
            }
        } catch (Exception ex) {
            
            //ex.printStackTrace();
            
            try{
                Method.getClientslist().remove(this);
                Method.getTxt().append("Client Name : " + username + " has been out of this server ...\n");
                for (ClientHandler clienthandler : Method.getClientslist()) {
                    Message ms = new Message();
                    ms.setStatus("Error");
                    ms.setID(ID);
                    ms.setName(username);
                    clienthandler.getOut().writeObject(ms);
                    clienthandler.getOut().flush();
                }
            }
            catch(Exception e)
            {
               // ex.printStackTrace();
              Method.getTxt().append("Error : " + e+"\n");
            }
                    
        }
                
    }
    
    private void sendFile(Message ms)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String fID = ms.getMessage();
                File file = new File("data");
                for(File f:file.listFiles())
                {
                    if(f.getName().startsWith(fID))
                    {
                        try {
                            FileInputStream ins = new FileInputStream(f);
                            byte data[] = new byte[ins.available()];
                            ins.read(data);
                            ins.close();
                            ms.setData(data);
                            ms.setStatus("GetFile");
                            out.writeObject(ms);
                            out.flush();
                            break;
                        } catch (Exception ex) {
                            //error
                        }
                    }
                }
            }
        }).start();
    }
    
    
}
