package benchmark;

public record SparseArrayBenchmarkResult(int size, double sparse, long getAtRankTime, long getAtIndexTime,
                                  long getIndexOfTime,
                                  long numElementsAtTime, long overhead, long altOverhead) implements BenchmarkResult{
    public static SparseArrayBenchmarkResult of(int size, double sparse, long getAtRankTime, long getAtIndexTime,
                                                long getIndexOfTime,
                                                long numElementsAtTime, long overhead, long altOverhead) {
        return new SparseArrayBenchmarkResult(size, sparse, getAtRankTime, getAtIndexTime, getIndexOfTime,
                numElementsAtTime, overhead, altOverhead);
    }

    public String toCSV() {
        return String.format("%d,%2f,%d,%d,%d,%d,%d,%d", size, sparse, getAtRankTime, getAtIndexTime, getIndexOfTime,
                numElementsAtTime, overhead, altOverhead);
    }

    public static String CSVHeader() {
        return "size,sparse,getAtRankTime,getAtIndexTime,getIndexOfTime,numElementsAtTime,overhead,altOverhead";
    }
}
