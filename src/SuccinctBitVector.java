import edu.berkeley.cs.succinct.util.vector.IntVector;


class SuccinctChunk extends RankedBitVector {

    IntVector subChunkRanks;
    IntVector lookupIndices;
    int subChunkSize;
    int totalElements;
    IntVector[] lookupTable;

    public SuccinctChunk(boolean[] data, int start, int end, int subChunkSize) {
        this(data, start, end, subChunkSize, Utils.createLookupTable(subChunkSize));
    }

    public SuccinctChunk(boolean[] data, int start, int end) {
        this(data, start, end, (int) (Math.min(Math.sqrt(end - start) / 2, 10)));
    }

    public SuccinctChunk(boolean[] data, int start, int end, int subChunkSize, IntVector[] lookupTable) {
        this.subChunkSize = subChunkSize;
        this.totalElements = end - start;
        this.lookupTable = lookupTable;
        int numSubChunks = (int) Math.ceil(totalElements / (double) subChunkSize);
        var subChunkRankBitWidth = (int) Math.ceil(Math.log(totalElements) / Math.log(2));
        var lookupIndexBitWidth = (int) Math.ceil(Math.log(lookupTable.length) / Math.log(2));
        lookupIndices = new IntVector(numSubChunks, lookupIndexBitWidth);
        subChunkRanks = new IntVector(numSubChunks, subChunkRankBitWidth);
        int counts = 0;
        for (int i = 0; i < numSubChunks; i++) {
            int subChunkStart = i * subChunkSize + start;
            int subChunkEnd = Math.min((i + 1) * subChunkSize + start, end);
            int lookupIndex = Utils.getLookupIndex(data, subChunkStart, subChunkEnd);
            counts += lookupTable[lookupIndex].get(subChunkEnd - subChunkStart - 1);
            subChunkRanks.add(i, counts);
            lookupIndices.add(i, lookupIndex);
        }
    }

    @Override
    public int getRank(int i) {
        int chunkIndex = i / subChunkSize;
        int lookupIndex = lookupIndices.get(chunkIndex);
        int chunkOffset = i % subChunkSize;
        int relativeRank = lookupTable[lookupIndex].get(chunkOffset);
        int chunkRank = chunkIndex > 0 ? subChunkRanks.get(chunkIndex - 1) : 0;
//        System.out.println("chunkRank: " + chunkRank + " relativeRank: " + relativeRank);
        return chunkRank + relativeRank;
    }

    @Override
    public int size() {
        return totalElements;
    }

    @Override
    public long overhead() {
        long overhead = overheadExcludeLookup();
        for (IntVector lookup : lookupTable) {
            overhead += lookup.overhead();
        }
        return overhead;
    }


    public long overheadExcludeLookup() {
        long overhead = subChunkRanks.overhead() + lookupIndices.overhead();
        return overhead + 3 * Integer.SIZE; // chunkSize, totalElements, and lookupTable.length
    }
}

public class SuccinctBitVector extends RankedBitVector {
    IntVector chunkRanks;
    SuccinctChunk[] chunks;

    IntVector[] lookupTable;
    int chunkSize;

    int subChunkSize;
    int totalElements;

    public SuccinctBitVector(boolean[] data, int chunkSize) {
        this.chunkSize = chunkSize;
        this.subChunkSize = (int) (Math.sqrt(chunkSize) / 2);
        System.out.println("Creating SuccinctBitVector of size " + data.length + " with chunkSize " + chunkSize + " and subChunkSize " + subChunkSize);
        this.totalElements = data.length;
        int numChunks = (int) Math.ceil(totalElements / (double) chunkSize);
        var bitsPerElement = (int) Math.ceil(Math.log(totalElements) / Math.log(2));

        chunks = new SuccinctChunk[numChunks];
        chunkRanks = new IntVector(numChunks, bitsPerElement);
        int counts = 0;
        lookupTable = Utils.createLookupTable(subChunkSize);
        for (int i = 0; i < numChunks; i++) {
            int chunkStart = i * chunkSize;
            int chunkEnd = Math.min((i + 1) * chunkSize, data.length);
            chunks[i] = new SuccinctChunk(data, chunkStart, chunkEnd, subChunkSize, lookupTable);
            counts += chunks[i].getRank(chunkEnd - chunkStart - 1);
            chunkRanks.add(i, counts);
        }
    }

    public SuccinctBitVector(boolean[] data) {
        this(data, (int) Math.pow(Math.log(data.length) / Math.log(2), 2));
    }

    public static void main(String[] args) {
        boolean[] data = {true, true, false, false, true, false, true, false, false, false, false, true, true, false,
                true, false, true, true, true, false};
        int[] bits = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            bits[i] = data[i] ? 1 : 0;
        }
        int nBits = 4;
        IntVector[] lookupTable = Utils.createLookupTable(nBits);
        for (int i = 0; i < lookupTable.length; i++) {
            System.out.println(i + " " + lookupTable[i]);
        }
    }


    @Override
    public int getRank(int i) {
        return Utils.getRank(i, chunkSize, chunks, chunkRanks);
    }

    @Override
    public int size() {
        return totalElements;
    }

    @Override
    public long overhead() {
        long overhead = chunkRanks.overhead();
        long chunkOverhead = 0;
        for (SuccinctChunk c : chunks) {
            chunkOverhead += c.overheadExcludeLookup();
        }
        long lookupTableOverhead = 0;
        for (IntVector v : lookupTable) {
            lookupTableOverhead += v.overhead();
        }
        return overhead + chunkOverhead + lookupTableOverhead + 2 * Integer.SIZE; // chunkSize, totalElements
    }
}
