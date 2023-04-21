# opt_methods_project

# Genetic Algorithm

Genetic Algorithm (GA): Core Ideas
Search domain $\mathrm{D}_{\mathrm{s}}=\{\boldsymbol{p} \mid \boldsymbol{p}=$ bit string of length $L\}$ 
Fitness function 

$$
\begin{align}
F: & p \rightarrow \mathbb{R} \quad 
\\ & p \mapsto \Sigma_{i=0}^{n-1}  p_i
\quad & \text{where } n=|p| \quad\forall \boldsymbol{p} \in \mathrm{D}_{\text {s }}
\end{align}
$$

coding real problem to bit strings is difficult! Exist invalid $\boldsymbol{p}$ ? not discussed here! (all $\boldsymbol{p}$ are considered as valid)
evaluation of $F(\boldsymbol{p})$ also excluded but assumed to be available

## Algorithm Description

1. create random population of $N$ individuals $\boldsymbol{p}_n$ (initialization)
1. select parents from population
2. generate children using mutation and crossover simplicity: again $N$ individuals
3. define and check stopping criteria. If not met: goto 2 .

## Problem Ideas:
1. Finding the optimal allocation of resources in a supply chain to minimize costs and maximize efficiency. (Zdenek)
2. Optimizing the design of an aircraft wing to maximize lift and minimize drag. (Radim)
3. Designing a portfolio of investments that maximizes returns while minimizing risk. (Rene)
