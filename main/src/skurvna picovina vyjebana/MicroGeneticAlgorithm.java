package neviem ;
import java.util.Random;

public class MicroGeneticAlgorithm {
	private static final double PI = Math.PI;
    private static final int POPULATION_SIZE = 1000;
    private static final int NUM_GENERATIONS = 1000;
  

    private static final double TARGET_POWER = 150.0;
    private static final double TARGET_FUEL_CONSUMPTION = 16.0;

    private static final double POWER_WEIGHT = 1.0;
    private static final double FUEL_WEIGHT = -1.0;

    private static final double MIN_VALVE_TIMING = 200.0;
    private static final double MAX_VALVE_TIMING = 300.0;
    private static final double MIN_FUEL_INJECTION_TIMING = 20.0;
    private static final double MAX_FUEL_INJECTION_TIMING = 40.0;
    private static final double MIN_IGNITION_TIMING = 10.0;
    private static final double MAX_IGNITION_TIMING = 40.0;
    private static final double MIN_COMPRESSION_RATIO = 8.0;
    private static final double MAX_COMPRESSION_RATIO = 14.0;

    private static final Random random = new Random();

    public static void main(String[] args) {
        // Initialize population
        double[][] population = new double[POPULATION_SIZE][4];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i][0] = random.nextDouble() * (MAX_VALVE_TIMING - MIN_VALVE_TIMING) + MIN_VALVE_TIMING;
            population[i][1] = random.nextDouble() * (MAX_FUEL_INJECTION_TIMING - MIN_FUEL_INJECTION_TIMING) + MIN_FUEL_INJECTION_TIMING;
            population[i][2] = random.nextDouble() * (MAX_IGNITION_TIMING - MIN_IGNITION_TIMING) + MIN_IGNITION_TIMING;
            population[i][3] = random.nextDouble() * (MAX_COMPRESSION_RATIO - MIN_COMPRESSION_RATIO) + MIN_COMPRESSION_RATIO;
        }

        // Run genetic algorithm
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            // Evaluate fitness of population
            double[] fitness = new double[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                double[] engineParams = population[i];
                double power = calculatePower(engineParams);
                double fuelConsumption = calculateFuelConsumption(engineParams);
                fitness[i] = POWER_WEIGHT * (power / TARGET_POWER) + FUEL_WEIGHT * (TARGET_FUEL_CONSUMPTION / fuelConsumption);
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
        }

        // Find best engine parameter set and display results
        double bestFitness = Double.NEGATIVE_INFINITY;
        double[] bestParams = null;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] engineParams = population[i];
            double power = calculatePower(engineParams);
            double fuelConsumption = calculateFuelConsumption(engineParams);
            double fitness = POWER_WEIGHT * (power / TARGET_POWER) + FUEL_WEIGHT * (TARGET_FUEL_CONSUMPTION / fuelConsumption);

            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestParams = engineParams;
            }
        }

        System.out.println("Best engine parameters:");
        System.out.println("Valve timing: " + bestParams[0]);
        System.out.println("Fuel injection timing: " + bestParams[1]);
        System.out.println("Ignition timing: " + bestParams[2]);
        System.out.println("Compression ratio: " + bestParams[3]);
        System.out.println("Power: " + calculatePower(bestParams));
        System.out.println("Fuel consumption: " + calculateFuelConsumption(bestParams));
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

    private static double calculatePower(double[] engineParams) {
    	 // Engine parameters
        double valveTiming = (engineParams[0]*PI)/180;
        double fuelInjectionTiming = (engineParams[1]*PI)/180;
        double ignitionTiming = (engineParams[2]*PI)/180;
        double compressionRatio = (engineParams[3]*PI)/180;
        double engineSpeed = 6000.0; // RPM

        // Engine geometry
        double bore = 86.0; // mm
        double stroke = 86.0; // mm
        double connectingRodLength = 147.7; // mm
        double crankRadius = stroke / 2.0;

        // Other constants
        double fuelEnergyDensity = 42.8e6; // J/kg
        double fuelAirRatio = 14.7;
        double heatCapacityRatio = 1.4;
        double airDensity = 1.2; // kg/m^3
        double airSpecificHeat = 1005.0; // J/(kg*K)
        double cylinderCount = 4.0;

        // Calculate engine displacement
        double cylinderVolume = PI * Math.pow(bore / 2.0, 2) * stroke;
        double displacement = cylinderVolume * cylinderCount;

        // Calculate air mass flow rate
        double airMassFlowRate = (airDensity * engineSpeed * displacement) / (4.0 * connectingRodLength);

        // Calculate fuel mass flow rate
        double fuelMassFlowRate = airMassFlowRate / fuelAirRatio;

        // Calculate fuel energy input
        double fuelEnergyInput = fuelMassFlowRate * fuelEnergyDensity;

        // Calculate cylinder pressure and temperature
        double crankAngle = (2.0 * PI * engineSpeed * ignitionTiming) / 60.0;
        double cylinderVolumeAtCrankAngle = displacement / 2.0 * (1.0 - Math.cos(crankAngle));
        double cylinderVolumeAtTopDeadCenter = cylinderVolume / compressionRatio;
        double cylinderPressure = (fuelEnergyInput / cylinderVolumeAtCrankAngle) * (1.0 + (heatCapacityRatio - 1.0) / 2.0 * (compressionRatio - 1.0));
        double cylinderTemperature = cylinderPressure / (airDensity * airSpecificHeat);

        // Calculate engine power
        double power = cylinderPressure * cylinderVolumeAtCrankAngle * engineSpeed / (4.0 * PI);
        power /= 745.7; // Convert power to horsepower

        return power;
    }

    private static double calculateFuelConsumption(double[] engineParams) {
    	  // Engine parameters
    	 double valveTiming = (engineParams[0]*PI)/180;
         double fuelInjectionTiming = (engineParams[1]*PI)/180;
         double ignitionTiming = (engineParams[2]*PI)/180;
         double compressionRatio = (engineParams[3]*PI)/180;
        double engineSpeed = 7000.0; // RPM

        // Engine geometry
        double bore = 86.0; // mm
        double stroke = 86.0; // mm
        double connectingRodLength = 147.7; // mm
        double crankRadius = stroke / 2.0;

        // Other constants
        double fuelEnergyDensity = 42.8e6; // J/kg
        double fuelAirRatio = 14.7;
        double heatCapacityRatio = 1.4;
        double airDensity = 1.2; // kg/m^3
        double airSpecificHeat = 1005.0; // J/(kg*K)
        double cylinderCount = 4.0;
        double densityConversionFactor = 1000.0; // kg/m^3 to g/L
        double distanceConversionFactor = 100000.0; // m to km

        // Calculate engine displacement
        double cylinderVolume = PI * Math.pow(bore / 2.0, 2) * stroke;
        double displacement = cylinderVolume * cylinderCount;

        // Calculate air mass flow rate
        double airMassFlowRate = (airDensity * engineSpeed * displacement) / (4.0 * connectingRodLength);

        // Calculate fuel mass flow rate
        double fuelMassFlowRate = airMassFlowRate / fuelAirRatio;

        // Calculate fuel energy input
        double fuelEnergyInput = fuelMassFlowRate * fuelEnergyDensity;

        // Calculate fuel consumption
        double fuelConsumption = (fuelMassFlowRate / densityConversionFactor) / ((engineSpeed / 60.0) * distanceConversionFactor / 1000.0) * 100.0;

        return fuelConsumption;
    }
}

            
