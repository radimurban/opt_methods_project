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
Optimizing the mean effective pressure(MEP), stroke, bore and revolutions per minute to achieve maximal power output of an engine. (Rene)

### Population
Let's assume that each candidate solution (i.e., chromosome) in the population is represented by a vector $p = MEP, stroke, bore , revs$ of design variables that define a part of an aircraft. The dimensions correspond to:

- $MEP$ is a measure of the average pressure exerted by the gases in the combustion chamber of an engine during the power stroke ($psi$)  $\in \[170; 280\]$
- $Stroke_length$ is the distance that the piston travels in the cylinder between the top dead center (TDC) and the bottom dead center (BDC) positions. ($ft$)  $\in \[0.27; 0.3\]$
- $Bore$ is the diameter of the cylinder in which the piston moves ($in$)  $\in \[2.9; 3.5\]$
- $Revs$ refer to the number of times an engine's crankshaft rotates in a given period of time. ($rpm$)  $\in \[0; 1\]$
For the example we choose a specific range of values that represent the specification of a typical diesel engine. 

### Fitness function
Each member of the population is evaluated using a fitness function that computes the power output of engine. In this case we want to maximize the power of an engine.
$$ 
\frac{(cylindersnumber*engineParams[0]*engineParams[1]*(PI/4)*(Math.pow(engineParams[2],2))*(engineParams[3]))}{(2*33000) }
$$


### Parent Selection
### Generating Children
### Stopping Criteria


