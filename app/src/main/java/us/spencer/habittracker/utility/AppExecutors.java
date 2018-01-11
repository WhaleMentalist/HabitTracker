package us.spencer.habittracker.utility;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Modified from source code on 'googlesamples/android-architecture'. It will allow
 * certain tasks to be executed on seperate thread (i.e disk reading or GUI thread).
 * It will contain executors for each specified task. It will expand as new functionality
 * is added such as networking. This will also prevent unnecessary waiting from
 * disk I/O slowness.
 */
public class AppExecutors {

    /** Eventually plan to add a network, so another thread is necessary */
    private static final int THREAD_COUNT = 2;

    private final Executor mDiskIO;

    private final Executor mMainThread;

    private AppExecutors(DiskIOThreadExecutor diskIO, MainThreadExecutor mainThread) {
        mDiskIO = diskIO;
        mMainThread = mainThread;
    }

    public AppExecutors() {
        this(new DiskIOThreadExecutor(), new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }

    /**
     * Return executor that will run I/O in background
     *
     * @return  the {@link Executor} that is responsible for handling I/O based operations
     */
    public Executor diskIO() {
        return mDiskIO;
    }

    /**
     * Return executor that will run UI elements
     *
     * @return the {@link Executor} that is responsible for handling UI
     */
    public Executor mainThread() {
        return mMainThread;
    }
}
