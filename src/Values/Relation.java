package Values;

import java.util.ArrayList;

public class Relation {

	private int mainPoint = -1;
	private ArrayList<Double> fitness = new ArrayList<Double>();
	private ArrayList<Integer> point = new ArrayList<Integer>();
	
	public void setMainPoint(int mainPoint) {
		this.mainPoint = mainPoint;
	}
	
	public void setFitness(double fitness) {
		this.fitness.add(fitness);
	}
	
	public void setPoint(int point) {
		this.point.add(point);
	}
	
	public void replacePointFitness(int position, double fitness) {
		int num = this.point.indexOf(position);
		this.fitness.add(num, fitness);
	}
	
	public int getMainPoint() {
		return this.mainPoint;
	}
	
	public double getFitness(int position) {
		return this.fitness.get(position);
	}
	
	public double getPointFitness(int value) {
		int num = this.point.indexOf(value);
		return this.fitness.get(num);
	}
	
	public int getPoint(int position) {
		return this.point.get(position);
	}
	
	public int getPointSize() {
		return this.point.size();
	}
	
	public boolean isPointContained(int value) {
		return this.point.contains(value);
	}
}
