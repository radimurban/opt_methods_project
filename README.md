# opt_methods_project

For our project, we will implement generic GA and apply it to 3 various problems.

## Genetic Algorithm

Assume search domain $\mathrm{D}_{\mathrm{s}} = \\{ \boldsymbol{p} \mid \boldsymbol{p} =$ bit string of length $L \\}$ representing the population.
We will generate bitstrings as representation of the population randomly. 

Fitness function evaluates strength for samples of the population (the higher the better)

$$
\begin{align}
F: 
& p \rightarrow \mathbb{R} \\ 
& p \mapsto \Sigma_{i=0}^{n-1}  p_i
& \text{where } n= \text{length of the bitstring} \quad \forall \boldsymbol{p} \in \mathrm{D}_{\text {s }}
\end{align}
$$


## Algorithm Description

1. create random population of $N$ individuals $\boldsymbol{p}_n$ (initialization)
1. select parents from population
2. generate children using mutation and crossover simplicity: again $N$ individuals
3. define and check stopping criteria. If not met: goto 2 .

## Problems for GA to be applied on:
1. Finding the optimal allocation of resources in a supply chain to minimize costs and maximize efficiency. (Zdenek)
2. Optimizing the design of an aircraft wing to maximize lift and minimize drag. (Radim)
3. Designing a portfolio of investments that maximizes returns while minimizing risk. (Rene)
