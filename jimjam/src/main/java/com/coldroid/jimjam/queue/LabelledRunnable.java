package com.coldroid.jimjam.queue;

public interface LabelledRunnable<T> extends LabelledElement, Runnable, Comparable<T> {
}