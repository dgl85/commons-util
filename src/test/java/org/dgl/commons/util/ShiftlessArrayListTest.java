package org.dgl.commons.util;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShiftlessArrayListTest {

    @Test
    public void testAdd() {
        ShiftlessArrayList<Integer> shiftlessArrayList = new ShiftlessArrayList<>();
        ShiftlessArrayList<Integer> invertedShiftlessArrayList = new ShiftlessArrayList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            shiftlessArrayList.addLast(random);
            invertedShiftlessArrayList.addFirst(random);
            arrayList.add(random);
        }
        for (int i = 0; i < 10000; i++) {
            assertEquals(shiftlessArrayList.get(i),invertedShiftlessArrayList.get(10000-1-i));
            assertEquals(arrayList.get(i), shiftlessArrayList.get(i));
        }
    }

    @Test
    public void testRemove() {
        ShiftlessArrayList<Integer> shiftlessArrayList = new ShiftlessArrayList<>();
        for (int i = 0; i < 20; i++) {
            shiftlessArrayList.addLast(i);
        }
        for (int i = 0; i < 8; i++) {
            shiftlessArrayList.removeFirst();
            shiftlessArrayList.removeLast();
        }
        assertEquals(8, (int) shiftlessArrayList.get(0));
        assertEquals(11, (int) shiftlessArrayList.get(shiftlessArrayList.size()-1));
        assertEquals(9, (int) shiftlessArrayList.get(1));
        assertEquals(10, (int) shiftlessArrayList.get(shiftlessArrayList.size()-2));
        assertEquals(10, (int) shiftlessArrayList.get(2));
        assertEquals(9, (int) shiftlessArrayList.get(shiftlessArrayList.size()-3));
        assertEquals(11, (int) shiftlessArrayList.get(3));
        assertEquals(8, (int) shiftlessArrayList.get(shiftlessArrayList.size()-4));
    }

    @Test
    public void testSet() {
        ShiftlessArrayList<Integer> shiftlessArrayList = new ShiftlessArrayList<>();
        for (int i = 0; i < 100; i++) {
            shiftlessArrayList.addLast(0);
        }
        for (int i = 0; i < 100; i++) {
            shiftlessArrayList.set(i,99-i);
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(99-i, (int) shiftlessArrayList.get(i));
        }
    }

    @Test
    public void testClearAndSize() {
        ShiftlessArrayList<Integer> shiftlessArrayList = new ShiftlessArrayList<>();
        for (int i = 0; i < 100; i++) {
            shiftlessArrayList.addLast(0);
        }
        assertEquals(100, shiftlessArrayList.size());
        shiftlessArrayList.clear();
        assertEquals(0, shiftlessArrayList.size());
    }

    @Test
    public void testRestrictions() {
        ShiftlessArrayList<Integer> shiftlessArrayList = new ShiftlessArrayList<>();
        for (int i = 0; i < 100; i++) {
            shiftlessArrayList.addLast(0);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.get(100));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.get(shiftlessArrayList.size()));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.set(-1,0));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.set(100,0));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.set(shiftlessArrayList.size(),0));
        shiftlessArrayList.clear();
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> shiftlessArrayList.set(0,0));
    }
}
