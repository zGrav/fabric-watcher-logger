package zgrav.fabricwatcherlogger;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import static zgrav.fabricwatcherlogger.Utils.sendToDiscord;

public class FabricWatcherLogger implements ModInitializer {
    private static final String MODID = "zgrav-fabric-watcher-logger";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    private static int minutes = 30;
    private static long millis = minutes * 60 * 1000;

    @Override
    public void onInitialize() {
        LOGGER.info("Loaded!");

        Timer timer = new Timer();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int playersOnline = server.getCurrentPlayerCount();
                int maxPlayers = server.getMaxPlayerCount();

                double cpuLoad = ManagementFactory.getPlatformMXBean(
                        com.sun.management.OperatingSystemMXBean.class).getProcessCpuLoad();
                int cpuUsageRounded = (int) Math.round(cpuLoad * 100);

                MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMaximumFractionDigits(2);

                MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                long heapMemoryUsageBytes = heapMemoryUsage.getUsed();
                long heapMemoryMaxBytes = heapMemoryUsage.getMax();

                MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();
                long nonHeapMemoryUsageBytes = nonHeapMemoryUsage.getUsed();
                long nonHeapMemoryMaxBytes = nonHeapMemoryUsage.getMax();

                long totalMemoryUsageBytes = heapMemoryUsageBytes + nonHeapMemoryUsageBytes;
                String totalMemoryUsageString = numberFormat.format(totalMemoryUsageBytes / (1024.0 * 1024.0)) + "MB";

                long totalMemoryMaxBytes = heapMemoryMaxBytes + nonHeapMemoryMaxBytes;
                String totalMemoryMaxString = numberFormat.format(totalMemoryMaxBytes / (1024.0 * 1024.0)) + "MB";

                long totalPhysicalMemoryBytes = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getTotalMemorySize();
                String totalPhysicalMemoryString = numberFormat.format(totalPhysicalMemoryBytes / (1024.0 * 1024.0)) + "MB";

                long freePhysicalMemoryBytes = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getFreeMemorySize();
                String freePhysicalMemoryString = numberFormat.format(freePhysicalMemoryBytes / (1024.0 * 1024.0)) + "MB";

                double systemCpuLoad = ManagementFactory.getPlatformMXBean(
                        com.sun.management.OperatingSystemMXBean.class).getCpuLoad();
                int systemCpuLoadRounded = (int) Math.round(systemCpuLoad * 100);

                sendToDiscord("Players online: " + playersOnline + " - Max players: " + maxPlayers + " - JVM CPU Usage: " + cpuUsageRounded + "% - JVM RAM Usage: " + totalMemoryUsageString + " / " + totalMemoryMaxString + " - System CPU Load: " + systemCpuLoadRounded + "% - Physical RAM Free: " + freePhysicalMemoryString + " - Physical RAM Total: " + totalPhysicalMemoryString);
            }
        }, 0, millis));

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> timer.cancel());
    }
}
