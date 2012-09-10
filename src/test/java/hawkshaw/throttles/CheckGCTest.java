package hawkshaw.throttles;

import hawkshaw.drivers.GcChurner;
import hawkshaw.drivers.SimpleMain;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.junit.Assert;
import org.junit.Test;

public class CheckGCTest {

    @Test
    public void checkManagedCacheAllocationCausesGC() {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        SimpleMain.run(1000000);
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
    }

    @Test
    public void checkDualThreadedManagedCacheAllocationCausesGC() {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        GcChurner.run(40);
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
        System.out.println(afterCount+" "+beforeCount);
    }

    private static GarbageCollectorMXBean getGCMBean() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            GarbageCollectorMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, "java.lang:type=GarbageCollector,name=PS MarkSweep", GarbageCollectorMXBean.class);
            return bean;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
