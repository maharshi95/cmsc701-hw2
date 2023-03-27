import edu.berkeley.cs.succinct.util.vector.IntVector;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static int[] makeRankArray(boolean[] bits) {
        int [] rankArray = new int[bits.length];
        int rank = 0;
        for (int i = 0; i < bits.length; i++) {
            rank += bits[i] ? 1 : 0;
            rankArray[i] = rank;
        }
        return rankArray;
    }

    public static int[] createSelectArray(int[] rankArray) {
        int[] selectArray = new int[rankArray[rankArray.length - 1]];
        int selectIndex = 0;
        for (int i = 1; i < rankArray.length; i++) {
            if(rankArray[i] > rankArray[i - 1]) {
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

    static int getLookupIndex(boolean[] data, int start, int end) {
        int lookupIndex = 0;
        int base = 1;
        for (int j = start; j < end; j++) {
            lookupIndex += base * (data[j] ? 1 : 0);
            base *= 2;
        }
        return lookupIndex;
    }

    static int getSetBits(int number) {
        int count = 0;
        while (number > 0) {
            count++;
            number &= (number - 1);
        }
        return count;
    }

    public static IntVector[] createLookupTable(int nBits) {
        int nPatterns = (int) Math.pow(2, nBits);
        IntVector[] lookupTable = new IntVector[nPatterns];
        for (int i = 0; i < nPatterns; i++) {
            lookupTable[i] = createRankArray(i, nBits);
        }
        return lookupTable;
    }

    static IntVector createRankArray(int number, int nBits) {
        int nSetBits = getSetBits(number);
        IntVector rankArray = new IntVector(nBits, nSetBits);
        int rank = 0;
        int i = 0;
        while(i < nBits) {
            rank += (number & 1);
            rankArray.add(i++, rank);
            number /= 2;
        }
        return rankArray;
    }
}
