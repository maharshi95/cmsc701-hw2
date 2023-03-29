package bitvec;

import java.io.*;

public abstract class RankedBitVector implements Serializable {
    /**
     * Abstract method to return the 1-rank (inclusive) at position index.
     * This method must be implemented by all subclasses.
     * @param index the position
     * @return the 1-rank at position index
     */
    public abstract int getRank(int index);

    /**
     * Returns the bit-rank (inclusive) at position i.
     * @param i the position
     * @param bit the bit (true for 1, false for 0)
     * @return the bit-rank at position i
     */
    public int getRank(boolean bit, int i) {
        return bit ? getRank(i) : i - getRank(i);
    }

    /**
     * Returns the number of bits represented by this bit vector.
     * This might not be the number of bits in the underlying bit vector.
     * This method must be implemented by all subclasses.
     */
    public abstract int size();

    /**
     * Returns the overhead of this bit vector in # of bits.
     * This method must be implemented by all subclasses.
     * @return the overhead
     */
    public abstract long overhead();

    /**
     * Returns the largest index i such that getRank(i) <= rank.
     * @param rank the rank
     * @return index i
     */
    public int select(int rank) {
        // Use binary search to find the first index that has the rank greater than rank
        int N = size();

        int maxRank = getRank(N - 1);

        if (rank > maxRank)
            return -1;

        if (rank == maxRank)
            return N - 1;

        int lo = 0, hi = N - 1, mid;

        // Finds the first index that has the rank greater than input rank
        while (lo < hi) {
            mid = (lo + hi) / 2;
            if (getRank(mid) > rank)
                hi = mid;
            else
                lo = mid + 1;
        }
        return lo - 1;
    }

    /**
     * Returns a rank array for the underlying bit vector.
     * Only for testing purposes.
     * @return the rank array
     */
    public int[] makeRankArray() {
        int n = size();
        System.out.println("n = " + n);
        int[] rankArray = new int[n];
        for (int i = 0; i < n; i++) {
            rankArray[i] = getRank(i);
        }
        return rankArray;
    }

    public int[] makeSelectArray() {
        int n = size();
        int maxRank = getRank(n - 1);
        int[] selectArray = new int[maxRank + 1];
        for (int i = 0; i < selectArray.length; i++) {
            selectArray[i] = select(i);
        }
        return selectArray;
    }

    static int defaultChunkSize(int N, int depth) {
        if (depth == 1)
            return (int) Math.ceil(Math.sqrt(N) / 2);
        else
            return (int) Math.ceil(Math.pow(Math.log(N) / Math.log(2), 2));
    }

    public void save(String filename){
        try(var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RankedBitVector load(String filename) throws IOException, ClassNotFoundException {
        try(var in = new ObjectInputStream(new FileInputStream(filename))) {
            return (RankedBitVector) in.readObject();
        }
    }
}
