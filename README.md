# Project for _Optimization Methods for Engineers_

This repository implements generic Genetic Algorithm and applies it to 3 various problems.

## Genetic Algorithm Description

1. Create random population of $N$ individuals $p_n$ (initialization)
2. Select parents from population
3. Generate children using mutation and crossover simplicity: again $N$ individuals
4. Check stopping criteria. If not met: goto 2 .

## Problems that we apply the GA on:
### 1. Optimal allocation of resources (OAR)
Finding the optimal allocation of resources in a supply chain to minimize costs and maximize efficiency. (Zdenek)

A practical real-world example of optimizing resource allocation in a supply chain to minimize costs and maximize efficiency could be in the production of a consumer electronics product, such as a smartphone. In this example, the supply chain involves several stages, including the sourcing of raw materials, manufacturing of components, assembly of the final product, and distribution to retailers.
To minimize costs and maximize efficiency, the optimal allocation of resources in the supply chain must be determined. In our example we simplify this to determining the optimal number of raw materials to purchase and which products to manufacture from the available resources.

#### Population
There are two classes, Resource and Product. Each population contains n resources and m products. A member of the class Resources represents all the resources we can acquire, the total cost of these resources and the fitness score. A product that the company can make is represented by the class Product. Each product has information about the selling price and the resources neccessary to produce it. The price of a product must be at least the cost of the needed resources. Both of these classes are managed by a Manager. There you can find all the functions for the (micro)GA algorithm as well as the number of Resource individuals, the resource limit and the price per resource. 

#### Fitness Function
Each Resource is evaluated by a fitness function that assigns a fitness score. Since we live in a capitalist society, our goal is to maximize the profit while minimizing the cost. We also want to produce as much as possible so we encourage filling up the warehouse with useful parts. The fitness is computed as follows:

$$ (profit / res.getCost()) * (Arrays.stream(res.getResources()).sum() - penalty). $$

```java
public double[] evalProfit(Resource res) {
    	Arrays.sort(products, Comparator.comparingDouble(Product::getPrice).reversed());
    	double profit = 0.0;
    	int[] localCopy = Arrays.copyOf(res.getResources(), res.getResources().length);
        
    	for (int i = 0; i < products.length; i++) {
    		while(isAvailable(products[i].getNeededResources(), localCopy)) {
    			Product product = products[i];
    			Arrays.setAll(localCopy, j -> localCopy[j] - product.getNeededResources()[j]);
    			profit += product.getPrice();
    		}
    	}
    	double penalty = Arrays.stream(localCopy).sum();
    	return new double[]{profit, penalty};
    }
    
    private boolean isAvailable(int[] needed, int[] available) {
    	assert needed.length == available.length;
    	for (int i = 0; i < available.length; i++) {
    		if (available[i] - needed[i] < 0) {
    			return false;
    		}
    	}
    	return true;
    }
```

It is the quotient of the profit and the cost. Maximizing the profit or minimizing the cost leads to higher fitness score. Multiplying with the overall available resources penalized with the number of unneeded resources.

#### Parent Selection
We choose two parents. Both are Resources maximizing the fitness function among 5 randomly selected Resources. Selecting one parent will look like this:

```java
public Resource select() {
        Resource best = null;
        for (int i = 0; i < 5; i++) {
            Resource res = resources[random.nextInt(n)];
            if (best == null || res.getFitness() > best.getFitness()) {
                best = res;
            }
        }
        return best;
    }
```

#### Generating Children
We use two-point crossover. It is also neccessary to keep track of the new cost and the number of resources. Upon creating the child we check in a do-while loop that the sum of the resources is under the limit.

```java
public Resource crossover(Resource parent1, Resource parent2) {
        int[] childResources = new int[n];
        int crossoverPoint1 = random.nextInt(n);
        int crossoverPoint2 = random.nextInt(n - crossoverPoint1) + crossoverPoint1;
        double varCost = 0.0;
        
        for (int i = 0; i < crossoverPoint1; i++) {
            childResources[i] = parent1.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        for (int i = crossoverPoint1; i < crossoverPoint2; i++) {
            childResources[i] = parent2.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        for (int i = crossoverPoint2; i < n; i++) {
            childResources[i] = parent1.getResources()[i];
            varCost += childResources[i] * prices[i];
        }
        
        return new Resource(childResources, varCost);
    }
```

### 2. Optimizing the design of an aircraft (ODA)
Optimizing the design of an aircraft to maximize lift. (Radim)

#### Population
Let's assume that each candidate solution (i.e., chromosome) in the population is represented by a vector $p = V, S, \alpha , e, AR$ of design variables that define a part of an aircraft. The dimensions correspond to:

- $V$ is the speed of the aircraft ($m/s$)  $\in \[20; 350\]$
- $S$ is the wing area ($m^2$)  $\in \[60; 200\]$
- $\alpha$ is the angle of attack  $\in \[0; 1.57079633\]$
- $e$ is the Oswald efficiency factor  $\in \[0; 1\]$
- $AR$ is the wing aspect ratio  $\in \[5; 15\]$

#### Fitness function
Each chromosome is evaluated by a fitness function that computes its performanceand assigns a fitness score. In this case we want to compute the maximum lift.

**Lift ($L$):** The lift generated by the plane depends on the air density, the speed of the aircraft, and the geometry of the wing. The lift can be computed using the following formula:

$$
L = \frac{1}{2} * \rho * V^2 * S * \frac{(2 * \pi * \alpha)}{ (1 + (\pi * e * AR))}
$$

where:
- $\rho$ is the air density (kg/m^3) -> Assume constant at $1.293 kg/m^3$

#### Parent Selection
We will choose two parents. Both as chromosome maximizing the fitness function among 10 randomly selected chromosomes.
Randomness might make sense because for example of different conditions for the plane (meaning, the chromosome maximizing the fitness function might not maximize it in all conditions). 

Selecting one parent will look like this:

```java
Chromosome best = null;
    for (int i = 0; i < 10; i++) {
        Chromosome individual = individuals[random.nextInt(individuals.length)];
        if (best == null || individual.getFitness() > best.getFitness()) {
            best = individual;
        }
    }
return best;
```

#### Generating Children
We generate children as follows. To optimize but also to keep randomness in the process we _randomly_ mix the genes of the two parents. 

```java
double[] childGenes = new double[genes.length];
for (int i = 0; i < childGenes.length; i++) {
    childGenes[i] = random.nextBoolean() ?  this.getGenes()[i] : other.getGenes()[i];
}
return new Chromosome(childGenes);
```
#### Stopping Criteria
We will pre-define the number of generations we want to optimize over and abort after achieving this number.

#### Sample Results
Following result was obtained by having `POPULATION_SIZE = 150` and stopping criteria constant `MAX_GENERATIONS = 100`.
```
Generation: 1
Best fitness: 1.8135317035889294E7
Generation: 2
Best fitness: 2.1045501991996493E7
...
Generation: 7
Best fitness: 2.1045501991996493E7
Generation: 8
Best fitness: 2.154905052924889E7
Generation: 9
Best fitness: 2.154905052924889E7
Generation: 10
Best fitness: 2.154905052924889E7
...
...
...
Generation: 62
Best fitness: 4.3653154580234885E7
Generation: 63
Best fitness: 4.3653154580234885E7
...
...
Generation: 94
Best fitness: 4.552910518702196E7
Generation: 95
Best fitness: 4.552910518702196E7
Generation: 96
Best fitness: 4.552910518702196E7
Generation: 97
Best fitness: 4.552910518702196E7
Generation: 98
Best fitness: 4.552910518702196E7
Generation: 99
Best fitness: 4.552910518702196E7
Generation: 100
Best fitness: 4.552910518702196E7

```
Result is returning the maximized Lift $L$. At this point it would obviously easy to extract the genes which have maximized this property.
We can reason that the delievered result is somewhat reasonable by realizing that an airplane can weigh up to $600.000 kg$ corresponding rouhgly to needed lift of at least $6MN$. At generation 100, the best configuration has a lift force of roughly $45 MN$. That means there is an order of magnitude difference to the enforced minimum and in real life, ther would be many factors further limiting the lift force.

### 3. Optimizing the power of an engine
Optimizing the mean effective pressure(MEP), stroke, bore and revolutions per minute to achieve maximalpower output of an engine. (Rene)
#### Population
Let's assume that each candidate solution (i.e., chromosome) in the population is represented by a vector $p = MEP, stroke, bore , revs$ of design variables that define a part of an aircraft. The dimensions correspond to:

- $MEP$ is a measure of the average pressure exerted by the gases in the combustion chamber of an engine during the power stroke ($psi$)  $\in \[170; 280\]$
- $Stroke length$ is the distance that the piston travels in the cylinder between the top dead center (TDC) and the bottom dead center (BDC) positions. ($ft$)  $\in \[0.27; 0.3\]$
- $Bore$ is the diameter of the cylinder in which the piston moves ($in$)  $\in \[2.9; 3.5\]$
- $revs$ refer to the number of times an engine's crankshaft rotates in a given period of time. ($rpm$)  $\in \[0; 1\]$
For the example we choose a specific range of values that represent the specification of a typical diesel engine. 


