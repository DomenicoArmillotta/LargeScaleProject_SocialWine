package scraping;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Start thread for scraping and keep it on for a period of time.
 */
public class InitTh {

    /**
     * Start thread and assign it to scheduler
     */
    public void initThread() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final ScheduledFuture<?> scraperHandle =scheduler.scheduleAtFixedRate(new ScraperThread(), 0, 1, TimeUnit.MINUTES);
        scheduler.schedule(new Runnable() {
            public void run() { scraperHandle.cancel(true); }
        }, 15, TimeUnit.HOURS);
    }
}
