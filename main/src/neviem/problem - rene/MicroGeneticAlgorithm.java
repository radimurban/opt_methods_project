package neviem;

import java.util.Random;

public class MicroGeneticAlgorithm {

    private static final int POPULATION_SIZE = 10;
    private static final int MAX_GENERATIONS = 100;

    // Investment data
    private static final double[] INVESTMENT_RETURNS = {0.05, 0.07, 0.09, 0.11, 0.13};
    private static final double[] INVESTMENT_RISKS = {0.03, 0.035, 0.04, 0.045, 0.05};
    private static final double TARGET_RISK = 0.04;

    public static void main(String[] args) {

        // Initialize population
        Population population = new Population(POPULATION_SIZE, INVESTMENT_RETURNS,INVESTMENT_RISKS);

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

        // Print final portfolio
        System.out.println("Final portfolio:");
        double[] portfolio = population.getBestIndividual().getGenes();
        for (int i = 0; i < portfolio.length; i++) {
            System.out.println("Investment " + (i+1) + ": " + portfolio[i]*100 + "%");
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
    private double[] returns;
    private double[] risks;
    private double totalReturn;
    private double totalRisk;
    
    public Population(int size, double[] returns, double[] risks) {
        individuals = new Chromosome[size];
        random = new Random();
        this.returns = returns;
        this.risks = risks;
        this.totalReturn = 0;
        this.totalRisk = 0;
        
        for (int i = 0; i < size; i++) {
            double[] genes = new double[returns.length];
            for (int j = 0; j < genes.length; j++) {
                genes[j] = random.nextInt(2);
            }
            individuals[i] = new Chromosome(genes);
        }
    }
    
    public void evaluate() {
        double bestFitness = Double.MIN_VALUE;
        totalReturn = 0;
        totalRisk = 0;
        for (Chromosome individual : individuals) {
            double[] weights = getWeights(individual);
            double fitness = evaluate(weights);
            individual.setFitness(fitness);
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
    }
    
    private double evaluate(double[] weights) {
        double portfolioReturn = 0;
        double portfolioRisk = 0;
        
        for (int i = 0; i < returns.length; i++) {
            portfolioReturn += weights[i] * returns[i];
            portfolioRisk += Math.pow(weights[i] * risks[i], 2);
        }
        
        portfolioRisk = Math.sqrt(portfolioRisk);
        totalReturn = portfolioReturn;
        totalRisk = portfolioRisk;
        
        return portfolioReturn / portfolioRisk;
    }
    
    private double[] getWeights(Chromosome individual) {
        double[] genes = individual.getGenes();
        double[] weights = new double[genes.length];
        int sum = 0;
        for (double gene : genes) {
            sum += gene;
        }
        for (int i = 0; i < genes.length; i++) {
            weights[i] = genes[i] * 1.0 / sum;
        }
        return weights;
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
    
    public double getTotalReturn() {
        return totalReturn;
    }
    
    public double getTotalRisk() {
        return totalRisk;
    }
    
}
