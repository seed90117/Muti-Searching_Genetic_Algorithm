package Values;

public class Parameter {

	private int generation;
	private int chromosome;
	private double crossoverRate;
	private double mutationRateC;
	private double mutationRateE;
	private double sizeX;
	private double sizeY;
	private double addNumber;
	private static Parameter instance = null;
	private Parameter(){}
	
	public static synchronized Parameter getInstance() {
		if (instance == null) {
			instance = new Parameter();
		}
		return instance;
	}
	
	public void setGeneration(int generation) {
		this.generation = generation;
	}
	
	public void setChromosome(int chromosome) {
		if (chromosome%2 != 0)
			chromosome++;
		this.chromosome = chromosome;
	}
	
	public void setCossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}
	
	public void setMutationRateC(double mutationRateC) {
		this.mutationRateC = mutationRateC;
	}
	
	public void setMutationRateE(double mutationRateE) {
		this.mutationRateE = mutationRateE;
	}
	
	public void setPointParameter(double sizeX, double sizeY, double addNumber) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.addNumber = addNumber;
	}
	
	public int getGeneration() {
		return this.generation;
	}
	
	public int getChromosome() {
		return this.chromosome;
	}
	
	public double getCossoverRate() {
		return this.crossoverRate;
	}
	
	public double getMutationRateC() {
		return this.mutationRateC;
	}
	
	public double getMutationRateE() {
		return this.mutationRateE;
	}
	
	public double getSizeX () {
		return this.sizeX;
	}
	
	public double getSizeY () {
		return this.sizeY;
	}
	
	public double getAddNumber () {
		return this.addNumber;
	}
}
