<h1 class="library">path</h1>

## Synopsis

Functions to compute the shortest path between two elements in a graph directed.

All functions of this library require an argument that represent the vertices of the graph. This argument is a function evaluated as a list of elements that represent all elements that can be reached from the context element in a single step.

## Functons

<a name="between">

### between

`path:between(from, to, vert)`

Evaluates *from* and to

 as lists of elements, then computes the shortest from the first element of the former to the first element of the latter.

<a name="to">

### to

`path:to(to, vert)`

Evaluates *to* as a list of elements, then computes the shortest path from the context element to the first element in the result.

