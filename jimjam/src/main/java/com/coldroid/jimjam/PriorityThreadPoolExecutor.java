package com.coldroid.jimjam;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is a {@link ThreadPoolExecutor} which is backed by a {@link PriorityBlockingQueue PriorityQueue}, which is an
 * unbounded queue used to hold {@link Runnable Runnables} when the pool of threads has hit the max thread limit
 * provided in the constructor. The pool of threads will drop down to the minimum thread pool limit when there are no
 * available Runnables.<p/>
 *
 * When you call {@link Executor#execute(Runnable)} the normal behaviour for ThreadPoolExeuctors is to add new threads
 * until reaching the "minimum" number of threads is reached. At this point further calls to execute will be added to
 * the queue,
 * even if the number of threads is under the max thread limit. Only when the queue is full and {@link
 * BlockingDeque#offer(Object)} returns false (meaning it can't add any more) will the ThreadPoolExecutor increase the
 * threads. If the queue is full and the thread limit is at max the ThreadPoolExecutor will drop the runnable.<p/>
 *
 * Because we use an unbounded queue, we will accept Runnables until memory runs out, and there will only ever be the
 * minimum number of threads (or 1 thread if the minimum is 0). To work around this, we provide our own {@link
 * PriorityExecutorBlockingQueue PriorityQueue} implementation. We will fail all calls to offer(Runnable), forcing the
 * ThreadPoolExecutor to always increase the number of threads (because it thinks the queue is full). When the max
 * number of threads is reached and another Runnable is passed in, {@link RejectedExecutionHandler#rejectedExecution
 * (Runnable, ThreadPoolExecutor)} is called. <p/>
 *
 * Here we provide our own implementation of RejectedExecutionHandler() which will add the Runnable to the queue by
 * calling the queues add(E) method which will add the Runnable to the queue. When a Runnable finishes executing in a
 * Thread, the PriorityThreadPoolExecutor will dequeue the highest priority task from the queue and execute it.
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

    public PriorityThreadPoolExecutor(int minimumPoolSize, int maximumPoolSize, long threadKeepAliveMillis) {
        super(minimumPoolSize, maximumPoolSize, threadKeepAliveMillis, TimeUnit.MILLISECONDS, new
                PriorityExecutorBlockingQueue<Runnable>(), new PriorityRejectedExecutionHandler());
    }

    private static class PriorityExecutorBlockingQueue<E> extends PriorityBlockingQueue<E> {
        /**
         * This method is overriden to force the {@link ThreadPoolExecutor} to actually create new threads above the
         * minimum. See @{link PriorityThreadPoolExecutor} for a detailed explanation.
         */
        @Override
        public boolean offer(E e) {
            return false;
        }

        /**
         * We override this method to allow {@link PriorityRejectedExecutionHandler#rejectedExecution(Runnable,
         * ThreadPoolExecutor)} to call into the actual {@link PriorityBlockingQueue#offer(E)} method instead of {@link
         * PriorityExecutorBlockingQueue#offer(Object)} which doesn't do anything.
         */
        @Override
        public boolean add(E e) {
            return super.offer(e);
        }
    }

    private static class PriorityRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            executor.getQueue().add(runnable);
        }
    }
}
