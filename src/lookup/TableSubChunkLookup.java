package lookup;

import edu.berkeley.cs.succinct.util.vector.IntVector;
import util.Utils;

public class TableSubChunkLookup extends SubChunkLookup {
    IntVector[] lookupTable;

    public TableSubChunkLookup(int nBits) {
        super(nBits);
        int nPatterns = (int) Math.pow(2, nBits);
        lookupTable = new IntVector[nPatterns];
        for (int i = 0; i < nPatterns; i++) {
            lookupTable[i] = Utils.createRankArray(i, nBits);
        }
    }

    @Override
    public long overhead() {
        long overhead = super.overhead();
        for (IntVector v : lookupTable) {
            overhead += v.size();
        }
        return overhead;
    }

    @Override
    public int lookupSubChunkRank(int index, int offset) {
        return lookupTable[index].get(offset);
    }
}
