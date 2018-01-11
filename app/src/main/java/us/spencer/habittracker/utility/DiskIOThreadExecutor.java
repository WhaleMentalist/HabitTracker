package us.spencer.habittracker.utility;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Taken from source code on 'googlesamples/android-architecture'.
 * It basically will allow disk I/O to be handled on a seperate
 * thread.
 */
public class DiskIOThreadExecutor implements ExecutorService {

    private final ExecutorService mDiskIO;

    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable runnable) {
        mDiskIO.execute(runnable);
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> callable) {
        return mDiskIO.submit(callable);
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable runnable) {
        return mDiskIO.submit(runnable);
    }

    @Override
    public <T> Future<T> submit(@NonNull Runnable runnable, T type) {
        return mDiskIO.submit(runnable, type);
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        return mDiskIO.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks,
                                         long timeout, TimeUnit unit) throws InterruptedException {
        return mDiskIO.invokeAll(tasks, timeout, unit);
    }

    @NonNull
    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return mDiskIO.invokeAny(tasks);
    }

    @NonNull
    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks,
                                         long timeout, TimeUnit unit) throws InterruptedException,
                                                                                ExecutionException,
                                                                                TimeoutException {
        return mDiskIO.invokeAny(tasks, timeout, unit);
    }

    @Override
    public boolean isShutdown() {
        return mDiskIO.isShutdown();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return mDiskIO.shutdownNow();
    }

    @Override
    public boolean isTerminated() {
        return mDiskIO.isTerminated();
    }

    @Override
    public boolean awaitTermination(long l, @NonNull TimeUnit timeUnit)
                                                                    throws InterruptedException {
        return mDiskIO.awaitTermination(l, timeUnit);
    }

    @Override
    public void shutdown() {
        mDiskIO.shutdown();
    }
}
