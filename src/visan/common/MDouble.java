/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visan.common;

import java.io.Serializable;

/**
 *
 * @author Ramzan
 */
public class MDouble implements Comparable, Serializable{

    public double value = 0;

    public int compareTo(Object o) {
        MDouble a = (MDouble) o;
        if(this.value > a.value)
            return 1;
        else if(this.value < a.value)
            return -1;
        else
            return 0; 
    }
}
