package resource_allocation;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ResourceAllocation {
	private static final int POPULATION_SIZE = 400;
	private static final int NUMBER_RESOURCES = 6;
	private static final int NUMBER_PRODUCTS = 4;
    private static final int MAX_GENERATIONS = 100;
    private static final int RESOURCE_LIMIT = 100;
    private static final double ELITISM_RATE = 0.2;
    private static final Random RANDOM = new Random();

	public static void main(String[] args) {
		// Initialize manager and population
		double prices[] = new double[] {1.5, 4.0, 6.5, 8.0, 2.0, 9.5};
		int[][] neededResources = new int[][] { {2, 1, 3, 0, 2, 1},
			{1, 3, 0, 2, 1, 3},
			{3, 2, 1, 0, 1, 3},
			{0, 2, 1, 3, 0, 1} };
			double sellingPrices[] = new double[] {120, 180, 148.5, 144};
		Manager man = new Manager(NUMBER_RESOURCES, prices, RESOURCE_LIMIT, POPULATION_SIZE, NUMBER_PRODUCTS, neededResources, sellingPrices, 0.7);
		
		man.populateR(POPULATION_SIZE);
		man.populateP(3, neededResources, sellingPrices);

		// Initial evaluation
		man.evaluate();
		
        // Evolution loop
		int generation = 0;
		double oldFitness = 0;
		while (generation < MAX_GENERATIONS) {

			// Sort the population by fitness in descending order
			Arrays.sort(man.getResource(), Comparator.comparingDouble(Resource::getFitness).reversed());
			
			// Print attributes
			System.out.println("Generation: " + generation);
			Resource bestres = man.getResource()[0];
			System.out.println("Best allocation: " + Arrays.toString(bestres.getResources()));
			System.out.println("Cost: " + bestres.getCost());
			System.out.println("Profit: " + man.evalProfit(bestres)[0]);
			System.out.println("Penalty: " + man.evalProfit(bestres)[1]);
			System.out.println("Fitness: " + bestres.getFitness());
			
			if (bestres.getFitness() - oldFitness < 0.000001) {
				break;
			}
			oldFitness = bestres.getFitness();

			
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
                	// Only for testing purposes against a full GA
                	// Default mutation rate is 0.0
                	if (RANDOM.nextDouble() <= man.getMutationRate()) {
                        man.mutate(child);
                    }
                } while (Arrays.stream(child.getResources()).sum() > RESOURCE_LIMIT);

                newPopulation[i] = child;
            }
        	
            man.setResource(newPopulation);
            
            // Evaluate new fitness
            man.evaluate();
            
            generation++;
        }
		
		if (generation == MAX_GENERATIONS+1) {
			System.out.println("Generation: " + generation);
			Resource bestres = man.getResource()[0];
			System.out.println("Best allocation: " + Arrays.toString(bestres.getResources()));
			System.out.println("Cost: " + bestres.getCost());
			System.out.println("Profit: " + man.evalProfit(bestres)[0]);
			System.out.println("Penalty: " + man.evalProfit(bestres)[1]);
			System.out.println("Fitness: " + bestres.getFitness());			
		}

	}
}

class Manager {
	private int n;
	private double prices[];
	private int limit;
	private double mutationRate;
	private Random random = new Random();
	
	private Resource[] resources;
	private Product[] products;
	
	// Constructor with user defined values
	public Manager(int n, double[] prices, int limit, int populationSize, int numberProducts, int[][] neededResources, double[] sellingPrices) {
		this.n = n;
		this.prices = prices;
		this.limit = limit;
		this.mutationRate = 0.0;
		
		this.populateR(populationSize);
		this.populateP(3, neededResources, sellingPrices);
	}
	
	public Manager(int n, double[] prices, int limit, int populationSize, int numberProducts, int[][] neededResources, double[] sellingPrices, double mutationRate) {
		this.n = n;
		this.prices = prices;
		this.limit = limit;
		this.mutationRate = mutationRate;
		
		this.populateR(populationSize);
		this.populateP(3, neededResources, sellingPrices);
	}
	
	// Get and set methods
	public Resource[] getResource() {
		return resources;
	}
	
	public void setResource(Resource[] resources) {
		this.resources = resources;
	}
	
	public double getMutationRate() {
		return mutationRate;
	}
	
	// Create num Resources
	public void populateR(int num) {
		Resource[] population = new Resource[num];
		for (int i = 0; i < num; i++) {
			int runningSum = 0;
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
	
	// Evaluate and set the fitness of a resource
	public void evaluate() {
        for (Resource res : resources) {
            double fitness = evaluate(res);
            res.setFitness(fitness);
        }
    }
	
    private double evaluate(Resource res) {
    	double[] arr = evalProfit(res);
    	double profit = arr[0];
    	double penalty = arr[1];
    	double result = 0.0;
    	try {
    		result = (profit / res.getCost()) * (Arrays.stream(res.getResources()).sum() - penalty) / (3 * limit);
    		if (result < 0) {
    			result = 0;
    		}
    	} catch (Exception NullPointerException) {
    		result = 0;
    	}
        return result;
    }
    
    // Helper function - returns the maximum profit we can get from the given resource
    public double[] evalProfit(Resource res) {
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
    	double penalty = Arrays.stream(localCopy).sum();
    	return new double[]{profit, penalty};
    }
    
    // Helper function - returns true if all the neccessary resources for the given product are available
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
        for (int i = 0; i < 5; i++) {
            Resource res = resources[random.nextInt(n)];
            if (best == null || res.getFitness() > best.getFitness()) {
                best = res;
            }
        }
        return best;
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
        int index = random.nextInt(res.getResources().length);
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
	
	// Get and set methods
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
	
	// Get and set methods
	public int[] getNeededResources() {
		return neededResources;
	}
	
	public double getPrice() {
		return price;
	}
}
