package airplane_design;

import java.util.Random;


public class ODA {
    private static final int POPULATION_SIZE = 150;
    private static final int MAX_GENERATIONS = 100;
    
    public static void main(String[] args) {
        
        // Initialize population
        Population population = new Population(POPULATION_SIZE);
        
        // Evaluate initial fitness
        population.evaluate();
        
        // Initialize the counter for stopping criteria
        int generation = 1;
        
        // Main evolution loop
        while (generation <= MAX_GENERATIONS) {
            
            // Print current generation
            System.out.print("Generation: " + generation);
            
            // Select parents
            Chromosome parent1 = population.select();
            Chromosome parent2 = population.select();
            
            // Crossover
            Chromosome[] children = parent1.crossover(parent2);
            
            // Replace x worst individuals with x children
            for (Chromosome child : children){
                population.replaceWorst(child);
            }
            
            // Evaluate new fitness
            population.evaluate();
            
            // Print best fitness
            System.out.print(" with best fitness: " + population.getBestFitnessIndividual().getFitness() + "\n");
            
            generation++;
        }
        
        population.getBestFitnessIndividual().getInfo();
    }
    
}

class Chromosome {
    
    /** Each chromosome has genes array storing its parameters like this:
     * genes[0] ... Speed of the aircarft
     * genes[1] ... Wing Area
     * genes[2] ... Angle of attack
     * genes[3] ... Oswald efficiency factor
     * genes[4] ... Wing Aspect Ratio
     * 
     * This corresponds to value encoding.
     * */
    private double[] genes;
    private double fitness;
    private Random random;
    
    // Constructor
    public Chromosome(double[] genes) {
        this.genes = genes;
        this.random = new Random();
    }
    
    // Getters and fitness-setter
    public double[] getGenes() {
        return genes;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    // Uniform Crossover with *random* mask
    public Chromosome[] crossover(Chromosome other) {
        double[] childGenes = new double[genes.length];
        double[] otherChildGenes = new double[genes.length];

        for (int i = 0; i < childGenes.length; i++) {
            boolean mask = random.nextBoolean();

            // Mask bit stays unchanged for both assignments => children inherit from different parents
            childGenes[i] = mask ? this.getGenes()[i] : other.getGenes()[i];
            otherChildGenes[i] = mask ? other.getGenes()[i] : this.getGenes()[i];
          
        }

        Chromosome first_child = new Chromosome(childGenes);
        Chromosome second_child = new Chromosome(otherChildGenes);
        Chromosome[] children = {first_child, second_child};

        return children;
    }
    
    public void getInfo() {
    	System.out.println("\n"
    					 + "------Component--------|----Value----\n");
    	System.out.println("Speed of the aircraift:   "+ (double)Math.round(this.getGenes()[0]*100)/100);
        System.out.println("Wing area:                "+ (double)Math.round(this.getGenes()[1]*100)/100);
        System.out.println("Angle of attack:          "+ (double)Math.round(this.getGenes()[2]*100)/100);
        System.out.println("Oswald eff. factor:       "+ (double)Math.round(this.getGenes()[3]*100)/100);
        System.out.println("Wing aspect ratio:        "+ (double)Math.round(this.getGenes()[4]*100)/100);
    }
    
}

class Population {
    
    public Chromosome[] individuals;
    private Random random;
    
    private double random(double min, double max) {
    	return Math.random() * (max - min) + min;
    }
    
    public Population(int size) {
        individuals = new Chromosome[size];
        random = new Random();
        for (int i = 0; i < size; i++) {
            double[] genes = new double[5];
            
            genes[0] = random(50,350); //SPEED
            genes[1] = random(60,200); //WING AREA
            genes[2] = random(0.1,0.43); //ANGLE OF ATTACK
            genes[3] = random(0.1,1); // OSWALD EFF. FACTOR
            genes[4] = random(5,15); // WING ASPECT RATIO
            
            
            individuals[i] = new Chromosome(genes);
        }
    }
    
    // Compute and assign fitness to all Individuals in Population
    public void evaluate() {
        for (Chromosome individual : individuals) {
            double fitness = evaluate(individual); //compute fitness for chrom.
            individual.setFitness(fitness); //assign it
        }
    }
    
    // Compute a fitness function for a specific individual
    private double evaluate(Chromosome individual) {
    	//Fitness function for lift
    	double[] genes = individual.getGenes();
    	double v = genes[0];
    	double s = genes[1];
    	double alpha = genes[2];
    	double e = genes[3];
    	double ar = genes[4];
    	
    	final double RHO = 1.293;
    	double cl = ((2*Math.PI*alpha)/(1 + (Math.PI*e*ar)));
    	double fitness = .5 * RHO * (v*v) * s * cl;

        return fitness;
    }
    
    public Chromosome select() {
    	// Choose five random individuals and choose the best one of them - Tournament Selection
        Chromosome best = null;

        for (int i = 0; i < 10; i++) {
            Chromosome individual = individuals[random.nextInt(individuals.length)];
            if (best == null || individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }

        return best;
    }
    
    // Find worst child index a replace that position with new child
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
    
    // Extract an individual with best fitness score
    public Chromosome getBestFitnessIndividual() {
        Chromosome best = individuals[0];
        for (Chromosome individual : individuals) {
            if (individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }
        return best;
    }
    
}

