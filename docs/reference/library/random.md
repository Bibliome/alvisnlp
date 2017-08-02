<h1 class="library">random</h1>

## Synopsis

Pseudo-random number generator.

## Functons

<a name="gaussian">

### gaussian

`random:gaussian()`

Generates a random double. The returned value behaves like a random variable with mean 0.0 and standard deviation 1.0.

<a name="init">

### init

`random:init(seed)`

Initializes the current RNG with the specified *seed* evaluated as a double.

<a name="init">

### init

`random:init()`

Initializes the current RNG with a seed generated from the internal clock. The value of the seed is returned.

<a name="next">

### next

`random:next()`

Generates a random integer, double or boolean according to the context.

<a name="next">

### next

`random:next(max)`

Evaluates *max* as an integer and generates a random integer, double or boolean according to the context. If this function must return an integer, then the result is between 0 (inclusive) and *max* (exclusive).

<a name="seed">

### seed

`random:seed()`

Returns the seed of the current RNG.

<a name="shuffle">

### shuffle

`random:shuffle(it)`

Evaluates *it* as a list of elements, and returns this list randomly shuffled.

