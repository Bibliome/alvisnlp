<h1 class="converter">ElementSimilarity</h1>

## Synopsis

Converts into a similarity metrics for <a href="../module/CompareElements" class="module">CompareElements</a> .

## String conversion


*  *annotation-strict* : strict comparison of annotation boundaries (if boundaries are equal, then 1, otherwise 0);
*  *annotation-relaxed* : relaxed comparison of annotation boundaries (if boundaries overlap, then 1, otherwise 0);
*  *annotation-jaccard* : Jaccard index of annotation boundaries (common offsets divided by all offsets);
*  *edit(KEY)* : levenshtein distance between the values of feature with key *KEY* ;
*  *KEY(FAIL)* : 1 if the values of feature with key *KEY* are equal, *FAIL* otherwise;
*  *KEY* : 1 if the values of feature with key *KEY* are equal, 0 otherwise.



## XML conversion

String conversion of the tag contents or the value of attribute *value* .

