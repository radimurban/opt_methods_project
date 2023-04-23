package oga;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ResourceAllocation {
	private static final int POPULATION_SIZE = 5;
	private static final int NUMBER_RESOURCES = 3;
    private static final int MAX_GENERATIONS = 100;
    private static final int RESOURCE_LIMIT = 100;
    private static final double ELITISM_RATE = 0.2;
    private static final double MUTATION_RATE = 0.3;
    private static final Random RANDOM = new Random();

	public static void main(String[] args) {
		// Initialize manager and population
		double prices[] = new double[] {0.5, 1.5, 2.0};
		Manager man = new Manager(NUMBER_RESOURCES, prices, RESOURCE_LIMIT);
		
		int[][] neededResources = new int[][] { {1, 1, 0},
												{0, 1, 1},
												{1, 0, 1} };
		double sellingPrices[] = new double[] {6, 10.5, 7.5};
		man.populateR(POPULATION_SIZE);
		man.populateP(3, neededResources, sellingPrices);

		// Initial evaluation
		man.evaluate();
		
        // Evolution loop
		for (int generation = 0; generation < MAX_GENERATIONS; generation++) {

			// Sort the population by fitness in descending order
			Arrays.sort(man.getResource(), Comparator.comparingDouble(Resource::getFitness).reversed());
			
			// Print attributes
			System.out.println("Generation: " + generation);
			Resource bestres = man.getResource()[0];
			System.out.println("Best allocation: " + Arrays.toString(bestres.getResources()));
			System.out.println("Cost: " + bestres.getCost());
			System.out.println("Profit: " + man.evalProfit(bestres));
			System.out.println("Fitness: " + bestres.getFitness());
			// System.out.println("Best fitness: " + man.getBestFitness());

			
            // Select the elite resources for the next generation
            int eliteSize = (int) (POPULATION_SIZE * ELITISM_RATE);
            Resource newPopulation[] = new Resource[POPULATION_SIZE];
            for (int i = 0; i < eliteSize; i++) {
                newPopulation[i] = man.getResource()[i];
            }

            // Create the next generation
            for (int i = eliteSize; i < POPULATION_SIZE; i++) {
                // Select parents
                Resource parent1 = man.select();
                Resource parent2 = man.select();

                Resource child;
                do {
                	child = man.crossover(parent1, parent2);
                	if (RANDOM.nextDouble() < MUTATION_RATE) {
                        man.mutate(child);
                    }
                } while (Arrays.stream(child.getResources()).sum() > 100);

                newPopulation[i] = child;
            }
        	
            man.setResource(newPopulation);
            
            // Evaluate new fitness
            man.evaluate();
            
        }
		
		System.out.println("Generation: 100");
		Resource bestres = man.getResource()[0];
		System.out.println("Best allocation: " + Arrays.toString(bestres.getResources()));
		System.out.println("Cost: " + bestres.getCost());
		System.out.println("Profit: " + man.evalProfit(bestres));
		System.out.println("Fitness: " + bestres.getFitness());

	}
}

class Manager {
	private int n;
	private double prices[];
	private int limit;
	private Random random = new Random();
	
	private Resource[] resources;
	private Product[] products;
	
	// Constructor with user defined values
	public Manager(int n, double[] prices, int limit) {
		this.n = n;
		this.prices = prices;
		this.limit = limit;
	}
	
	// Create num Resources
	public void populateR(int num) {
		Resource[] population = new Resource[num];
		for (int i = 0; i < num; i++) {
			int runningSum = 50;
			int resources[] = new int[n];
			double varCost = 0.0;
			
			for (int j = 0; j < n; j++) {
				resources[j] = random.nextInt(limit - runningSum);
				runningSum += resources[j];
				varCost += resources[j] * prices[j];
			}
			
			population[i] = new Resource(resources, varCost);
		}
		
		resources = population;
	}
	
	// Create num Products
	public void populateP(int num, int[][] res, double[] prix) {
		Product[] population = new Product[num];
		for (int i = 0; i < num; i++) {
			population[i] = new Product(res[i], prix[i]);
		}
		
		products = population;
	}
	
	public void evaluate() {
        for (Resource res : resources) {
            double fitness = evaluate(res);
            res.setFitness(fitness);
        }
    }
	
	public Resource[] getResource() {
		return resources;
	}
	
	public void setResource(Resource[] resources) {
		this.resources = resources;
	}
    
    private double evaluate(Resource res) {
        return (evalProfit(res) / res.getCost()) * (Arrays.stream(res.getResources()).sum());
    }
    
    public double evalProfit(Resource res) {
    	Arrays.sort(products, Comparator.comparingDouble(Product::getPrice).reversed());
    	double profit = 0.0;
    	int[] localCopy = Arrays.copyOf(res.getResources(), res.getResources().length);
        
    	for (int i = 0; i < products.length; i++) {
    		while(isAvailable(products[i].getNeededResources(), localCopy)) {
    			Product product = products[i];
    			Arrays.setAll(localCopy, j -> localCopy[j] - product.getNeededResources()[j]);
    			profit += product.getPrice();
    		}
    	}
    	return profit;
    }
    
    private boolean isAvailable(int[] needed, int[] available) {
    	assert needed.length == available.length;
    	for (int i = 0; i < available.length; i++) {
    		if (available[i] - needed[i] < 0) {
    			return false;
    		}
    	}
    	return true;
    }
	
	// Tournament selection
	// Choose five random resources and choose the best one of them
	public Resource select() {
        Resource best = null;
        int end = 5;
        for (int i = 0; i < end; i++) {
            Resource res = resources[random.nextInt(n)];
            if (best == null || res.getFitness() > best.getFitness()) {
                best = res;
            }
        }
        return best;
    }
	
    
    public double getBestFitness() {
        double bestFitness = Double.MIN_VALUE;
        for (Resource res : resources) {
            if (res.getFitness() > bestFitness) {
                bestFitness = res.getFitness();
            }
        }
        return bestFitness;
    }
    
    // Perform crossover using a two-point crossover
    public Resource crossover(Resource parent1, Resource parent2) {
        int[] childResources = new int[n];
        int crossoverPoint1 = random.nextInt(n);
        int crossoverPoint2 = random.nextInt(n - crossoverPoint1) + crossoverPoint1;
        double varCost = 0.0;
        
        for (int i = 0; i < crossoverPoint1; i++) {
            childResources[i] = parent1.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        for (int i = crossoverPoint1; i < crossoverPoint2; i++) {
            childResources[i] = parent2.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        for (int i = crossoverPoint2; i < n; i++) {
            childResources[i] = parent1.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        
        return new Resource(childResources, varCost);
    }
    
    // Perform mutation by randomly changing one resource allocation
    public void mutate(Resource res) {
        int index = random.nextInt(3);
        res.setCost(res.getCost() - res.getResources()[index] * prices[index]);
        res.getResources()[index] = random.nextInt(limit);
        res.setCost(res.getCost() + res.getResources()[index] * prices[index]);
    }
}

class Resource {
	private int resources[];
	private double cost;
	private double fitness;
	
	// Construction with user defined values
	public Resource(int[] resources, double cost) {
		this.resources = resources;
		this.cost = cost;
	}
	
	// Getter and setter methods
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public int[] getResources() {
		return resources;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getFitness() {
		return fitness;
	}
}

class Product {
	private int[] neededResources;
	private double price;
	
	// Construction with user defined values
	public Product(int[] res, double price) {
		this.neededResources = res;
		this.price = price;
	}
	
	// Getter and setter methods
	public int[] getNeededResources() {
		return neededResources;
	}
	
	public double getPrice() {
		return price;
	}
}
