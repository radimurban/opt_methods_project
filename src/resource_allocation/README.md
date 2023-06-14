## Problem 1: Optimal Allocation of Resources (OAR)

A practical and real-world example of optimizing resource allocation in a supply chain to minimize costs and maximize efficiency could be in the production of a consumer electronics product, such as a smartphone. In this example, the supply chain involves several stages, including the sourcing of raw materials, manufacturing of components, assembly of the final product, and distribution to retailers.
We decided to implement the following micro genetic algorithm to solve the problem of optimal resource allocation to minimize costs and maximize efficiency. In our example we simplify this to determining the optimal number of raw materials to purchase and which products to manufacture from the available resources.


## The Implementation (method)
#### Population
There are two classes, Resource and Product. Each population contains n resources and m products. Each resource and product object is represented mainly through a vector (array) with integer entries. This approach is known as value encoding. A member of the class Resources represents all the resources we can acquire, the total cost of these resources and the fitness score. A product that the company can make is represented by the class Product. Each product has information about the selling price and the resources necessary to produce it. The price of a product must be at least the cost of the needed resources (otherwise it does not make sense economically). Both of these classes are managed by a Manager. The main pupouse of the Manager class is to make testing easier. You only have to create one Manager object with the corresponding arguments and it takes care of all the initialization for you. There is also the possibility to enter the mutation rate for testing purpouses to see how a micro genetic algorithm compares to a full genetic algorithm. Each Manager object has all the functions for the (micro)GA algorithm as well as important properties.


#### Fitness Function
Each Resource is evaluated by a fitness function that assigns a fitness score. Since we live in a capitalist society, our goal is to maximize the profit while minimizing the cost. We also want to produce as much as possible so we encourage filling up the warehouse with useful parts. The fitness is computed as follows:

$$ (maximal profit / total cost) * (number of parts in the warehouse - penalty for parts not used in manufacturing the products) * normalizationConstant. $$

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

Maximizing the profit or minimizing the cost leads to higher fitness score. The fitness function consists of the quotient of the profit and the cost multiplied with the overall available resources penalized with the number of unneeded resources. The evalProfit function computes the profit from products made out of the available resources. It starts with the most expensive product first and when there are no more enough resources it continues with the next most expensive product. The resources that are left at the end are not enough to create any of the products and are used as a penalty since the resources have cost but no profit.

The normalizationConstant is used to normalize the result of the fitness function to the interval [0,1]. For the sake of simplicity for this project the selling prices of the products are exactly three times the cost of their resources. The theoretical maximum value of the fitness function is 300. The left factor's maximum is reached when all the available resources are used to produce something and since the selling price is three times greater, the maximum is thus 3. For the second factor, the maximal value is attained when the resource warehouse is full (the resource capacity is fully reached) and there is no penalty for unnecessary resources. In the default case the resource capacity is set to 100. Since it is not always possible to fully utilize the resource capacity for all combinations of given products, we call the maximum value theoretical. The theoretical maximum can only be reached if it is possible to fill the resource capacity with penalty equal to zero. The result of the fitness function in the default case is multiplied by 1/300 (the reciprocal of the maximum theoretical value) for the reasons described above.

#### Parent Selection
We choose two parents in a tournament selection process. Both parents are Resource objects maximizing the fitness function among 5 randomly selected Resources. Selecting one parent will look like this:

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

#### Next Generation
The next generation cosists of the elite of the previous poulation and the children of any population members. The default elitism reate is 0.2 and this means that 20% of the best members of the population are propagated to the next generation (survival of the fittest). 

```java
int eliteSize = (int) (POPULATION_SIZE * ELITISM_RATE);
    Resource newPopulation[] = new Resource[POPULATION_SIZE];
    for (int i = 0; i < eliteSize; i++) {
        newPopulation[i] = man.getResource()[i];
}
```

Any member of the current population can generate childen. This decision was made to not lose too many values along the process, since a microGA only deals with crossover. Values from non-elitist parents may improve the overall fitness, e.g. by decreasing the number of unused materials.
For generating children, we use two-point crossover. In this problem, the multi-point crossover makes more sence that a single point crossover. All the products require different types and amoutns of resources and it is better to better to mix up the current values so there is more room for new combinations. The optimal number of crossover points depend also in the nature of the products. As an example, If each product only required one unit of one resource then also a single point crossover could be suffient. The more intrigue the products get the more you have to adjust the number of crossover points. In the comparisson with the standard genetic algorithm this makes sense. For the mGA we can only use the values that we get during the initialization and they cannot be mutated. This also means that a higher number of first generation resources is better (and necessary) for mGA beacause you have more values to choose from during crossover. For this implementation it is also necessary to keep track of the new cost and the number of resources. Upon creating the child we check in a do-while loop that the sum of the resources is under the limit.

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

For the comparison of a microGa and a full GA it is also possible to enter a mutation rate upon creating a Manager object for testing. The mutation of one randomly chosen value is implemented as follows in the code.

```java
do {
    child = man.crossover(parent1, parent2);
    // Only for testing purposes against a full GA
    // Default mutation rate is 0.0
    if (RANDOM.nextDouble() <= man.getMutationRate()) {
        man.mutate(child);
    }
} while (Arrays.stream(child.getResources()).sum() > RESOURCE_LIMIT);
```

```java
public void mutate(Resource res) {
    int index = random.nextInt(res.getResources().length);
    res.setCost(res.getCost() - res.getResources()[index] * prices[index]);
    res.getResources()[index] = random.nextInt(limit);
    res.setCost(res.getCost() + res.getResources()[index] * prices[index]);
}
```

The value of the mutation rate works as a threshold value in an if statement. If the generated double is less than or equal to the mutation rate, the method mutate is called. In the mutate method a random index is chosen. To maintain consistency of the values among the function calls, we have to subtract the cost of the current number of the resource from the total cost and then, after having the mutated value we again have to add the cost to the total cost. As mentioned above, this is purely for testing purposes agains the full GA and we are aware that a microGA does not implement any mutation.

#### Termination
This implementation of a microGA for resource allocation always terminates. The main while loop ends after at most a given number of generations (initialized in the code to be 100) or when the change in the fitness of the best resource allocation in the current and the previous generation is equal. This property is implemented in the code as follows due to possible machine numbers imprecision when computing with doubles (machine precision).

```java
if (bestres.getFitness() - oldFitness < 0.000001) {
	break;
}
```

#### Convergence
This microGA implementation converges to a local optimum. When comparing microGA and full GA with mutation rate 0.4 and 0.7, we see that mutation allows the algorithm to escape local minima but does not guarantee convergence to a global maximum. You can see the average of five simulations with different mutation rates.

<img src="/src/resource_allocation/Graphs/mutation_00.jpg" alt="Mutation rate 0.0" title="Mutation rate 0.0">

<img src="/src/resource_allocation/Graphs/mutation_04.jpg" alt="Mutation rate 0.4" title="Mutation rate 0.4">

<img src="/src/resource_allocation/Graphs/mutation_07.jpg" alt="Mutation rate 0.7" title="Mutation rate 0.7">


#### Sample Results with Mutation Rate 0.0 (microGA)
Setting:
POPULATION_SIZE = 400;
NUMBER_RESOURCES = 6;
NUMBER_PRODUCTS = 4;
MAX_GENERATIONS = 100;
RESOURCE_LIMIT = 100;
ELITISM_RATE = 0.2;

```
Generation: 0
Best allocation: [16, 12, 26, 3, 13, 23]
Cost: 509.5
Profit: 894.0
Penalty: 34.0
Fitness: 0.34508341511285573

Generation: 1
Best allocation: [16, 21, 6, 15, 17, 23]
Cost: 519.5
Profit: 1260.0
Penalty: 28.0
Fitness: 0.5659287776708374

Generation: 2
Best allocation: [16, 21, 6, 15, 13, 23]
Cost: 511.5
Profit: 1260.0
Penalty: 24.0
Fitness: 0.5747800586510264

Generation: 3
Best allocation: [16, 21, 6, 15, 13, 23]
Cost: 511.5
Profit: 1260.0
Penalty: 24.0
Fitness: 0.5747800586510264
```

```
Generation: 0
Best allocation: [21, 42, 6, 4, 6, 14]
Cost: 415.5
Profit: 777.0
Penalty: 44.0
Fitness: 0.305439229843562

Generation: 1
Best allocation: [21, 19, 10, 8, 13, 14]
Cost: 395.5
Profit: 960.0
Penalty: 27.0
Fitness: 0.4692793931731985

Generation: 2
Best allocation: [21, 19, 6, 8, 13, 14]
Cost: 369.5
Profit: 960.0
Penalty: 23.0
Fitness: 0.502300405953992

Generation: 3
Best allocation: [21, 19, 6, 8, 13, 14]
Cost: 369.5
Profit: 960.0
Penalty: 23.0
Fitness: 0.502300405953992
```

#### Sample Results with Muatation Rate 0.4
Setting:
POPULATION_SIZE = 400;
NUMBER_RESOURCES = 6;
NUMBER_PRODUCTS = 4;
MAX_GENERATIONS = 100;
RESOURCE_LIMIT = 100;
ELITISM_RATE = 0.2;

```
Generation: 0
Best allocation: [38, 24, 4, 6, 6, 16]
Cost: 391.0
Profit: 837.0
Penalty: 44.0
Fitness: 0.3567774936061381

Generation: 1
Best allocation: [38, 24, 7, 6, 6, 18]
Cost: 429.5
Profit: 985.5
Penalty: 39.0
Fitness: 0.4589057043073341

Generation: 2
Best allocation: [13, 24, 7, 6, 6, 18]
Cost: 392.0
Profit: 985.5
Penalty: 14.0
Fitness: 0.5028061224489796

Generation: 3
Best allocation: [13, 15, 7, 6, 6, 18]
Cost: 356.0
Profit: 985.5
Penalty: 5.0
Fitness: 0.5536516853932584

Generation: 4
Best allocation: [13, 15, 3, 6, 6, 18]
Cost: 330.0
Profit: 985.5
Penalty: 1.0
Fitness: 0.5972727272727273

Generation: 5
Best allocation: [12, 15, 3, 6, 6, 18]
Cost: 328.5
Profit: 985.5
Penalty: 0.0
Fitness: 0.6

Generation: 6
Best allocation: [12, 15, 3, 6, 6, 18]
Cost: 328.5
Profit: 985.5
Penalty: 0.0
Fitness: 0.6
```

```
Generation: 0
Best allocation: [22, 25, 8, 9, 25, 10]
Cost: 402.0
Profit: 660.0
Penalty: 60.0
Fitness: 0.21343283582089556

Generation: 1
Best allocation: [22, 25, 8, 9, 25, 11]
Cost: 411.5
Profit: 780.0
Penalty: 52.0
Fitness: 0.30328068043742407

Generation: 2
Best allocation: [19, 22, 1, 14, 7, 21]
Cost: 448.5
Profit: 1260.0
Penalty: 14.0
Fitness: 0.6555183946488294

Generation: 3
Best allocation: [19, 22, 1, 14, 7, 21]
Cost: 448.5
Profit: 1260.0
Penalty: 14.0
Fitness: 0.6555183946488294
```

#### Sample Results with Muatation Rate 0.7
Setting:
POPULATION_SIZE = 400;
NUMBER_RESOURCES = 6;
NUMBER_PRODUCTS = 4;
MAX_GENERATIONS = 100;
RESOURCE_LIMIT = 100;
ELITISM_RATE = 0.2;

```
Generation: 0
Best allocation: [13, 38, 9, 2, 25, 11]
Cost: 400.5
Profit: 717.0
Penalty: 50.0
Fitness: 0.2864419475655431

Generation: 1
Best allocation: [13, 18, 10, 5, 9, 23]
Cost: 433.0
Profit: 925.5
Penalty: 19.0
Fitness: 0.4203579676674365

Generation: 2
Best allocation: [28, 18, 10, 5, 9, 23]
Cost: 455.5
Profit: 1222.5
Penalty: 14.0
Fitness: 0.7067508232711306

Generation: 3
Best allocation: [28, 18, 10, 0, 9, 23]
Cost: 415.5
Profit: 1159.5
Penalty: 9.0
Fitness: 0.7348616125150422

Generation: 4
Best allocation: [28, 18, 10, 0, 9, 31]
Cost: 491.5
Profit: 1336.5
Penalty: 6.0
Fitness: 0.815768056968464

Generation: 5
Best allocation: [28, 18, 10, 0, 9, 28]
Cost: 463.0
Profit: 1336.5
Penalty: 3.0
Fitness: 0.8659827213822894

Generation: 6
Best allocation: [27, 18, 10, 0, 9, 27]
Cost: 452.0
Profit: 1336.5
Penalty: 1.0
Fitness: 0.8870575221238937

Generation: 7
Best allocation: [27, 18, 10, 0, 9, 27]
Cost: 452.0
Profit: 1336.5
Penalty: 1.0
Fitness: 0.8870575221238937
```

```
Generation: 0
Best allocation: [33, 18, 9, 13, 6, 20]
Cost: 486.0
Profit: 1080.0
Penalty: 39.0
Fitness: 0.4444444444444445

Generation: 1
Best allocation: [25, 18, 9, 13, 16, 17]
Cost: 465.5
Profit: 1140.0
Penalty: 30.0
Fitness: 0.5551020408163265

Generation: 2
Best allocation: [25, 18, 9, 6, 16, 17]
Cost: 409.5
Profit: 1077.0
Penalty: 23.0
Fitness: 0.5961416361416361

Generation: 3
Best allocation: [25, 18, 9, 5, 16, 24]
Cost: 468.0
Profit: 1251.0
Penalty: 17.0
Fitness: 0.7128205128205128

Generation: 4
Best allocation: [25, 19, 9, 5, 16, 25]
Cost: 481.5
Profit: 1371.0
Penalty: 10.0
Fitness: 0.8447144340602285

Generation: 5
Best allocation: [25, 19, 9, 5, 10, 25]
Cost: 469.5
Profit: 1371.0
Penalty: 4.0
Fitness: 0.866304579339723

Generation: 6
Best allocation: [25, 19, 9, 4, 10, 25]
Cost: 461.5
Profit: 1371.0
Penalty: 3.0
Fitness: 0.8813217768147346

Generation: 7
Best allocation: [22, 19, 9, 4, 10, 25]
Cost: 457.0
Profit: 1371.0
Penalty: 0.0
Fitness: 0.89

Generation: 8
Best allocation: [22, 19, 9, 4, 10, 25]
Cost: 457.0
Profit: 1371.0
Penalty: 0.0
Fitness: 0.89
```

As one can see from the data, GA with mutation generally finds better solutions. The advantage of mutation in the full GA, compared to microGA, lie predominantly in its ability to explore a wider search space and aid in escaping local optima. These factors contribute to the ability to find more diverse and potentially better solutions in the optimization process.