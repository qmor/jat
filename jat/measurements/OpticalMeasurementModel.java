/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2006 The JAT Project. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can 
 * redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Emergent Space Technologies
 * File created by Richard C. Page III 
 * Some implementation translated from Matlab written by Sun Hur-Diaz.
 **/
package jat.measurements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import jat.alg.estimators.MeasurementFileModel;
import jat.alg.estimators.MeasurementModel;
import jat.alg.integrators.LinePrinter;
import jat.eph.DE405;
import jat.math.Interpolator;
import jat.math.MathUtils;
import jat.matvec.data.Quaternion;
import jat.matvec.data.RandomNumber;
import jat.matvec.data.VectorN;
import jat.matvec.data.Matrix;
import jat.spacetime.EarthRef;
import jat.spacetime.LunaRef;
import jat.spacetime.TimeUtils;
import jat.spacetime.Time;
import jat.sim.EstimatorSimModel;
import jat.sim.initializer;

public class OpticalMeasurementModel implements MeasurementModel{
	
	public static final int TYPE_YANGLE_STAR = 1;
	public static final int TYPE_YANGLE_LOS = 2;
	public static final int TYPE_RANGE = 3;
	public static final int TYPE_LANDMARK = 4;
	
	public static final int BODY_EARTH = 1;
	public static final int BODY_MOON = 2;
	private VectorN H;
	
	private int type;
	private double freq;
	private double t0,tf;
	private VectorN ustar;
	private int cbody;
	private int vbody;
	private Quaternion q;
	private double R;
	
	
	private double mjd0;
	//* global from y_angle
	private double jd0;
	
	//* global from camerr
	private int biasflag;
	
	private DE405 ephem;
	
	public static LinePrinter fobs,fpred;
	
	private RandomNumber rnd;
	
	public OpticalMeasurementModel(double mjd_epoch,DE405 jpl){
		mjd0=mjd_epoch;
		jd0 = mjd0+2400000.5;
		ephem = jpl;
		rnd = new RandomNumber();
	}
	
	public OpticalMeasurementModel(HashMap hm, int measNum) {
		mjd0 = initializer.parseDouble(hm,"init.MJD0")+initializer.parseDouble(hm,"init.T0")/86400;
		jd0 = mjd0+2400000.5;
		ephem = new DE405();
		initialize(hm,measNum);
		fobs = new LinePrinter("C:/Code/Jat/jat/sim/output/obs.txt");
		fpred = new LinePrinter("C:/Code/Jat/jat/sim/output/pred.txt");
		rnd = new RandomNumber();
	}
	
	private void initialize(HashMap hm, int measNum){
		String pref = "MEAS."+measNum+".";
		type = chooseType(initializer.parseString(hm,pref+"type"));
		String tmp;
		switch(type){
		case TYPE_YANGLE_STAR:
			freq = initializer.parseDouble(hm,pref+"frequency");
			t0 = initializer.parseDouble(hm,pref+"t0");
			tf = initializer.parseDouble(hm,pref+"tf");
			ustar = new VectorN(initializer.parseDouble(hm,pref+"ustar.1"),
								initializer.parseDouble(hm,pref+"ustar.2"),
								initializer.parseDouble(hm,pref+"ustar.3"));
			tmp = initializer.parseString(hm,pref+"cbody");
			if(tmp.equalsIgnoreCase("earth"))
				cbody = BODY_EARTH;
			else if(tmp.equalsIgnoreCase("moon"))
				cbody = BODY_MOON;
			else
				cbody = 0;
			R = initializer.parseDouble(hm,pref+"R");
			break;
		case TYPE_YANGLE_LOS:
			freq = initializer.parseDouble(hm,pref+"frequency");
			t0 = initializer.parseDouble(hm,pref+"t0");
			tf = initializer.parseDouble(hm,pref+"tf");
			q = new Quaternion(initializer.parseDouble(hm,pref+"q.1"),
								initializer.parseDouble(hm,pref+"q.2"),
								initializer.parseDouble(hm,pref+"q.3"),
								initializer.parseDouble(hm,pref+"q.4"));
			tmp = initializer.parseString(hm,pref+"cbody");
			if(tmp.equalsIgnoreCase("earth"))
				cbody = BODY_EARTH;
			else if(tmp.equalsIgnoreCase("moon"))
				cbody = BODY_MOON;
			else
				cbody = 0;
			tmp = initializer.parseString(hm,pref+"vbody");
			if(tmp.equalsIgnoreCase("earth"))
				vbody = BODY_EARTH;
			else if(tmp.equalsIgnoreCase("moon"))
				vbody = BODY_MOON;
			else
				vbody = 0;
			R = initializer.parseDouble(hm,pref+"R");
			break;
		case TYPE_RANGE:
			freq = initializer.parseDouble(hm,pref+"frequency");
			t0 = initializer.parseDouble(hm,pref+"t0");
			tf = initializer.parseDouble(hm,pref+"tf");
			tmp = initializer.parseString(hm,pref+"cbody");
			if(tmp.equalsIgnoreCase("earth"))
				cbody = BODY_EARTH;
			else if(tmp.equalsIgnoreCase("moon"))
				cbody = BODY_MOON;
			else
				cbody = 0;
			if(tmp.equalsIgnoreCase("earth"))
				vbody = BODY_EARTH;
			else if(tmp.equalsIgnoreCase("moon"))
				vbody = BODY_MOON;
			else
				vbody = 0;
			R = initializer.parseDouble(hm,pref+"R");
			break;
		case TYPE_LANDMARK:
			break;
		default:
			break;			
		}
	}
	
	public static int chooseType(String s){
		if(s.equalsIgnoreCase("y_angle_star")){
			return OpticalMeasurementModel.TYPE_YANGLE_STAR;
		}else if(s.equalsIgnoreCase("y_angle_los")){
			return OpticalMeasurementModel.TYPE_YANGLE_LOS;
		}else if(s.equalsIgnoreCase("range")){
			return OpticalMeasurementModel.TYPE_RANGE;
		}else if(s.equalsIgnoreCase("landmark")){
			return OpticalMeasurementModel.TYPE_LANDMARK;
		}else{
			return 0;
		}
	}
	
	/**
	 * Code converted from Matlab
	 % [y,dydx]=y_angle(x,t,p,s,inoise)
	 % Computes the predicted cosine angle measurement and its Jacobian given the 
	 % inertial position state, time, and vector observation specification.  The 
	 % use of this function is intended for navigation computation.  Note that
	 % this function can be used for angle measurement between body and star
	 % observation or for a scalar component of the line-of-sight measurement.
	 %
	 % INPUT:
	 %   x      6x1   [x y z xdot ydot zdot ]' inertial position and velocity
	 %                (km,sec)
	 %   t      1x1   simulation time (sec)
	 %   p      1x1   flag indicating type of vector observation
	 %                  1 = Earth
	 %                  2 = Moon
	 %   s      3x1   inertial reference unit vector from the s/c, e.g. star unit 
	 %                vector or a coordinate transformation axis from ECI frame to
	 %                sensor frame.  
	 %   inoise 1x1   flag to indicate apply noise model
	 %
	 % OUTPUT:
	 %   y      1x1   cosine of angle between the unit vector from the s/c to
	 %                the body as specified by p and the inertial reference unit
	 %                vector
	 %   dydx   1x6   Jacobian
	 % 
	 % GLOBAL:
	 %   jd0    1x1   Julian date of initial time when t=0
	 %
	 % Written by S. Hur-Diaz  6/20/2006
	 *
	 */
	private double[] y_angle(VectorN x, double t, int p, VectorN s, int inoise){
		//int lx = x.length;
		
		//% Compute radial distance from Earth
		VectorN r = x.get(0,3);
		//double rmag=r.mag();
		VectorN v = new VectorN(3);
		//% Determine unit vector observation 1 (Either Earth or Moon)
		switch(p){
		case 1: //% Earth obs
			v=r.times(-1.0);
			break;
		case 2: //% Moon obs
			//% Get lunar position relative to the Earth
			VectorN xm=ephem.get_Geocentric_Moon_pos(Time.TTtoTDB(Time.UTC2TT(jd0+t/86400))); 
			//getmoon(jd0+t/86400);  
			v=xm.minus(r);
			break;
		default:
			System.err.println("Invalid flag for vector observation 1.");
		break;
		}
		
		VectorN u=v.unitVector();
		Matrix eye = new Matrix(3);
		Matrix dudx=(eye.minus(u.outerProduct(u))).divide(v.mag());  
		
		//% Compute measurement cos(theta)
		double y=s.dotProduct(u);
		
		if (inoise==1){
			double[] arnd_abias = camerr(v.mag(),p); //% 1-sigma noise and bias on angular measurement
			y=Math.cos(Math.acos(y)+arnd_abias[0]*randn()+arnd_abias[1]);
		}
		
		//% Compute Jacobian
		//dydx= -[s'*dudx zeros(1,3)];
		VectorN tmp = new VectorN(s.times(dudx),new VectorN(3)).times(-1.0);
		double[] dydx= tmp.x;
		
		
		//% % Assume the bias is on the pseudo measurement rather than angle itself
		//% if lx==8
		//%     y=y+x(7);
		//%     dydx=[dydx 1 0];
		//% end
		double[] out = new double[7];
		out[0] = y;
		for(int i=1; i<7; i++) out[i] = dydx[i-1];
		return out;
	}
	
	/**
	 * Returns a normally distributed random variable.
	 */
	private double randn(){
		//* http://www.mathworks.com/access/helpdesk/help/techdoc/matlab.html
		//RandomNumber rnd = new RandomNumber(System.currentTimeMillis());
		return rnd.normal();
		//return 0;
	}
	
	/*
	 * Code converted from Matlab
	 % [arnd,abias]=camerr(r,ibody)
	 % Camera anguler error as provided by Dan Schwab, Boeing
	 % 
	 % INPUT:
	 %   r     1x1   range from the target body (km)
	 %   ibody 1x1   integer indicating Earth (=1) or Moon (=2)
	 % 
	 % OUTPUT:
	 %   arnd  1x1    random error (rad)
	 %   abias 1x1    bias error (rad)
	 %
	 % GLOBAL:
	 %   biasflag   1x1  flag indicating if bias error should be applied
	 %
	 % Written by S. Hur-Diaz   6/20/2006
	 */
	private double[] camerr(double r, int ibody){
		//% Range from Earth in m
		//double[] erange= {1069177850, 213835570, 71278520, 21383560, 13364720};
		double[] erange= {13364720,21383560,71278520,213835570,1069177850};
		//VectorN erange = new VectorN(erange_tmp);
		
		//% Range from Moon in m
		//double[] mrange = {291342820, 58268560, 19422850, 5826860, 3641790};
		double[] mrange = {3641790,5826860,19422850,58268560,291342820};
		//VectorN mrange = new VectorN(mrange_tmp);
		
		double[] rv = new double[5];
		if (ibody == 1) //% Earth
			rv=erange;
		else if (ibody ==2) // % Moon
			rv=mrange;
		else
			System.err.println("Invalid body flag");
		
		
		//double[] angerr_rnd_deg= {0.0022, 0.011, 0.032, 0.105, 0.169};
		double[] angerr_rnd_deg= {0.169,0.105,0.032,0.011,0.0022};
		//double[] angerr_bias_deg= {biasflag*0.0046, biasflag*0.023, biasflag*0.070, biasflag*0.235, biasflag*0.375};
		double[] angerr_bias_deg= {biasflag*0.375,biasflag*0.235,biasflag*0.070,biasflag*0.023,biasflag*0.0046};
		
		//% Apollo numbers corresponding to 3km horizon sensing error
		//%angerr_rnd_deg=  [ 0.0002    0.0008    0.0024    0.0080    0.0129]';
		
		//% Interpolate/extrapolate based on actual range
		Interpolator interp1 = new Interpolator(rv,angerr_rnd_deg);
		double arnd=MathUtils.DEG2RAD*(interp1.get_value(r));//interp1(rv,angerr_rnd_deg,r,'linear','extrap')*pi/180;
		interp1 = new Interpolator(rv,angerr_bias_deg);
		double abias=MathUtils.DEG2RAD*(interp1.get_value(r));//interp1(rv,angerr_bias_deg,r,'linear','extrap')*pi/180;
		//*TODO watch this
		//arnd = 0;
		//abias = 0;
		double[] out = {arnd, abias};
		return out;
	}
	
	/**
	 * Converted from Matlab
	 * % [y,dydx]=y_disk2(x,t,p,R,inoise)
	 % Similar to y_disk but uses sin formulation which is more accurate than
	 % the tangent formulation.
	 % Compute the sin of the predicted half-disk angle.
	 %
	 % INPUT:
	 %   x       6x1  Earth-centered inertial position and velocity vector (km)
	 %   t       1x1  time (sec)
	 %   p       1x1  integer parameter indicating Earth (=1) or Moon (=2)
	 %   R       1x1  body's mean physical radius (km)
	 %   inoise  1x1  integer noise flag, 1=add noise
	 %
	 % OUTPUT:
	 %   y       1x1  scalar measurement R/r, where r is the distance from the
	 %                s/c to the body
	 %   dydx    1x6  Jacobian of the y wrt x
	 %
	 % GLOBAL:
	 %   jd0     1x1  Julian Date of initial time corresponding to t=0
	 %
	 % Written by S. Hur-Diaz    6/20/2006
	 % 
	 */
	private double[] y_disk2(VectorN state, double t, int p, double R, int inoise){
		//double jd=this.jd0+t/86400;
		//int lx=state.x.length;
		
		VectorN xr = new VectorN(3);
		VectorN pos = state.get(0,3);
		if (p==1) //% Earth
			xr=pos;
		else if (p==2){ //% Moon
			VectorN xm=ephem.get_Geocentric_Moon_pos(Time.TTtoTDB(Time.UTC2TT(jd0+t/86400)));
			xr=(pos.minus(xm));
		} else
			System.err.println("Parameter must be 1 for Earth or 2 for Moon.");
		
		double r=xr.mag();
		double y=R/r;  //% sin(half-angle)
		
		if (y>1){
			System.out.println("R/r greater than 1, pause");
			try{
				System.in.read();
			}catch(Exception e){}
		}
		
		//% Add noise
		if (inoise==1){
			double[] arnd_abias =camerr(r,p); //% 1-sigma noise and bias on angular measurement
			y=Math.sin(Math.asin(y)+arnd_abias[0]*randn()+arnd_abias[1]);
		}
		
		//% Compute Jacobian
		VectorN tmp = new VectorN(xr.times(-R/(r*r*r)),new VectorN(3));
		double[] dydx = tmp.x;
		
		//% % Assume the bias is on the pseudo measurement rather than angle itself
		//% if lx==8
		//%     y=y+x(8);
		//%     dydx=[dydx 0 1];
		//% end
		
		
		double[] out = new double[7];
		out[0] = y;
		for(int i=1; i<7; i++) out[i] = dydx[i-1];
		return out;
	}
	
	/**
	 * Converted from Matlab.
	 * % It either checks to see if there is enough illumination to obtain centroid 
	 % information of the body based on the user-specified limit on the fraction of
	 % illumination or if an earth-fixed landmark position is visible and
	 % illuminated.  The check selection depends on the size of the input
	 % parameter frac_or_xlf.
	 * @param jd
	 * @param cbody
	 * @param state
	 * @param frac_or_xlf
	 * @return
	 */
	private double[] illum(double jd, CentralBody cbody, String eval, VectorN state, double[] frac_or_xlf){
		VectorN r = state.get(0,3);
		//% x is position relative to the central body
		double xnorm=r.mag();
		VectorN xhat=r.unitVector();
		
		//[rasc, decl, xs] = sun (jd);
		VectorN rasc_decl_xs = ephem.get_Geocentric_Sun_pos(Time.TTtoTDB(Time.UTC2TT(jd)));
		//VectorN rasc = rasc_decl_xs.get();
		//VectorN decl = rasc_decl_xs.get();
		VectorN xs = rasc_decl_xs;
		if (cbody.name.equalsIgnoreCase("moon")){
			VectorN xm= new VectorN(feval(cbody.fn,jd));
			xs=xs.minus(xm);
		}
		//double xsnorm=xs.mag();
		VectorN xshat=xs.unitVector();
		
		double R2r=0;
		R2r=cbody.R/xnorm;
		
		int flag = 0;
		double ratio = 0;
		if (frac_or_xlf.length==1){
			double frac=frac_or_xlf[0];
			//% Determine if accurate measurements can be had
			double theta=Math.acos(R2r); //% half-angle of the horizon disk centered at body
			double gam=Math.acos(xshat.dotProduct(xhat));    // % angle between spacecraft and sun centered at body
			ratio=(theta+Math.PI/2-gam)/(2*theta);
			if (ratio>=frac){
				flag=1;  
			} else{
				flag=0;  //% Not enough illumination for accurate measurement
			}
		}else if (frac_or_xlf.length==3){
			VectorN xlf= new VectorN(frac_or_xlf);
			//xlf=xlf(:);
			//% Check if the landmark is visible and illuminated
			VectorN xli = (feval2(cbody.fn_f2i,jd)).times(xlf);
			VectorN xlihat=xli.unitVector();//xli/norm(xli);
			flag=0;          //% initialize
			if (xlihat.dotProduct(xhat) > R2r){
				flag=1;      //% is visible
				if (xlihat.dotProduct(xshat) > 0){
					flag=2;  //% is visible and illuminated
				}
			}
		} else{
			System.err.println("Invalid frac_or_xlf");
		}
		
		double[] out = {flag,ratio};
		return out;
		
	}
	
	private Matrix nadir_dcm(double jd, VectorN xsc, int cbody, int vbody){

		//xsc=xsc(:);
		//% Need to get x relative to vbody
		//% First get cbody relative to earth
		//xce=feval(cbody.fn,jd);
		VectorN xce = new VectorN(3);
		VectorN xve = new VectorN(3);
		if(cbody==BODY_EARTH){
			xce = new VectorN(3);
		}else if(cbody == BODY_MOON){
			xce = ephem.get_Geocentric_Moon_pos(Time.TTtoTDB(Time.UTC2TT(jd)));
		}
		//% Second get vbody relative to earth
		//xve=feval(vbody.fn,jd);
		if(vbody==BODY_EARTH){
			xve = new VectorN(3);
		}else if(vbody == BODY_MOON){
			xve = ephem.get_Geocentric_Moon_pos(Time.TTtoTDB(Time.UTC2TT(jd)));
		}
		
		//% Finally spacecraft relative to vbody
		VectorN xsv= xsc.get(0,3).plus(xce.minus(xve));

		//% Get unit vector from vbody to spacecraft
		//xsvnorm=norm(xsv(1:3));
		VectorN xsvhat=xsv.unitVector();


		VectorN zm=xsvhat.times(-1.0);
		VectorN z = new VectorN(0.0,0.0,1.0);
		VectorN xm=z.crossProduct(zm);
		xm= xm.unitVector();
		VectorN ym=zm.crossProduct(xm);
		Matrix A_sensor_2_inertial= new Matrix(3);
		A_sensor_2_inertial.A[0][0] = xm.x[0];
		A_sensor_2_inertial.A[1][0] = xm.x[1];
		A_sensor_2_inertial.A[2][0] = xm.x[2];
		A_sensor_2_inertial.A[0][1] = ym.x[0];
		A_sensor_2_inertial.A[1][1] = ym.x[1];
		A_sensor_2_inertial.A[2][1] = ym.x[2];
		A_sensor_2_inertial.A[0][2] = zm.x[0];
		A_sensor_2_inertial.A[1][2] = zm.x[1];
		A_sensor_2_inertial.A[2][2] = zm.x[2];
		
		return A_sensor_2_inertial.transpose();
	}
	
	private double[] feval(String fn, double jd){
		double[] out;
		if(fn.equalsIgnoreCase("getearth")){
			return new double[6];
		}else if(fn.equalsIgnoreCase("getmoon")){
			VectorN xm=ephem.get_Geocentric_Moon_pos(Time.TTtoTDB(Time.UTC2TT(jd)));
			out = xm.x;
			return out;
		}else if(fn.equalsIgnoreCase("getsun")){
			VectorN sun = ephem.get_Geocentric_Sun_pos(Time.TTtoTDB(Time.UTC2TT(jd)));
			out = sun.x;
			return out;
		}
		out = new double[1];
		return out;
	}
	
	private Matrix feval2(String fn, double jd){
		if(fn.equalsIgnoreCase("ef2ei")){
			EarthRef earth = new EarthRef(new Time(jd-2400000.5));
			return earth.ECI2ECEF().transpose();
		}else if(fn.equalsIgnoreCase("mf2ei")){
			
		}			
		return new Matrix(3);
	}
	
	public double observedMeasurement(int whichMeas, double t, VectorN x){
		double[] out = new double[1];
		//double t = (mjd-mjd0)*86400;
		double mjd = mjd0+t/86400;
		switch(type){
		case TYPE_YANGLE_STAR:
			out = y_angle(x,t,vbody,ustar,1);
			break;
		case TYPE_YANGLE_LOS:
			//Matrix A = q.quat2DCM();
			Matrix A = nadir_dcm(mjd+2400000.5,x,cbody,vbody);
			VectorN s;
			if(whichMeas==0)
				s = new VectorN(A.getRowVector(0));
			else
				s = new VectorN(A.getRowVector(1));
			out = y_angle(x,t,vbody,s,1);
			break;
		case TYPE_RANGE:
			out = y_disk2(x,t,vbody,EarthRef.R_Earth,1);
			break;
		default:
			return 0;
		}
		H = new VectorN(6);
		for(int i=1; i<7;i++) H.x[i-1] = out[i];
		return out[0];
	}
	
	public double predictedMeasurement(int whichMeas, double t, VectorN x){
		double[] out = new double[1];
		//double t = (mjd-mjd0)*86400;
		double mjd = mjd0+t/86400;
		switch(type){
		case TYPE_YANGLE_STAR:
			out = y_angle(x,t,vbody,ustar,0);
			break;
		case TYPE_YANGLE_LOS:
			VectorN truestate = new VectorN(EstimatorSimModel.truth[0].get_spacecraft().toStateVector());
			Matrix A = nadir_dcm(mjd+2400000.5,truestate,cbody,vbody);//q.quat2DCM();
			VectorN s;
			if(whichMeas==0)
				s = new VectorN(A.getRowVector(0));
			else
				s = new VectorN(A.getRowVector(1));
			out = y_angle(x,t,vbody,s,0);
			break;
		case TYPE_RANGE:
			out = y_disk2(x,t,vbody,EarthRef.R_Earth,0);
			break;
		default:
			return 0;
		}
		return out[0];
	}
	
	public int get_type(){
		return type;
	}
	
//	******* Interface Implementation ******* //
	
	
	public VectorN H(VectorN xref) {
		//* Cheating by calculating this in the call to observed measurement
		return this.H;
	}
	
	public double R() {
		return R;
	}
	
	public double zPred(int whichMeas, double t_sim, VectorN state) {
		double out,obs;
		obs = observedMeasurement(whichMeas,t_sim,new VectorN(EstimatorSimModel.truth[0].get_spacecraft().toStateVector()));
		double pred = predictedMeasurement(whichMeas, t_sim, state); 
		
		if(obs == 0)
		    out = 0.0;
		else
			out = obs-pred;
		
		String typestring = "blank";
		if(type==TYPE_YANGLE_STAR){
			typestring = "yangle_star";
			OpticalMeasurementModel.fobs.println("obs: "+Math.acos(obs)*MathUtils.RAD2DEG+"   "+typestring);
			OpticalMeasurementModel.fpred.println("obs: "+Math.acos(pred)*MathUtils.RAD2DEG+"   "+typestring);
		}else if(type==TYPE_YANGLE_LOS){
			typestring = "yangle_los";
			OpticalMeasurementModel.fobs.println("obs: "+Math.acos(obs)*MathUtils.RAD2DEG+"   "+typestring);
			OpticalMeasurementModel.fpred.println("obs: "+Math.acos(pred)*MathUtils.RAD2DEG+"   "+typestring);
		}else if(type== TYPE_RANGE){
			typestring = "range";
			OpticalMeasurementModel.fobs.println("obs: "+Math.asin(obs)*MathUtils.RAD2DEG+"   "+typestring);
			OpticalMeasurementModel.fpred.println("obs: "+Math.asin(pred)*MathUtils.RAD2DEG+"   "+typestring);
		}
		
		return out;
	}
	
	
	private class CentralBody {
		public String name;
		public String fn;
		public String fn_f2i;
		public double R;
		public double MU;
		
		public static final int SUN = 0;
		public static final int EARTH = 1;
		public static final int MOON = 10;
		
		public CentralBody(int body){
			switch(body){
			case SUN:
				name = "sun";
				MU = 1.32712440018e11;
				fn = "getsun";
				break;
			case EARTH:
				name = "earth";
				fn = "getearth";
					break;
			case MOON:
				break;
			}
		}
	}
}
