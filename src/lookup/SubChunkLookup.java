package lookup;

import java.io.Serializable;

public abstract class SubChunkLookup implements Serializable {
    int nBits;

    public SubChunkLookup(int nBits) {
        if (nBits <= 0) {
            throw new IllegalArgumentException("nBits must be positive");
        }
        this.nBits = nBits;
    }

    public long overhead() {
        return Integer.SIZE; // nBits
    }

    public abstract int lookupSubChunkRank(int index, int offset);
}
