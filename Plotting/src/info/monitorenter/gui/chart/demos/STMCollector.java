package info.monitorenter.gui.chart.demos;

import java.util.Arrays;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import src.LibUSBTest;
import src.USBLIstener;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.io.ADataCollector;

public class STMCollector extends ADataCollector implements USBLIstener{

	LibUSBTest usb = new LibUSBTest();
	private long startTime;
	private short[] gyro = new short[3];
	private short[] acc = new short[3];
	private short[] mag = new short[3];
	static float magCompensationZ = 1100f/980f;
	
	public STMCollector(ITrace2D trace, long latency) {
		super(trace, latency);
		usb.setListener(this);
		new Thread(usb).start();
		
		startTime = (long) (System.nanoTime()/1000000f);
	}

	@Override
	public ITracePoint2D collectData() {
		Vector3f magV = new Vector3f(mag[0],mag[2],mag[1]);
		Vector3f accV = new Vector3f(acc[0],acc[1],acc[2]);
		TracePoint2D out = new TracePoint2D(System.nanoTime()/1000000f - startTime, magV.length());
		return out;
	}

	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		acc[0] = x;
		acc[1] = y;
		acc[2] = z;
	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		mag[0] = x;
		mag[1] = (short) (y * magCompensationZ);
		mag[2] = z;
		System.out.println(Arrays.toString(mag));
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		gyro[0] = x;
		gyro[1] = y;
		gyro[2] = z;
	}

}
