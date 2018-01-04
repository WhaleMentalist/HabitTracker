package us.spencer.habittracker.utility;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Modified from source code on 'googlesamples/android-architecture'. It will allow
 * certain tasks to be executed on seperate thread (i.e disk reading or GUI thread).
 * It will contain executors for each specified task. It will expand as new functionality
 * is added such as networking. This will also prevent unnecessary waiting from
 * disk I/O slowness.
 */
public class AppExecutor {

    private static final int THREAD_COUNT = 2;

    private final Executor mDiskIO;

    private final Executor mMainThread;

    private AppExecutor(DiskIOThreadExecutor diskIO, MainThreadExecutor mainThread) {
        mDiskIO = diskIO;
        mMainThread = mainThread;
    }

    public AppExecutor() {
        this(new DiskIOThreadExecutor(), new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}
