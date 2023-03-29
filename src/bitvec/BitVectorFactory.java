package bitvec;

import lookup.LookupMode;

public class BitVectorFactory {
    public static PrecomputedLongBitVector createPrecomputedLongBitVector(boolean[] data, int start, int end) {
        return new PrecomputedLongBitVector(data, start, end);
    }

    public static PrecomputedEfficientBitVector createPrecomputedEfficientBitVector(boolean[] data, int start, int end) {
        return new PrecomputedEfficientBitVector(data, start, end);
    }

    public static SuccinctChunk createSuccinctChunk(boolean[] data, int start, int end, int subChunkSize) {
        return new SuccinctChunk(data, start, end, subChunkSize, LookupMode.TABLE);
    }

    public static SuccinctChunk createSuperSuccinctChunk(boolean[] data, int start, int end, int subChunkSize) {
        return new SuccinctChunk(data, start, end, subChunkSize, LookupMode.FUNCTIONAL);
    }

    public static SuccinctBitVector createSuccinctBitVector(boolean[] data) {
        return new SuccinctBitVector(data, LookupMode.TABLE);
    }

    public static SuccinctBitVector createSuperSuccinctBitVector(boolean[] data) {
        return new SuccinctBitVector(data, LookupMode.FUNCTIONAL);
    }

    public static RankedBitVector createRankedBitVector(boolean[] data, BitVectorType type) {
        return switch (type) {
            case PRECOMPUTED_LONG -> new PrecomputedLongBitVector(data, 0, data.length);
            case PRECOMPUTED_EFFICIENT -> new PrecomputedEfficientBitVector(data, 0, data.length);
            case SUCCINCT_CHUNK -> new SuccinctChunk(data, 0, data.length, 10, LookupMode.TABLE);
            case SUPER_SUCCINCT_CHUNK -> new SuccinctChunk(data, 0, data.length, 10, LookupMode.FUNCTIONAL);
            case SUCCINCT_BITVEC -> new SuccinctBitVector(data, LookupMode.TABLE);
            case SUPER_SUCCINCT_BITVEC -> new SuccinctBitVector(data, LookupMode.FUNCTIONAL);
            default -> throw new IllegalArgumentException("Unknown BitVectorType: " + type);
        };
    }
}
