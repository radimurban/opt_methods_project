package opti_des_air;

import java.util.Random;


public class ODA {
    
    private static final int POPULATION_SIZE = 500;
    private static final int MAX_GENERATIONS = 50000;
    
    public static void main(String[] args) {
        
        // Initialize population
        Population population = new Population(POPULATION_SIZE);
        
        // Evaluate initial fitness
        population.evaluate();
        
        int generation = 1;
        
        // Evolution loop
       
        while (generation <= MAX_GENERATIONS) {
            
            // Print current generation
            System.out.println("Generation: " + generation);
            
            // Select parents
            Chromosome parent1 = population.select();
            Chromosome parent2 = population.select();
            
            // Crossover
            Chromosome child = parent1.crossover(parent2);
            
            // Replace worst individual with child
            population.replaceWorst(child);
            
            // Evaluate new fitness
            population.evaluate();
            
            // Print best fitness
            System.out.println("Best fitness: " + population.getBestFitness());
            
            generation++;
        }
        
    }
    
}

class Chromosome {
    
    private double[] genes;
    private double fitness;
    private Random random;
    
    public Chromosome(double[] genes) {
        this.genes = genes;
        this.random = new Random();
    }
    
    public double[] getGenes() {
        return genes;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public Chromosome crossover(Chromosome other) {
        double[] childGenes = new double[genes.length];
        
        
        for (int i = 0; i < childGenes.length; i++) {
        	
        	childGenes[i] = random.nextBoolean() ?  this.getGenes()[i] : other.getGenes()[i];
        }
        
        return new Chromosome(childGenes);
    }
    
}

class Population {
    
    public Chromosome[] individuals;
    private Random random;
    
    private double random(int min, int max) {
    	return Math.random() * (max - min + 1) + min;
    }
    
    public Population(int size) {
        individuals = new Chromosome[size];
        random = new Random();
        for (int i = 0; i < size; i++) {
            double[] genes = new double[5];
            
            genes[0] = random(20,100);
            
            genes[1] = random(60,200);
            
            genes[2] = random(0,90);
            
            genes[3] = random(0,1);
            
            genes[4] = random(5,15);
            		
            
            individuals[i] = new Chromosome(genes);
        }
    }
    
    public void evaluate() {
        double bestFitness = Double.MIN_VALUE;
        for (Chromosome individual : individuals) {
            double fitness = evaluate(individual);
            individual.setFitness(fitness);
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
    }
    
    private double evaluate(Chromosome individual) {
    	//Fitness function for lift
    	double[] genes = individual.getGenes();
    	double v = genes[0];
    	double s = genes[1];
    	double alpha = genes[2];
    	double e = genes[3];
    	double ar = genes[4];
    	
    	double rho = 1.293;
    	
    	double fitness = 0.5 * rho * (v*v) * s * ((2*Math.PI*alpha)/(1 + (Math.PI*e*ar)));
        return fitness;
    }
    
    public Chromosome select() {
        // Tournament selection
    	
    	// Choose five random individuals and choose the best one of them
        Chromosome best = null;
        for (int i = 0; i < 10; i++) {
            Chromosome individual = individuals[random.nextInt(individuals.length)];
            if (best == null || individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }
        return best;
    }
    
    public void replaceWorst(Chromosome child) {
        int worstIndex = 0;
        double worstFitness = Double.MAX_VALUE;
        for (int i = 0; i < individuals.length; i++) {
            if (individuals[i].getFitness() < worstFitness) {
                worstIndex = i;
                worstFitness = individuals[i].getFitness();
            }
        }
        individuals[worstIndex] = child;
    }
    
    public double getBestFitness() {
        double bestFitness = Double.MIN_VALUE;
        for (Chromosome individual : individuals) {
            if (individual.getFitness() > bestFitness) {
                bestFitness = individual.getFitness();
            }
        }
        return bestFitness;
    }
    
}

