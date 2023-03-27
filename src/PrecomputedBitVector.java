import edu.berkeley.cs.succinct.util.vector.IntVector;

class PrecomputedLongBitVector extends RankedBitVector {

    private final long[] relativeRank;

    /**
     *
     * @param data the input data to be compressed
     * @param start the start index of the data (inclusive)
     * @param end the end index of the data (exclusive)
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

    public int getRank(int i) {
        return (int) relativeRank[i];
    }

    public int size() {
        return relativeRank.length;
    }

    public long overhead() {
        return (long) relativeRank.length * Long.SIZE;
    }

}

class PrecomputedEfficientBitVector extends RankedBitVector {

    private IntVector relativeRank;

    /**
     *
     * @param data the input data to be compressed
     * @param start the start index of the data (inclusive)
     * @param end the end index of the data (exclusive)
     */
    public PrecomputedEfficientBitVector(boolean[] data, int start, int end) {
        int bitWidth = (int) Math.ceil(Math.log(end - start) / Math.log(2));
        relativeRank = new IntVector(data.length, bitWidth);
        int count = 0;
        for (int i = start; i < end; i++) {
            if (data[i]) {
                count++;
            }
            relativeRank.add(i - start, count);
        }
    }

    public int getRank(int i) {
        return relativeRank.get(i);
    }

    public int size() {
        return relativeRank.size();
    }

    public long overhead() {
        return relativeRank.overhead();
    }

}
