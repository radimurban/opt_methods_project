package resource_allocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MicroGeneticAlgorithm {
    private static final int POPULATION_SIZE = 500;
    private static final int MAX_GENERATIONS = 100;
    private static final int RESOURCE_LIMIT = 100;
    private static final double CROSSOVER_RATE = 0.8;
    private static final double ELITISM_RATE = 0.2;
    private static final double MUTATION_RATE = 0.0;
    private static final Random RANDOM = new Random();

    private static class ResourceAllocation {
        private final int[] resources;
        private double fitness;

        public ResourceAllocation(int[] resources) {
            this.resources = resources;
        }

        public int[] getResources() {
            return resources;
        }

        public double getCost() {
            // Calculate the total cost of the supply chain
            // based on the allocated resources
            return resources[0] + resources[1] + resources[2];
        }

        public double getEfficiency() {
            // Calculate the efficiency of the supply chain
            // based on the allocated resources
            double input = resources[0] + resources[1];
            double output = resources[2];
            return output / input;
        }

        public double getFitness() {
            return fitness;
        }
    }

    public static void main(String[] args) {
        // Initialize the population
        ArrayList<ResourceAllocation> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] resources = new int[3];
            resources[0] = RANDOM.nextInt(RESOURCE_LIMIT);
            resources[1] = RANDOM.nextInt(RESOURCE_LIMIT - resources[0]);
            resources[2] = RESOURCE_LIMIT - resources[0] - resources[1];
            population.add(new ResourceAllocation(resources));
        }

        // Evolution loop
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate the fitness of each res in the population
            for (ResourceAllocation res : population) {
                res.fitness = 1 / (res.getCost() + res.getEfficiency());
            }

            // Sort the population by fitness in descending order
            Collections.sort(population, Comparator.comparingDouble(ResourceAllocation::getFitness).reversed());

            // Select the elite ress for the next generation
            int eliteSize = (int) (POPULATION_SIZE * ELITISM_RATE);
            ArrayList<ResourceAllocation> newPopulation = new ArrayList<>();
            for (int i = 0; i < eliteSize; i++) {
                newPopulation.add(population.get(i));
            }

            // Create the next generation
            for (int i = eliteSize; i < POPULATION_SIZE; i++) {
                ResourceAllocation parent1 = selectParent(population);
                ResourceAllocation parent2 = selectParent(population);

                ResourceAllocation child = crossover(parent1, parent2);

                if (RANDOM.nextDouble() < MUTATION_RATE) {
                    mutate(child);
                }

                newPopulation.add(child);
            }

            population = newPopulation;
        }

        // Print the best solution found
        ResourceAllocation bestres = population.get(0);
        System.out.println("Best allocation: " + Arrays.toString(bestres.getResources()));
        System.out.println("Cost: " + bestres.getCost());
        System.out.println("Efficiency: " + bestres.getEfficiency());
    }

    private static ResourceAllocation selectParent(ArrayList<ResourceAllocation> population) {
        // Select a parent using tournament selection
        int tournamentSize = 2;
        ArrayList<ResourceAllocation> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(RANDOM.nextInt(population.size())));
        }
        Collections.sort(tournament, Comparator.comparingDouble(ResourceAllocation::getFitness).reversed());
        return tournament.get(0);
    }

    private static ResourceAllocation crossover(ResourceAllocation parent1, ResourceAllocation parent2) {
        // Perform crossover using a two-point crossover
        int[] childResources = new int[3];
        int crossoverPoint1 = RANDOM.nextInt(3);
        int crossoverPoint2 = RANDOM.nextInt(3 - crossoverPoint1) + crossoverPoint1;
        for (int i = 0; i < crossoverPoint1; i++) {
            childResources[i] = parent1.getResources()[i];
        }
        for (int i = crossoverPoint1; i < crossoverPoint2; i++) {
            childResources[i] = parent2.getResources()[i];
        }
        for (int i = crossoverPoint2; i < 3; i++) {
            childResources[i] = parent1.getResources()[i];
        }
        return new ResourceAllocation(childResources);
    }

    private static void mutate(ResourceAllocation res) {
        // Perform mutation by randomly changing one resource allocation
        int index = RANDOM.nextInt(3);
        res.getResources()[index] = RANDOM.nextInt(RESOURCE_LIMIT);
    }
}
