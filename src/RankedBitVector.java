public abstract class RankedBitVector {
    /**
     * Abstract method to return the 1-rank (inclusive) at position i.
     * This method must be implemented by all subclasses.
     * @param i the position
     * @return the 1-rank at position i
     */
    public abstract int getRank(int i);

    /**
     * Returns the bit-rank (inclusive) at position i.
     * @param i the position
     * @param bit the bit (true for 1, false for 0)
     * @return the bit-rank at position i
     */
    public int getRank(boolean bit, int i) {
        return bit ? getRank(i) : i - getRank(i);
    }

    public abstract int size();

    public abstract long overhead();

    public int select(int num) {
        // Use binary search to find the first index that has the rank greater than num
        int n = this.size();
        if (num > getRank(n - 1))
            return -1;
        int lo = 0;
        int hi = n - 1;
        int mid;
        while (lo < hi) {
            mid = (lo + hi) / 2;
            if (getRank(mid) > num)
                hi = mid;
            else
                lo = mid + 1;
        }
        return lo;
    }

    /**
     * Returns a rank array for the underlying bit vector.
     * Only for testing purposes.
     * @return the rank array
     */
    public int[] makeRankArray() {
        int n = this.size();
        int[] rankArray = new int[n];
        for (int i = 0; i < n; i++) {
            rankArray[i] = getRank(i);
        }
        return rankArray;
    }

    static int defaultChunkSize(int N, int depth) {
        if (depth == 1)
            return (int) Math.ceil(Math.sqrt(N) / 2);
        else
            return (int) Math.ceil(Math.pow(Math.log(N) / Math.log(2), 2));
    }
}
