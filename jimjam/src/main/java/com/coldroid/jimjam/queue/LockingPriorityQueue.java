package com.coldroid.jimjam.queue;

import java.util.PriorityQueue;

/**
 * This is a wrapper around {@link PriorityQueue} which allows it to be "locked". The locking and unlocking is done by
 * the user of this class.
 */
public class LockingPriorityQueue<E> extends PriorityQueue<E> {
    private boolean mLocked = false;
    private final boolean mLockable;

    public LockingPriorityQueue(String label) {
        // Null labels are not lockable.
        mLockable = label != null;
    }

    @Override
    public boolean offer(E o) {
        return super.offer(o);
    }

    public boolean isLocked() {
        return mLocked;
    }

    public boolean lock() {
        // We do not lock null labels.
        return mLocked = mLockable;
    }

    public boolean unlock() {
        boolean wasLocked = mLocked;
        mLocked = false;
        return wasLocked;
    }
}
