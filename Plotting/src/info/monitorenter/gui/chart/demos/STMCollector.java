package info.monitorenter.gui.chart.demos;

import java.util.Arrays;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

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
	private short[] accRaw = new short[3];
	private short[] acc = new short[3];
	private float[] magRaw = new float[3];
	private float[] mag = new float[3];
	private float[] maxMag = new float[3];
	private float[] minMag = new float[3];
	private float[] magS = new float[3];
	private float[] quat = new float[4];
	private long pwm;

	private static float xCenterMag = -18.5f;
	private static float yCenterMag = -27.5f;
	private static float zCenterMag = 8.0f;

	private static float xCenterAcc = -350f;
	private static float yCenterAcc = -300f;
	private static float zCenterAcc = -200f;

	private static float xScaleAcc = 33100f;
	private static float yScaleAcc = 33600f;
	private static float zScaleAcc = 32400f;

	private static float xScaleMag = 548f;
	private static float yScaleMag = 530f;
	private static float zScaleMag = 545f;

	static float toRad = FastMath.DEG_TO_RAD*(2293.76f/32768f);
	static final float toDeg = 2293.76f/32768f;
	private static final float ALPHA = 0.1f;

	public STMCollector(ITrace2D trace, long latency) {
		super(trace, latency);
		usb.setListener(this);
		new Thread(usb).start();

		startTime = (long) (System.nanoTime()/1000000f);
	}

	@Override
	public ITracePoint2D collectData() {
		Vector3f magV = new Vector3f(magRaw[0],magRaw[2],magRaw[1]);
		Vector3f accV = new Vector3f(accRaw[0],accRaw[1],accRaw[2]);
		TracePoint2D out = new TracePoint2D(System.nanoTime()/1000000f - startTime, pwm);
		return out;
	}

	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		accRaw[0] = x;
		accRaw[1] = y;
		accRaw[2] = z;
		
		float xF = (x - xCenterAcc);
		float yF = (y - yCenterAcc);
		float zF = (z - zCenterAcc);

		xF /= xScaleAcc;
		yF /= yScaleAcc;
		zF /= zScaleAcc;

		acc[0] = (short) (xF * 200);
		acc[1] = (short) (yF * 200);
		acc[2] = (short) (zF * 200);	

	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		magRaw[0] = x;
		magRaw[1] = y;
		magRaw[2] = z;
		
		float xF = 0, yF = 0, zF = 0;

		xF = x - xCenterMag;
		yF = y - yCenterMag;
		zF = z - zCenterMag;

		yF *= 100/yScaleMag;
		xF *= 100/xScaleMag;
		zF *= 100/zScaleMag;	
		
		magS[0] += (xF - magS [0]) * ALPHA;
		magS[1] += (xF - magS [1]) * ALPHA;
		magS[2] += (xF - magS [2]) * ALPHA;

		mag[0] = (short) (xF);
		mag[1] = (short) (yF);
		mag[2] = (short) (zF);	

		if(mag[0]>maxMag[0]){
			maxMag[0] = mag[0];
		}
		if(mag[0]<minMag[0]){
			minMag[0] = mag[0];
		}
		if(mag[1]>maxMag[1]){
			maxMag[1] = mag[1];
		}
		if(mag[1]<minMag[1]){
			minMag[1] = mag[1];
		}
		if(mag[2]>maxMag[2]){
			maxMag[2] = mag[2];
		}
		if(mag[2]<minMag[2]){
			minMag[2] = mag[2];
		}
		System.out.println("Current: "+Arrays.toString(mag));
		System.out.println("Min: "+Arrays.toString(minMag));
		System.out.println("Max: "+Arrays.toString(maxMag));
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		gyro[0] = x;
		gyro[1] = y;
		gyro[2] = z;
	}

	@Override
	public void setDCM(float[] q) {
		quat[0]  = q[0];
		quat[1]  = q[1];
		quat[2]  = q[2];
		quat[3]  = q[3];
	}

	@Override
	public void setEulerianBypass(float[] ypr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPWM(long pwm) {
		this.pwm = pwm;
	}

}
