/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package function;

import Main.Main;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import message.Message;
import model.Friend;

/**
 *
 * @author asus
 */
public class Method {
    
    private static HashMap<Integer, Friend> friends = new HashMap<>();

    public static HashMap<Integer, Friend> getFriends() {
        return friends;
    }

    public static void setFriends(HashMap<Integer, Friend> friends) {
        Method.friends = friends;
    }
    
    private static Recorder recorder=new Recorder();

    public static Recorder getRecorder() {
        return recorder;
    }

    public static void setRecorder(Recorder recorder) {
        Method.recorder = recorder;
    }
    
    private static JFrame fram;

    public static JFrame getFram() {
        return fram;
    }

    public static void setFram(JFrame fram) {
        Method.fram = fram;
    }
    
    
    
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static Socket socket;
    private static String myname;
    private static String time;
    private static int myID;

    public static int getMyID() {
        return myID;
    }

    public static void setMyID(int myID) {
        Method.myID = myID;
    }
    

    public static ObjectOutputStream getOut() {
        return out;
    }

    public static void setOut(ObjectOutputStream out) {
        Method.out = out;
    }

    public static ObjectInputStream getIn() {
        return in;
    }

    public static void setIn(ObjectInputStream in) {
        Method.in = in;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        Method.socket = socket;
    }

    public static String getMyname() {
        return myname;
    }

    public static void setMyname(String myname) {
        Method.myname = myname;
    }

    public static String getTime() {
        return time;
    }

    public static void setTime(String time) {
        Method.time = time;
    }
    
    public static void connect(ImageIcon icon,String username,String IP) throws IOException
    {
        socket=new Socket(IP,9876);
        out=new ObjectOutputStream(socket.getOutputStream());
        in=new ObjectInputStream(socket.getInputStream());
        
        SimpleDateFormat df=new SimpleDateFormat("hh:mm aa");
        String t=df.format(new Date());
        
        Message ms=new Message();
        ms.setStatus("New");
        ms.setImage(icon);
        ms.setName(username+"!"+t);
        out.writeObject(ms);
        out.flush();
        myname=username;
        time=t;
    }
    
    public static void sendmessage(String txt) throws IOException
    {
        Message ms=new Message();
        ms.setStatus("Message");
        ms.setID(Method.getMyID());
        ms.setMessage(txt);
        out.writeObject(ms);
        out.flush();
    }
    
    public static void sendphoto(ImageIcon photo) throws IOException
    {
        Message ms=new Message();
        ms.setStatus("Photo");
        ms.setID(Method.getMyID());
        ms.setImage(photo);
        out.writeObject(ms);
        out.flush();
    }
    
    public static void sendsound(ByteArrayOutputStream sound,int time) throws IOException
    {
        Message ms=new Message();
        ms.setStatus("Sound");
        ms.setID(Method.getMyID());
        ms.setMessage(getDurationString(time) + "!" + time);
        ms.setData(sound.toByteArray());
        out.writeObject(ms);
        out.flush();
    }
    
    private static String getDurationString(int seconds) {
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return oneDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private static String oneDigitString(int number) {
        if (number == 0) {
            return "0";
        }
        if (number / 10 == 0) {
            return "" + number;
        }
        return String.valueOf(number);
    }
    
    public static void sendfile(File file) throws FileNotFoundException, IOException
    {
        FileInputStream in=new FileInputStream(file);
        byte data[]=new byte[in.available()];
        in.read(data);
        in.close();
        String fileSize = convertSize(file.length());
        Message ms = new Message();
        ms.setStatus("File");
        ms.setID(Method.getMyID());
        ms.setData(data);
        ms.setName(file.getName() + "!" + fileSize);
        out.writeObject(ms);
        out.flush();
    }
    
    private static final String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    private static String convertSize(double bytes) {
        String sizeToReturn;
        DecimalFormat df = new DecimalFormat("0.#");
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        System.out.println("Systematic file size: " + bytes + " " + fileSizeUnits[index]);
        sizeToReturn = df.format(bytes) + " " + fileSizeUnits[index];
        return sizeToReturn;
    }
    
    public static void downloadFile(int ID, String name)
    {
        String ex[]=name.split("\\.");
        String x=ex[ex.length-1];
        JFileChooser ch=new JFileChooser();
        ch.setSelectedFile(new File(name));
        int c=ch.showSaveDialog(Main.getFrames()[0]);
        if(c==JFileChooser.APPROVE_OPTION)
        {
            try {
                File f=ch.getSelectedFile();
                if(f.exists())
                {
                    int click = JOptionPane.showConfirmDialog(Main.getFrames()[0], "This file name has already do you want to replace", "Save File", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (click != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                String path=f.getAbsolutePath();
                if (!path.endsWith("." + x)) {
                    path += "." + x;
                }
                Message ms = new Message();
                ms.setStatus("download");
                ms.setID(Method.getMyID());
                ms.setName(path);
                ms.setMessage(ID + "");
                out.writeObject(ms);
                out.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(fram, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
