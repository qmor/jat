package jat.spacetime;

public class TimeUtils {
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

    /** Convert Day of Month to Day of Year.
     * @param year Year.
     * @param month Month.
     * @param mday Day of month.
     * @return Day of year.
     */
    public static int day2doy(int year, int month, int mday){
        int [] regu_month_day = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int [] leap_month_day = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
        int yday = 0;

        // check for leap year
        if((year%4) == 0 ){
            yday = leap_month_day[month - 1] + mday;
        }
        else {
            yday = regu_month_day[month - 1] + mday;
        }
        return yday;
    }

    /** Compute the month from day of year. From GPS Toolkit code.
     * @param year Year.
     * @param doy Day of year.
     * @return Month.
     */
    public static int doy2month(int year, int doy){
        int [] regu_month_day = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
        int [] leap_month_day = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
        int yday = 0;

        int month = 0;
        int mday = 0;

        int guess = (int)(doy*0.032);
        int more = 0;

        // check for leap year
        if((year%4) == 0 ){
            if ((doy - leap_month_day[guess+1]) > 0) more = 1;
            month = guess + more + 1;
            mday = doy - leap_month_day[guess+more];
        }
        else {
            if ((doy - regu_month_day[guess+1]) > 0) more = 1;
            month = guess + more + 1;
            mday = doy - regu_month_day[guess+more];
        }
        return month;
    }

    /** Compute the day of month from day of year. From GPS Toolkit code.
     * @param year Year.
     * @param doy Day of year.
     * @return Day of month.
     */
    public static int doy2day(int year, int doy){
        int [] regu_month_day = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
        int [] leap_month_day = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
        int yday = 0;

        int month = 0;
        int mday = 0;

        int guess = (int)(doy*0.032);
        int more = 0;

        // check for leap year
        if((year%4) == 0 ){
            if ((doy - leap_month_day[guess+1]) > 0) more = 1;
            month = guess + more + 1;
            mday = doy - leap_month_day[guess+more];
        }
        else {
            if ((doy - regu_month_day[guess+1]) > 0) more = 1;
            month = guess + more + 1;
            mday = doy - regu_month_day[guess+more];
        }
        return mday;
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
	       if ((mjd >=51179.0)&&(mjd < 53736.0)) return 32;
	       if  (mjd >= 53736.0) return 33;

	       System.out.println("Input MJD out of bounds");
	       return 0;
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
	   
	

}
