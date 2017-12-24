package Program;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import Values.BestChromosome;
import Values.Data;
import Values.Parameter;

public class MainMethod {
	
	private Data data = Data.getInstance();
	private Parameter parameter = Parameter.getInstance();
	private Mechanism mechanism = new Mechanism();
	
	@SuppressWarnings("unchecked")
	private ArrayList<Integer>[] chromosome = new ArrayList[parameter.getChromosome()];
	private double[] distance = new double[parameter.getChromosome()];
	private double[] fitness = new double[parameter.getChromosome()];
	private boolean isInteger = false;
	
	public BestChromosome mainProgram(boolean isInteger) {
		this.isInteger = isInteger;
		// 初始化染色體
		initialChromosome();
		for (int i=0; i<parameter.getGeneration(); i++) {
			ArrayList<Integer>[] pool;
			// 依照適應值排序
			sortChromosome();
			// 候選人機制，產生新的染色體，計算探險者適應值
			addExplorer(this.mechanism.Candidate(this.chromosome, this.fitness));
			// 選擇機制，選擇較佳的染色體放入池中
			pool = this.mechanism.Selection(this.chromosome, this.fitness);
			// 交配機制，交配染色體
			pool = this.mechanism.Crossover(pool);
			// 突變機制
			pool = this.mechanism.Mutation(pool);
			// 優化機制
			pool = this.mechanism.Optimization(pool);
			// 新的染色體放回母池，並計算適應值
			this.chromosome = pool;
			caculateDistance();
		}
		return getBest();
	}
	
	private void initialChromosome() {
		for (int i=0; i<parameter.getChromosome(); i++){
			this.chromosome[i] = new ArrayList<Integer>();
			this.chromosome[i].addAll(createChromosome());
		}
		caculateDistance();
	}
	
	// 產生染色體
	private ArrayList<Integer> createChromosome() {
		Random random = new Random();
		boolean run = true;
		ArrayList<Integer> newChromosome = new ArrayList<Integer>();
		while (run) {
			int num = random.nextInt(data.total);
			if (!newChromosome.contains(num)) {
				newChromosome.add(num);
				if (newChromosome.size() == data.total)
					run = false;
			} else {
				run = true;
			}
		}
		return newChromosome;
	}
	
	// 計算所有染色體總距離
	private void caculateDistance() {
		for (int i=0; i<this.parameter.getChromosome(); i++){
			double tmpDistance = 0;
			int size = this.chromosome[i].size();
			for (int j=0; j<size; j++){
				int pointA = this.chromosome[i].get(j);
				int pointB = 0;
				if (j == size-1) {
					pointB = this.chromosome[i].get(0);
				}
				if (j < size-1) {
					pointB = this.chromosome[i].get(j+1);
				}
				if (this.isInteger)
					tmpDistance += (double)Math.round(Point.distance(
							this.data.x[pointA], this.data.y[pointA], 
							this.data.x[pointB], this.data.y[pointB]));
				else
					tmpDistance += Point.distance(
							this.data.x[pointA], this.data.y[pointA], 
							this.data.x[pointB], this.data.y[pointB]);
			}
			this.distance[i] = tmpDistance;
			this.fitness[i] = 1/tmpDistance;
		}
	}
	
	// 計算探險者染色體總距離
	private void caculateExplorerDistance() {
		for (int i=this.parameter.getChromosome()/2; i<this.parameter.getChromosome(); i++){
			double tmpDistance = 0;
			int size = this.chromosome[i].size();
			for (int j=0; j<size; j++){
				int pointA = this.chromosome[i].get(j);
				int pointB = 0;
				if (j == size-1) {
					pointB = this.chromosome[i].get(0);
				}
				
				if (j > 0 && j < size-1) {
					pointB = this.chromosome[i].get(j+1);
				}
				if (this.isInteger)
					tmpDistance += (double)Math.round(Point.distance(
							this.data.x[pointA], this.data.y[pointA], 
							this.data.x[pointB], this.data.y[pointB]));
				else
					tmpDistance += Point.distance(
							this.data.x[pointA], this.data.y[pointA], 
							this.data.x[pointB], this.data.y[pointB]);
			}
			this.distance[i] = tmpDistance;
			this.fitness[i] = 1/tmpDistance;
		}
	}

	// 依照適應值排序染色體
	private void sortChromosome() {
		boolean run = true;
		while (run) {
			run = false;
			for (int i=0; i<this.parameter.getChromosome(); i++){
				if (i>0 && this.distance[i] < this.distance[i-1]){
					ArrayList<Integer> tmpChromosome = new ArrayList<Integer>();
					double tmpDistance, tmpFitness;
					tmpDistance = this.distance[i];
					this.distance[i] = this.distance[i-1];
					this.distance[i-1] = tmpDistance;
					
					tmpFitness = this.fitness[i];
					this.fitness[i] = this.fitness[i-1];
					this.fitness[i-1] = tmpFitness;
					
					tmpChromosome = this.chromosome[i];
					this.chromosome[i] = this.chromosome[i-1];
					this.chromosome[i-1] = tmpChromosome;
					
					run = true;
				}
			}
		}
	}
	
	// 加入探險者染色體
	private void addExplorer(ArrayList<Integer>[] explorer) {
		for (int i=this.parameter.getChromosome()/2; i<this.parameter.getChromosome(); i++){
			for (int j=0; j<this.chromosome[i].size(); j++){
				this.chromosome[i] = explorer[i-parameter.getChromosome()/2];
			}
		}
		caculateExplorerDistance();
	}
	
	// 找出最佳解
	private BestChromosome getBest() {
		BestChromosome bestChromosome = new BestChromosome();
		int size = this.parameter.getChromosome();
		int best = 0;
		for (int i=0; i<size; i++) {
			if (this.fitness[best] < this.fitness[i])
				best = i;
		}
		bestChromosome.chromosome = this.chromosome[best];
		bestChromosome.distance = this.distance[best];
		bestChromosome.fitness = this.fitness[best];
		return bestChromosome;
	}
}
