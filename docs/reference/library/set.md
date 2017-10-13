<h1 class="library">set</h1>

## Synopsis



## Functons

<a name="arg">

### arg

`set:arg:role(arg)`

Evaluates *arg* as a list of elements, then sets the context element argument with role *role* to the first element of *arg*. This function will not do anything if the context element is not a tuple.

<a name="arg">

### arg

`set:arg(role, arg)`

Evaluates *role* as a string, and *arg* as a list of elements, then sets the context element argument with role *role* to the first element of *arg*. This function will not do anything if the context element is not a tuple.

<a name="feat">

### feat

`set:feat:key(value)`

Evaluates *value* as a string, then adds a feature to the context element with key *key* and value *value*.

<a name="feat">

### feat

`set:feat(key, value)`

Evaluates *key* and *value* as strings, then adds a feature to the context element with key *key* and value *value*.

<a name="remove-arg">

### remove-arg

`set:remove-arg:role()`

Removes the argument with role *role* from the context element. This function will not do anything if the context element is not a tuple.

<a name="remove-arg">

### remove-arg

`set:remove-arg(role)`

Evaluates *role* as a string, removes the argument with role *role* from the context element. This function will not do anything if the context element is not a tuple.

<a name="remove-feature">

### remove-feature

`set:remove-feature:key()`

Removes all features with key *key* from the context element.

