package uz.mh.messenger.model;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
public class MyLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyLog.class);

    public void logMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = runtime.freeMemory();

        LOGGER.debug("Heap Memory Usage - Total: {} bytes, Free: {} bytes, Used: {} bytes",
                totalMemory, freeMemory, usedMemory);
    }
}
