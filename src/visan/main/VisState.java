package visan.main;

import java.io.Serializable;

import visan.ml.BPN.BPNBoundaryClass;
import visan.ml.DA.DiscriminantAnalysis;
import visan.ml.Ensemble.AdaBoost;
import visan.ml.Ensemble.RandomForest;
import visan.ml.LS.LeastSquares;
import visan.ml.NN.NearestNeighbours;
import visan.ml.PCA.PCA;
import visan.ml.SVM.SVM;
import visan.common.ClassesCollection;

public class VisState implements Serializable{
	public String projectName, projectPath;
	public String negativeClass = "", origFileName = "";
	public String delimeter;
	public LeastSquares ls;
	public SVM sv;
	public DiscriminantAnalysis da;
	public PCA pca;
	public RandomForest rfc;
	public AdaBoost ab;
	public BPNBoundaryClass bpn;
	public NearestNeighbours nn;
	public ClassesCollection trainingSet;
	public int plotCount, histogramCount, trendCount,
	numberOfBins = 50;
	public boolean histogramShape, multiClassShape;
	public boolean consoleEnabled = true, showName = true, testingSet;
	public boolean blackTheme; 

	public VisState(){
		trainingSet = new ClassesCollection();
		ls = new LeastSquares();
		da = new DiscriminantAnalysis();
		sv = new SVM();
		bpn = new BPNBoundaryClass();
		nn = new NearestNeighbours();
		rfc = new RandomForest();
		ab = new AdaBoost();
		delimeter = "\\s+";
	}
	
	public boolean chechIfBusy(){
		if(da.isBusy()){
			return true;
		}
		if(ls.isBusy()){
			return true;
		}
		if(sv.isBusy()){
			return true;
		}
		if(bpn.isBusy()){
			return true;
		}
		if(nn.isBusy()){
			return true;
		}
		return false;
	}
}
