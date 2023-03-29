import benchmark.BenchMarker;
import benchmark.RankSelectBenchMarkResult;
import benchmark.TestCase;
import bitvec.*;
import util.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class RankSelectDemo {

    public static void analyzeBitVec(RankedBitVector bitVector, boolean[] data, int[] setBitsGT) {
        int[] bitVectorRanks = bitVector.makeRankArray();
        int[] bitVectorSelects = bitVector.makeSelectArray();

        int[] trueRanks = Utils.makeRankArray(data);
        int[] trueSelects = Utils.makeSelectArray(data);
        var setBitIndices = Utils.makeSetBitIndexArray(bitVectorRanks);

        System.out.println("Ranks correct: " + Arrays.equals(bitVectorRanks, trueRanks));
        System.out.println("Selects correct: " + Arrays.equals(bitVectorSelects, trueSelects));
        System.out.println("setBitIndices correct: " + Arrays.equals(setBitIndices, setBitsGT));

        var diff = Utils.arrayDiff(bitVectorRanks, trueRanks);
        System.out.println("# Ranks mismatches: " + diff.size());

        System.out.printf("Overhead: %,d bits\n", bitVector.overhead());
    }

    public static void main(String[] args) {

        int bitVectorSize = 10000;
        boolean[] segment = new boolean[]{false, true, false, true, true, false, false, false, true, false, false};

        int[] rank = Utils.makeRankArray(segment);
        int[] select = Utils.makeSelectArray(segment);
        int[] select_rank = Utils.createSelectFromRanks(rank);
        System.out.println("R: " + Arrays.toString(rank));
        System.out.println("S: " + Arrays.toString(select));
        System.out.println("S: " + Arrays.toString(select_rank));

        // repeat the segment to make it longer
        boolean[] data = new boolean[bitVectorSize];
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


        System.out.println("\nPrecomputedLongArray");
        var precomputedEfficientBitVector = BitVectorFactory.createPrecomputedLongBitVector(data, 0, data.length);
        analyzeBitVec(precomputedEfficientBitVector, data, selects);

        System.out.println("\nPrecomputedBitVector");
        var precomputedBitVector = BitVectorFactory.createPrecomputedEfficientBitVector(data, 0, data.length);
        analyzeBitVec(precomputedBitVector, data, selects);

        System.out.println("\nbitvec.SuccinctChunk");
        var succinctChunk = BitVectorFactory.createSuccinctChunk(data, 0, data.length, 10);
        analyzeBitVec(succinctChunk, data, selects);

        System.out.println("\nbitvec.SuccinctBitVector");
        var succinctBitVec = BitVectorFactory.createSuccinctBitVector(data);
        analyzeBitVec(succinctBitVec, data, selects);

        System.out.println("\nbitvec.SuperSuccinctBitChunk");
        var superSuccinctChunk = BitVectorFactory.createSuperSuccinctChunk(data, 0, data.length, 10);
        analyzeBitVec(superSuccinctChunk, data, selects);

        System.out.println("\nbitvec.SuperSuccinctBitVector");
        var superSuccinctBitVector = BitVectorFactory.createSuperSuccinctBitVector(data);
        analyzeBitVec(superSuccinctBitVector, data, selects);

        System.out.println("\nNaiveJacobsonBitChunk");
        var jacobsonBitChunk = new NaiveJacobsonBitVector(data, 0, data.length, 1);
        analyzeBitVec(jacobsonBitChunk, data, selects);

        System.out.println("\nNaiveJacobsonBitVector");
        var jacobsonBitVector = new NaiveJacobsonBitVector(data);
        analyzeBitVec(jacobsonBitVector, data, selects);

    }
}
