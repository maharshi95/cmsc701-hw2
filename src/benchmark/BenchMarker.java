package benchmark;

import bitvec.BitVectorFactory;
import bitvec.BitVectorType;
import bitvec.RankedBitVector;
import bitvec.SparseArray;

public class BenchMarker {
    public static RankSelectBenchMarkResult benchMarkRankSelect(TestCase testCase, RankedBitVector bitVector, String bitVectorName, String testCaseName) {
        long tick = System.nanoTime();
        for (int idx : testCase.rankQueries) {
            assert idx < testCase.bits.length;
            bitVector.getRank(idx);
        }
        long rankQueryTime = (System.nanoTime() - tick) / 1000000;

        tick = System.nanoTime();
        for (int r : testCase.selectQueries) {
            bitVector.select(r);
        }
        long selectQueryTime = (System.nanoTime() - tick) / 1000000;

        return RankSelectBenchMarkResult.of(bitVectorName, testCaseName, rankQueryTime, selectQueryTime, bitVector.overhead(),
                testCase.bits.length, testCase.sparseRatio);
    }

    public static SparseArrayBenchmarkResult benchMarkSparseArray(TestCase testCase,
                                                                 SparseArray<String> sparseArray) {
        long tick;
        assert sparseArray != null : "sparseArray is null";
        System.out.println("Num Rank Queries: " + testCase.rankQueries.length);
        System.out.println("Num Select Queries: " + testCase.selectQueries.length);
        tick = System.nanoTime();
        for (int idx : testCase.rankQueries) {
            assert idx < testCase.bits.length;
            sparseArray.getAtIndex(idx);
        }
        long getAtIndexTime = (System.nanoTime() - tick) / 1000000;

        tick = System.nanoTime();
        for (int idx : testCase.rankQueries) {
            assert idx < testCase.bits.length;
            sparseArray.numElementsAt(idx);
        }
        long numElementsAtTime = (System.nanoTime() - tick) / 1000000;

        tick = System.nanoTime();
        for (int r : testCase.selectQueries) {
            sparseArray.getAtRank(r);
        }
        long getAtRankTime = (System.nanoTime() - tick) / 1000000;

        tick = System.nanoTime();
        for (int r : testCase.selectQueries) {
            sparseArray.getIndexOf(r);
        }
        long getIndexOfTime = (System.nanoTime() - tick) / 1000000;

        return SparseArrayBenchmarkResult.of(testCase.bits.length, testCase.sparseRatio,
                getAtRankTime, getAtIndexTime, getIndexOfTime, numElementsAtTime, sparseArray.overhead(),
                sparseArray.altOverhead());
    }

    public static RankSelectBenchMarkResult[] benchMarkRankSelect(TestCase[] testCases, BitVectorType bitVectorType,
                                                                  String testCaseName) {

        RankSelectBenchMarkResult[] results = new RankSelectBenchMarkResult[testCases.length];
        for (int i = 0; i < testCases.length; i++) {
            var bitVector = BitVectorFactory.createRankedBitVector(testCases[i].bits, bitVectorType);
            String bitVectorName = bitVectorType.toString();
            results[i] = benchMarkRankSelect(testCases[i], bitVector, bitVectorName, testCaseName);
        }
        return results;
    }
}
