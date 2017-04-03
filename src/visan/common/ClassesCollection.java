package visan.common;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ClassesCollection implements Serializable {
	public static String minMax;
	private ArrayList<ClassOfObjects> trainingSet = new ArrayList<ClassOfObjects>();
	private ArrayList<ClassOfObjects> testingSet = new ArrayList<ClassOfObjects>();
	private double[] max, min;
	public ArrayList<String> features = new ArrayList<String>();
	//public ArrayList<Boolean> fType = new ArrayList<Boolean>();
	private int transformation;
	private int[] tClasses, tFeatures;

	public int containsClass(String name) {
		String className = name;
		int index = -1;
		for (int i = 0; i < trainingSet.size(); i++) {
			if (trainingSet.get(i).name.equals(className)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void addClass(String className) {
		ClassOfObjects cof1 = new ClassOfObjects(className);
		ClassOfObjects cof2 = new ClassOfObjects(className);
		trainingSet.add(cof1);
		testingSet.add(cof2);
	}

	public void removeClass(int index) {
		trainingSet.remove(index);
		testingSet.remove(index);
	}

	public void addTrainingExample(int c, ArrayList<Double> nData) {
		ClassOfObjects ref = trainingSet.get(c);
		double[] nd = new double[nData.size()];
		for (int i = 0; i < nData.size(); i++) {
			nd[i] = nData.get(i);
		}
		ref.nObjects.add(nd);
	}

	public void addTrainingExample(int c, double[] trainingObject) {
		ClassOfObjects ref = trainingSet.get(c);
		ref.nObjects.add(trainingObject);
	}

	public void addFeature() {
		for (int i = 0; i < trainingSet.size(); i++) {
			ClassOfObjects ref = trainingSet.get(i);
			for (int j = 0; j < ref.nObjects.size(); j++) {
				double[] d = new double[ref.nObjects.get(j).length + 1];
				System.arraycopy(ref.nObjects.get(j), 0, d, 0, ref.nObjects.get(j).length);
				ref.nObjects.set(j, d);
			}
		}
		for (int i = 0; i < testingSet.size(); i++) {
			ClassOfObjects ref = testingSet.get(i);
			for (int j = 0; j < ref.nObjects.size(); j++) {
				double[] d = new double[ref.nObjects.get(j).length + 1];
				System.arraycopy(ref.nObjects.get(j), 0, d, 0, ref.nObjects.get(j).length);
				ref.nObjects.set(j, d);
			}
		}
	}

	public int getSize() {
		return trainingSet.size();
	}

	public int getTrainingElementsSum(int[] classesList) {
		int sum = 0;
		for (int i = 0; i < classesList.length; i++) {
			sum += getTrainingClassSize(classesList[i]);
		}
		return sum;
	}

	public int getTrainingClassSize(int index) {
		return trainingSet.get(index).nObjects.size();
	}

	public int getTestingClassSize(int index) {
		return testingSet.get(index).nObjects.size();
	}

	public double[] trainingObject(int classIndex, int index, int[] features) {
		double[] o = trainingSet.get(classIndex).nObjects.get(index);
		double[] f = new double[features.length];
		for (int i = 0; i < features.length; i++) {
			if (tClasses != null && tFeatures != null && contains(tClasses, classIndex) && contains(tFeatures, features[i])) {
				f[i] = transform(o[features[i]], features[i]);
			} else {
				f[i] = o[features[i]];
			}
		}
		return f;
	}
	
	public double[] testingObject(int classIndex, int index, int[] features) {
		double[] o = testingSet.get(classIndex).nObjects.get(index);
		double[] f = new double[features.length];
		for (int i = 0; i < features.length; i++) {
			if (tClasses != null && tFeatures != null && contains(tClasses, classIndex) && contains(tFeatures, features[i])) {
				f[i] = transform(o[features[i]], features[i]);
			} else {
				f[i] = o[features[i]];
			}
		}
		return f;
	}

	public double[] trainingObject(int classIndex, int index) {
		double[] o = trainingSet.get(classIndex).nObjects.get(index);
		double[] f = new double[features.size()];
		for (int i = 0; i < features.size(); i++) {
			if (tClasses != null && tFeatures != null && contains(tClasses, classIndex) && contains(tFeatures, i)) {
				f[i] = transform(o[i], i);
			} else {
				f[i] = o[i];
			}
		}
		return f;
	}

	public double[] testingObject(int classIndex, int index) {
		double[] o = testingSet.get(classIndex).nObjects.get(index);
		double[] f = new double[features.size()];
		for (int i = 0; i < features.size(); i++) {
			if (tClasses != null && tFeatures != null && contains(tClasses, classIndex) && contains(tFeatures, i)) {
				f[i] = transform(o[i], i);
			} else {
				f[i] = o[i];
			}
		}
		return f;
	}

	public String[] getClassesNames() {
		String[] classesNames = new String[trainingSet.size()];
		for (int i = 0; i < trainingSet.size(); i++) {
			classesNames[i] = trainingSet.get(i).name;
		}
		return classesNames;
	}

	public void changeClassObject(int classIndex, int objectIndex, int featureIndex, double newVal) {
		double tempRef[] = trainingSet.get(classIndex).nObjects.get(objectIndex);
		tempRef[featureIndex] = newVal;
	}

	public int[] getTrainingClassSizes() {
		int[] s = new int[trainingSet.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = trainingSet.get(i).nObjects.size();
		}
		return s;
	}

	public int[] getTrainingClassSizes(int[] classInd) {
		int[] s = new int[classInd.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = trainingSet.get(classInd[i]).nObjects.size();
		}
		return s;
	}

	public int[] getTestingClassSizes() {
		int[] s = new int[testingSet.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = testingSet.get(i).nObjects.size();
		}
		return s;
	}

	public int[] getTestingClassSizes(int[] classInd) {
		int[] s = new int[classInd.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = testingSet.get(classInd[i]).nObjects.size();
		}
		return s;
	}

	public void replace(int a, int b) {
		try {
			ClassOfObjects temp = trainingSet.get(a);
			trainingSet.set(a, trainingSet.get(b));
			trainingSet.set(b, temp);

			ClassOfObjects temp2 = testingSet.get(a);
			testingSet.set(a, testingSet.get(b));
			testingSet.set(b, temp2);
		} catch (Exception e) {
		}
	}

	public void removeFeature(int index) {
		for (int i = 0; i < trainingSet.size(); i++) {
			ClassOfObjects ref = trainingSet.get(i);
			for (int j = 0; j < ref.nObjects.size(); j++) {
				double[] d = new double[ref.nObjects.get(j).length - 1];
				System.arraycopy(ref.nObjects.get(j), 0, d, 0, index);
				System.arraycopy(ref.nObjects.get(j), index + 1, d, index, ref.nObjects.get(j).length - index - 1);
				ref.nObjects.set(j, d);
			}
		}

		for (int i = 0; i < testingSet.size(); i++) {
			ClassOfObjects ref = testingSet.get(i);
			for (int j = 0; j < ref.nObjects.size(); j++) {
				double[] d = new double[ref.nObjects.get(j).length - 1];
				System.arraycopy(ref.nObjects.get(j), 0, d, 0, index);
				System.arraycopy(ref.nObjects.get(j), index + 1, d, index, ref.nObjects.get(j).length - index - 1);
				ref.nObjects.set(j, d);
			}
		}
	}

	public void clear() {
		trainingSet.clear();
		testingSet.clear();
	}

	public void normalise() {
		max = new double[features.size()];
		min = new double[features.size()];
		for (int j = 0; j < features.size(); j++) {
			min[j] = Double.MAX_VALUE;
			max[j] = -Double.MAX_VALUE;
			for (int i = 0; i < trainingSet.size(); i++) {
				for (int c = 0; c < trainingSet.get(i).nObjects.size(); c++) {
					if (trainingSet.get(i).nObjects.get(c)[j] > max[j]) {
						max[j] = trainingSet.get(i).nObjects.get(c)[j];
					}
					if (trainingSet.get(i).nObjects.get(c)[j] < min[j]) {
						min[j] = trainingSet.get(i).nObjects.get(c)[j];
					}
				}
			}
		}
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(20);
		minMax = "";
		for (int i = 0; i < min.length; i++) {
			minMax += df.format(min[i]) + " ";
		}
		minMax += "\n";
		for (int i = 0; i < max.length; i++) {
			minMax += df.format(max[i]) + " ";
		}
	}

	public void addTestingExample(int c, ArrayList<Double> nData) {
		ClassOfObjects ref = testingSet.get(c);
		double[] nd = new double[nData.size()];
		for (int i = 0; i < nData.size(); i++) {
			nd[i] = nData.get(i);
		}
		ref.nObjects.add(nd);
	}

	public void addTestingExample(int i, double[] data) {
		ClassOfObjects ref = testingSet.get(i);
		ref.nObjects.add(data);
	}

	public double transform(double d, int i) {
		switch (transformation) {
		case 1:
			return 2 * ((d - min[i]) / (max[i] - min[i]) - 0.5);
		case 2:
			if (min[i] >= 0) {
				return Math.log(d);
			} else {
				return Math.log(d + min[i]);
			}
		case 3:
			if (min[i] >= 0) {
				return Math.sqrt(d);
			} else {
				return Math.sqrt(d + min[i]);
			}
		case 4:
			if (d != 0) {
				return 1 / d;
			} else {
				return d;
			}
		}
		return d;
	}

	public void setTransformation(int i, int[] classes, int[] features) {
		transformation = i;
		tClasses = classes;
		tFeatures = features;
		switch (transformation) {
		case 1:
			normalise();
			break;
		case 2:
			shift();
			break;
		case 3:
			shift();
			break;
		}
	}

	public int getTransformation() {
		return transformation;
	}

	private void shift() {
		min = new double[features.size()];
		for (int j = 0; j < features.size(); j++) {
			min[j] = Double.MAX_VALUE;
			for (int i = 0; i < trainingSet.size(); i++) {
				for (int c = 0; c < trainingSet.get(i).nObjects.size(); c++) {
					if (trainingSet.get(i).nObjects.get(c)[j] < min[j]) {
						min[j] = trainingSet.get(i).nObjects.get(c)[j];
					}
				}
			}
		}
	}

	public void changeFeatureName(int i, String name) {
		features.set(i, name);
	}

	public void changeClassName(int i, String name) {
		trainingSet.get(i).name = name;
	}

	public void cleanTestingSet() {
		for (int i = 0; i < testingSet.size(); i++) {
			testingSet.get(i).nObjects = new ArrayList<double[]>();
		}
	}

	public void permanentTransform() {
		for (int i = 0; i < trainingSet.size(); i++) {
			for (int j = 0; j < trainingSet.get(i).nObjects.size(); j++) {
				trainingSet.get(i).nObjects.set(j, trainingObject(i, j));
			}
		}
		if (testingSet.size() > 0) {
			for (int i = 0; i < testingSet.size(); i++) {
				for (int j = 0; j < testingSet.get(i).nObjects.size(); j++) {
					testingSet.get(i).nObjects.set(j, testingObject(i, j));
				}
			}
		}
		transformation = 0;
	}

	private boolean contains(int[] a, int b) {
		boolean r = false;
		for (int i : a) {
			if (i == b) {
				r = true;
				break;
			}
		}
		return r;
	}

	public double[] trainingObject(int classIndex, int[] features, int j) {
		double[] v = new double[features.length];
    	double[] to = trainingObject(classIndex, j);
		for (int t = 0; t < features.length; t++) {
			v[t]=to[features[t]];
		}
		return v;
	}
	
	public double[] testingObject(int classIndex, int[] features, int j) {
		double[] v = new double[features.length];
    	double[] to = testingObject(classIndex, j);
		for (int t = 0; t < features.length; t++) {
			v[t]=to[features[t]];
		}
		return v;
	}

	

}
