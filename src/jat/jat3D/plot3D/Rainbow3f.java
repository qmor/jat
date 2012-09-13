package jat.jat3D.plot3D;

import javax.vecmath.Color3f;


public class Rainbow3f
{
	private double min,max;
	private static Color3f rtable[] = new Color3f[100];
	static
	{
		for(int j=0;j<40;j++)
		{
			byte r = (byte) (255 * (1 - j/40.0));
			byte g = (byte) (255 * (j/40.0));
			byte b = 0;
			rtable[j] = new Color3f(r,g,b);		
		}
		for(int j=40;j<80;j++) 
		{
			byte r =  0;
			byte g = (byte) (255 * (80-j)/40.0);
			byte b = (byte) (255 * (j-40)/40.0);
			rtable[j] = new Color3f(r,g,b);					
		}		
		for(int j=80;j<100;j++) 
		{
			byte r = (byte) (255 * (j-80)/40.0);
			byte g = 0;
			byte b = (byte) (255 * (120-j)/40.0);
			rtable[j] = new Color3f(r,g,b);						
		}
	}
	public Rainbow3f()
	{
		this(0,1);
	}
	public Rainbow3f(double min, double max)
	{
		this.min = min;
		this.max = max;
	}
	public Color3f colorFor(double d)
	{
		int i = (int) Math.floor((d-min)*100/(max-min));
		if (i<0) i=0;
		else if (i>=rtable.length) i = rtable.length-1;
		return rtable[i];
 	}
}
