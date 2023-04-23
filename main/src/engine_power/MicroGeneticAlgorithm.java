package engine_power ;
import java.util.Random;

public class MicroGeneticAlgorithm {
    private static final double PI = Math.PI;
    private static final int POPULATION_SIZE = 1000;
    private static final int NUM_GENERATIONS = 2000;






    private static final int cylindersnumber = 4;

    private static final double MIN_VALVE_MEP = 170.0; //psi
    private static final double MAX_VALVE_MEP = 280.0;
    private static final double MIN_STROKE_LENGTH = 0.27;//ft
    private static final double MAX_STROKE_LENGTH = 0.3;
    private static final double MIN_BORE = 2.9; //in
    private static final double MAX_BORE = 3.5;
    private static final double MIN_REVS = 3500;
    private static final double MAX_REVS = 4000;

    private static final Random random = new Random();

    public static void main(String[] args) {
        // Initialize population
        double[][] population = new double[POPULATION_SIZE][4];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i][0] = random.nextDouble() * (MAX_VALVE_MEP - MIN_VALVE_MEP) + MIN_VALVE_MEP;
            population[i][1] = random.nextDouble() * (MAX_STROKE_LENGTH - MIN_STROKE_LENGTH) + MIN_STROKE_LENGTH;
            population[i][2] = random.nextDouble() * (MAX_BORE - MIN_BORE) + MIN_BORE;
            population[i][3] = random.nextDouble() * (MAX_REVS - MIN_REVS) + MIN_REVS;
        }

        // Run genetic algorithm
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            // Evaluate fitness of population
            double[] fitness = new double[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double[] engineParams = population[i];

                fitness[i] = (cylindersnumber*engineParams[0]*engineParams[1]*(PI/4)*(Math.pow(engineParams[2],2))*(engineParams[3]))/(2*33000);

            }

            // Select parents for crossover
            double[][] parents = new double[POPULATION_SIZE][4];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                int parent1Index = selectParent(fitness);
                int parent2Index = selectParent(fitness);
                parents[i] = crossover(population[parent1Index], population[parent2Index]);
            }

            // Replace old population with new offspring
            population = parents;


            double bestFitness = Double.NEGATIVE_INFINITY;
            double[] bestParams = null;
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double[] engineParams = population[i];

                double fitnessik = (cylindersnumber*engineParams[0]*engineParams[1]*(PI/4)*(Math.pow(engineParams[2],2))*(engineParams[3]))/(2*33000);

                if (fitnessik > bestFitness) {
                    bestFitness = fitnessik;
                    bestParams = engineParams;
                }
            }
            System.out.println("Currentgen: "+generation+" Power: " + (cylindersnumber*bestParams[0]*bestParams[1]*(PI/4)*(Math.pow(bestParams[2],2))*(bestParams[3]))/(2*33000) );


        }

        // Find best engine parameter set and display results
        double bestFitness = Double.NEGATIVE_INFINITY;
        double[] bestParams = null;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] engineParams = population[i];

            double fitness = (cylindersnumber*engineParams[0]*engineParams[1]*(PI/4)*(Math.pow(engineParams[2],2))*(engineParams[3]))/(2*33000);

            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestParams = engineParams;
            }
        }

        System.out.println("Best engine parameters:");
        System.out.println("Mean effective pressure: " + bestParams[0]);
        System.out.println("Stroke: " + bestParams[1]);
        System.out.println("Bore: " + bestParams[2]);
        System.out.println("Revs: " + bestParams[3]);
        System.out.println("Power: " + (cylindersnumber*bestParams[0]*bestParams[1]*(PI/4)*(Math.pow(bestParams[2],2))*(bestParams[3]))/(2*33000) );

    }

    private static int selectParent(double[] fitness) {
        double totalFitness = 0.0;
        for (double f : fitness) {
            totalFitness += f;
        }

        double rand = random.nextDouble() * totalFitness;
        int index = 0;
        while (rand > 0) {
            rand -= fitness[index];
            index++;
        }
        index--;

        return index;
    }

    private static double[] crossover(double[] parent1, double[] parent2) {
        double[] offspring = new double[4];
        for (int i = 0; i < 4; i++) {
            offspring[i] = random.nextBoolean() ? parent1[i] : parent2[i];
        }

        return offspring;
    }



}

            
