/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.swing.ImageIcon;

/**
 *
 * @author asus
 */
public interface Friend {
    
    public void set(ImageIcon image, int ID, String name, String time);

    public ImageIcon getImage();

    public String getfName();

}
