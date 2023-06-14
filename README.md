# Project for _Optimization Methods for Engineers_

This repository implements generic Genetic Algorithm and applies it to 3 various problems.

## Genetic Algorithm Description

See more about GA and the general implementation [here](/src/general_ga_implementation).

1. Create random population of $N$ individuals $p_n$ (initialization)
2. Select parents from population
3. Generate children using mutation and crossover simplicity: again $N$ individuals
4. Check stopping criteria. If not met: goto 2 .

## Problem 1: Optimal allocation of resources (OAR)
Finding the optimal allocation of resources in a supply chain to minimize costs and maximize efficiency. Detailed problem description and solution are [here](/src/resource_allocation).

### Population
There are two classes, Resource and Product. Each population contains n resources and m products. 

### Fitness Function
Each Resource is evaluated by a fitness function that assigns a fitness score. Since we live in a capitalist society, our goal is to maximize the profit while minimizing the cost. We also want to produce as much as possible so we encourage filling up the warehouse with useful parts. 

### Parent Selection
We choose two parents. Both are Resources maximizing the fitness function among 5 randomly selected Resources.

### Generating Children
We use two-point crossover. It is also necessary to keep track of the new cost and the number of resources. Upon creating the child we check in a do-while loop that the sum of the resources is under the limit.

## Problem 2: Optimizing the design of an aircraft (ODA)

Optimizing the design of an aircraft to maximize lift. Detailed problem description and solution are [here](/src/airplane_design).

### Population
Let's assume that each candidate solution (i.e., chromosome) in the population is represented by a vector $p = V, S, \alpha , e, AR$ of design variables that define a part of an aircraft.

### Fitness function
Each chromosome is evaluated by a fitness function that computes its performanceand assigns a fitness score. In this case we want to compute the maximum lift.

### Parent Selection
We will choose two parents. Both as chromosome maximizing the fitness function among 10 randomly selected chromosomes.
Randomness might make sense because for example of different conditions for the plane (meaning, the chromosome maximizing the fitness function might not maximize it in all conditions).

### Generating Children
We generate children as follows. To optimize but also to keep randomness in the process we _randomly_ mix the genes of the two parents. 

### Stopping Criteria
We will pre-define the number of generations we want to optimize over and abort after achieving this number.

## Problem 3: Optimizing the power of an engine
Optimizing the mean effective pressure(MEP), stroke, bore and revolutions per minute to achieve maximal power output of an engine. 

### Population
Population is a set of chromosomes (essentially a vector) respresenting the mentioned parameters that influence power.

### Fitness function
Each member of the population is evaluated using a fitness function that computes the power output of engine. We will predefine the number of cylinders, which is also a part of the formula. In this case we want to maximize the power of an engine.


### Parent Selection
We use a Tournament Selection. It involves randomly selecting a subset of individuals from the population (we can choose the tournament size in the function call), and then choosing the best individual from that subset as a parent for the next generation. Here's how this is implemented in the code:

### Generating Children

We generate children by randomly mixing up the attributes of parents with the following program (Unifrom Crossover with randomly generated mask):


### Stopping Criteria
Maximal number of generations will be pre-defined.

### Sample Results

We ran the program five times with the following fixed parameters: number of cylinders: 4, constant "c" from the fitness formula: 2 (representing a four-stroke engine), tournament size: 5, number of generations: 100, and population size: 100, obtaining the following results: 
<img width="573" alt="image" src="https://github.com/radimurban/opt_methods_project/assets/115483491/ce0536c8-86ab-42be-8893-890eb00e32b2">
The effectiveness of the genetic algorithm becomes evident as we observe consistent improvement in the results with each successive generation. On average, we achieve a notable increase to approximately 185kw. However, it's worth noting that the algorithm's convergence rate is relatively slow due to the utilization of a small tournament size.

