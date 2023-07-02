# The (Micro) Genetic Algorithm - generally and specifically

In this folder, e implement the (Micro) Genetic Algorithm and use this implementation as basis
to solve three various problems.

## What is GA?

The genetic algorithm is a method for solving optimization problems that is based on natural selection. The genetic algorithm repeatedly improves a _population_ of _individuals_. At each iteration,
the GA selects parents from the current population of individuals (_ranking_), which then produce
the children for the next generation (_breeding_) using crossover and mutation (if this is omitted, we
talk about **Micro GA**). Over many generations, the population converges to an optimal solution by
replacing the weakest individuals by the children. This process can be easily described using the
following flowchart:

<img width="1673" alt="standard-micro-ga" src="https://github.com/radimurban/opt_methods_project/assets/78273894/ff311183-31f7-41c2-8118-088e2215380c">


To summarize: Population is the set of all the individuals (also called chromosomes). Each
chromosome carries some information about itself stored and segmented into genes.
We now implement the Micro GA on a high level by specifying the necessary aspects of the algorithm.
We use this implementation as a base to our concrete problems later on in the report.



## Population Representation & Initialization
The representation of the population can be encoded in many different ways. Most common are:
- **Binary/Octal/Hexadecimal Encoding**
- **Permutation Encoding** - having a predefined set of genes, each chromosome is a permutation
of this set and each gene is represented at most once.
- **Value Encoding** - genes simply represent the specific value.
- **Tree Encoding** - usually used to encode programs or expression. Representation of objects,
where order is important.

We initialize population of $N$ individuals $p_n$ (initialization), where $p_n$ is a bitstring of length $L$.

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
Based on the fitness function, these are many methods to select children, such as:
- **Tournament Selection** - choosing individuals randomly and selecting the best one among them.
- **Rank Selection** - sort candidate chromosomes by their fitness value. These ranks will be used in a roulette wheel style selection.
- **Roulette Wheel Selection** - let $S$ be a sum of the fitness function for all candidate chromosomes and $r \in[0, S]$ randomly generated number. Then iterate through candidates and compute the sum of their fitness. Choose the candidate for which the sum $=r$ was achieved.

In this simple implementation, we use the tournament methods. To choose one parent, we randomly choose $x$ (we use 5) individuals from the population and select the one with the highest fitness function.
```java
public Chromosome select() {
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


### Children Generation
Crossover\&Mutation specify how the algorithm uses the parents to obtain the children (in this implementation we omit the mutation step as we are focusing on Micro GA). For crossover, there are many options and the following are the most common ones:

- **Single/Multi-Point Crossover** - We choose a point $k \in [1,n-1]$ where $n$
 is number of genes and we mix the parents at this midpoint to obtain the children (offspring). For multi-point crossover just assume multiple "midpoints".

- **Uniform Crossover** - This is a generalization of the multi-point crossover. Assume a bitmask with the same length as the parents chromosomes. A $1$
 in the bitmask on the position $p$
 can represent that the $p$-th bit in the first child will be inherited from the first parent and in the second child from the second parent and vice versa for bit 0 in the bitmask.
- and other like Partially Mapped Crossover, Order Crossover, Shuffle Crossover...

We choose to implement **one-point crossover**, i.e. randomly generate a midpoint $\in [0;L]$ and generate child by taking bits from 0 to `midpoint` from 'parent1' and the rest from 'parent2'.

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

### Stopping Criteria
As seen in the flowchart above, stopping criteria is how we determine whether we are done. Factors
can be:
- **Pre-defined number of generations**
- **Reaching desired value of a fitness function**
- **Convergence** - essentially not improving by a significant amount for given number of gener-
ations.
For this implementation we only simply check pre-defined number of generations (=iterations)
```java
while (generation <= MAX_GENERATIONS) {

    /*
     * Main Algorithm Loop
    */
    
    generation++;
}
```
