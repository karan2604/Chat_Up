/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package function;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author asus
 */
public class Recorder {
    
    private boolean running;
    private boolean runPlay;
    private ByteArrayOutputStream out;
    final AudioFormat format=getformat();
    private final DataLine.Info info=new DataLine.Info(TargetDataLine.class, format);
    private TargetDataLine line;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    public Recorder()
    {
        try{
            line=(TargetDataLine) AudioSystem.getLine(info);
        }catch(Exception ex){
            System.out.println(ex);
        }
        }

    private AudioFormat getformat() {
        
        float samplerate=44100.0f;
        int samplesizeinbits=16;
        int channel=2;
        boolean signed=true;
        boolean bigendian=true;
        return new AudioFormat(samplerate,samplesizeinbits,channel,signed,bigendian);
    }
    
    public ByteArrayOutputStream stop()
    {
        try {
            Thread.sleep(1000);
            running=false;
            return out;
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return null;
    }
     
    public void captureAudio()
    {
            try {
                line.open(format);
                line.start();
                Runnable runner=new Runnable() {
                    int bufferedsize=(int) (format.getSampleRate()*format.getFrameSize());
                    byte buffer[]=new byte[bufferedsize];
                    @Override
                    public void run() {
                        out=new ByteArrayOutputStream();
                        running=true;
                        try{
                            time=0;
                            while(running)
                            {
                                int count=line.read(buffer,0,buffer.length);
                                if(count>0)
                                    out.write(buffer,0, count);
                            }
                            out.close();
                        }catch(Exception ex)
                        {
                            System.out.println(ex);
                        }
                    }
                };
                Thread CaptureThread=new Thread(runner);
                CaptureThread.start();
            } catch (LineUnavailableException ex) {
                System.out.println(ex);
            }
    }
    
    public void playAudio(byte[] audio, JProgressBar bar)
    {
        try {
            InputStream input=new ByteArrayInputStream(audio);
            final AudioFormat f=getformat();
            final AudioInputStream ais=new AudioInputStream(input, f, audio.length / f.getFrameSize());
            DataLine.Info fo=new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine l=(SourceDataLine) AudioSystem.getLine(fo);
            l.open(f);
            l.start();
            Runnable runner = new Runnable(){
                int bufferSize = (int) f.getSampleRate() * f.getFrameSize();
                byte buffer[] = new byte[bufferSize];
                @Override
                public void run() {
                    try {
                        runPlay = true;
                        int count;
                        while(runPlay&&(count = ais.read(buffer, 0, buffer.length)) != -1)
                        {
                            bar.setValue(bar.getValue()+1);
                            if (count > 0) {
                                l.write(buffer, 0, count);
                            }
                        }
                        bar.setValue(bar.getMaximum());
                        l.drain();
                        l.close();
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
                
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (LineUnavailableException ex) {
            System.out.println(ex);
        }
        
    }
    public void stopPlay() {
        runPlay = false;
    }
    
}
