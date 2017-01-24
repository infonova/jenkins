package jenkins.util;

import hudson.util.DaemonThreadFactory;
import hudson.util.NamingThreadFactory;
import jenkins.model.Configuration;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Holds the {@link ScheduledExecutorService} for running async DependencyGraphRebuild tasks in Jenkins.
 * This ExecutorService will create additional threads to execute due (enabled) tasks.
 *
 * @author Stefan Eder
 * @since 1.609.3-INFONOVA-3
 */
public class DependencyGraphRebuildTimer {

    private static final int THREAD_POOL_SIZE = Configuration.getIntConfigParameter("dgcThreadPoolSize", 10);

    private static ScheduledExecutorService executorService;


    /**
     * Returns the scheduled executor service used by the async dependency rebuild method.
     *
     * @return the single {@link ScheduledExecutorService}.
     */
    @Nonnull
    public static synchronized ScheduledExecutorService get() {
        if (executorService == null) {
            executorService =  new ErrorLoggingScheduledThreadPoolExecutor(THREAD_POOL_SIZE, new NamingThreadFactory(new DaemonThreadFactory(), "jenkins.util.DependencyGraphRebuildTimer"));
        }
        return executorService;
    }

    /**
     * Shutdown the timer and throw it away.
     */
    public static synchronized void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    /**
     * Do not create this.
     */
    private DependencyGraphRebuildTimer() {};

}
