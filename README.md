Homework 2 solutions for CMSC 701: Computational Genomics (Spring 2023)
--
Language of the solutions: Java

### Rank and Select (Task 1 and 2)

The Rank and Select data structures extend the `bitvec.RankSelect` class. 
To use the SuccintBitVector, you can use the `bitvec.SuccinctBitVector` class.
More so, you can use the BitVectorFactory to create a SuccinctBitVector from an array of booleans.

Example:

```java
boolean[] bits = new boolean[]{false, true, false, true, true, false, false, false, true, false, false};
SuccinctBitVector bitVector = BitVectorFactory.createSuccinctBitVector(bits);

// Rank
int index = 5;
int rank = bitVector.getRank(index);

// Select
int rank = 3;
int select = bitVector.select(rank);
```

You can find a working example in the `RankSelectDemo` class.

### Sparse Array (Task 3)

The Sparse Array data structure can be found at `bitvec.SparseArray`.

```java
SparseArray<String> array = new SparseArray<>(10);

array.append("Hello", 1);
array.append("World", 4);
array.append("!", 7);
array.finalizeArray();

String value = array.getAtRank(1);
int rank = array.getAtIndex(5);
```

You can find a working example in the `SparseArrayDemo` class.

### Benchmarking

The benchmarking classes are located in the `benchmark` package.

You can find a working example of the benchmarking in the `BenchmarkMain` class.
Running this will generate two CSV files in the root directory of the project:
`rankSelectBenchmark.csv` and `sparseArrayBenchmark.csv'.

Once you have the CSV files, you can use the `make_plots_rank_select.py` to generate the plots for the Rank and Select data structures.
Similarly, you can use the `make_plots_sparse_array.py` to generate the plots for the Sparse Array data structure.

