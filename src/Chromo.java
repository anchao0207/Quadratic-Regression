import java.util.Arrays;
import java.util.Random;


public class Chromo  implements Comparable<Chromo>{
	
	//public static double mutationAmount = 0.05;
	//public static double crossoverAmount = 0.7;
	
	public int[] policy;
	public static int length=3;
	private double fitness;
	private int id;
	
	public static int min=-10000;
	public static int max=10000;
	
	public static int numChromos =0;
	
	// ============== constructors ===================
	public Chromo() {
		Random rand = new Random();
		this.policy = new int[Chromo.length]; //by default all zeros in Java
		for (int c=0; c<Chromo.length; c++) {
			this.policy[c] = rand.nextInt(max-min)+min;//random in range, shifted over
		}
		this.fitness=-1;
		numChromos++;
		this.id=numChromos;
	}
	
	public Chromo (int[] policy) {
		this.policy = policy.clone();
		this.fitness=-1;
		numChromos++; 
		this.id=numChromos;
	}
	
	public Chromo (int[] policy, int id) {
		this.policy = policy.clone();
		this.fitness=-1;
		this.id = id;
		//numChromos++; //not creasing Chromo count cause this is a copy of an existing chromo
		this.id=numChromos;
	}
	
	//======================================================
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id=id;
	}
	
	public int[] getPolicy() {
		return this.policy;
	}
	
	public String toString() {
		return "["+this.id+"] Policy: "+ Arrays.toString(this.policy) + " Fitness: "+Math.round(this.fitness);
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitness()	{
		return this.fitness;
	}
	
	public int compareTo (Chromo other) {
		if (this.fitness<other.fitness)
			return 1;
		else if (this.fitness>other.fitness)
			return -1;
		else {
			if (this.id<other.id)
				return 1;
			else if (this.id>other.id)
				return -1;
			return 0;
		}
		
	}
	
	public static Chromo crossover (Chromo chromo1, Chromo chromo2) {
		
		//FILL OUT
		return null;
		
	}
	
	public void mutate() {
		//FILL OUT
	}
	
	public void calculateFitness() {
		
		this.fitness = 0;
		int a = this.policy[0];
		int b = this.policy[1];
		int c = this.policy[2];

		for(int p=0; p<10; p++) {
			int x = GeneticAlgorithm.points[p].x;//random x
			int y = a*x*x+b*x+c;//corresponding y, according to this chromo
			this.fitness += Math.pow(GeneticAlgorithm.points[p].y-y,2);
		}
		//System.out.println(this);
	}
	
}
