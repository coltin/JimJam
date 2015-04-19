package com.coldroid.jimjam.queue;

import android.util.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class stubs out all BlockingQueue methods, so only the methods you care about need to be implemented.
 *
 * Logs are very temporary.
 */
public class BlockingQueueAdapter<E> implements BlockingQueue<E> {
    @Override
    public boolean add(E e) {
        Log.e("BlockingQueueAdapter", "add()");
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Log.e("BlockingQueueAdapter", "addAll()");
        return false;
    }

    @Override
    public void clear() {
        Log.e("BlockingQueueAdapter", "clear()");
    }

    @Override
    public boolean offer(E e) {
        Log.e("BlockingQueueAdapter", "offer()");
        return false;
    }

    @Override
    public E remove() {
        Log.e("BlockingQueueAdapter", "remove()");
        return null;
    }

    @Override
    public E poll() {
        Log.e("BlockingQueueAdapter", "poll()");
        return null;
    }

    @Override
    public E element() {
        Log.e("BlockingQueueAdapter", "element()");
        return null;
    }

    @Override
    public E peek() {
        Log.e("BlockingQueueAdapter", "peek()");
        return null;
    }

    @Override
    public void put(E e) throws InterruptedException {
        Log.e("BlockingQueueAdapter", "put()");
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        Log.e("BlockingQueueAdapter", "offer(E, long, TimeUnit)");
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        Log.e("BlockingQueueAdapter", "take()");
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        Log.e("BlockingQueueAdapter", "poll(long, TimeUnit)");
        return null;
    }

    @Override
    public int remainingCapacity() {
        Log.e("BlockingQueueAdapter", "remainingCapacity()");
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        Log.e("BlockingQueueAdapter", "remove()");
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Log.e("BlockingQueueAdapter", "removeAll()");
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Log.e("BlockingQueueAdapter", "retainAll()");
        return false;
    }

    @Override
    public int size() {
        Log.e("BlockingQueueAdapter", "size()");
        return 0;
    }

    @Override
    public Object[] toArray() {
        Log.e("BlockingQueueAdapter", "toArray()");
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] array) {
        Log.e("BlockingQueueAdapter", "toArray()");
        return null;
    }

    @Override
    public boolean contains(Object o) {
        Log.e("BlockingQueueAdapter", "contains()");
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Log.e("BlockingQueueAdapter", "containsAll()");
        return false;
    }

    @Override
    public boolean isEmpty() {
        Log.e("BlockingQueueAdapter", "isEmpty()");
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        Log.e("BlockingQueueAdapter", "iterator()");
        return null;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        Log.e("BlockingQueueAdapter", "drainTo()");
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        Log.e("BlockingQueueAdapter", "drainTo(maxElements)");
        return 0;
    }
}
