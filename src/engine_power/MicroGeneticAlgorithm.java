package engine_power;
import java.util.Random;

public class prva {
    private static final double PI = Math.PI;
    private static final int POPULATION_SIZE = 100;
    private static final int NUM_GENERATIONS = 100;
    private static final int TOURNAMENT_SIZE = 5; // Tournament size for selection

    private static final int cylindersnumber = 4;

    private static final double MIN_VALVE_MEP = 170.0; // psi
    private static final double MAX_VALVE_MEP = 280.0;
    private static final double MIN_STROKE_LENGTH = 0.27; // ft
    private static final double MAX_STROKE_LENGTH = 0.3;
    private static final double MIN_BORE = 2.9; // in
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

                fitness[i] = calculateFitness(engineParams);
            }

            // Select parents for crossover
            double[][] parents = new double[POPULATION_SIZE][4];
            parents[0] = tournamentSelection(population, fitness, TOURNAMENT_SIZE);
            parents[1] = tournamentSelection(population, fitness, TOURNAMENT_SIZE);
            double[] offspring = crossover (parents[0], parents[1]);
            // replace worst
            int worstIndex = 0;
            double worstFitness = Double.MAX_VALUE;
            for (int i = 0; i < POPULATION_SIZE; i++) {
                if (fitness[i] < worstFitness) {
                    worstIndex = i;
                    worstFitness = fitness[i];
                }
            }
            population[worstIndex] = offspring;



            // Output best fitness in the current generation
            double bestFitness = Double.NEGATIVE_INFINITY;
            double[] bestParams = null;
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double[] engineParams = population[i];
                double fitnessik = calculateFitness(engineParams);

                if (fitnessik > bestFitness) {
                    bestFitness = fitnessik;
                    bestParams = engineParams;
                }
            }
            System.out.println(bestFitness + ",");
        }

        // Find best engine parameter set and display results
        double bestFitness = Double.NEGATIVE_INFINITY;
        double[] bestParams = null;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] engineParams = population[i];
            double fitness = calculateFitness(engineParams);

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
        System.out.println("Power: " + calculateFitness(bestParams));

    }



    private static double[] crossover(double[] parent1, double[] parent2) {
        double[] offspring = new double[4];
        for (int i = 0; i < 4; i++) {
            offspring[i] = random.nextBoolean() ? parent1[i] : parent2[i];
        }

        return offspring;
    }

    private static double calculateFitness(double[] engineParams) {
        return (cylindersnumber * engineParams[0] * engineParams[1] * (PI / 4) * (Math.pow(engineParams[2], 2))
                * engineParams[3]) / (2 * 33000);
    }

    private static double[] tournamentSelection(double[][] population, double[] fitness, int tournamentSize) {
        int[] tournamentIndices = new int[tournamentSize];

        // Randomly select individuals for the tournament
        for (int i = 0; i < tournamentSize; i++) {
            tournamentIndices[i] = random.nextInt(population.length);
        }

        // Find the fittest individual in the tournament
        int fittestIndex = tournamentIndices[0];
        double fittestFitness = fitness[fittestIndex];
        
        for (int i = 1; i < tournamentSize; i++) {
            int currentIndex = tournamentIndices[i];
            double currentFitness = fitness[currentIndex];

            if (currentFitness > fittestFitness) {
                fittestIndex = currentIndex;
                fittestFitness = currentFitness;
            }
        }

        // Return the fittest individual
        return population[fittestIndex];
    }
}

