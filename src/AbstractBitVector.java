import edu.berkeley.cs.succinct.util.vector.IntVector;

public abstract class AbstractBitVector {
    public abstract int getRank(int i);

    public int getRank(boolean bit, int i) {
        return bit ? getRank(i) : i - getRank(i);
    }

    public static int getRank(int i, int chunkSize, AbstractBitVector[] chunks, IntVector chunkRanks) {
        int chunkIndex = i / chunkSize;
        int chunkOffset = i % chunkSize;
        int chunkRank = chunkIndex > 0 ? chunkRanks.get(chunkIndex - 1) : 0;
        int relativeRank = chunks[chunkIndex].getRank(chunkOffset);
        return chunkRank + relativeRank;
    }

    public abstract int size();

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
}
