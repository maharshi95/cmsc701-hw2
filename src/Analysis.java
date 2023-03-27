import java.util.Arrays;
import java.util.BitSet;

public class Analysis {
    public static void analyzeBitVec(RankedBitVector bitVector, boolean[] data, int[] selectsTrue) {
        int[] ranks = bitVector.makeRankArray();
        var selects = Utils.createSelectArray(ranks);
        int[] trueRanks = Utils.makeRankArray(data);
        var diff = Utils.arrayDiff(ranks, trueRanks);
        System.out.println("# Ranks mismatches: " + diff.size());
        System.out.println("Rank diffs: "+ diff);
//        System.out.println(Arrays.toString(selects));
//        System.out.println(Arrays.toString(selectsTrue));
        System.out.printf("Overhead: %,d bits", bitVector.overhead());
        System.out.println("Selects correct: " + Arrays.equals(selects, selectsTrue));
    }

    public static void main(String[] args) {
        boolean segment[] = new boolean[]{false, true, false, true, true, false, false, false, true, false, false};

        // repeat the segment to make it longer
        boolean[] data = new boolean[segment.length * 1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = segment[i % segment.length];
        }
        System.out.printf("Data length: %,d\n", data.length);
        System.out.println("# of 1s: " + Utils.makeRankArray(data)[data.length - 1]);


        BitSet bitSet = new BitSet(data.length);
        for (int i = 0; i < data.length; i++) {
            bitSet.set(i, data[i]);
        }

        // create select array from bitset
        int[] selects = new int[bitSet.cardinality()];
        for (int i = 0; i < bitSet.cardinality(); i++) {
            selects[i] = bitSet.nextSetBit(i == 0 ? 0 : selects[i - 1] + 1);
        }

        System.out.println("\nPrecomputedBitVector");
        PrecomputedEfficientBitVector precomputedEfficientBitVector = new PrecomputedEfficientBitVector(data, 0, data.length);
        analyzeBitVec(precomputedEfficientBitVector, data, selects);

        System.out.println("\nBrutePrecomputedBitVector");
        PrecomputedLongBitVector precomputedBitVector = new PrecomputedLongBitVector(data, 0, data.length);
        analyzeBitVec(precomputedBitVector, data, selects);

        System.out.println("\nSuccinctChunk");
        var succinctChunk = new SuccinctChunk(data, 0, data.length, 10);
        analyzeBitVec(succinctChunk, data, selects);

        System.out.println("\nSuccinctBitVector");
        var succinctBitVec = new SuccinctBitVector(data);
        analyzeBitVec(succinctBitVec, data, selects);

        System.out.println("\nJacobsonBitChunk");
        var jacobsonBitChunk = new NaiveJacobsonBitVector(data, 0, data.length, 1);
        analyzeBitVec(jacobsonBitChunk, data, selects);

        System.out.println("\nJacobsonBitVector");
        var jacobsonBitVector = new NaiveJacobsonBitVector(data);
        analyzeBitVec(jacobsonBitVector, data, selects);
    }
}
