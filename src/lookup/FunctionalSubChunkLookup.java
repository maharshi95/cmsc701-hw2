package lookup;

public class FunctionalSubChunkLookup extends SubChunkLookup{
    public FunctionalSubChunkLookup(int nBits) {
        super(nBits);
    }

    public int lookupSubChunkRank(int index, int offset) {
        return Integer.bitCount(index & ((1 << (offset + 1)) - 1));
    }
}
