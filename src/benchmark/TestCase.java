package benchmark;

import bitvec.SparseArray;
import util.Utils;

import java.util.*;

public class TestCase {

    private static final int DEFAULT_N_QUERIES = 50000;
    public final boolean[] bits;
    public final int[] rankQueries;
    public final int[] selectQueries;

    public final double sparseRatio;

    public TestCase(boolean[] bits, int[] rankQueries, int[] selectQueries) {
        this.bits = bits;
        this.rankQueries = rankQueries;
        this.selectQueries = selectQueries;
        this.sparseRatio = Utils.countBits(bits) / (double) bits.length;
    }

    public static boolean[] makeBitArray(int N, double sparseRatio, Random random) {
        int[] setBits = Utils.reservoirSample(N, (int) (N * sparseRatio), random);
        Set<Integer> setBitsSet = new HashSet<>();
        for (int i : setBits) {
            setBitsSet.add(i);
        }
        boolean[] bits = new boolean[N];
        for (int i = 0; i < N; i++) {
            bits[i] = setBitsSet.contains(i);
        }
        return bits;
    }

    public static TestCase createRandomTestCase(int N, int nRankQueries, int nSelectQueries, double sparseRatio) {
        Random random = new Random(42);

        var bits = makeBitArray(N, sparseRatio, random);

        var rankQueries = new int[nRankQueries];
        for (int i = 0; i < nRankQueries; i++) {
            rankQueries[i] = random.nextInt(N);
        }

        int maxRank = Utils.countBits(bits);
        var selectQueries = new int[nSelectQueries];
        for (int i = 0; i < nSelectQueries; i++) {
            selectQueries[i] = random.nextInt(maxRank) + 1;
        }
        return new TestCase(bits, rankQueries, selectQueries);
    }

    public static TestCase createStandardTestCase(int N, double sparseRatio) {
        return createRandomTestCase(N, DEFAULT_N_QUERIES, DEFAULT_N_QUERIES, sparseRatio);
    }

    public static TestCase createSparseTestCase(int N) {
        return createStandardTestCase(N, 0.05);
    }

    public static TestCase createDenseTestCase(int N) {
        return createStandardTestCase(N, 0.95);
    }

    public static TestCase createUniformTestCase(int N) {
        return createStandardTestCase(N, 0.5);
    }
}
