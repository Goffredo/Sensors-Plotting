package info.monitorenter.gui.chart.demos;

import src.LibUSBTest;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.io.ADataCollector;

public class STMCollector extends ADataCollector {

	LibUSBTest usb = new LibUSBTest();
	private long startTime;
	
	public STMCollector(ITrace2D trace, long latency) {
		super(trace, latency);
		new Thread(usb).start();
		startTime = (long) (System.nanoTime()/1000000f);
	}

	@Override
	public ITracePoint2D collectData() {
		TracePoint2D out = new TracePoint2D(System.nanoTime()/1000000f - startTime, usb.get(0));
		return out;
	}

}
