package bitvec;

import bitvec.BitVectorFactory;
import bitvec.RankedBitVector;

import java.io.*;
import java.util.ArrayList;


public class SparseArray<T> implements Serializable {
    private final int size;
    private boolean finalized = false;
    private final ArrayList<Integer> positions;
    private final ArrayList<T> values;
    private RankedBitVector bitVector;

    public SparseArray(int size) {
        this.size = size;
        positions = new ArrayList<>();
        values = new ArrayList<>();
    }

    /**
     * Append a value to the array at the given position. Positions must be in increasing order.
     * If the array is already finalized, this will throw an exception.
     * If the position is out of bounds, this will throw an exception.
     * If the position is not in increasing order, this will throw an exception.
     *
     * @param value the value to append
     * @param pos   the 0-indexed position to append the value at
     */
    public void append(T value, int pos) {
        if (finalized) throw new RuntimeException("Cannot append to finalized array");
        if (pos >= size) throw new RuntimeException("Position out of bounds");
        if (!positions.isEmpty() && positions.get(positions.size() - 1) >= pos)
            throw new RuntimeException("Positions must be in increasing order");
        positions.add(pos);
        values.add(value);
    }

    public void finalizeArray() {
        if (finalized) return;
        boolean[] data = new boolean[size];
        for (int i = 0; i < positions.size(); i++) {
            data[positions.get(i)] = true;
        }
        bitVector = BitVectorFactory.createSuperSuccinctBitVector(data);
        finalized = true;
    }

    /**
     * Get the rth element in the array. Here, r is 1-indexed.
     *
     * @param r the 1-indexed rank of the element
     * @return the element at the given rank, or null if r is out of bounds
     */
    public ReturnItem<T> getAtRank(int r) {
        if (r > positions.size() || r <= 0) {
            return new ReturnItem<>(null, false);
        }
        return new ReturnItem<>(values.get(r - 1), true);
    }

    /**
     * Get the element at the given position.
     *
     * @param p the 0-indexed position of the element
     * @return the element at the given position wrapped in a ReturnItem.
     */
    public ReturnItem<T> getAtIndex(int p) {
        if (p >= size || p < 0) {
            return new ReturnItem<>(null, false);
        }
        return getAtRank(bitVector.getRank(p));
    }

    /**
     * Find the index in the array where the rth element first appears.
     * Here, r is 1-indexed. If r is out of bounds, this will return -1.
     *
     * @param r the 1-indexed rank of the element
     * @return the index of the element, or -1 if r is out of bounds
     */
    public int getIndexOf(int r) {
        if (r > positions.size() || r <= 0) {
            return -1;
        }

        return bitVector.select(r - 1) + 1;
    }

    /**
     * Find the number of elements in the array that appear at or before the given position.
     *
     * @param p the position to check
     * @return the number of elements that appear at or before the given position, or -1 if p is out of bounds
     */
    public int numElementsAt(int p) {
        if (p >= size || p < 0) {
            return -1;
        }
        return bitVector.getRank(p);
    }

    public int size() {
        return size;
    }

    public int getNumElements() {
        return positions.size();
    }

    public long overhead() {
        long overhead = bitVector.overhead() + (long) positions.size() * Integer.SIZE;
        return overhead + valuesOverhead(overhead);
    }

    public long altOverhead() {
        long overhead = (long) positions.size() * Integer.SIZE;
        return overhead + valuesOverhead(overhead) + (long) (size - getNumElements()) * Character.SIZE;
    }

    private long valuesOverhead(long overhead) {
        for (var v : values) {
            overhead += (long) (v.toString().length() + 1) * Character.SIZE;
        }
        overhead += Byte.SIZE; // finalized
        overhead += Integer.SIZE; // numElements
        overhead += Integer.SIZE; // size
        return overhead;
    }

    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static SparseArray load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (SparseArray) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public record ReturnItem<E>(E item, boolean success) {
        @Override
        public String toString() {
            return "ReturnItem{" + "elem=" + item + ", success=" + success + "}";
        }
    }

}



