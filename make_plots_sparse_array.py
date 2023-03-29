import matplotlib.pyplot as plt
import pandas as pd

import matplotlib
matplotlib.use("Agg")

df = pd.read_csv("sparseArrayBenchmark.csv")
print(df)

sparsity = df["sparse"].unique()
print(sparsity)

ALPHA = 0.7

for func_name in ["getAtRank", "getAtIndex", "getIndexOf", "numElementsAt"]:
    for s in sparsity:
        df_s = df[df["sparse"] == s]
        X = df_s["size"]
        col_name = func_name + "Time"
        plt.plot(X, df_s[col_name], label=f"sparse={s:.2f}", alpha=ALPHA, marker="o", linestyle="-")
    plt.xlabel("Size of the bit vector in number of bits (log scale)")
    plt.ylabel("Time taken / 50k queries (µs) for {}".format(func_name))
    plt.title(f"{func_name}: Query time vs size of the bit vector")
    plt.xscale("log")
    plt.xticks(X)
    plt.legend()
    plt.tight_layout(pad=1)
    plt.savefig(f"plots/sparse_array_{func_name}.png")
    plt.close()


for i, s in enumerate([0.01, 0.05, 0.1, 0.3, 0.5, 0.9]):
    df_s = df[df["sparse"] == s]
    X = df_s["size"]
    plt.plot(X, df_s["overhead"], label=f"sparseArray", alpha=ALPHA, marker="o", linestyle="-")
    plt.plot(X, df_s["altOverhead"], label=f"denseArray", alpha=ALPHA, marker="x", linestyle="--")
    plt.xlabel("Size of the bit vector in number of bits (log scale)")
    plt.ylabel("Overhead in number of bits")
    plt.title(f"Memory overhead (Sparse Array: {s * 100:.2f}% filled)")
    plt.xscale("log")
    plt.xticks(X)
    plt.legend()
    plt.tight_layout(pad=1)
    plt.savefig(f"plots/sparse_array_overhead_{s:.2f}.png")
    plt.close()
