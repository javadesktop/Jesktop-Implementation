package net.sourceforge.jesktopimpl.core;

import org.jesktop.ThreadPool;

public class ThreadPoolImpl implements ThreadPool {

    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
