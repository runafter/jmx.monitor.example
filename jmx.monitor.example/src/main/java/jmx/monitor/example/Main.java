package jmx.monitor.example;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;

/**
 * usage : java jmx.monitor.example.Main 127.0.0.1 5555
 * @author runafter
 *
 */
@SuppressWarnings("restriction")
public class Main {
	public static void main(String[] args) throws IOException {
		String hostName = valueOf(args, 0, "127.0.0.1");
		String portNum = valueOf(args, 1, "5555");
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum + "/jmxrmi");
		JMXConnector connector = JMXConnectorFactory.connect(url);
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		
		final RuntimeMXBean runtime = getRemoteRuntimeMXBean(connection);
		print(runtime);
		
		final OperatingSystemMXBean os = getOperatingSystemMXBean(connection);
		print(os);
		
		final ThreadMXBean thread = getThreadMXBean(connection);
		print(thread);
		
		final MemoryMXBean memory = getMemoryMXBean(connection);
		print(memory);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while (true) {
					MemoryUsage heapMemory = memory.getHeapMemoryUsage();
					System.out.println(String.format("%s process[%3.2f]\tsystem[%3.2f]\t heap-used[%s]", current(), toCpuUsagePercentage(os.getProcessCpuLoad()), toCpuUsagePercentage(os.getSystemCpuLoad()), toMemoryUsage(heapMemory.getUsed())));
					try {
						Thread.sleep(500L);
					} catch (InterruptedException e) {
					}
				}
			}




			private String current() {
				return DateFormat.getTimeInstance().format(new Date());
			}
		}).start();
	}
	private static double toCpuUsagePercentage(double usage) {
		return usage * 100;
	}
	private static String toMemoryUsage(long usage) {
		if (usage >= 1000000) {
			return String.format("%.3f MB", usage / 1000000.0);
		} else if (usage >= 1000) {
			return String.format("%.3f KB", usage / 1000.0);
		} else {
			return String.format("%d Byte", usage);
		}
	}
	private static void print(MemoryMXBean memory) {
		System.out.println("--------------------- MemoryMXBean ----------------------");
		System.out.println("HeapMemoryUsage\t: " + memory.getHeapMemoryUsage());
		System.out.println("NonHeapMemoryUsage\t: " + memory.getNonHeapMemoryUsage());
		System.out.println("---------------------------------------------------------\n");
	}

	private static void print(ThreadMXBean thread) {
		System.out.println("--------------------- ThreadMXBean ----------------------");
		long[] threadIds = thread.getAllThreadIds();
		Arrays.sort(threadIds);
		System.out.println("AllThreadIds\t\t\t: " + Arrays.toString(threadIds));
		System.out.println("ThreadCount\t\t\t: " + thread.getThreadCount());
		System.out.println("DaemonThreadCount\t\t: " + thread.getDaemonThreadCount());
		System.out.println("PeakThreadCount\t\t\t: " + thread.getPeakThreadCount());
		System.out.println("TotalStartedThreadCount\t\t: " + thread.getTotalStartedThreadCount());
		System.out.println("CurrentThreadCpuTime\t\t: " + thread.getCurrentThreadCpuTime());
		System.out.println("CurrentThreadUserTime\t\t: " + thread.getCurrentThreadUserTime());
		for (long id : threadIds) {
			String idString = String.format("%3d", id);
			ThreadInfo threadInfo = thread.getThreadInfo(id);
			System.out.println(idString + ".threadInfo.ThreadId\t\t: " + threadInfo.getThreadId());	
			System.out.println(idString + ".threadInfo.ThreadName\t: " + threadInfo.getThreadName());	
			System.out.println(idString + ".threadInfo.ThreadState\t: " + threadInfo.getThreadState());	
			System.out.println(idString + ".ThreadCpuTime\t\t: " + thread.getThreadCpuTime(id));
			System.out.println(idString + ".ThreadUserTime\t\t: " + thread.getThreadUserTime(id));
		}
		System.out.println("---------------------------------------------------------\n");
	}

	private static void print(OperatingSystemMXBean os) {
		System.out.println("----------------- OperatingSystemMXBean -----------------");
		System.out.println("Name\t\t\t: " + os.getName());
		System.out.println("Arch\t\t\t: " + os.getArch());
		System.out.println("Version\t\t\t: " + os.getVersion());
		System.out.println("AvailableProcessors\t: " + os.getAvailableProcessors());
		System.out.println("SystemLoadAverage\t: " + os.getSystemLoadAverage());
		System.out.println("ProcessCpuTime\t: " + os.getProcessCpuTime());
		System.out.println("ProcessCpuLoad\t: " + os.getProcessCpuLoad());
		System.out.println("SystemCpuLoad\t: " + os.getSystemCpuLoad());
		System.out.println("---------------------------------------------------------\n");
	}

	private static void print(RuntimeMXBean runtime) {
		System.out.println("--------------------- RuntimeMXBean ---------------------");
		System.out.println("Name\t\t: " + runtime.getName());
		System.out.println("VmName\t\t: " + runtime.getVmName());
		System.out.println("Vendor\t\t: " + runtime.getVmVendor());
		System.out.println("VmVersion\t: " + runtime.getVmVersion());
		System.out.println("StartTime\t: " + runtime.getStartTime());
		System.out.println("Uptime\t\t: " + runtime.getUptime());
		System.out.println("SpecName\t: " + runtime.getSpecName());
		System.out.println("SpecVendor\t: " + runtime.getSpecVendor());
		System.out.println("SpecVersion\t: " + runtime.getSpecVersion() );
		System.out.println("---------------------------------------------------------\n");
	}

	private static RuntimeMXBean getRemoteRuntimeMXBean(MBeanServerConnection connection) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
	}
	private static OperatingSystemMXBean getOperatingSystemMXBean(MBeanServerConnection connection) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
	}
	private static ThreadMXBean getThreadMXBean(MBeanServerConnection connection) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
	}
	private static MemoryMXBean getMemoryMXBean(MBeanServerConnection connection) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
	}

	private static String valueOf(String values[], int i, String defaultValue) {
		return values == null || values.length <= i ? defaultValue : values[i];
	}
}
