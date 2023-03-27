import edu.berkeley.cs.succinct.util.vector.IntVector;

public class NaiveJacobsonBitVector extends RankedBitVector {
    RankedBitVector[] chunks;
    IntVector chunkRanks;
    int chunkSize;
    int totalElements;

    int depth;

    public NaiveJacobsonBitVector(boolean[] data, int start, int end, int chunkSize, int depth) {

        this.depth = depth;
        this.chunkSize = chunkSize;
        totalElements = end - start;
        int numChunks = (int) Math.ceil(totalElements / (double) chunkSize);
        var bitsPerElement = (int) Math.ceil(Math.log(totalElements) / Math.log(2));

        if (depth > 1)
            chunks = new NaiveJacobsonBitVector[numChunks];
        else
            chunks = new PrecomputedEfficientBitVector[numChunks];

        chunkRanks = new IntVector(numChunks, bitsPerElement);
        int counts = 0;
        for (int i = 0; i < numChunks; i++) {
            int chunkStart = i * chunkSize + start;
            int chunkEnd = Math.min((i + 1) * chunkSize + start, end);
            if (depth > 1) {
                chunks[i] = new NaiveJacobsonBitVector(data, chunkStart, chunkEnd, chunkSize, depth - 1);
            } else {
                chunks[i] = new PrecomputedEfficientBitVector(data, chunkStart, chunkEnd);
            }
            counts += chunks[i].getRank(chunkEnd - chunkStart - 1);

            chunkRanks.add(i, counts);
        }
    }

    public NaiveJacobsonBitVector(boolean[] data, int start, int end, int depth) {
        this(data, start, end, defaultChunkSize(end - start, depth), depth);
    }

    public NaiveJacobsonBitVector(boolean data[]) {
        this(data, 0, data.length, 2);
    }

    public int size() {
        return totalElements;
    }

    public long overhead() {
        long overhead = chunkRanks.overhead();
        for (var chunk : chunks) {
            overhead += chunk.overhead();
        }
        return overhead + Integer.SIZE * 2; // chunkSize and totalElements
    }

    public int getRank(int i) {
        return Utils.getRank(i, chunkSize, chunks, chunkRanks);
    }

}
