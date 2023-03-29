package util;

import bitvec.RankedBitVector;
import edu.berkeley.cs.succinct.util.vector.IntVector;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {

    /**
     * Returns the maximum bit width required to represent a number in the range [0, range)
     * @param range the range of numbers to be represented
     * @return the bit width
     */
    public static int getMaxBitWidth(int range) {
        return (int) Math.ceil(Math.log(range + 1) / Math.log(2));
    }
    public static int[] makeRankArray(boolean[] bits) {
        int[] rankArray = new int[bits.length];
        int rank = 0;
        for (int i = 0; i < bits.length; i++) {
            rank += bits[i] ? 1 : 0;
            rankArray[i] = rank;
        }
        return rankArray;
    }

    public static int countBits(boolean[] bits, int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += bits[i] ? 1 : 0;
        }
        return sum;
    }

    public static int countBits(boolean[] bits) {
        return countBits(bits, 0, bits.length);
    }

    public static int[] makeSelectArray(boolean[] bits) {
        int nSetBits = countBits(bits);
        int[] selectArray = new int[nSetBits + 1];
        int selectIndex = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                selectArray[selectIndex++] = i - 1;
            }
        }
        selectArray[selectIndex] = bits.length - 1;
        return selectArray;
    }

    public static int[] createSelectFromRanks(int[] ranks) {
        int maxRank = ranks[ranks.length - 1];
        int[] selectArray = new int[maxRank + 1];
        int selectIndex = 0;
        for (int i = 1; i < ranks.length; i++) {
            if (ranks[i] > ranks[i - 1]) {
                selectArray[selectIndex++] = i - 1;
            }
        }
        selectArray[selectIndex] = ranks.length - 1;
        return selectArray;
    }

    public static int[] makeSetBitIndexArray(boolean[] bits) {
        int nSetBits = countBits(bits);
        int[] setBitIndexArray = new int[nSetBits];
        int setBitIndex = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                setBitIndexArray[setBitIndex++] = i;
            }
        }
        return setBitIndexArray;
    }
    public static int[] makeSetBitIndexArray(int[] rankArray) {
        int[] selectArray = new int[rankArray[rankArray.length - 1]];
        int selectIndex = 0;
        for (int i = 1; i < rankArray.length; i++) {
            if (rankArray[i] > rankArray[i - 1]) {
                selectArray[selectIndex++] = i;
            }
        }
        return selectArray;
    }

    public static Map<Integer, Integer> arrayDiff(int[] a1, int[] a2) {
        int n = Math.max(a1.length, a2.length);
        int[] diff = new int[n];
        for (int i = 0; i < n; i++) {
            if (i >= a1.length)
                diff[i] = -a2[i];
            else if (i >= a2.length)
                diff[i] = a1[i];
            else
                diff[i] = a1[i] - a2[i];
        }
        Map<Integer, Integer> diffMap = new HashMap<>();
        for (int i = 0; i < diff.length; i++) {
            if (diff[i] != 0) {
                diffMap.put(i, diff[i]);
            }
        }
        return diffMap;
    }

    public static int getRank(int i, int chunkSize, RankedBitVector[] chunks, IntVector chunkRanks) {
        int chunkIndex = i / chunkSize;
        int chunkOffset = i % chunkSize;
        int chunkRank = chunkIndex > 0 ? chunkRanks.get(chunkIndex - 1) : 0;
        int relativeRank = chunks[chunkIndex].getRank(chunkOffset);
        return chunkRank + relativeRank;
    }

    public static int getLookupIndexBigEndian(boolean[] data, int start, int end) {
        int lookupIndex = 0;
        int base = 1;
        for (int j = start; j < end; j++) {
            lookupIndex += base * (data[j] ? 1 : 0);
            base *= 2;
        }
        return lookupIndex;
    }

    public static IntVector createRankArray(int number, int nBits) {
        int nSetBits = Math.max(Integer.bitCount(number), 1);
        IntVector rankArray = new IntVector(nBits, nSetBits);
        int rank = 0;
        int i = 0;
        while (i < nBits) {
            rank += (number & 1);
            rankArray.add(i++, rank);
            number /= 2;
        }
        return rankArray;
    }

    public static int[] reservoirSample(int n, int k, Random random)  {

        int[] data = new int[n];
        int[] reservoir = new int[k];

        for (int i = 0; i < n; i++)
            data[i] = i;

        int i;
        for (i = 0; i < k; i++)
            reservoir[i] = data[i];
        while(i < data.length) {
            int j = random.nextInt(i + 1);
            if (j < k)
                reservoir[j] = data[i];
            i++;
        }
        return reservoir;
    }
}
