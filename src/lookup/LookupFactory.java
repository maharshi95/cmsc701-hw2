package lookup;

public class LookupFactory {

    public static SubChunkLookup createLookup(LookupMode mode, int subChunkSize) {
        return switch (mode) {
            case TABLE -> new TableSubChunkLookup(subChunkSize);
            case FUNCTIONAL -> new FunctionalSubChunkLookup(subChunkSize);
            default -> throw new IllegalArgumentException("Unknown lookup mode: " + mode);
        };
    }
}
