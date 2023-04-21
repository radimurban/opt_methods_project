package neviem ;
import java.util.Random;

public class GeneticAlgorithm {
    
    private static final int POPULATION_SIZE = 10;
    private static final int MAX_GENERATIONS = 100;
    
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
    
    private int[] genes;
    private double fitness;
    private Random random;
    
    public Chromosome(int[] genes) {
        this.genes = genes;
        this.random = new Random();
    }
    
    public int[] getGenes() {
        return genes;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public Chromosome crossover(Chromosome other) {
        int[] childGenes = new int[genes.length];
        int midpoint = random.nextInt(genes.length);
        for (int i = 0; i < genes.length; i++) {
            if (i < midpoint) {
                childGenes[i] = genes[i];
            } else {
                childGenes[i] = other.genes[i];
            }
        }
        return new Chromosome(childGenes);
    }
    
}

class Population {
    
    private Chromosome[] individuals;
    private Random random;
    
    public Population(int size) {
        individuals = new Chromosome[size];
        random = new Random();
        for (int i = 0; i < size; i++) {
            int[] genes = new int[10];
            for (int j = 0; j < genes.length; j++) {
                genes[j] = random.nextInt(2);
            }
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
        // Fitness function goes here
        // This implementation simply returns the sum of the genes
        int[] genes = individual.getGenes();
        double sum = 0;
        for (int gene : genes) {
            sum += gene;
        }
        return sum;
    }
    
    public Chromosome select() {
        // Tournament selection
        Chromosome best = null;
        for (int i = 0; i < 5; i++) {
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
