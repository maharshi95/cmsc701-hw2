import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import pandas as pd

df = pd.read_csv("rankSelectBenchmark.csv")

precomp_long_df = df[df["bitVectorName"] == "PRECOMPUTED_LONG"]
precomp_efficient_df = df[df["bitVectorName"] == "PRECOMPUTED_EFFICIENT"]
succinct_df = df[df["bitVectorName"] == "SUCCINCT_BITVEC"]
super_succinct_df = df[df["bitVectorName"] == "SUPER_SUCCINCT_BITVEC"]

X = precomp_long_df["size"]

# set default marker type
matplotlib.rcParams['lines.marker'] = 'o'
matplotlib.rcParams['lines.linestyle'] = '-'
ALPHA = 0.5

# Plot 1: Rank query time vs size of the bit vector
# Make the points circled

plt.plot(X, precomp_long_df["rankQueryTime"], label="PRECOMPUTED_LONG", alpha=ALPHA)
plt.plot(X, precomp_efficient_df["rankQueryTime"], label="PRECOMPUTED_EFFICIENT", alpha=ALPHA)
plt.plot(X, succinct_df["rankQueryTime"], label="SUCCINCT_BITVEC", alpha=ALPHA)
plt.plot(X, super_succinct_df["rankQueryTime"], label="SUPER_SUCCINCT_BITVEC", alpha=ALPHA)
plt.xlabel("Size of the bit vector in number of bits (log scale)")
plt.ylabel("Time taken (µs) for rank query")
plt.title("Rank query time vs size of the bit vector")
plt.xscale("log")
plt.xticks(X)
plt.legend()
plt.tight_layout(pad=1.0)
plt.savefig("plots/query_times_rank.png")
plt.close()

# Plot 2: Select query time vs size of the bit vector
plt.plot(X, precomp_long_df["selectQueryTime"], label="PRECOMPUTED_LONG", alpha=ALPHA)
plt.plot(X, precomp_efficient_df["selectQueryTime"], label="PRECOMPUTED_EFFICIENT", alpha=ALPHA)
plt.plot(X, succinct_df["selectQueryTime"], label="SUCCINCT_BITVEC", alpha=ALPHA)
plt.plot(X, super_succinct_df["selectQueryTime"], label="SUPER_SUCCINCT_BITVEC", alpha=ALPHA)
plt.xlabel("Size of the bit vector in number of bits (log scale)")
plt.ylabel("Time taken (µs) for select query")
plt.title("Select query time vs size of the bit vector")
plt.xscale("log")
plt.xticks(X)
plt.legend()
plt.tight_layout(pad=1.0)
plt.savefig("plots/query_times_select.png")
plt.close()

# Plot 3: Memory usage vs size of the bit vector
plt.plot(X, precomp_long_df["overhead"], label="PRECOMPUTED_LONG", alpha=ALPHA)
plt.plot(X, precomp_efficient_df["overhead"], label="PRECOMPUTED_EFFICIENT", alpha=ALPHA)
plt.plot(X, succinct_df["overhead"], label="SUCCINCT_BITVEC", alpha=ALPHA)
plt.plot(X, super_succinct_df["overhead"], label="SUPER_SUCCINCT_BITVEC", alpha=ALPHA)
plt.xlabel("Size of the bit vector in number of bits (log scale)")
plt.ylabel("Overhead in number of bits (log scale)")
plt.title("Memory overhead vs size of the bit vector")
plt.xscale("log")
plt.yscale("log")
plt.xticks(X)
plt.legend()
plt.tight_layout(pad=1.0)
plt.savefig("plots/rank_select_overhead.png")
plt.close()