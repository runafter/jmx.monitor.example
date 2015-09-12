package jmx.monitor.example.target;

import org.apache.commons.cli.ParseException;

/**
 * run with vmargs
 * 
 * <pre>
 * <code>
 * -Dcom.sun.management.jmxremote=true
 * -Dcom.sun.management.jmxremote.port=5555
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 * </code>
 * </pre>
 * 
 * @author runafter
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException, ParseException {
//		Options options = new Options();
//		options.addOption("m", true, "Mode." + );
//		
//		CommandLineParser parser = new DefaultParser();
//		CommandLine cmd = parser.parse(options, args);

//		new Thread(new Runnable(){
//			public void run() {
//				while (true) {
//				}
//			}
//		}, "while(true)").start();
//		runThreadWithSleep(0);
		runThreadWithSleep(10);
		runThreadWithSleep(100);
		runThreadWithSleep(1000);
		while (true) {
			Thread.sleep(1000000L);
		}
	}

	private static void runThreadWithSleep(final long sleep) {
		new Thread(new Runnable(){
			public void run() {
				while (true) {
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
					}
				}
			}
		}, "sleep(" + sleep + ")").start();
	}
}
