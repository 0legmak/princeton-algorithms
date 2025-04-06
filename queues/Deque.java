import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {

    private class ListElement<DequeItem> {
        DequeItem item;
        ListElement<DequeItem> next;
        ListElement<DequeItem> prev;
    }

    private ListElement<Item> front;
    private ListElement<Item> back;
    private int size_;

    // construct an empty deque
    public Deque() {
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size_ == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size_;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        ListElement<Item> newElem = new ListElement<>();
        newElem.item = item;
        if (isEmpty()) {
            front = newElem;
            back = newElem;
        } else {
            newElem.next = front;
            front.prev = newElem;
            front = newElem;
        }
        ++size_;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        ListElement<Item> newElem = new ListElement<>();
        newElem.item = item;
        if (isEmpty()) {
            front = newElem;
            back = newElem;
        } else {
            back.next = newElem;
            newElem.prev = back;
            back = back.next;
        }
        ++size_;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        Item item = front.item;
        front = front.next;
        --size_;
        if (isEmpty()) {
            back = null;
        } else {
            front.prev = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        Item item = back.item;
        back = back.prev;
        --size_;
        if (isEmpty()) {
            front = null;
        } else {
            back.next = null;            
        }
        return item;
    }

    private class DequeIter<DequeItem> implements Iterator<DequeItem> {
        
        private ListElement<DequeItem> elem;

        public DequeIter(ListElement<DequeItem> front) {
            elem = front;
        }

        public boolean hasNext() {
            return elem != null;
        }

        public DequeItem next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            DequeItem item = elem.item;
            elem = elem.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIter<>(front);
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        StdOut.println("isEmpty() = " + deque.isEmpty());
        StdOut.println("size() = " + deque.size());
        deque.addFirst("str1");
        StdOut.println("isEmpty() = " + deque.isEmpty());
        StdOut.println("size() = " + deque.size());
        deque.addLast("str2");
        StdOut.println("isEmpty() = " + deque.isEmpty());
        StdOut.println("size() = " + deque.size());

        StdOut.print("iterate:");
        for (String str : deque) {
            StdOut.print(" " + str);
        }
        StdOut.println();

        StdOut.println("removeLast() = " + deque.removeLast());
        StdOut.println("isEmpty() = " + deque.isEmpty());
        StdOut.println("size() = " + deque.size());
        StdOut.print("iterate:");
        for (String str : deque) {
            StdOut.print(" " + str);
        }
        StdOut.println();

        StdOut.println("removeFirst() = " + deque.removeFirst());
        StdOut.println("isEmpty() = " + deque.isEmpty());
        StdOut.println("size() = " + deque.size());
        StdOut.print("iterate:");
        for (String str : deque) {
            StdOut.print(" " + str);
        }
        StdOut.println();
    }
}
