# Genetic Algorithm (GA)

In this folder, we implement the general version of the GA, as described below.

## What is GA?

> The genetic algorithm is a method for solving both constrained and unconstrained optimization problems that is based on natural selection, 
the process that drives biological evolution. The genetic algorithm repeatedly modifies a population of individual solutions. 
At each step, the genetic algorithm selects individuals from the current population to be parents and uses them to produce the children 
for the next generation. Over successive generations, the population "evolves" toward an optimal solution.

from [MathWorks.com](https://www.mathworks.com/help/gads/what-is-the-genetic-algorithm.html)

![](https://www.eejournal.com/wp-content/uploads/2020/07/max-0040-02-genetic-algorithms.png)

## Algorithm Description
```
create population
while (stopping criteria not met)
  select 2 parents from population
  generate children
  replace worst individual with child in the population
extraxct the best individual
```
## Aspects of the algorithm

### Population Initilization 
Random population of $N$ individuals $p_n$ (initialization), where $p_n$ is a bitstring of length $L$.
```java
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
```
### Fitness Function
This is the metric for choosing the parents and individual to be replaced by the child(ren). In this implementation the fitness function is the number of bits in individual that are 1. 

$$
F: p_n \mapsto \sum_{i = 0}^{L-1} p_{n_i} \quad \text{where } p_{n_i} \text{ is the i-th bit in } p_n
$$

### Parents Selection
Generally speaking, there are many ways to select parents. In this simple implementation, we use the tournament methods. To choose one parent, we randomly choose $x$ individuals from the population and select the one with the highest fitness function. 
```java
public Chromosome select()í 
  Chromosome best = null;
  for (int i = 0; i < 5; i++) {
      Chromosome individual = individuals[random.nextInt(individuals.length)];
      if (best == null || individual.getFitness() > best.getFitness()) {
          best = individual;
      }
  }
  return best;
}
```
We then replace this new child with the weakest individual (individual) with the lowest fitness function.
```java
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
```

### Children Generation
We choose to implement one-point crossover, i.e. randomly generate a midpoint $\in \[0;L]$ and generate child by taking bits from 0 to midpoint from parent1 and the rest from parent2.

```java
public Chromosome crossover(Chromosome other) {
    int[] childGenes = new int[genes.length];
    int midpoint = random.nextInt(genes.length);
    for (int i = 0; i < midpoint; i++) {
        childGenes[i] = genes[i];
    }
    for (int i = midpoint; i<genes.length; i++) {
      childGenes[i] = other.genes[i];
    }
    return new Chromosome(childGenes);
}
```

### Stopping Criteria
For this implementation we only simply check pre-defined number of generations (=iterations).

```java
while (generation <= MAX_GENERATIONS) {

    /*
     * Main Algorithm Loop
    */
    
    generation++;
}
```
