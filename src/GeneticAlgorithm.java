import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GeneticAlgorithm {
	
	private Chromo[] population;
	private double totalFitness;
	
	//public static String fitnessType; //use this if you have multiple fitness types
	//public static String crossoverType; //use this if you have multiple crossover types
	public static String selectionType;
	public static int populationSize;
	public static double crossoverRate;
	public static double mutationRate;
	public static int numElites; //keep as an even to simplify things (we usually make 2 kids at a time)
	public static int numRuns;
	public static int timeout;
	public static int numTests;
	public static int tournamentSize;
	
	public static Point[] points;
	
	public GeneticAlgorithm() {	
		population = new Chromo[populationSize];
		for (int chromo=0; chromo<populationSize; chromo++) {
			population[chromo] = new Chromo();
		}
	}
	
	public Chromo selectParent() {
		Random rand = new Random();
		
		//---------------- FITNESS PROPORTIONAL SELECTION --------------------
		if (selectionType.equals("fitness-proportional")){
			double runningFitness = 0;
			double probability = rand.nextDouble();
			//System.out.println("totalFit: "+totalFitness+"; selection probability: "+probability);
			double worstFitness = this.population[0].getFitness();
			double adjustedTotalFitness =  populationSize*worstFitness-totalFitness;

			runningFitness=0;
			for (int c=0; c<populationSize; c++) {
				runningFitness += (worstFitness - this.population[c].getFitness());
				//System.out.println("["+c+"] running fitness: "+runningFitness + "/total = "+runningFitness/totalFitness);
				if (probability < runningFitness/adjustedTotalFitness)
					return this.population[c];			
			}
			//we'll only end up here if all fitnesses were zero, so let's pick a parent at random
			return this.population[rand.nextInt(populationSize)];
		}
		else if (selectionType.equals("rank-proportional")) {
			//---------------- RANK PROPORTIONAL SELECTION --------------------
			//since chromos in current generation have been sorted, their spot+1 is their rank
			double sumRank = 0;
			double overallSumRanks = (populationSize+1)*populationSize/2.0;//sum of 1 through n
			
			double probability = rand.nextDouble();
			//System.out.println("selection probability: "+probability);
			for (int c=0; c<populationSize; c++) {
				sumRank += (c+1);//ranks are not zero based
				//System.out.println(population[c] + " rank "+(c+1));
				if (probability < sumRank/overallSumRanks) {
					//System.out.println("Chosen parent: "+this.population[c]);
					return this.population[c];	
				}
			}
			//if somehow we got here without picking anyone, we could pick best
			return this.population[populationSize-1];
		}
		else {//RANDOM SELECTION AS BASELINE
			return this.population[rand.nextInt(populationSize)];
		}
	}
	
	public void makeNextGen() {
		
		Chromo[] tng = new Chromo[populationSize];
		int tngSize;
		
		// -------------------ELITISM----------------------------
		for (tngSize=0; tngSize<numElites; tngSize++) {//pick top, i.e. last
			tng[tngSize] = this.population[populationSize-1-tngSize];
		}
		
		Random rand = new Random();
		Chromo parent1, parent2, child;
		//----------------- MUTATION ------------
		int mutants = (int)(populationSize*mutationRate);
		for (int m=0; m<mutants; m++) {
			parent1 = selectParent();
			child = new Chromo (parent1.getPolicy());//don't point to parent in case we need changes like mutation 
			child.mutate();
			tng[tngSize++] = child;
		}
		//----------------- CROSSOVER ------------
		while (tngSize < populationSize) {
		
			parent1 = selectParent();
			parent2 = null;
			child = null;
			
			if (rand.nextDouble()<crossoverRate) {
				parent2 = selectParent();
				child = Chromo.crossover(parent1,parent2);
			}
			else {//no crossover, just copy the parent into new generation
				child = new Chromo (parent1.getPolicy(), parent1.getID());//don't point to parent in case we need changes like mutation 
				//just copy of parent1, so same id
			}
				
			tng[tngSize++] = child;
		}
		
		this.population = tng;
	}
	
	public static void printTestSettings() {
		System.out.printf("\tselectionType:\t" + selectionType + "\n"
						+ "\ttournamentSize:\t" + tournamentSize + "\n"
						//+ "crossoverType:\t" + crossoverType + "\n"
						+ "\tpopulationSize:\t" + populationSize + "\n"
						+ "\tcrossoverRate:\t" + crossoverRate + "\n"
						+ "\tmutationRate:\t" + mutationRate +"\n"
						+ "\tnumElites:\t" + numElites + "\n"
						+ "\tnumRuns:\t" + numRuns + "\n"
						+ "\ttimeout:\t" + selectionType +"\n");
	}

	public static void main(String[] args)  throws Exception {

		//File file = new File("./src/Problem1_1_-4_9.txt");
		//File file = new File("./src/Problem2_19_47_-90.txt");
		//File file = new File("./src/Problem3_-100_-32_-7.txt");
		File file = new File("./src/Problem4_1570_-3200_-6053.txt");//title has answers, for reference only
		 Scanner sc = new Scanner (file);
		 

		//-----------------------------------------------------
		points = new Point[100];
		for (int p=0; p<points.length; p++) {
			points[p] = new Point(sc.nextInt(),sc.nextInt());
		}
		sc.close();
		double maxFitness = 0; //ideal sum distance from points is zero
		boolean converged;
		boolean writeGenerations=true;
		
		//fitnessType = "ones"; //"integer" or "ones"
		selectionType = "fitness-proportional"; //"fitness-proportional", "rank-proportional"
		//crossoverType = "single-point"; //"single-point" or "uniform"	
		populationSize = 100;
		crossoverRate = 0.8;
		mutationRate = 0.01;
		numElites = 2; //just to simplify things (as we often make 2 kids at a time)
		tournamentSize = 2;
		numRuns=10;
		timeout=100000;
		int numTests = 3;
		double[][] runNum = new double[numTests][numRuns];
		double[][] genSolved = new double[numTests][numRuns];
		for (int test=0; test<numTests; test++) {
			
			switch (test) {
				case 0: 
					populationSize = 30;
					selectionType = "rank-proportional";
					break;
				case 1: 
					populationSize = 30;
					selectionType = "fitness-proportional";
					break;
				case 2: 
					populationSize = 30;
					selectionType = "tournament";
					break;
			}
			
			FileWriter fw = new FileWriter(new File ("./src/test"+test+"_output.txt"), false);
			for (int run=0; run<numRuns; run++) {
				GeneticAlgorithm ga = new GeneticAlgorithm();
				converged=false;
				int gen=0;
				while(true && gen<timeout) {//evolve until solved or timed out
					ga.totalFitness = 0;
					
					//--------- test everyone's fitness -----------
					for (int c=0; c<populationSize; c++) {
						ga.population[c].calculateFitness();
						ga.totalFitness +=ga.population[c].getFitness();
					}
					//--------- sort, establish best, count copies of best ------------
					Arrays.sort(ga.population);
					
					int bests = 0;
					int[] bestValues = ga.population[populationSize-1].policy;
					if(writeGenerations) 
						fw.write("Run["+run+"]Gen["+gen+"] ");
					for (int c=0; c<populationSize; c++) {
						if (Arrays.equals(ga.population[c].policy,bestValues)) {
							bests++;
							if(writeGenerations) 
								fw.write("c*:"+ga.population[c]+"; ");//adding asterisks to copies of best in output file
						}
						else if(writeGenerations) 
							fw.write("c:"+ga.population[c]+"; ");
					}
					if(writeGenerations) 
						fw.write("\n");
					System.out.println("Test["+test+"]Run["+run+"]Gen["+gen+"] Best Chromo (x"+bests+"):"+ga.population[populationSize-1]);
					
					//--------- check if we are done searching------------
					if (ga.population[populationSize-1].getFitness()==maxFitness)
						break;
					if (ga.population[populationSize-1].policy[0]==ga.population[0].policy[0] &&
						ga.population[populationSize-1].policy[1]==ga.population[0].policy[1] &&
						ga.population[populationSize-1].policy[2]==ga.population[0].policy[2]) {
						converged=true;
						break;
					}
					//--------- breed a new generation ---------------
					ga.makeNextGen();
					gen++;
				}
				if (converged)
					System.out.printf("\n[run %3d] POPULATION IS FULLY CONVERGED in gen %d",run+1,gen);
				else if (gen==timeout)
					System.out.printf("\n[run %3d] still unsolved at timeout in gen %d",run+1,gen);
				else
					System.out.printf("\n[run %3d] exact solution found in gen %d",run+1,gen);
				genSolved[test][run] = gen;
				runNum[test][run] = (run+1);//this is just for the visualizer
			}
			fw.close();
		}
		
		System.out.println();
		for (int t=0; t<numTests; t++) {
			int successfulRuns = 0;
			double avgGenToSolution = 0.0;
			for (int r=0; r<numRuns; r++) {
				if (genSolved[t][r]<timeout) {
					avgGenToSolution += genSolved[t][r]; //to be divided by successfulRuns later
					successfulRuns++;
				}
			}
			avgGenToSolution /= successfulRuns;
			double stdevAvgGenToSolution = 0.0;
			for (int r=0; r<numRuns; r++) {
				if (genSolved[t][r]<timeout)
					stdevAvgGenToSolution += Math.pow(genSolved[t][r] - avgGenToSolution, 2);
		    }
			stdevAvgGenToSolution =  Math.sqrt(stdevAvgGenToSolution/numRuns);
			System.out.printf("Test %d: avg. gen to solution %.2f (st.dev.%.2f); successRate: %.2f\n",t,avgGenToSolution, stdevAvgGenToSolution,((double)successfulRuns)/numRuns);
			printTestSettings();
		}
		//visualizer takes 2 2D arrays of doubles: xValues[][], yValues[][] 
		//first dimension represents the series/experiment and second dimension represents the points in that series/experiment
		//you can pass in null for the xValues[][], in which case indices will be used as xValues 
		//for example: plotting values per generation over time, we don't need to pass in anything for the generation numbers
		//visualizer also accepts two strings to label your axes
		Visualizer.visualize(runNum, genSolved, "Run#", "Gen Solution Found"); 
				
	}
}
