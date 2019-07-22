package cn.mzhong.janytask.test;

import java.util.BitSet;

public class BitSetTest {
    public static void main(String[] args) {
        BitSet bitSet = new BitSet(1000);
        bitSet.set(63);
        bitSet.set(64);
        System.out.println(bitSet.get(63));
    }
}
