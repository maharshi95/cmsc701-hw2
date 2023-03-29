package benchmark;

public interface BenchmarkResult {
    String toCSV();

    static String CSVHeader() {
        return "";
    }
}
