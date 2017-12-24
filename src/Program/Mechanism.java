package Program;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Values.Data;
import Values.Parameter;
import Values.Relation;

public class Mechanism {

	private Data data = Data.getInstance();
	private Parameter parameter = Parameter.getInstance();
	private ArrayList<Integer>[] conservative;
	private ArrayList<Integer>[] explorer;
	
	// 候選人機制
	public ArrayList<Integer>[] Candidate(ArrayList<Integer>[] chromosome, double[] fitness) {
		Relation[] relation = new Relation[data.total];
		// 取得保守者
		conservative = getConservative(chromosome);
		// 找出所有點的關係
		relation = getPointRelation(relation, chromosome, fitness);
		// 產生探險者
		createExplorer(relation);
		// 回傳探險者
		return explorer;
	}
	
	// 選擇機制
	public ArrayList<Integer>[] Selection(ArrayList<Integer>[] chromosome, double[] fitness) {
		int listSize = chromosome.length;
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] selectPool = new ArrayList[listSize];
		Random random = new Random();
		for (int i=0; i<listSize; i++) {
			int randomNumber1 = random.nextInt(listSize);
			int randomNumber2 = random.nextInt(listSize);
			while (randomNumber1 == randomNumber2) {
				randomNumber2 = random.nextInt(listSize);
			}
			selectPool[i] = selectChromosome(chromosome[randomNumber1], fitness[randomNumber1], 
											chromosome[randomNumber2], fitness[randomNumber2]);
		}
		return selectPool;
	}
	
	// 交配機制
	public ArrayList<Integer>[] Crossover(ArrayList<Integer>[] selectPool) {
		Set<Integer> isCrossover = new HashSet<Integer>();
		Random random = new Random();
		int listSize = selectPool.length;
		while (isCrossover.size() != listSize) {
			int crossoverNumber1 = random.nextInt(listSize);
			int crossoverNumber2 = random.nextInt(listSize);
			while (!isCrossover.add(crossoverNumber1)) {
				crossoverNumber1 = random.nextInt(listSize);
			}
			while (!isCrossover.add(crossoverNumber2)) {
				crossoverNumber2 = random.nextInt(listSize);
			}
			if (parameter.getCossoverRate() >= random.nextDouble()) {
				ArrayList<Integer>[] tmp = swapChromosome(selectPool[crossoverNumber1], selectPool[crossoverNumber2]);
				selectPool[crossoverNumber1] = tmp[0];
				selectPool[crossoverNumber2] = tmp[1];
			}
		}
		return selectPool;
	}
	
	// 突變機制
	public ArrayList<Integer>[] Mutation(ArrayList<Integer>[] chromosome) {
		Random random = new Random();
		int listSize = chromosome.length;
		for (int i=0; i<listSize; i++) {
			double ran = random.nextDouble();
			if (parameter.getMutationRateC() >= ran && i < listSize/2)
				chromosome[i] = pointSwap(chromosome[i]);
			if (parameter.getMutationRateE() >= ran && i >= listSize/2)
				chromosome[i] = pointSwap(chromosome[i]);
		}
		return chromosome;
	}
	
	// 優化機制
	public ArrayList<Integer>[] Optimization(ArrayList<Integer>[] chromosome) {
		for (int i=0; i<chromosome.length; i++) {
			chromosome[i] = twoOptimization(chromosome[i]);
		}
		return chromosome;
	}
	
	// 候選人機制-找尋點之間關係
	private Relation[] getPointRelation(Relation[] relation, ArrayList<Integer>[] chromosome, double[] fitness) {
		for (int i=0; i<parameter.getChromosome()/2; i++) {
			for (int j=0; j<conservative[i].size(); j++) {
				int nowPoint = conservative[i].get(j);
				if (relation[nowPoint] == null) { // MainPoint為空
					relation[nowPoint] = new Relation();
					relation[nowPoint].setMainPoint(conservative[i].get(j)); // 點j輸入
				}
				
				if (j==conservative[i].size()-1) { // 最後一個點，與第一個點的關係
					int nextPoint = conservative[i].get(0);
					if (relation[nowPoint].isPointContained(nextPoint)) { // 已包含染色體內第0個點
						if (relation[nowPoint].getPointFitness(nextPoint) < fitness[i]) // 比較適應值，若現有適應值小於新的適應值則取代
							relation[nowPoint].replacePointFitness(nextPoint, fitness[i]); // 取代染色體內第0個點之適應值
					} else { // 不包含第0個點
						relation[nowPoint].setPoint(nextPoint); // 加入點0
						relation[nowPoint].setFitness(fitness[i]); // 加入點0適應值
					}
				} else {
					int nextPoint = conservative[i].get(j+1);
					if (relation[nowPoint].isPointContained(nextPoint)) { // 已包含染色體內第j+1個點
						if (relation[nowPoint].getPointFitness(nextPoint) < fitness[i]) // 比較適應值，若現有適應值小於新的適應值則取代
							relation[nowPoint].replacePointFitness(nextPoint, fitness[i]); // 取代染色體內第j+1個點之適應值
					} else { // 不包含第j+1個點
						relation[nowPoint].setPoint(nextPoint); // 加入點j
						relation[nowPoint].setFitness(fitness[i]); // 加入點j適應值
					}
				}
			}
		}
		return relation;
	}
	
	// 候選人機制-產生探險者
	@SuppressWarnings("unchecked")
	private void createExplorer(Relation[] relations) {
		this.explorer = new ArrayList[this.conservative.length];
		for (int i=0;i<this.conservative.length; i++) {
			for (int j=0;j<this.conservative[i].size(); j++) {
				if (j == 0) {
					this.explorer[i] = new ArrayList<Integer>();
					this.explorer[i].add(this.conservative[i].get(j));
				} else {
					this.explorer[i].add(getExplorerPoint(relations, this.explorer[i].get(j-1), this.explorer[i]));
				}
			}
		}
	}
	
	// 候選人機制-取得探險者基因
	private int getExplorerPoint(Relation[] relation, int lastPoint, ArrayList<Integer> alreadyPoint) {
		ArrayList<Integer> candidatePoint = new ArrayList<Integer>();
		ArrayList<Double> candidateFitness = new ArrayList<Double>();
		boolean run = true;
		int size = relation[lastPoint].getPointSize();
		for (int i=0; i<size; i++) {
			candidatePoint.add(relation[lastPoint].getPoint(i));
			candidateFitness.add(relation[lastPoint].getFitness(i));
		}
		
		while (run) {
			run = false;
			for (int i=1; i<candidatePoint.size(); i++) {
				if (candidateFitness.get(i-1) < candidateFitness.get(i)) {
					int oldPoint = candidatePoint.get(i-1);
					int nowPoint = candidatePoint.get(i);
					double oldFitness = candidateFitness.get(i-1);
					double nowFitness = candidateFitness.get(i);
					candidatePoint.remove(i-1);
					candidatePoint.add(i-1, nowPoint);
					candidateFitness.remove(i-1);
					candidateFitness.add(i-1, nowFitness);
					
					candidatePoint.remove(i);
					candidatePoint.add(i, oldPoint);
					candidateFitness.remove(i);
					candidateFitness.add(i, oldFitness);
					run = true;
				}
			}
		}
		
		int returnPoint = -1;
		for (int i=0; i<candidatePoint.size(); i++) {
			if (!alreadyPoint.contains(candidatePoint.get(i))) {
				returnPoint = candidatePoint.get(i);
				break;
			}
		}
		
		return returnPoint;
	}
	
	private ArrayList<Integer>[] getConservative(ArrayList<Integer>[] chromosome) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] tmpChromosome = new ArrayList[parameter.getChromosome()/2];
		for(int i=0; i<parameter.getChromosome()/2; i++) {
			tmpChromosome[i] = chromosome[i];
		 }
		return tmpChromosome;
	}
	
 	// 選擇機制-選擇染色體，比較兩個染色體之適應值，選擇適應值較佳之染色體
 	private ArrayList<Integer> selectChromosome(ArrayList<Integer> chromosome1, double fitness1, 
 												ArrayList<Integer> chromosome2, double fitness2 ) {
 		if (fitness1 > fitness2) {
 			return chromosome1;
 		}
 		else {
 			return chromosome2;
		}
 	}
 	
 	// 交配機制-多點互換，交換染色體內基因
 	private ArrayList<Integer>[] swapChromosome(ArrayList<Integer> chromosome1, ArrayList<Integer> chromosome2) {
 		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] finish = new ArrayList[2];
 		ArrayList<Integer> chromosomeOne = new ArrayList<Integer>();
 		ArrayList<Integer> chromosomeTwo = new ArrayList<Integer>();
 		Random random = new Random();
 		int size = chromosome1.size();
 		int max = random.nextInt(size);
 		int min = random.nextInt(size);
 		while (max == min) {
 			min = random.nextInt(size);
 		}
 		if (max < min) {
 			int tmp = max;
 			max = min;
 			min = tmp;
 		}
 		finish[0] = new ArrayList<Integer>();
 		finish[1] = new ArrayList<Integer>();
 		for (int j=0; j<size; j++) {
 			if (j>=min && j<=max) {
 				// 欲交配染色體
 				chromosomeOne.add(chromosome1.get(j));
 				chromosomeTwo.add(chromosome2.get(j));
 			} else {
 				// 未交配染色體
 				finish[0].add(chromosome1.get(j));
 				finish[1].add(chromosome2.get(j));
 			}
 		}
 		finish[0] = geneReplace(finish[0], chromosomeTwo, chromosomeOne);
 		finish[1] = geneReplace(finish[1], chromosomeOne, chromosomeTwo);
 		finish[0].addAll(min, chromosomeTwo);
 		finish[1].addAll(min, chromosomeOne);
 		return finish;
 	}
 	
 	// 交配機制-取代基因，將選擇將配基因與未交配基因做比對，把重複點替換掉
 	private ArrayList<Integer> geneReplace(ArrayList<Integer> chromosome, ArrayList<Integer> newPoint, ArrayList<Integer> oldPoint) {
 		for (int i=0; i<newPoint.size(); i++) {
 			if (chromosome.contains(newPoint.get(i))) {
 				int position = chromosome.indexOf(newPoint.get(i));
 				
 				for (int j=0; j<oldPoint.size(); j++) {
 	 	 			if (!newPoint.contains(oldPoint.get(j)) && !chromosome.contains(oldPoint.get(j))) {
 	 	 				chromosome.remove(position);
 	 	 				chromosome.add(position, oldPoint.get(j));
 	 	 				break;
 	 	 			}
 	 	 		}
 			}
 		}
 		return chromosome;
 	}
 	
 	// 突變機制-兩點交換，隨機選擇兩點進行交換
 	private ArrayList<Integer> pointSwap(ArrayList<Integer> chromosome) {
		Random random = new Random();
		int size = chromosome.size();
		int position1 = random.nextInt(size);
		int position2 = random.nextInt(size);
		while (position1 == position2) {
			position1 = random.nextInt(size);
			position2 = random.nextInt(size);
		}
		int tmp1 = chromosome.get(position1);
		int tmp2 = chromosome.get(position2);
		chromosome.remove(position1);
		chromosome.add(position1, tmp2);
		chromosome.remove(position2);
		chromosome.add(position2, tmp1);
		return chromosome;
	}
 	
 	// 優化機制-判斷是否交叉
 	private boolean isCross(int pointA, int pointB, int pointC, int pointD) {
		double lineAB = Point.distance(data.x[pointA], data.y[pointA], data.x[pointB], data.y[pointB]);
		double lineCD = Point.distance(data.x[pointC], data.y[pointC], data.x[pointD], data.y[pointD]);
		double lineAB_CD = lineAB + lineCD;
		double lineAC = Point.distance(data.x[pointA], data.y[pointA], data.x[pointC], data.y[pointC]);
		double lineBD = Point.distance(data.x[pointB], data.y[pointB], data.x[pointD], data.y[pointD]);
		double lineAC_BD = lineAC + lineBD;
		if (lineAB_CD < lineAC_BD)
			return false;
		else
			return true;
	}
 	
 	// 優化機制-二元優化法
 	private ArrayList<Integer> twoOptimization(ArrayList<Integer> chromosome) {
 		int size = chromosome.size();
 		for (int i=0; i<size-2; i++) {
			for (int j=i+2; j<size-1; j++) {
				if (isCross(chromosome.get(i), chromosome.get(i+1), chromosome.get(j), chromosome.get(j+1))) {
					chromosome = sortGene(chromosome, i+1, j);
				}
			}
		}
 		return chromosome;
	}
 	
 	// 優化機制-交換點並重新排序
 	private ArrayList<Integer> sortGene(ArrayList<Integer> chromosome, int min, int max) {
 		ArrayList<Integer> finish = new ArrayList<Integer>();
 		ArrayList<Integer> tmp = new ArrayList<Integer>();
 		int size = chromosome.size();
 		for (int i=0; i<size; i++) {
 			if (i>= min && i<=max)
 				tmp.add(0,chromosome.get(i));
 			else
				finish.add(chromosome.get(i));
 		}
 		finish.addAll(min, tmp);
 		return finish;
 	}
}
