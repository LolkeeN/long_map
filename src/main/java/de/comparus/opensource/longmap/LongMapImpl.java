package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LongMapImpl<V> implements LongMap<V> {

    private float loadFactor;
    private final V[] valueArray;
    private List<List<Node<V>>> buckets = new ArrayList<>();

    public LongMapImpl(Class<V> clazz) {
        @SuppressWarnings("unchecked") final V[] a = (V[]) Array.newInstance(clazz, 0);
        this.valueArray = a;
        this.loadFactor = 0.75f;
        addBuckets(5);
    }

    public LongMapImpl(Class<V> clazz, float loadFactor) {
        if (loadFactor > 1 || loadFactor < 0) {
            throw new RuntimeException("Load factor should be more than 0 and less then 1");
        }
        @SuppressWarnings("unchecked") final V[] a = (V[]) Array.newInstance(clazz, 0);
        this.valueArray = a;
        this.loadFactor = loadFactor;
        addBuckets(5);
    }

    @Override
    public V put(long key, V value) {
        Node<V> node = new Node<>();
        node.setKey(key);
        node.setValue(value);

        if (needToRehash()) {
            rehash();
        }

        insertNodeIntoBucket(node, buckets);
        return node.getValue();
    }

    private void rehash() {
        List<List<Node<V>>> newBuckets = new ArrayList<>(buckets.size() + 5);
        for (int i = 0; i < buckets.size() + 5; i++) {
            newBuckets.add(new ArrayList<>());
        }
        for (List<Node<V>> nodes : buckets) {
            for (Node<V> bucketNode : nodes) {
                insertNodeIntoBucket(bucketNode, newBuckets);
            }
        }
        buckets = newBuckets;
    }

    private boolean needToRehash() {
        return size() / (float) buckets.size() > loadFactor;
    }

    private void insertNodeIntoBucket(Node<V> node, List<List<Node<V>>> buckets) {
        int bucketIndex = getBucketIndex(node);
        List<Node<V>> bucket = buckets.get(bucketIndex);
        if (!buckets.get(bucketIndex).isEmpty()) {
            bucket.add(node);
            bucket.get(bucket.size() - 1).setNext(node);
        } else {
            bucket.add(node);
        }
    }

    private int getBucketIndex(Node<V> node) {
        return node.hashCode() % buckets.size();
    }

    private void addBuckets(int count) {
        for (int i = 0; i < count; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    @Override
    public V get(long key) {
        Node<V> node = new Node<>();
        node.setKey(key);
        List<Node<V>> nodes = buckets.get(getBucketIndex(node));
        if (nodes.isEmpty()) {
            return null;
        }
        for (Node<V> nodeInBucket : nodes) {
            if (nodeInBucket.getKey() == key) {
                return nodeInBucket.value;
            }
        }
        return null;
    }

    @Override
    public V remove(long key) {
        Node<V> node = new Node<>();
        node.setKey(key);
        List<Node<V>> bucket = buckets.get(getBucketIndex(node));
        Node<V> resultNode;
        if (!bucket.isEmpty()) {
            resultNode = bucket.get(0);
        } else {
            resultNode = null;
        }
        bucket.clear();
        return resultNode == null ? null : resultNode.getValue();
    }

    @Override
    public boolean isEmpty() {
        return buckets.isEmpty() || buckets.stream()
                .allMatch(List::isEmpty);
    }

    @Override
    public boolean containsKey(long key) {
        boolean contains;
        for (List<Node<V>> bucket : buckets) {
            if (!bucket.isEmpty()) {
                contains = bucket.stream()
                        .anyMatch(x -> x.getKey() == key);
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        boolean contains;
        for (List<Node<V>> bucket : buckets) {
            if (!bucket.isEmpty()) {
                contains = bucket.stream()
                        .anyMatch(x -> x.getValue().equals(value));
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        return buckets.stream()
                .mapToLong(x -> x.stream()
                        .map(Node::getKey)
                        .findFirst().orElse(Long.MIN_VALUE))
                .filter(x -> x != Long.MIN_VALUE)
                .toArray();
    }

    @Override
    public V[] values() {
        List<V> collect = buckets.stream()
                .flatMap(x -> x.stream()
                        .map(Node::getValue))
                .collect(Collectors.toList());
        return toArray(collect);
    }

    @Override
    public long size() {
        return buckets.stream()
                .filter(x -> !x.isEmpty())
                .mapToLong(x -> (long) x.size())
                .reduce(Long::sum).orElse(0);
    }

    @Override
    public void clear() {
        buckets.clear();
    }

    private V[] toArray(List<V> list) {
        V[] result = Arrays.copyOf(valueArray, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private static class Node<V> {

        private long key;
        private V value;
        private Node<V> next;

        public long getKey() {
            return key;
        }

        public void setKey(long key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<V> getNext() {
            return next;
        }

        public void setNext(Node<V> next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node<?> node = (Node<?>) o;

            return key == node.key;
        }

        @Override
        public int hashCode() {
            return (int) (key ^ (key >>> 32));
        }
    }
}
