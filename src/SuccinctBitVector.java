import edu.berkeley.cs.succinct.util.vector.IntVector;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;


class SubChunk extends AbstractBitVector{

    private IntVector relativeRank1;

    /**
     *
     * @param data the input data to be compressed
     * @param start the start index of the data (inclusive)
     * @param end the end index of the data (exclusive)
     */
    public SubChunk(boolean[] data, int start, int end) {
        int bitWidth = (int) Math.ceil(Math.log(end - start) / Math.log(2));
        relativeRank1 = new IntVector(data.length, bitWidth);
        int count = 0;
        for (int i = start; i < end; i++) {
            if (data[i]) {
                count++;
            }
            relativeRank1.add(i - start, count);
        }
    }

    public int getRank(int i) {
        return relativeRank1.get(i);
    }

    public int size() {
        return relativeRank1.length();
    }

}

class Chunk extends AbstractBitVector {
    SubChunk[] subChunks;
    IntVector subChunkRanks;
    int subChunkSize;
    int chunkSize;
    public Chunk(boolean[] data, int start, int end, int subChunkSize) {
        this.subChunkSize = subChunkSize;
        chunkSize = end - start;
        int numSubChunks = (int) Math.ceil(chunkSize / (double) subChunkSize);
        var bitsPerElement = (int) Math.ceil(Math.log(chunkSize) / Math.log(2));
        subChunks = new SubChunk[numSubChunks];
        subChunkRanks = new IntVector(numSubChunks, bitsPerElement);
        int counts = 0;
        for (int i = 0; i < numSubChunks; i++) {
            int subChunkStart = i * subChunkSize + start;
            int subChunkEnd = Math.min((i + 1) * subChunkSize + start, end);
            subChunks[i] = new SubChunk(data, subChunkStart, subChunkEnd);
            counts += subChunks[i].getRank(subChunkEnd - subChunkStart - 1);
            subChunkRanks.add(i, counts);
        }
    }

    public Chunk(boolean[] data, int start, int end) {
        this(data, start, end, (int) Math.ceil(Math.sqrt(end - start) / Math.log(2)));
    }

    public int size() {
        return chunkSize;
    }

    public int getRank(int i) {
        return getRank(i, subChunkSize, subChunks, subChunkRanks);
    }
}

public class SuccinctBitVector extends AbstractBitVector {

    int n;
    int nChunks;
    int chunkSize;
    Chunk[] chunks;
    IntVector chunkRanks;

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

    public SuccinctBitVector(boolean[] data, int chunkSize) {
        this.n = data.length;
        this.chunkSize = chunkSize;
        this.nChunks = (int) Math.ceil(data.length / (double) chunkSize);
        chunks = new Chunk[nChunks];
        int bitsPerElement = (int) Math.ceil(Math.log(data.length) / Math.log(2));
        chunkRanks = new IntVector(nChunks, bitsPerElement);
        int counts = 0;
        for (int i = 0; i < nChunks; i++) {
            int chunkStart = i * chunkSize;
            int chunkEnd = Math.min((i + 1) * chunkSize, data.length);
            chunks[i] = new Chunk(data, chunkStart, chunkEnd);
            counts += chunks[i].getRank(chunkEnd - chunkStart - 1);
            chunkRanks.add(i, counts);
        }
    }

    public SuccinctBitVector(boolean[] data) {
        this(data, (int) Math.ceil(Math.pow(Math.log(data.length) / Math.log(2), 2)));
    }

    public int size() {
        return n;
    }

    public int getRank(int i) {
        return getRank(i, chunkSize, chunks, chunkRanks);
    }

    public static void main(String[] args) {
        boolean segment[] = new boolean[]{false, true, false, true, true, false, false, false, true, false, false};

        // repeat the segment to make it longer
        boolean[] data = new boolean[segment.length * 10];
        for (int i = 0; i < data.length; i++) {
            data[i] = segment[i % segment.length];
        }

        // print 0 1 array instead of false true
        for (boolean b : data) {
            System.out.print((b ? 1 : 0) + " ");
        }
        System.out.println();

        BitSet bitSet = new BitSet(data.length);
        for (int i = 0; i < data.length; i++) {
            bitSet.set(i, data[i]);
        }
        System.out.println(bitSet);
        SubChunk subChunk = new SubChunk(data, 0, data.length);
        var ranks = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = subChunk.getRank(i);
        }
        var selects_1 = createSelectArray(ranks);
        System.out.println(Arrays.toString(ranks));
        System.out.println(Arrays.toString(selects_1));


        Chunk chunk = new Chunk(data, 0, data.length);
        for (int i = 0; i < data.length; i++) {
            ranks[i] = chunk.getRank(i);
        }
        System.out.println(Arrays.toString(ranks));
        var selects_2 = createSelectArray(ranks);
        System.out.println(Arrays.toString(selects_2));

        SuccinctBitVector succinctBitVector = new SuccinctBitVector(data);
        for (int i = 0; i < data.length; i++) {
            ranks[i] = succinctBitVector.getRank(i);
        }
        System.out.println(Arrays.toString(ranks));
        var selects_3 = createSelectArray(ranks);
        System.out.println(Arrays.toString(selects_3));

        System.out.println("Selects equal: " + Arrays.equals(selects_1, selects_2) + " " + Arrays.equals(selects_2, selects_3));

        System.out.println("Cardinality: " + bitSet.cardinality());
        for (int i = 0; i <= bitSet.cardinality() + 1; i++) {
            System.out.println("Select " + i + ": " + succinctBitVector.select(i) + " " + bitSet.nextSetBit(i));
        }
    }
}
