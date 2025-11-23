package dataaccessobjects.tilejobs;

import dataaccessinterface.TileNotFoundException;
import dataaccessobjects.CachedTileRepository;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;


/** The scheduler class for TileJobs which tracks which TileJobs
 *  are being processed and selects which ones to process next on different
 *  threads
 */
public class TileJobSystem {
    private final LinkedBlockingDeque<TileJob> queue = new LinkedBlockingDeque<>();
    private final Set<TileJob> processingJobs = Collections.synchronizedSet(new HashSet<>());

    public TileJobSystem(int numWorkers) {
        try (ExecutorService executor = Executors.newFixedThreadPool(numWorkers)) {
            for (int i = 0; i < numWorkers; i++) {
                executor.submit(this::worker);
            }
        }
    }

    /**
     * Submit a {@link TileJob} to be completed
     *
     * @param job   the TileJob to be completed
     */
    public void submitJob(TileJob job){
        processingJobs.add(job);
        queue.add(job);
    }

    private void processJob(TileJob job){
        try {

            BufferedImage image = CachedTileRepository.getInstance().getTileImageData(job.getTile());
            processingJobs.remove(job);
            job.getFuture().component2().complete(image);


        } catch (TileNotFoundException e) {
            processingJobs.remove(job);
            job.getFuture().component2().completeExceptionally(e);
        }
    }

    private void worker(){
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TileJob job = queue.take();
                processJob(job);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }
}
