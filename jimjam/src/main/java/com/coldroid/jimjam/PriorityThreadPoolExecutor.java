package com.coldroid.jimjam;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is a {@link ThreadPoolExecutor} which is backed by a {@link PriorityBlockingQueue PriorityQueue}, which is an
 * unbounded queue used to hold {@link Runnable Runnables} when the pool of threads has hit the max thread limit
 * provided in the constructor. The pool of threads will drop down to the minimum thread pool limit when there are no
 * available Runnables.<p/>
 *
 * When you call {@link Executor#execute(Runnable)} the normal behaviour for ThreadPoolExeuctors is to add new threads
 * until reaching the "minimumPoolSize" or 1, whichever is higher. At this point further calls to execute will be added
 * to the queue, even if the number of threads is under the max thread limit. Only when the queue is full and {@link
 * BlockingDeque#offer(Object)} returns false (meaning it can't add any more) will the ThreadPoolExecutor increase the
 * threads. If the queue is full and the thread limit is at max the ThreadPoolExecutor will drop the Runnable.<p/>
 *
 * Because we use an unbounded queue, we will accept Runnables until memory runs out, and there will only ever be the
 * minimum number of threads. To work around this, we provide our own {@link PriorityExecutorBlockingQueue
 * PriorityQueue} implementation. This queue has access to the Executor, so when it sees that more threads can be
 * created it will reject new Runnables, forcing the Executor to spin up a new thread. If the number of workers has
 * reached capacity, then the queue will add the Runnable for later processing when a worker frees up.
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

    public PriorityThreadPoolExecutor(int minimumPoolSize, int maximumPoolSize, long threadKeepAliveMillis) {
        super(minimumPoolSize, maximumPoolSize, threadKeepAliveMillis, TimeUnit.MILLISECONDS,
                new PriorityExecutorBlockingQueue());
        ((PriorityExecutorBlockingQueue) getQueue()).setExecutor(this);
    }

    /**
     * This {@link PriorityBlockingQueue} contains a reference to the {@link ThreadPoolExecutor} it lives in so that it
     * can spy on the Executor, and make informed decisions. This helps us work around an issue in thread pools
     * implementation when using unbounded queues. Instead of creating "max worker count" threads and then adding the
     * overflow to the queue, the executor will create 1 thread and then try to add all new tasks to the queue. When the
     * queue is full it will create new threads upto the max count. Since this queue is unbounded the executor will only
     * ever create 1 thread. <p/>
     *
     * To work around this limitation, we will trick the executor and tell it there is no more room in the queue (return
     * false on {@link #offer(Runnable)} if more workers/threads can be added to the queue. If all threads are working,
     * then we will actually add the {@link Runnable} to the queue.
     */
    private static class PriorityExecutorBlockingQueue extends PriorityBlockingQueue<Runnable> {
        private ThreadPoolExecutor mThreadPoolExecutor;

        private void setExecutor(ThreadPoolExecutor threadPoolExecutor) {
            mThreadPoolExecutor = threadPoolExecutor;
        }

        /**
         * This method is overriden to force the {@link ThreadPoolExecutor} to actually create new threads above the
         * minimum. See @{link PriorityExecutorBlockingQueue} for a detailed explanation.
         */
        @Override
        public boolean offer(Runnable runnable) {
            if (mThreadPoolExecutor.getPoolSize() == mThreadPoolExecutor.getMaximumPoolSize()) {
                return super.offer(runnable);
            }
            return false;
        }
    }
}
