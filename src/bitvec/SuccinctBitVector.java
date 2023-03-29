package bitvec;

import edu.berkeley.cs.succinct.util.vector.IntVector;
import jdk.jshell.execution.Util;
import lookup.*;
import util.Utils;


class SuccinctChunk extends RankedBitVector {

    IntVector subChunkRanks;
    IntVector lookupIndices;
    byte subChunkSize;
    int chunkSize;
    SubChunkLookup lookupTable;

    public SuccinctChunk(boolean[] data, int start, int end, int subChunkSize, LookupMode mode) {
        this(data, start, end, subChunkSize, LookupFactory.createLookup(mode, subChunkSize));
    }

    public SuccinctChunk(boolean[] data, int start, int end, int subChunkSize) {
        this(data, start, end, subChunkSize, LookupMode.TABLE);
    }

    public SuccinctChunk(boolean[] data, int start, int end, int subChunkSize, SubChunkLookup lookupTable) {
        this.subChunkSize = (byte) subChunkSize;
        chunkSize = (end - start);
        this.lookupTable = lookupTable;
        int numSubChunks = (int) Math.ceil(chunkSize / (double) subChunkSize);
        var subChunkRankBitWidth = Utils.getMaxBitWidth(chunkSize);
        lookupIndices = new IntVector(numSubChunks, subChunkSize);
        subChunkRanks = new IntVector(numSubChunks, subChunkRankBitWidth + 1);
        int counts = 0;
        for (int i = 0; i < numSubChunks; i++) {
            int subChunkStart = i * subChunkSize + start;
            int subChunkEnd = Math.min(subChunkStart + subChunkSize, end);
            int lookupIndex = Utils.getLookupIndexBigEndian(data, subChunkStart, subChunkEnd);
            counts += lookupTable.lookupSubChunkRank(lookupIndex, subChunkEnd - subChunkStart - 1);
            subChunkRanks.add(i, counts);
            lookupIndices.add(i, lookupIndex);
        }
    }

    @Override
    public int getRank(int index) {
        int chunkIndex = index / subChunkSize;
        int lookupIndex = lookupIndices.get(chunkIndex);
        int chunkOffset = index % subChunkSize;
        int relativeRank = lookupTable.lookupSubChunkRank(lookupIndex, chunkOffset);
        int chunkRank = chunkIndex > 0 ? subChunkRanks.get(chunkIndex - 1) : 0;
        return chunkRank + relativeRank;
    }

    @Override
    public int size() {
        return chunkSize;
    }

    @Override
    public long overhead() {
        long overhead = overheadExcludeLookup();
        return overhead + lookupTable.overhead();
    }


    public long overheadExcludeLookup() {
        return subChunkRanks.overhead() + lookupIndices.overhead() + Byte.SIZE;
    }
}

public class SuccinctBitVector extends RankedBitVector {
    IntVector chunkRanks;
    SuccinctChunk[] chunks;

    SubChunkLookup lookupTable;
    int chunkSize;
    int subChunkSize;
    int totalElements;

    public SuccinctBitVector(boolean[] data, int chunkSize, LookupMode lookupMode) {
        this.chunkSize = chunkSize;
        this.subChunkSize = (int) (Math.sqrt(chunkSize) / 2);
        this.totalElements = data.length;
        int numChunks = (int) Math.ceil(totalElements / (double) chunkSize);
        var bitsPerElement = (int) Math.ceil(Math.log(totalElements) / Math.log(2));

        chunks = new SuccinctChunk[numChunks];
        chunkRanks = new IntVector(numChunks, bitsPerElement);
        int counts = 0;
        lookupTable = LookupFactory.createLookup(lookupMode, subChunkSize);
        for (int i = 0; i < numChunks; i++) {
            int chunkStart = i * chunkSize;
            int chunkEnd = Math.min((i + 1) * chunkSize, data.length);
            chunks[i] = new SuccinctChunk(data, chunkStart, chunkEnd, subChunkSize, lookupTable);
            counts += chunks[i].getRank(chunkEnd - chunkStart - 1);
            chunkRanks.add(i, counts);
        }
    }


    public SuccinctBitVector(boolean[] data, int chunkSize) {
        this(data, chunkSize, LookupMode.TABLE);
    }

    public SuccinctBitVector(boolean[] data, LookupMode lookupMode) {
        this(data, defaultChunkSize(data.length), lookupMode);
    }

    public SuccinctBitVector(boolean[] data) {
        this(data, defaultChunkSize(data.length), LookupMode.TABLE);
    }

    public static int defaultChunkSize(int N) {
        return (int) Math.pow(Math.log(N) / Math.log(2), 2);
    }


    @Override
    public int getRank(int index) {
        return Utils.getRank(index, chunkSize, chunks, chunkRanks);
    }

    @Override
    public int size() {
        return totalElements;
    }

    @Override
    public long overhead() {
        long chunkRankOverhead = chunkRanks.size();
        long chunkOverhead = 0;
        for (SuccinctChunk c : chunks) {
            chunkOverhead += c.overheadExcludeLookup();
        }
        long lookupTableOverhead = lookupTable.overhead();
        return chunkRankOverhead + chunkOverhead + lookupTableOverhead + 3 * Integer.SIZE;
    }
}
