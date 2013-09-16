package info.monitorenter.gui.chart.demos;

import com.jme3.math.Vector3f;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.io.ADataCollector;
import src.LibUSBTest;
import src.USBLIstener;

public class LowPassFilterCollector extends ADataCollector implements
		USBLIstener {

	private long startTime;
	private static float ALPHA = 0.005f;
	
	private static float xCenterAcc = -350f;
	private static float yCenterAcc = -300f;
	private static float zCenterAcc = -200f;
	
	private static float xScaleAcc = 33100f;
	private static float yScaleAcc = 33600f;
	private static float zScaleAcc = 32400f;
	
	LibUSBTest usb = new LibUSBTest();
	private float value;

	public LowPassFilterCollector(ITrace2D trace, long latency) {
		super(trace, latency);
		usb.setListener(this);
		new Thread(usb).start();

		startTime = (long) (System.nanoTime()/1000000f);
	}

	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		float xF = (x - xCenterAcc);
		float yF = (y - yCenterAcc);
		float zF = (z - zCenterAcc);
		
		xF /= xScaleAcc;
		yF /= yScaleAcc;
		zF /= zScaleAcc;
		
		Vector3f dist = new Vector3f(xF,yF,zF);
		value = value + ALPHA * (yF - value);
	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		// TODO Auto-generated method stub

	}

	@Override
	public ITracePoint2D collectData() {
		TracePoint2D out = new TracePoint2D(System.nanoTime()/1000000f - startTime, value);
		return out;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDCM(float[] q) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEulerianBypass(float[] ypr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPWM(long pwm) {
		// TODO Auto-generated method stub
		
	}

}
