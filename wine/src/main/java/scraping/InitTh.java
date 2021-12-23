package scraping;



import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class InitTh {

    public void initThread() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final ScheduledFuture<?> scraperHandle =scheduler.scheduleAtFixedRate(new ScraperThread(), 0, 1, TimeUnit.MINUTES);
        scheduler.schedule(new Runnable() {
            public void run() { scraperHandle.cancel(true); }
        }, 10, TimeUnit.MINUTES);
    }
}
