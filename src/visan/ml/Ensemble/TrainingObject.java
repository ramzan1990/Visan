package visan.ml.Ensemble;

import java.io.Serializable;

/**
 *
 * @author Ramzan
 */
public class TrainingObject implements Serializable{
    public Double[] data;
    public double weight;
    public TrainingObject(Double[] data, double weight){
        this.data = data;
        this.weight = weight;
    }
}
