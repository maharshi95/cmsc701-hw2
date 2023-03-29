import bitvec.SparseArray;

public class SparseArrayDemo {
    public static void main(String[] args) {
        int size = 10;
        SparseArray<String> array = new SparseArray<>(size);

        array.append("Hello", 1);
        array.append("World", 4);
        array.append("!", 7);
        array.finalizeArray();

        System.out.println("size = " + array.size());
        System.out.println("numElements = " + array.getNumElements());

        for (int i = 0; i < size + 3; i++) {
            SparseArray.ReturnItem<String> ret = array.getAtIndex(i);
            System.out.println("elem_at_Index["  + i + "] = " + ret.item());
            System.out.println("numElementsAt["  + i + "] = " + array.numElementsAt(i));
        }
        System.out.println("\n");
        for (int i=0; i < array.getNumElements() + 2; i++) {
            SparseArray.ReturnItem<String> ret = array.getAtRank(i);
            System.out.println("elem_at_Rank["  + i + "] = " + ret.item());
            System.out.println("getIndexOf["  + i + "] = " + array.getIndexOf(i));
        }
    }
}
