import benchmark.*;
import bitvec.*;
import util.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class BenchmarkMain {

    public static void saveBenchMarkResults(List<BenchmarkResult> benchmarkResults, String filename, String header) {
        try (var writer = new FileWriter(filename)) {
            writer.write(header);
            writer.write("\n");
            for (var result : benchmarkResults) {
                writer.write(result.toCSV());
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void benchMarkRankSelect(String filename) {
        int[] sizes = new int[]{100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000, 5000000, 10000000};
        BitVectorType[] bitVectorTypes = new BitVectorType[]{BitVectorType.PRECOMPUTED_LONG, BitVectorType.PRECOMPUTED_EFFICIENT, BitVectorType.SUCCINCT_BITVEC, BitVectorType.SUPER_SUCCINCT_BITVEC};

        List<BenchmarkResult> rankSelectBenchMarkresults = new ArrayList<>();
        for (var bitVectorType : bitVectorTypes) {
            System.out.println("BitVectorType: " + bitVectorType);
            TestCase[] testCases = new TestCase[sizes.length];
            for (int i = 0; i < sizes.length; i++) {
                testCases[i] = TestCase.createUniformTestCase(sizes[i]);
            }
            var results = BenchMarker.benchMarkRankSelect(testCases, bitVectorType, "Uniform");
            rankSelectBenchMarkresults.addAll(Arrays.asList(results));
        }
        // Write the results to a CSV file
        saveBenchMarkResults(rankSelectBenchMarkresults, filename, RankSelectBenchMarkResult.CSVHeader());
    }

    public static SparseArray<String> makeDummySparseArray(boolean[] bits) {
        var setBits = Utils.makeSetBitIndexArray(bits);
        SparseArray<String> sparseArray = new SparseArray<>(bits.length);
        for (int i = 0; i < setBits.length; i++) {
            sparseArray.append("Value " + i, setBits[i]);
        }
        sparseArray.finalizeArray();
        return sparseArray;
    }

    public static void benchmarkSparseArray(String filename) {
        int[] sizes = new int[]{100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000};
        double[] sparseRatio = new double[]{0.01, 0.05, 0.1, 0.3, 0.5, 0.75, 0.8, 0.9};

        List<BenchmarkResult> results = new ArrayList<>();
        for (var sparse : sparseRatio) {
            System.out.println("Sparse ratio: " + sparse);
            TestCase[] testCases = new TestCase[sizes.length];
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("Size: " + sizes[i]);
                testCases[i] = TestCase.createStandardTestCase(sizes[i], sparse);
                var sparseArray = makeDummySparseArray(testCases[i].bits);
                var result = BenchMarker.benchMarkSparseArray(testCases[i], sparseArray);
                results.add(result);
            }
        }

        // Write the results to a CSV file
        saveBenchMarkResults(results, filename, SparseArrayBenchmarkResult.CSVHeader());

    }

    public static void main(String[] args) {
        benchMarkRankSelect("rankSelectBenchmark.csv");
        benchmarkSparseArray("sparseArrayBenchmark.csv");
    }
}
