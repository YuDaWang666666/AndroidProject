package com.example.newbeemall.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全局线程池管理器，替代裸 new Thread()。
 * 提供 IO 线程池 + 主线程 Handler，避免线程泄漏和 ANR。
 */
public class AppExecutors {

    private static final AppExecutors INSTANCE = new AppExecutors();

    private final ExecutorService ioExecutor = Executors.newFixedThreadPool(3);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static AppExecutors getInstance() {
        return INSTANCE;
    }

    /** 在 IO 线程执行任务 */
    public void executeOnIO(Runnable task) {
        ioExecutor.execute(task);
    }

    /** 在主线程执行任务 */
    public void runOnMain(Runnable task) {
        mainHandler.post(task);
    }

    /**
     * 创建一个生命周期感知的异步任务。
     * Fragment/Activity 销毁后自动跳过回调，防止泄漏和崩溃。
     */
    public LifecycleTask createLifecycleTask() {
        return new LifecycleTask();
    }

    /**
     * 后台任务的回调接口，支持返回结果。
     */
    public interface BackgroundCallable<T> {
        T call() throws Exception;
    }

    public interface ResultCallback<T> {
        void onResult(T result);
    }

    public interface ErrorCallback {
        void onError();
    }

    public static class LifecycleTask {
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        /**
         * 执行带返回值的后台任务，生命周期结束后自动跳过回调。
         * @param background 后台任务，返回结果
         * @param onSuccess 成功回调（UI 线程）
         * @param onError 失败回调（UI 线程）
         */
        public <T> void submit(BackgroundCallable<T> background, ResultCallback<T> onSuccess, ErrorCallback onError) {
            AppExecutors.getInstance().executeOnIO(() -> {
                if (cancelled.get()) return;
                try {
                    T result = background.call();
                    if (cancelled.get()) return;
                    AppExecutors.getInstance().runOnMain(() -> {
                        if (!cancelled.get()) onSuccess.onResult(result);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cancelled.get()) return;
                    AppExecutors.getInstance().runOnMain(() -> {
                        if (!cancelled.get()) onError.onError();
                    });
                }
            });
        }

        /**
         * 执行带返回值的后台任务，无错误回调。
         */
        public <T> void submit(BackgroundCallable<T> background, ResultCallback<T> onSuccess) {
            submit(background, onSuccess, () -> {});
        }

        /**
         * 执行无返回值的后台任务，带异常处理。
         */
        public void execute(Runnable background, Runnable onSuccess, Runnable onError) {
            AppExecutors.getInstance().executeOnIO(() -> {
                if (cancelled.get()) return;
                try {
                    background.run();
                    if (cancelled.get()) return;
                    AppExecutors.getInstance().runOnMain(() -> {
                        if (!cancelled.get()) onSuccess.run();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cancelled.get()) return;
                    AppExecutors.getInstance().runOnMain(() -> {
                        if (!cancelled.get()) onError.run();
                    });
                }
            });
        }

        /**
         * 执行无返回值的后台任务。
         */
        public void execute(Runnable background, Runnable onSuccess) {
            execute(background, onSuccess, () -> {});
        }

        /** 取消任务（在 onDestroy 中调用） */
        public void cancel() {
            cancelled.set(true);
        }

        public boolean isCancelled() {
            return cancelled.get();
        }
    }
}
