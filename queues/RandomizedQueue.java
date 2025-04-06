import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    
    private Item[] data;
    private int size_;

    private void grow() {
        int newSize = data == null ? 1 : data.length * 2;
        Item[] newData = (Item[]) new Object[newSize];
        for (int i = 0; i < size_; ++i) {
            newData[i] = data[i];
        }
        data = newData;
    }

    private void shrink() {
        int newSize = data.length / 2;
        if (newSize == 0) {
            data = null;
            return;
        }
        Item[] newData = (Item[]) new Object[newSize];
        for (int i = 0; i < size_; ++i) {
            newData[i] = data[i];
        }
        data = newData;
    }

    // construct an empty randomized queue
    public RandomizedQueue() {
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size_ == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size_;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (data == null || size_ == data.length) {
            grow();
        }
        data[size_] = item;
        size_++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniformInt(size_);
        Item item = data[index];
        size_--;
        data[index] = data[size_];
        data[size_] = null;
        if (size_ == data.length / 4) {
            shrink();
        }
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        return data[StdRandom.uniformInt(size_)];
    }

    private class RandomIter<QueueItem> implements Iterator<QueueItem> {
        
        private QueueItem[] data;
        private int index;

        public RandomIter(RandomizedQueue<QueueItem> queue) {
            data = (QueueItem[]) new Object[queue.size_];
            for (int i = 0; i < queue.size_; ++i) {
                data[i] = queue.data[i];
            }
            StdRandom.shuffle(data);
        }

        public boolean hasNext() {
            return index < data.length;
        }

        public QueueItem next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            QueueItem item = data[index];
            ++index;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomIter<>(this);
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> randQueue = new RandomizedQueue<>();
        StdOut.println("isEmpty() = " + randQueue.isEmpty());
        StdOut.println("size() = " + randQueue.size());
        randQueue.enqueue("str0");
        StdOut.println("isEmpty() = " + randQueue.isEmpty());
        StdOut.println("size() = " + randQueue.size());
        for (int i = 1; i < 10; ++i) {
            randQueue.enqueue("str" + i);
        }
        StdOut.println("size() = " + randQueue.size());

        StdOut.print("iterate:");
        for (String str : randQueue) {
            StdOut.print(" " + str);
        }
        StdOut.println();
        StdOut.print("iterate:");
        for (String str : randQueue) {
            StdOut.print(" " + str);
        }
        StdOut.println();

        StdOut.println("dequeue() = " + randQueue.dequeue());
        StdOut.println("dequeue() = " + randQueue.dequeue());
        StdOut.println("dequeue() = " + randQueue.dequeue());
        StdOut.print("iterate:");
        for (String str : randQueue) {
            StdOut.print(" " + str);
        }
        StdOut.println();
        
        while (!randQueue.isEmpty()) {
            StdOut.println("dequeue() = " + randQueue.dequeue());
        }
        StdOut.print("iterate:");
        for (String str : randQueue) {
            StdOut.print(" " + str);
        }
    }
}
