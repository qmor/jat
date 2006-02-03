/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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
 */
package jat.spacetime;

import java.lang.Math;

/**
 * Time implements conversions between various time systems.
 * e.g. TT, TDB...
 * 
 * Translated from c by Richard C. Page III
 * 
 * @author Richard C. Page III
 *
 */
public class Time {

    //*** See bottom of this file for the original c code headers for TT2TDB
    
    /**
     * Simulation time in seconds since epoch.
     */
    protected double sim_time = 0; // [s]    
    /** 
     * Modified Julian Date of the J2000 Epoch.
     */
    public static final double MJD_J2000 = 51544.5;
    /**
     * Fraction of a day per second.
     */
    public static final double sec2days = 1.0/86400;
    /**
     * Seconds per day.
     */
    public static final double days2sec = 86400; 
    /**
     * Constant used for conversion to Terrestrial Time.
     */
    public static final double TT_TAI = 32.184;  // constant

    /**
     * Constant used for conversion to GPS time.
     */
    public static final int TAI_GPS = 19;  // constant        
    /** 
     * Modified Julian Date of Terrestrial Time.
     */
    private double MJD_TT;
    /**
     * Modified Julian Date of Barycentric Dynamical Time.
     */
    private double MJD_TDB;
    /**
     * Modified Julian Date of Universal Coordinated Time.
     */
    private double MJD_UTC;
    /**
     * Modified Julian Date of Universal Time UT1
     */
    private double MJD_UT1;
    /**
     * Modified Julian Date of the simulation epoch in Universal Coordinated Time.
     */
    private double MJD_UTC_START;

    /** Difference between UT1 and UTC.  UT1-UTC [sec] obtained from IERS Bulletin A.
     * @see jat.spacetime.FitIERS
     */
    private double UT1_UTC=0;          // UT1-UTC time difference [s]. From IERS Bulletin A.

    //** see below
    //** static enum TimeSys {TT, TDB, TCG, TCB, TAI, ET, UTC} fromTS, toTS ;

    //** see below
    //** private class MJDTime { ts, MJDint, MJDfr, MJDTime(){}} ;
    
    /**
     * Default constructor initializes to current system time to nearest millisecond
     */
    public Time(){
    	java.util.Date d = new java.util.Date();
    	CalDate cd = new CalDate(1900+d.getYear(),1+d.getMonth(),d.getDay(),d.getHours(),
    			d.getMinutes(),d.getSeconds());
    	this.MJD_UTC = cd.mjd();
        this.MJD_UTC_START = this.MJD_UTC;
        this.MJD_TT = UTC2TT(this.MJD_UTC);
        this.MJD_UT1 = this.MJD_UTC + this.UT1_UTC/86400.0;
    }
    
    /**
     * Constructor.  Initializes all time values to the simulation epoch.
     * @param mjd_UTC Universal Coordinated Time in modified julian date
     */
    public Time(double mjd_UTC){
        this.MJD_UTC = mjd_UTC;
        this.MJD_UTC_START = mjd_UTC;
        this.MJD_TT = UTC2TT(mjd_UTC);
        this.MJD_TDB = TTtoTDB(this.MJD_TT);
        this.MJD_UT1 = this.MJD_UTC + this.UT1_UTC/86400.0;
    }
    
    /**
     * Constructor.  Calculates the simulation epoch time from the given Calendar Date.
     * @param date Gregorian Calendar Date
     */
    public Time(CalDate date){
        this.MJD_UTC = date.mjd();
        this.MJD_UTC_START = this.MJD_UTC;
        this.MJD_TT = UTC2TT(this.MJD_UTC);
        this.MJD_UT1 = this.MJD_UTC + this.UT1_UTC/86400.0;
    }
    
    /**
     * Converts modified julian date to julian date.
     * @param MJD Modified Julian Date
     * @return JD Julian Date
     */
    public static double MJDtoJD(double MJD){
        return MJD+2400000.5;
    }
    /**
     * Converts julian date to modified julian date.
     * @param JD Julian Date
     * @return MJD Modified Julian Date
     */
    public static double JDtoMJD(double JD){
        return JD-2400000.5;
    }
    /**
     * Returns Universal Coordinated Time in modified julian date
     * @return MJD_UTC
     */
    public double mjd_utc(){
        return this.MJD_UTC;
    }
    /**
     * Returns Terrestrial Dynamical Time in modified julian date
     * @return MJD_TT
     */
    public double mjd_tt(){
        return this.MJD_TT;
    }
    /**
     * Returns Universal Time in modified julian date
     * @return MJD_UT1
     */
    public double mjd_ut1(){
        return this.MJD_UT1;
    }
    /**
     * Returns Universal Coordinated Time in julian date
     * @return JD_UTC
     */
    public double jd_utc(){
        return this.MJD_UTC+2400000.5;
    }
    /**
     * Returns Terrestrial Dynamical Time in julian date
     * @return JD_TT
     */
    public double jd_tt(){
        return this.MJD_TT+2400000.5;
    }
    /**
     * Returns Universal Time in julian date
     * @return JD_UT1
     */
    public double jd_ut1(){
        return this.MJD_UT1+2400000.5;
    }    
    /**
     * Returns a count of seconds since the reference epoch. 
     * @return sim_time [sec]
     */
    public double get_sim_time(){
        return this.sim_time; // [s]
    }
    
    /**
     * Set the difference in seconds between Universal and Universal Coordinated Time
     * @param d [sec]
     */
    public void set_UT1_UTC(double d){
        this.UT1_UTC = d;        
    }
    
    /**
     * Update simulation time since epoch.
     * @param t Seconds since epoch.
     */
    public void update(double t){
        sim_time = t;
        this.MJD_UTC = this.MJD_UTC_START+t*sec2days;
        this.MJD_TT = UTC2TT(this.MJD_UTC);
        this.MJD_TDB = TTtoTDB(this.MJD_TT);
        this.MJD_UT1 = this.MJD_UTC + this.UT1_UTC/86400.0;
    }
    
    /**
     * Converts the MJD expressed in UTC to give the days since Jan 01 00:00
     * @return The number of days since the beginning of the current year.
     */
    public int dayOfYear(){
        GPSTimeFormat time = new GPSTimeFormat(this.MJD_UTC);
        CalDate date = time.calDate();
        return date.doy();
    }
    /**
     * Converts the MJD expressed in UTC to give the seconds since 00:00 UTC
     * @return The number of seconds since the beginning of the current day (UTC).
     */
    public double secOfDay(){
        GPSTimeFormat time = new GPSTimeFormat(this.MJD_UTC);
        CalDate date = time.calDate();
        return date.sec_of_day();
    }

    
    //*** See bottom of this file for original header
    /**
     * Convert from the TT to TDB time system.
     * @param MJD_TT Modified Julian Date of Terrestrial Time
     * @return time in TDB expressed as a modified julian date
     * (return time altered from original c)
     */
   public static double TTtoTDB (double MJD_TT){//long MJDint_in, double MJDfr_in) {
     Time.MJDTime mjdTT = Time.getInstanceMJDTime();
     mjdTT.MJDint = (long)Math.floor(MJD_TT);
     mjdTT.MJDfr = MJD_TT - mjdTT.MJDint;
     //public TimeSys ts ; //* not used in this function
     
     double tdbtdt =0;
     double tdbtdtdot =0;
     long oldmjd = 0 ;
     long l ;

     while ( mjdTT.MJDfr >= 1.0 ) {
       mjdTT.MJDint++ ;
       mjdTT.MJDfr-- ;
     }
     while ( mjdTT.MJDfr < 0.0 ) {
       mjdTT.MJDint-- ;
       mjdTT.MJDfr++ ;
     }

     if ( mjdTT.MJDint != oldmjd ) {
       oldmjd = mjdTT.MJDint ;
       l = oldmjd + 2400001 ;

       tdbtdt = ctatv (l, 0.0) ;
       tdbtdtdot = ctatv (l, 0.5) - ctatv (l, -0.5) ;
     }
     double TDB_minus_TT = ( tdbtdt + (mjdTT.MJDfr - 0.5) * tdbtdtdot );
     return MJD_TT+TDB_minus_TT/86400;
   }
    
   //*** See bottom of this file for original header
   /**
    * Computes the cumulative relativistic time correction to
    * earth-based clocks, TDB-TDT, for a given time. Routine
    * furnished by the Bureau des Longitudes, modified by
    * removal of terms much smaller than 0.1 microsecond.
    * @param jdno. Julian day number of lookup
    * @param fjdno. Fractional part of Julian day number
    * @return Time difference TDB-TDT (seconds)
    */
   private static double ctatv(long jdno, double fjdno)
   {

     double t, tt, t1, t2, t3, t4, t5, t24, t25, t29, t30, t31 ;

         t = ((jdno-2451545) + fjdno)/(365250.0) ;
         tt = t*t ;

         t1  =       1656.674564 * sin(  6283.075943033*t + 6.240054195)
              +        22.417471 * sin(  5753.384970095*t + 4.296977442)
              +        13.839792 * sin( 12566.151886066*t + 6.196904410)
              +         4.770086 * sin(   529.690965095*t + 0.444401603)
              +         4.676740 * sin(  6069.776754553*t + 4.021195093)
              +         2.256707 * sin(   213.299095438*t + 5.543113262)
              +         1.694205 * sin(    -3.523118349*t + 5.025132748)
              +         1.554905 * sin( 77713.772618729*t + 5.198467090)
              +         1.276839 * sin(  7860.419392439*t + 5.988822341)
              +         1.193379 * sin(  5223.693919802*t + 3.649823730)
              +         1.115322 * sin(  3930.209696220*t + 1.422745069)
              +         0.794185 * sin( 11506.769769794*t + 2.322313077)
              +         0.600309 * sin(  1577.343542448*t + 2.678271909)
              +         0.496817 * sin(  6208.294251424*t + 5.696701824)
              +         0.486306 * sin(  5884.926846583*t + 0.520007179)
              +         0.468597 * sin(  6244.942814354*t + 5.866398759)
              +         0.447061 * sin(    26.298319800*t + 3.615796498)
              +         0.435206 * sin(  -398.149003408*t + 4.349338347)
              +         0.432392 * sin(    74.781598567*t + 2.435898309)
              +         0.375510 * sin(  5507.553238667*t + 4.103476804) ;

         t2  =          0.243085 * sin(  -775.522611324*t + 3.651837925)
              +         0.230685 * sin(  5856.477659115*t + 4.773852582)
              +         0.203747 * sin( 12036.460734888*t + 4.333987818)
              +         0.173435 * sin( 18849.227549974*t + 6.153743485)
              +         0.159080 * sin( 10977.078804699*t + 1.890075226)
              +         0.143935 * sin(  -796.298006816*t + 5.957517795)
              +         0.137927 * sin( 11790.629088659*t + 1.135934669)
              +         0.119979 * sin(    38.133035638*t + 4.551585768)
              +         0.118971 * sin(  5486.777843175*t + 1.914547226)
              +         0.116120 * sin(  1059.381930189*t + 0.873504123)
              +         0.101868 * sin( -5573.142801634*t + 5.984503847)
              +         0.098358 * sin(  2544.314419883*t + 0.092793886)
              +         0.080164 * sin(   206.185548437*t + 2.095377709)
              +         0.079645 * sin(  4694.002954708*t + 2.949233637)
              +         0.075019 * sin(  2942.463423292*t + 4.980931759)
              +         0.064397 * sin(  5746.271337896*t + 1.280308748)
              +         0.063814 * sin(  5760.498431898*t + 4.167901731)
              +         0.062617 * sin(    20.775395492*t + 2.654394814)
   	   +         0.058844 * sin(   426.598190876*t + 4.839650148)
              +         0.054139 * sin( 17260.154654690*t + 3.411091093) ;

         t3  =          0.048373 * sin(   155.420399434*t + 2.251573730)
              +         0.048042 * sin(  2146.165416475*t + 1.495846011)
              +         0.046551 * sin(    -0.980321068*t + 0.921573539)
              +         0.042732 * sin(   632.783739313*t + 5.720622217)
              +         0.042560 * sin(161000.685737473*t + 1.270837679)
              +         0.042411 * sin(  6275.962302991*t + 2.869567043)
              +         0.040759 * sin( 12352.852604545*t + 3.981496998)
              +         0.040480 * sin( 15720.838784878*t + 2.546610123)
              +         0.040184 * sin(    -7.113547001*t + 3.565975565)
              +         0.036955 * sin(  3154.687084896*t + 5.071801441)
              +         0.036564 * sin(  5088.628839767*t + 3.324679049)
              +         0.036507 * sin(   801.820931124*t + 6.248866009)
              +         0.034867 * sin(   522.577418094*t + 5.210064075)
              +         0.033529 * sin(  9437.762934887*t + 2.404714239)
              +         0.033477 * sin(  6062.663207553*t + 4.144987272)
              +         0.032438 * sin(  6076.890301554*t + 0.749317412)
              +         0.032423 * sin(  8827.390269875*t + 5.541473556)
              +         0.030215 * sin(  7084.896781115*t + 3.389610345)
              +         0.029862 * sin( 12139.553509107*t + 1.770181024)
              +         0.029247 * sin(-71430.695617928*t + 4.183178762) ;

         t4  =          0.028244 * sin( -6286.598968340*t + 5.069663519)
   	   +         0.027567 * sin(  6279.552731642*t + 5.040846034)
              +         0.025196 * sin(  1748.016413067*t + 2.901883301)
              +         0.024816 * sin( -1194.447010225*t + 1.087136918)
              +         0.022567 * sin(  6133.512652857*t + 3.307984806)
              +         0.022509 * sin( 10447.387839604*t + 1.460726241)
              +         0.021691 * sin( 14143.495242431*t + 5.952658009)
              +         0.020937 * sin(  8429.241266467*t + 0.652303414)
              +         0.020322 * sin(   419.484643875*t + 3.735430632)
              +         0.017673 * sin(  6812.766815086*t + 3.186129845)
              +         0.017806 * sin(    73.297125859*t + 3.475975097)
              +         0.016155 * sin( 10213.285546211*t + 1.331103168)
              +         0.015974 * sin( -2352.866153772*t + 6.145309371)
              +         0.015949 * sin(  -220.412642439*t + 4.005298270)
              +         0.015078 * sin( 19651.048481098*t + 3.969480770)
              +         0.014751 * sin(  1349.867409659*t + 4.308933301)
              +         0.014318 * sin( 16730.463689596*t + 3.016058075)
              +         0.014223 * sin( 17789.845619785*t + 2.104551349)
              +         0.013671 * sin(  -536.804512095*t + 5.971672571)
              +         0.012462 * sin(   103.092774219*t + 1.737438797) ;

         t5  =          0.012420 * sin(  4690.479836359*t + 4.734090399)
              +         0.011942 * sin(  8031.092263058*t + 2.053414715)
              +         0.011847 * sin(  5643.178563677*t + 5.489005403)
              +         0.011707 * sin( -4705.732307544*t + 2.654125618)
              +         0.011622 * sin(  5120.601145584*t + 4.863931876)
              +         0.010962 * sin(     3.590428652*t + 2.196567739)
              +         0.010825 * sin(   553.569402842*t + 0.842715011)
              +         0.010396 * sin(   951.718406251*t + 5.717799605)
              +         0.010453 * sin(  5863.591206116*t + 1.913704550)
              +         0.010099 * sin(   283.859318865*t + 1.942176992)
              +         0.009858 * sin(  6309.374169791*t + 1.061816410)
              +         0.009963 * sin(   149.563197135*t + 4.870690598)
              +         0.009370 * sin(149854.400135205*t + 0.673880395) ;

         t24 = t * (  102.156724 * sin(  6283.075849991*t + 4.249032005)
              +         1.706807 * sin( 12566.151699983*t + 4.205904248)
              +         0.269668 * sin(   213.299095438*t + 3.400290479)
              +         0.265919 * sin(   529.690965095*t + 5.836047367)
              +         0.210568 * sin(    -3.523118349*t + 6.262738348)
              +         0.077996 * sin(  5223.693919802*t + 4.670344204) ) ;

         t25 = t * (    0.059146 * sin(    26.298319800*t + 1.083044735)
              +         0.054764 * sin(  1577.343542448*t + 4.534800170)
              +         0.034420 * sin(  -398.149003408*t + 5.980077351)
              +         0.033595 * sin(  5507.553238667*t + 5.980162321)
              +         0.032088 * sin( 18849.227549974*t + 4.162913471)
              +         0.029198 * sin(  5856.477659115*t + 0.623811863)
              +         0.027764 * sin(   155.420399434*t + 3.745318113)
              +         0.025190 * sin(  5746.271337896*t + 2.980330535)
              +         0.024976 * sin(  5760.498431898*t + 2.467913690)
   	   +         0.022997 * sin(  -796.298006816*t + 1.174411803)
              +         0.021774 * sin(   206.185548437*t + 3.854787540)
              +         0.017925 * sin(  -775.522611324*t + 1.092065955)
              +         0.013794 * sin(   426.598190876*t + 2.699831988)
              +         0.013276 * sin(  6062.663207553*t + 5.845801920)
              +         0.012869 * sin(  6076.890301554*t + 5.333425680)
              +         0.012152 * sin(  1059.381930189*t + 6.222874454)
              +         0.011774 * sin( 12036.460734888*t + 2.292832062)
              +         0.011081 * sin(    -7.113547001*t + 5.154724984)
              +         0.010143 * sin(  4694.002954708*t + 4.044013795)
              +         0.010084 * sin(   522.577418094*t + 0.749320262)
              +         0.009357 * sin(  5486.777843175*t + 3.416081409) ) ;

         t29 = tt * (   0.370115 * sin(                     4.712388980)
              +         4.322990 * sin(  6283.075849991*t + 2.642893748)
              +         0.122605 * sin( 12566.151699983*t + 2.438140634)
              +         0.019476 * sin(   213.299095438*t + 1.642186981)
              +         0.016916 * sin(   529.690965095*t + 4.510959344)
              +         0.013374 * sin(    -3.523118349*t + 1.502210314) ) ;

         t30 = t * tt * 0.143388 * sin( 6283.075849991*t + 1.131453581) ;

         return (t1+t2+t3+t4+t5+t24+t25+t29+t30) * 1.0e-6 ;
   }

   /** Return the difference between TAI and UTC (known as leap seconds).
    * Values from the USNO website: ftp://maia.usno.navy.mil/ser7/leapsec.dat
    * As of July 19, 2002, no leap second in Dec 2002 so next opportunity for
    * adding a leap second is July 2003. Check IERS Bulletin C.
    * @param mjd Modified Julian Date
    * @return number of leaps seconds.
    */

   public static int tai_utc(double mjd){
       if (mjd < 0.0) {
           System.out.println("MJD before the beginning of the leap sec table");
           return 0;
       }
       if ((mjd >=41317.0)&&(mjd < 41499.0)) return 10;
       if ((mjd >=41499.0)&&(mjd < 41683.0)) return 11;
       if ((mjd >=41683.0)&&(mjd < 42048.0)) return 12;
       if ((mjd >=42048.0)&&(mjd < 42413.0)) return 13;
       if ((mjd >=42413.0)&&(mjd < 42778.0)) return 14;
       if ((mjd >=42778.0)&&(mjd < 43144.0)) return 15;
       if ((mjd >=43144.0)&&(mjd < 43509.0)) return 16;
       if ((mjd >=43509.0)&&(mjd < 43874.0)) return 17;
       if ((mjd >=43874.0)&&(mjd < 44239.0)) return 18;
       if ((mjd >=44239.0)&&(mjd < 44786.0)) return 19;
       if ((mjd >=44786.0)&&(mjd < 45151.0)) return 20;
       if ((mjd >=45151.0)&&(mjd < 45516.0)) return 21;
       if ((mjd >=45516.0)&&(mjd < 46247.0)) return 22;
       if ((mjd >=46247.0)&&(mjd < 47161.0)) return 23;
       if ((mjd >=47161.0)&&(mjd < 47892.0)) return 24;
       if ((mjd >=47892.0)&&(mjd < 48257.0)) return 25;
       if ((mjd >=48257.0)&&(mjd < 48804.0)) return 26;
       if ((mjd >=48804.0)&&(mjd < 49169.0)) return 27;
       if ((mjd >=49169.0)&&(mjd < 49534.0)) return 28;
       if ((mjd >=49534.0)&&(mjd < 50083.0)) return 29;
       if ((mjd >=50083.0)&&(mjd < 50630.0)) return 30;
       if ((mjd >=50630.0)&&(mjd < 51179.0)) return 31;
       if  (mjd >=51179.0) return 32;
       System.out.println("Input MJD out of bounds");
       return 0;
   }

   /** Convert UTC time to TT.
    * @param mjd_utc MJD of Current UTC time
    * @return MJD of current TT.
    */
   public static double UTC2TT(double mjd_utc){

       // compute the difference between TT and UTC
       double tt_utc = (double)(tai_utc(mjd_utc) + TT_TAI);
       double out = mjd_utc + tt_utc/86400.0;
       return out;
   }

   /**
    * Helper for TTtoTDB.
    * @return MJDTime
    */
   public static MJDTime getInstanceMJDTime(){
       Time t = new Time();
       return t.new MJDTime();
   }
   
   private static double sin(double x){return Math.sin(x);}
   private static double cos(double x){return Math.cos(x);}

    /**
     * Test.
     * @param args
     */
    public static void main(String[] args) {
        double jd = 2448026.19653;
        double mjd = jd - 2400000.5;
        double mjd_TT = mjd + 57.184/60/60/24;
        double TDB = Time.TTtoTDB(mjd_TT);
        System.out.println("mjd_tt: "+mjd_TT);
        System.out.println("tdb :   "+TDB);
        System.out.println("tdb2:   "+(TDB+mjd_TT));
    }

    private class TimeSys {
        //* Flags
        private static final int TT = 1;
        private static final int TDB = 2;
        private static final int TCG = 3;
        private static final int TCB = 4;
        private static final int TAI = 5;
        private static final int ET = 6;
        private static final int UTC = 7;
        private int time;
        
        public TimeSys(int flag){
            time = flag;
        }
        public void set(int t){
            if(t > 0 && t < 8) time = t;
            else System.err.println("Warning: Unable to modify TimeSys "+this.toString());
        }
        public int get(){return time;}
    }

    private class MJDTime {
        public TimeSys ts ;
        public long MJDint ;
        public double MJDfr ;
        public MJDTime(){}
    } 

}
//*** Original .h file header
/* RCS: $Id: bary.h,v 3.4 2001/06/28 21:47:02 arots Exp arots $ */
/*-----------------------------------------------------------------------
 *
 *  bary.h
 *
 *  Date: 19 November 1997
 *  Unified FITS version
 *
 *  Arnold Rots, USRA
 *
 *  This is the header file for bary.c and related source files.
 *
 *  Do not ever use DE405 on a FITS file that has:
 *      TIMESYS='TDB'
 *    but not:
 *      RADECSYS='ICRS' or PLEPHEM='JPL-DE405'
 *  TIMESYS='TDB' _with_    RADECSYS='ICRS' and/or PLEPHEM='JPL-DE405'
 *                             should use denum=405.
 *  TIMESYS='TDB' _without_ RADECSYS='ICRS' or     PLEPHEM='JPL-DE405'
 *                             should use denum=200.
 *  
 *  Time is kept in three possible ways:
 *  The basic convention is:
 *    MJDTime t ;  The MJDTime struct is defined below;
 *                   a C++ class would be better.
 *  The JPL ephemeris functions use:
 *    double time[2] ;  Where:
 *      time[0]   Integer part of JD
 *      time[1]   Fractional part of JD; -0.5 <= time[1] < 0.5
 *  MET uses a single double:
 *    double time ;  Where:
 *      time      Seconds or days since MJDREF
 *
 * We shall use the following convention:
 *
 *   If the FITS file has   Then we shall adopt
 *   TIMESYS  TIMEUNIT      refTS  fromTS  mjdRef
 *
 *     TT        s           TT     TT     MJDREF
 *     TT        d           TT     TT     MJDREF
 *     UTC       s           UTC    TT     toTT(MJDREF)
 *     UTC       d           UTC    UTC    MJDREF
 *     TDB       s           TDB    TDB    MJDREF
 *     TDB       d           TDB    TDB    MJDREF
 *
 * System files (JPLEPH, JPLEPH.200, JPLEPH.405, psrtime.dat, psrbin.dat,
 * tai-utc.dat, tdc.dat) are first searched for in $TIMING_DIR,
 * then $LHEA_DATA ($ASC_DATA, depending on what LHEADIR is set to).
 *
 *----------------------------------------------------------------------*/

//*** Original .c file header
/*-----------------------------------------------------------------------
*
*  bary.c
*
*  Date: 20 November 1997
*
*  Arnold Rots, USRA
*
*  bary contains a set of functions around barycorr, calculating barycenter
*  corrections for, in principle, any observations.
*  Required:
*    bary.h
*    dpleph.c
*    cfitsio
*
*  Externally referenced:
*    int baryinit (enum Observatory, char *, char *, double, double, char *, int) ;
*    double barycorr (MJDTime *, double *, double *) ;
*    int timeparms (char *, MJDTime *, double, int, PsrTimPar *, PsrBinPar *, int) ;
*    double absphase (MJDTime *,  PsrTimPar *, int, PsrBinPar *,
*                     int, double *, char *, int *) ;
*    double xabsphase (double, PsrTimPar *, int, PsrBinPar *,
*                     int, double *, char *, int *) ;
*    fitsfile *openFFile (const char *name)
*    double *scorbit (char *, MJDTime *, int *oerror)
*    void met2mjd (double, MJDTime *) ;
*    double mjd2met (MJDTime *) ;
*    const char *convertTime (MJDTime *) ;
*    const char *fitsdate ()
*    double ctatv (long, double) ;
*    void c200to405 (double *, int) ;
*
*  MET equivalents:
*    double xbarycorr (double, double *, double *) ;
*    double *xscorbit (char *, double, int *) ;
*
*  Internal:
*    double TTtoTDB (MJDTime *) ;
*    double TTtoTCG (MJDTime *) ;
*    double TDBtoTCB (MJDTime *) ;
*    double UTCtoTAI (MJDTime *) ;
*    double toTT (MJDTime *) ;
*    double toUTC (MJDTime *) ;
*    double binorbit (MJDTime *, PsrBinPar *) ;
*
*  It is assumed that the environment variable $TIMING_DIR is defined
*  and that the ephemeris file $TIMING_DIR/ephem.fits exists.
*  It is also assumed that $TIMING_DIR/psrtime.dat and
*  $TIMING_DIR/psrbin.dat exist.
*  However, all these names are defined as mocros in bary.h.
*
*  Certain parts are adapted from software
*  by Joseph M. Fierro, Stanford University, EGRET project,
*  and from software provided with the JPL ephemeris.
*
*----------------------------------------------------------------------*/

//*** original TTtoTDB function header
/*-----------------------------------------------------------------------
*
*  TTtoTDB calculates TDB-TT at time TT.
*  double TTtoTDB (long jdTTint, double jdTTfr)
*   mjdTT:   MJD (TT) day
*   return:  TDB - TT (s)
*
*  It uses the coefficients from Fairhead & Bretagnon 1990,
*  A&A 229, 240, as provided by ctatv.
*  The accuracy is better than 100 ns.
*  
*  The proper way to do all this is to abandon TDB and use TCB.
*
*  The way this is done is as follows: TDB-TT and its derivative are
*  calculated for the integer part of the Julian Day (when needed).
*  The precise value is derived from fractional part and derivative.
*
*----------------------------------------------------------------------*/

//*** original ctatv function header
/*  $Id: ctatv.c,v 3.0 1998/08/25 19:26:49 arots Exp arots $
 *----------------------------------------------------------------------
 *
 *     Routine name: CTATV
 *
 *     Programmer and Completion Date:
 *        Lloyd Rawley - S.T.X. - 07/28/89
 *        (Retyped by Masaki Mori - 04/19/96)
 *        (Converted to C for bary, optimized,
 *         corrected following Faithead & Bretagnon 1990, A&A 229, 240
 *         by Arnold Rots - 1997-11-20)
 *
 *     Function: Computes the cumulative relativistic time correction to
 *               earth-based clocks, TDB-TDT, for a given time. Routine
 *               furnished by the Bureau des Longitudes, modified by
 *               removal of terms much smaller than 0.1 microsecond.
 *
 *     Calling Sequence:  tdbdt = ctacv(jdno, fjdno)
 *        Argument   Type   I/O                Description
 *        --------   ----   ---  ----------------------------------------
 *        jdno      long     I   Julian day number of lookup
 *        fjdno     double   I   Fractional part of Julian day number
 *        tdbtdt    double   O   Time difference TDB-TDT (seconds)
 *
 *     Called by:  TTtoTDB
 *
 *     Calls:  none
 *
 *     COMMON use:  none
 *
 *     Significant Local Variables:
 *        Variable   Type   Ini. Val.        Description
 *        --------   ----   ---------  ----------------------------------
 *          t       double      -      Time since 2000.0 (millennia)
 *          tt      double      -      Square of T
 *
 *     Logical Units Used:  none
 *
 *     Method:
 *        Convert input time to millennia since 2000.0
 *        For each sinusoidal term of sufficient amplitude
 *           Compute value of term at input time
 *        End for
 *        Add together all terms and convert from microseconds to seconds
 *     End CTATV
 *
 *     Note for the retyped version:
 *        Results of this routine has been confirmed up to (1E-10)microsecond
 *        level compared with the calculation by the IBM machine: this seems
 *        to be within the 64-bit precision allowance, but still the original
 *        hardcopy should be kept as the right one. (M.M.)
 *
 *     Note for the C version: the accuracy is guaranteed to 100 ns.
 *
 *---------------------------------------------------------------------------*/
