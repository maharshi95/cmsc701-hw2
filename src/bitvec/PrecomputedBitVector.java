package bitvec;

import edu.berkeley.cs.succinct.util.vector.IntVector;
import util.Utils;

class PrecomputedLongBitVector extends RankedBitVector {

    private final long[] relativeRank;

    /**
     * @param data  the input data to be compressed
     * @param start the start index of the data (inclusive)
     * @param end   the end index of the data (exclusive)
     */
    public PrecomputedLongBitVector(boolean[] data, int start, int end) {
        relativeRank = new long[end - start];
        int count = 0;
        for (int i = start; i < end; i++) {
            if (data[i]) {
                count++;
            }
            relativeRank[i - start] = count;
        }
    }

    public int getRank(int index) {
        return (int) relativeRank[index];
    }

    public int size() {
        return relativeRank.length;
    }

    public long overhead() {
        return (long) relativeRank.length * Long.SIZE;
    }

}

class PrecomputedEfficientBitVector extends RankedBitVector {

    private final IntVector ranks;
    private final int size;

    /**
     * @param data  the input data to be compressed
     * @param start the start index of the data (inclusive)
     * @param end   the end index of the data (exclusive)
     */
    public PrecomputedEfficientBitVector(boolean[] data, int start, int end) {
        int setBits = Utils.countBits(data, start, end);
        int bitWidth = Utils.getMaxBitWidth(setBits);

        size = end - start;
        ranks = new IntVector(size, bitWidth);

        int count = 0;
        for (int i = 0; i < ranks.size(); i++) {
            if (i + start < end && data[start + i]) {
                count++;
            }
            ranks.add(i, count);
        }
    }

    public PrecomputedEfficientBitVector(boolean[] data) {
        this(data, 0, data.length);
    }

    public int getRank(int index) {
        return ranks.get(index);
    }

    public int size() {
        return size;
    }

    public long overhead() {
        return ranks.overhead() + Integer.SIZE; // size
    }

}
