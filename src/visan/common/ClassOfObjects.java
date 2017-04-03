package visan.common;


import java.io.Serializable;
import java.util.ArrayList;

class ClassOfObjects implements Serializable{

    public String name;
    public ArrayList<double[]> nObjects;

    public ClassOfObjects(String name) {
    	this.name = name;
        nObjects = new ArrayList<double[]>();
    }
}