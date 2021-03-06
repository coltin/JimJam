package com.coldroid.jimjam;

import com.coldroid.jimjam.queue.LabelledBlockingPriorityQueue;

import java.util.LinkedList;
import java.util.List;
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

    private final List<Job> mWaitingForNetwork = new LinkedList<>();
    private final PriorityExecutorBlockingQueue mWorkerQueue;

    public PriorityThreadPoolExecutor(int minimumPoolSize, int maximumPoolSize, long threadKeepAliveMillis) {
        super(minimumPoolSize, maximumPoolSize, threadKeepAliveMillis, TimeUnit.MILLISECONDS,
                new PriorityExecutorBlockingQueue());
        mWorkerQueue = (PriorityExecutorBlockingQueue) getQueue();
        mWorkerQueue.setExecutor(this);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        mWorkerQueue.unlockQueue(runnable);
    }

    /**
     * Checks whether the current job requires network access. If it does and there is no network access, it will be
     * added to the mWaitingForNetwork list. This list will be processed when the network connection event occurs, and
     * these jobs will be added back to mJobExecutor's main queue for execution.
     *
     * @return boolean whether the job was rescheduled.
     */
    public boolean rescheduleNetworkJob(NetworkUtils networkUtils, Job job) {
        if (!job.requiresNetwork()) {
            return false;
        }
        synchronized (mWaitingForNetwork) {
            if (networkUtils.isNetworkConnected()) {
                return false;
            } else {
                mWaitingForNetwork.add(job);
                return true;
            }
        }
    }

    /**
     * When the network connects we will dump the jobs in the networks waiting queue, and return those jobs to be added
     * to the the executor. The reason we don't add them ourselves is because the JobManager may want to do some
     * pre-processing on those jobs first. In particular
     */
    public List<Job> networkConnected() {
        synchronized (mWaitingForNetwork) {
            List<Job> temporaryList = new LinkedList<>(mWaitingForNetwork);
            mWaitingForNetwork.clear();
            return temporaryList;
        }
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
    private static class PriorityExecutorBlockingQueue extends LabelledBlockingPriorityQueue {
        private ThreadPoolExecutor mThreadPoolExecutor;

        private void setExecutor(ThreadPoolExecutor threadPoolExecutor) {
            mThreadPoolExecutor = threadPoolExecutor;
        }

        /**
         * This is overridden to force the {@link ThreadPoolExecutor} to actually create new threads above the minimum.
         * See @{link PriorityExecutorBlockingQueue} for a detailed explanation.
         *
         * false would mean there is no room in the queue, create a worker.
         *
         * true would mean the {@link Runnable} was added to the queue, so don't create a worker for it.
         */
        @Override
        public boolean offer(Runnable runnable) {
            mLock.lock();
            try {
                if (mThreadPoolExecutor.getPoolSize() == mThreadPoolExecutor.getMaximumPoolSize()
                        || isQueueLocked(runnable)) {
                    return super.offer(runnable);
                } else {
                    lockQueue(runnable);
                    return false;
                }
            } finally {
                mLock.unlock();
            }
        }
    }
}
