package com.coldroid.jimjam.queue;

import android.util.Log;

import com.coldroid.jimjam.PriorityThreadPoolExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Acts as a {@link PriorityBlockingQueue} where each element of the queue has a label. If an element outside the queue
 * is active/running, all elements with that items label are considered inactive and won't be returned by this class. So
 * if "image_upload" is running and a worker wants a new job from this queue, all "image_upload" jobs are unavailable,
 * and some other job will be returned (or the thread will be put to sleep if there are no available jobs).
 *
 * This class is meant to be used with {@link PriorityThreadPoolExecutor} so while this class looks like it is of type
 * Runnable, it is actually of type {@link LabelledRunnable}. Passing in things that are not of this type will fail.
 * Yeah it's gross. If you can fix this, please open a pull request.
 *
 * Logs on this class are very temporary and will not be in the release.
 */
public class LabelledBlockingPriorityQueue extends BlockingQueueAdapter<Runnable> {

    protected final ReentrantLock mLock;
    private final Condition mNotEmpty;

    private final Map<String, LockingPriorityQueue<LabelledRunnable>> labeledQueues = new HashMap<>();
    private int mAvailableToRun;

    public LabelledBlockingPriorityQueue() {
        Log.e("LabelledBlockingPrio", "create()");
        mLock = new ReentrantLock();
        mNotEmpty = mLock.newCondition();
    }

    @Override
    public boolean offer(Runnable runnable) {
        Log.e("LabelledBlockingPrio", "offer()");
        LabelledRunnable labelledRunnable = ((LabelledRunnable) runnable);
        String label = labelledRunnable.getLabel();
        mLock.lock();
        try {
            ensureQueueExists(labelledRunnable.getLabel());
            LockingPriorityQueue<LabelledRunnable> queue = labeledQueues.get(label);
            queue.offer(labelledRunnable);
            if (!queue.isLocked()) {
                mAvailableToRun++;
                mNotEmpty.signal();
            }
        } finally {
            mLock.unlock();
        }
        return true;
    }

    @Override
    public boolean remove(Object object) {
        Log.e("LabelledBlockingPrio", "remove()");
        LabelledRunnable labelledRunnable = (LabelledRunnable) object;
        mLock.lock();
        try {
            LockingPriorityQueue<LabelledRunnable> queue = labeledQueues.get(labelledRunnable.getLabel());
            if (queue == null || !queue.remove(object)) {
                return false;
            } else {
                mAvailableToRun--;
                return true;
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Runnable take() throws InterruptedException {
        Log.e("LabelledBlockingPrio", "take()");
        mLock.lockInterruptibly();
        try {
            while (isEmpty()) {
                Log.e("LabelledBlockingPrio", "take.await()");
                mNotEmpty.await();
            }
            return poll();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Runnable poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Log.e("LabelledBlockingPrio", "poll(long, TimeUnit)");
        long nanoTimeout = timeUnit.toNanos(timeout);
        mLock.lockInterruptibly();
        try {
            while (isEmpty() && nanoTimeout > 0) {
                Log.e("LabelledBlockingPrio", "await()");
                nanoTimeout = mNotEmpty.awaitNanos(nanoTimeout);
            }
            return poll();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Returns one with the highest priority Runnable from the unlocked queues and locks the queue it belongs to. Ties
     * are broken at random. The Runnable is also removed from that queue.
     *
     * Note: The "null" label queue will not actually lock.
     */
    @Override
    public Runnable poll() {
        Log.e("LabelledBlockingPrio", "poll()");
        mLock.lock();
        try {
            // Not required, but good to fail fast.
            if (isEmpty()) {
                return null;
            }
            String highestPriorityLabel = null;
            LabelledRunnable highestPriorityRunnable = null;
            for (Map.Entry<String, LockingPriorityQueue<LabelledRunnable>> entry : labeledQueues.entrySet()) {
                String label = entry.getKey();
                LockingPriorityQueue<LabelledRunnable> queue = entry.getValue();
                if (!queue.isLocked() && !queue.isEmpty()) {
                    if (highestPriorityRunnable == null || highestPriorityRunnable.compareTo(queue.peek()) > 1) {
                        highestPriorityRunnable = queue.peek();
                        highestPriorityLabel = label;
                    }
                }
            }
            if (highestPriorityRunnable == null) {
                return null;
            } else {
                LockingPriorityQueue<LabelledRunnable> queue = labeledQueues.get(highestPriorityLabel);
                queue.lock();
                if (highestPriorityLabel == null) {
                    mAvailableToRun -= 1;
                } else {
                    mAvailableToRun -= queue.size();
                }
                return queue.poll();
            }
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Empty means that there are no jobs available to run. This is true when the null labelled queue is empty and all
     * the other labelled queues are locked or empty.
     */
    @Override
    public boolean isEmpty() {
        Log.e("LabelledBlockingPrio", "isEmpty(" + mAvailableToRun + ")");
        return mAvailableToRun == 0;
    }

    @Override
    public int size() {
        Log.e("LabelledBlockingPrio", "size()");
        return mAvailableToRun;
    }

    public void unlockQueue(Runnable runnable) {
        Log.e("LabelledBlockingPrio", "unlockQueue() " + mAvailableToRun);
        mLock.lock();
        try {
            LockingPriorityQueue<LabelledRunnable> queue = labeledQueues.get(((LabelledRunnable) runnable).getLabel());
            if (queue.unlock()) {
                mAvailableToRun += queue.size();
            }
        } finally {
            mLock.unlock();
        }
    }

    protected void lockQueue(Runnable runnable) {
        Log.e("LabelledBlockingPrio", "lockQueue() " + mAvailableToRun);
        mLock.lock();
        try {
            LockingPriorityQueue<LabelledRunnable> queue = labeledQueues.get(((LabelledRunnable) runnable).getLabel());
            if (queue.lock()) {
                mAvailableToRun -= queue.size();
            }
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Returns whether the queue for this LabelledRunnable is locked from running.
     */
    protected boolean isQueueLocked(Runnable runnable) {
        Log.e("LabelledBlockingPrio", "isQueueLocked() " + mAvailableToRun);
        String label = ((LabelledRunnable) runnable).getLabel();
        mLock.lock();
        try {
            if (ensureQueueExists(label)) {
                return labeledQueues.get(label).isLocked();
            } else {
                return false;
            }
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Convenience method to ensure the labelledQueues contain this label.
     *
     * Returns whether there was a queue already.
     */
    private boolean ensureQueueExists(String label) {
        if (labeledQueues.containsKey(label)) {
            return true;
        } else {
            labeledQueues.put(label, new LockingPriorityQueue<LabelledRunnable>(label));
            return false;
        }
    }
}
