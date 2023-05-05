## Problem 1: Optimal allocation of resources (OAR)

A practical and real-world example of optimizing resource allocation in a supply chain to minimize costs and maximize efficiency could be in the production of a consumer electronics product, such as a smartphone. In this example, the supply chain involves several stages, including the sourcing of raw materials, manufacturing of components, assembly of the final product, and distribution to retailers.
I decided to implement the following micro genetic algorithm to solve the problem of optimal resource allocation to minimize costs and maximize efficiency. In our example we simplify this to determining the optimal number of raw materials to purchase and which products to manufacture from the available resources.


My plan:
1. Population - Each individual (Resource) represents n different resources we can acquire
2. Product - represents a product the company can produce, each product has an array with the type and number of resources it needs
3. The price for a product is at least the sum of the cost of the resources needed to create it
4. To avoid maximizing to infinity there is a limit (warehouses have limited capacity)
5. The fitness is represented as a ratio between the profit from the products and the cost of the resources
6. Additionaly, the fitness is miltiplied by the sum of the resources to encourage increasing the number of resources

Some thoughts:
1. Mutation, even though we should not use it, woeks really well because it allows to change some entries and maybe
create a better configuration. With just crossover you only work with the values you get at the beginning. Thus it is
better to have more individuals (more values to choose from).

## The Implementation (method)
#### Population
There are two classes, Resource and Product. Each population contains n resources and m products. A member of the class Resources represents all the resources we can acquire, the total cost of these resources and the fitness score. A product that the company can make is represented by the class Product. Each product has information about the selling price and the resources necessary to produce it. The price of a product must be at least the cost of the needed resources (otherwise it does not make sense economically). Both of these classes are managed by a Manager. The main pupouse of the Manager class is to make testing easier. You only have to create one Manager object with the corresponding arguments and it takes care of all the initialization for you. There is also the possibility to enter the mutation rate for testing purpouses to see how a micro genetic algorithm compares to a full genetic algorithm. Each Manager object has all the functions for the (micro)GA algorithm as well as important properties.


#### Fitness Function
Each Resource is evaluated by a fitness function that assigns a fitness score. Since we live in a capitalist society, our goal is to maximize the profit while minimizing the cost. We also want to produce as much as possible so we encourage filling up the warehouse with useful parts. The fitness is computed as follows:

$$ (maximal profit / total cost) * (number of parts in the warehouse - penalty for parts not used in manufacturing the products). $$

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

It is the quotient of the profit and the cost. Maximizing the profit or minimizing the cost leads to higher fitness score. Multiplying with the overall available resources penalized with the number of unneeded resources. The evalProfit function computes the profit from products made out of the available resources. It starts with the most expensive product first and when there are no more enough resources it continues with the next most expensive product. The resources that are not left at the end are not enough to create any of the products and are used as a penalty since the resources have cost but no profit.

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
We use two-point crossover. For this problem, the K-point crossover makes more sence that a single point crossover. All the products require different types and amoutns of resources and it is better to better to mix up the current values so there is more room for new combinations. The optimal number of crossover points depend also in the nature of the products. As an example, If each product only required one unit of one resource then also a single point crossover could be suffient. The more intrigue the products get the more you have to adjust the number of crossover points. In the comparisson with the standard genetic algorithm this makes sense. For the mGA we can only use the values that we get during the initialization and they cannot be mutated. This also means that a higher number of first generation resources is better (and necessary) for mGA beacause you have more values to choose from during crossover. For this implementation. it is also necessary to keep track of the new cost and the number of resources. Upon creating the child we check in a do-while loop that the sum of the resources is under the limit.

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


#### Sample Results
```
Generation: 0
Best allocation: [12, 14, 7]
Cost: 41.0
Profit: 115.5
Penalty: 5.0
Fitness: 78.8780487804878

Generation: 1
Best allocation: [12, 14, 16]
Cost: 59.0
Profit: 162.0
Penalty: 10.0
Fitness: 87.86440677966101

Generation: 2
Best allocation: [12, 14, 16]
Cost: 59.0
Profit: 162.0
Penalty: 10.0
Fitness: 87.86440677966101
...
...
...
Generation: 98
Best allocation: [12, 14, 16]
Cost: 59.0
Profit: 162.0
Penalty: 10.0
Fitness: 87.86440677966101

Generation: 99
Best allocation: [12, 14, 16]
Cost: 59.0
Profit: 162.0
Penalty: 10.0
Fitness: 87.86440677966101

Generation: 100
Best allocation: [12, 14, 16]
Cost: 59.0
Profit: 162.0
Penalty: 10.0
Fitness: 87.86440677966101
```