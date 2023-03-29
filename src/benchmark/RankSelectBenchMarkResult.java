package benchmark;

public record RankSelectBenchMarkResult
        (String bitVectorName, String testCaseName, long rankQuerytime,
        long selectQueryTime,
                                 long overhead, int size, double sparseRatio)  implements BenchmarkResult {
    public static RankSelectBenchMarkResult of(String bitVectorName, String testCaseName, long rankQuerytime, long selectQueryTime, long overhead, int size, double sparseRatio) {
        return new RankSelectBenchMarkResult(bitVectorName, testCaseName, rankQuerytime, selectQueryTime, overhead, size, sparseRatio);
    }

    public String toCSV() {
        return String.format("%s,%s,%d,%2f,%d,%d,%d", bitVectorName, testCaseName, size, sparseRatio, rankQuerytime, selectQueryTime, overhead);
    }

    public static String CSVHeader() {
        return "bitVectorName,testCaseName,size,sparseRatio,rankQueryTime,selectQueryTime,overhead";
    }

    public String toString() {
        // Make an indented dictionary
        return String.format("""
                {
                    "bitVectorName": "%s",
                    "testCaseName": "%s",
                    "size": %,d,
                    "sparseRatio": %.2f %%,
                    "rankQueryTime": %d ms,
                    "selectQueryTime": %d ms,
                    "overhead": %d bits
                }
                """, bitVectorName, testCaseName, size, sparseRatio * 100, rankQuerytime, selectQueryTime, overhead);
    }
}
