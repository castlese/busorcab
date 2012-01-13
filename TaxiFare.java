package com.busorcab.server.services;

import java.util.Calendar;

public class TaxiFare {
	private static final String DECIMAL_POINT = ".";
	private static final String EMPTY_STRING = "";
	/** DECLARATION OF CONSTANTS */
	/** Standard Rate - between 8am and 8pm Mon - Sat excl Public Holidays */
	private static final int MIN_KM = 1;
	public static final int STD_INIT = 410;
	/** Initial Fare - includes 1km or 170 secs */
	public static final int STD_TARIFF_A_KM = 103;
	/** Tariff A Rate per km */
	public static final int STD_TARIFF_A_MIN = 36;
	/** Tarrif A Rate per minute */
	public static final int STD_TARIFF_A_KM_MAX = 14;
	/** Tariff A maximum kms */

	public static final int STD_TARIFF_B_KM = 135;
	/** Tariff B Rate per km */
	public static final int STD_TARIFF_B_MIN = 48;
	/** Tarrif B Rate per minute */
	public static final int STD_TARIFF_B_KM_MAX = 15;
	/** Tariff B maximum kms */

	public static final int STD_TARIFF_C_KM = 177;
	/** Tariff C Rate per km */
	public static final int STD_TARIFF_C_MIN = 63;
	/** Tarrif C Rate per minute */
	public static final int STD_TARIFF_C_KM_MIN = 30;
	/** Tariff C minimum kms */

	/**
	 * Premium Rate - between 8pm and 8am Mon to Sat, All Day Sunday and Public
	 * Holidays
	 */
	public static final int PREM_INIT = 445;
	/** Initial Fare - includes 1km or 170 secs */
	public static final int PREM_TARIFF_A_KM = 135;
	/** Tariff A Rate per km */
	public static final int PREM_TARIFF_A_MIN = 48;
	/** Tarrif A Rate per minute */
	public static final int PREM_TARIFF_A_KM_MAX = 14;
	/** Tariff A maximum kms */

	public static final int PREM_TARIFF_B_KM = 157;
	/** Tariff B Rate per km */
	public static final int PREM_TARIFF_B_MIN = 55;
	/** Tarrif B Rate per minute */
	public static final int PREM_TARIFF_B_KM_MAX = 15;
	/** Tariff B maximum kms */

	public static final int PREM_TARIFF_C_KM = 177;
	/** Tariff C Rate per km */
	public static final int PREM_TARIFF_C_MIN = 63;
	/** Tarrif C Rate per minute */
	public static final int PREM_TARIFF_C_KM_MIN = 30;
	/** Tariff C minimum kms */

	/** For peak times assume the following: */
	public static final double TARRIF_A_PEAK_MINS_PER_KM = 2;
	/** stoppage time per km, Tarrif A */
	public static final double TARRIF_B_PEAK_MINS_PER_KM = 0.5;
	/** stoppage time per km, Tarrif B */
	public static final double TARRIF_C_PEAK_MINS_PER_KM = 0.1;
	/** stoppage time per km, Tarrif C */
	public static final int TARRIF_A_MAX_KM = 9;
	/** maximum kms at Tarrif A */
	public static final int TARRIF_B_MAX_KM = 17;
	/** maximum kms at Tarrif B */

	private double distance;
	private int duration;
	private final Calendar datetime = Calendar.getInstance();

	/**
	 * @journey = array of doubles that will represent taxi information
	 * @journey[0] = minimum time
	 * @journey[1] = minimum fare
	 * @journey[2] = maximum time
	 * @journey[3] = maximum fare Can include 'DateTime time' here too using an
	 *             interface? Come back to later
	 */
	double[] journey = new double[4];

	public final void setDistance(final double distance) {
		this.distance = distance;
	}

	public final void setDuration(final int duration) {
		this.duration = duration;
	}

	public final void setTimeInMillis(final long time) {
		this.datetime.setTimeInMillis(time);
	}

	/**
	 * Initializer reads in distance and duration information
	 * 
	 * @distance = journey distance in kms
	 * @duration = journey duration in minutes from Google Maps
	 */
	public TaxiFareupdate(final double dist, final int dur, final long time) {
		distance = dist;
		duration = dur;
		datetime.setTimeInMillis(time);

	}

	public TaxiFareupdate() {
	}

	/**
	 * public method to call calculators depending and return array of fare
	 * information
	 */
	public String[] getTaxiInfo() {
		final int faretype = setFareType();

		switch (faretype) {
		case 0:
			minStandardFare();
			maxStandardFare();
			break;
		case 1:
			minPremiumFare();
			maxPremiumFare();
			break;
		case 2:
			minSpecialPremiumFare();
			maxSpecialPremiumFare();
			break;
		}
		/**
		 * Convert jourey to an array of strings, rounding also. Ensures that
		 * prices are properly formulated
		 */
		final String[] journeyString = new String[4];

		journeyString[0] = EMPTY_STRING + (int) Math.floor(journey[0] / 60);
		journeyString[0] = journeyString[0]
				+ " : "
				+ (int) Math.round(journey[0] - Math.floor(journey[0] / 60)
						* 60);
		;

		journeyString[1] = EMPTY_STRING + (int) Math.floor(journey[1])
				+ DECIMAL_POINT;
		journeyString[1] = journeyString[1]
				+ (int) Math.round((journey[1] - Math.floor(journey[1])) * 100);

		journeyString[2] = EMPTY_STRING + (int) Math.floor(journey[2] / 60);
		journeyString[2] = journeyString[2]
				+ " : "
				+ (int) Math.round(journey[2] - Math.floor(journey[2] / 60)
						* 60);
		;

		journeyString[3] = EMPTY_STRING + (int) Math.floor(journey[3])
				+ DECIMAL_POINT;
		journeyString[3] = journeyString[3]
				+ (int) Math.round((journey[3] - Math.floor(journey[3])) * 100);
		return journeyString;
	}

	/**
	 * private method which returns the fare type based on current date and time
	 * Come back to
	 *************************************/
	private int setFareType() {
		final int hour = datetime.get(Calendar.HOUR_OF_DAY);
		final int day = datetime.get(Calendar.DAY_OF_WEEK);
		final int date_day = datetime.get(Calendar.DATE);
		final int month = datetime.get(Calendar.MONTH);
		int rate;

		if ((hour >= 20 && hour < 8) || day == 1) {
			rate = 1;
			/**
			 * Represents Premium Rate (8pm - 8am Mon to Sat and all day Sunday)
			 */
		} else if ((date_day == 24 && month == 11 && hour >= 20)
				|| (date_day == 25 && month == 11)
				|| (date_day == 26 && month == 11 && hour < 8)
				|| (date_day == 31 && month == 11 && hour >= 20)
				|| (date_day == 1 && month == 0 && hour < 8)) {
			rate = 2;
			/** Represents Special Premium Rate around Christmas Time */
		} else {
			rate = 0;
			/** Represents the Standard Rate */
		}

		return rate;

	}

	/**
	 * applies 8am to 8pm Mon to Sat excl. public holidays private method to set
	 * the min time and min fare elements of the journey array
	 */

	private void minStandardFare() {
		double dist_temp = distance;
		double min_fare = STD_INIT;

		/** minimum fare */
		if (distance <= MIN_KM) {
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}

		/** Up to Tariff A fare */
		else if (distance > MIN_KM
				&& distance <= (MIN_KM + STD_TARIFF_A_KM_MAX)) {
			dist_temp = dist_temp - MIN_KM;
			min_fare += (dist_temp * STD_TARIFF_A_KM);
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}

		/** Up to Tariff B Fare */
		else if (distance > (MIN_KM + STD_TARIFF_A_KM_MAX)
				&& distance <= (MIN_KM + STD_TARIFF_A_KM_MAX + STD_TARIFF_B_KM_MAX)) {
			dist_temp = dist_temp - MIN_KM;
			min_fare += (STD_TARIFF_A_KM_MAX * STD_TARIFF_A_KM);
			dist_temp = dist_temp - STD_TARIFF_A_KM_MAX;
			min_fare += (dist_temp * STD_TARIFF_B_KM);
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}

		/** Up to Tariff C Fare */
		else if (distance > STD_TARIFF_C_KM_MIN) {
			dist_temp = dist_temp - MIN_KM;
			min_fare += (dist_temp * STD_TARIFF_A_KM);
			dist_temp = dist_temp - STD_TARIFF_A_KM_MAX;
			min_fare += (dist_temp * STD_TARIFF_B_KM);
			dist_temp = dist_temp - STD_TARIFF_B_KM_MAX;
			min_fare += (dist_temp * STD_TARIFF_C_KM);
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}

		else {

		}
	}

	private void maxStandardFare() {
		double dist_temp = distance;
		double max_duration = duration + TARRIF_A_PEAK_MINS_PER_KM;
		double max_fare = STD_INIT
				+ (TARRIF_A_PEAK_MINS_PER_KM * STD_TARIFF_A_MIN);

		/** minimum fare */
		if (distance <= MIN_KM) {
			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		/** Tariff A fare */
		else if (distance > MIN_KM && distance <= TARRIF_A_MAX_KM) {
			dist_temp = dist_temp - MIN_KM;
			max_duration += dist_temp * TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * STD_TARIFF_A_KM + (max_duration - duration - TARRIF_A_PEAK_MINS_PER_KM)
					* STD_TARIFF_A_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		/** Tariff B Fare */
		else if (distance > TARRIF_A_MAX_KM && distance <= TARRIF_B_MAX_KM) {

			max_duration += (TARRIF_A_MAX_KM - MIN_KM)
					* TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_A_MAX_KM - MIN_KM)
					* (STD_TARIFF_A_KM + TARRIF_A_PEAK_MINS_PER_KM
							* STD_TARIFF_A_MIN);

			dist_temp = dist_temp - (TARRIF_A_MAX_KM);
			max_duration += dist_temp * TARRIF_B_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * STD_TARIFF_B_KM + dist_temp
					* TARRIF_B_PEAK_MINS_PER_KM * STD_TARIFF_B_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		/** Tariff C Fare */
		else if (distance > TARRIF_B_MAX_KM) {

			max_duration += (TARRIF_A_MAX_KM - MIN_KM)
					* TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_A_MAX_KM - MIN_KM)
					* (STD_TARIFF_A_KM + TARRIF_A_PEAK_MINS_PER_KM
							* STD_TARIFF_A_MIN);

			max_duration += (TARRIF_B_MAX_KM - TARRIF_A_MAX_KM)
					* TARRIF_B_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_B_MAX_KM - TARRIF_A_MAX_KM)
					* (STD_TARIFF_B_KM + TARRIF_B_PEAK_MINS_PER_KM
							* STD_TARIFF_B_MIN);

			dist_temp = dist_temp - (TARRIF_B_MAX_KM);
			max_duration += dist_temp * TARRIF_C_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * STD_TARIFF_C_KM + dist_temp
					* TARRIF_C_PEAK_MINS_PER_KM * STD_TARIFF_C_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		else {

		}
	}

	/** applies 8pm to 8am Mon to Sat, All day Sunday & Public Holidays */
	public void minPremiumFare() {
		double dist_temp = distance;
		double minfare = PREM_INIT;

		/** minimum premium fare */
		if (distance <= MIN_KM) {
			journey[0] = duration;
			journey[1] = minfare / 100;
		}

		/** up to Tariff A Premium Fare */
		else if (distance > MIN_KM
				&& distance <= (MIN_KM + PREM_TARIFF_A_KM_MAX)) {
			dist_temp = dist_temp - MIN_KM;
			minfare += (dist_temp * PREM_TARIFF_A_KM);
			journey[0] = duration;
			journey[1] = minfare / 100;
		}

		/** Up to Tariff B Premium fare */
		else if (distance > (MIN_KM + PREM_TARIFF_A_KM_MAX)
				&& distance <= (MIN_KM + PREM_TARIFF_A_KM_MAX + PREM_TARIFF_B_KM_MAX)) {

			dist_temp = dist_temp - MIN_KM;
			minfare += (PREM_TARIFF_A_KM_MAX * PREM_TARIFF_A_KM);
			dist_temp = dist_temp - PREM_TARIFF_A_KM_MAX;
			minfare += (dist_temp * PREM_TARIFF_B_KM);
			journey[0] = duration;
			journey[1] = minfare / 100;
		}

		/** Up to Tariff C Premium Fare */
		else if (distance > MIN_KM + PREM_TARIFF_A_KM_MAX
				+ PREM_TARIFF_B_KM_MAX) {
			dist_temp = dist_temp - MIN_KM;
			minfare += (dist_temp * PREM_TARIFF_A_KM);
			dist_temp = dist_temp - PREM_TARIFF_A_KM_MAX;
			minfare += (dist_temp * PREM_TARIFF_B_KM);
			dist_temp = dist_temp - PREM_TARIFF_B_KM_MAX;
			minfare += (dist_temp * PREM_TARIFF_C_KM);
			journey[0] = duration;
			journey[1] = minfare / 100;
		}

		else {

		}

	}

	private void maxPremiumFare() {
		double dist_temp = distance;
		double max_duration = duration + TARRIF_A_PEAK_MINS_PER_KM;
		double max_fare = PREM_INIT
				+ (TARRIF_A_PEAK_MINS_PER_KM * PREM_TARIFF_A_MIN);

		/** minimum fare */
		if (distance <= MIN_KM) {
			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		/** Tariff A fare */
		else if (distance > MIN_KM && distance <= TARRIF_A_MAX_KM) {
			dist_temp = dist_temp - MIN_KM;
			max_duration += dist_temp * TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * PREM_TARIFF_A_KM + max_duration
					* PREM_TARIFF_A_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		/** Tariff B Fare */
		else if (distance > TARRIF_A_MAX_KM && distance <= TARRIF_B_MAX_KM) {
			max_duration += (TARRIF_A_MAX_KM - MIN_KM)
					* TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_A_MAX_KM - MIN_KM)
					* (PREM_TARIFF_A_KM + TARRIF_A_PEAK_MINS_PER_KM
							* PREM_TARIFF_A_MIN);

			dist_temp = dist_temp - (TARRIF_A_MAX_KM);
			max_duration += dist_temp * TARRIF_B_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * PREM_TARIFF_B_KM + dist_temp
					* TARRIF_B_PEAK_MINS_PER_KM * PREM_TARIFF_B_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;

		}

		/** Tariff C Fare */
		else if (distance > TARRIF_B_MAX_KM) {

			max_duration += (TARRIF_A_MAX_KM - MIN_KM)
					* TARRIF_A_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_A_MAX_KM - MIN_KM)
					* (PREM_TARIFF_A_KM + TARRIF_A_PEAK_MINS_PER_KM
							* PREM_TARIFF_A_MIN);

			max_duration += (TARRIF_B_MAX_KM - TARRIF_A_MAX_KM)
					* TARRIF_B_PEAK_MINS_PER_KM;
			max_fare += (TARRIF_B_MAX_KM - TARRIF_A_MAX_KM)
					* (PREM_TARIFF_B_KM + TARRIF_B_PEAK_MINS_PER_KM
							* PREM_TARIFF_B_MIN);

			dist_temp = dist_temp - (TARRIF_B_MAX_KM);
			max_duration += dist_temp * TARRIF_C_PEAK_MINS_PER_KM;
			max_fare += (dist_temp * PREM_TARIFF_C_KM + dist_temp
					* TARRIF_C_PEAK_MINS_PER_KM * PREM_TARIFF_C_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

		else {

		}
	}

	/**
	 * Applies 8pm Christmas Eve to 8am St. Stephen's Day and 8pm New Year's Eve
	 * to 8am New Year's Day
	 */
	public void minSpecialPremiumFare() {
		double dist_temp = distance;
		double min_fare = PREM_INIT;

		if (distance <= MIN_KM) {
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}
		/** Tarrif C fare */
		else {
			dist_temp = dist_temp - MIN_KM;
			min_fare += (dist_temp * PREM_TARIFF_C_KM);
			journey[0] = duration;
			journey[1] = min_fare / 100;
		}
	}

	private void maxSpecialPremiumFare() {
		double dist_temp = distance;
		double max_duration = duration + TARRIF_A_PEAK_MINS_PER_KM;
		double max_fare = PREM_INIT
				+ (TARRIF_A_PEAK_MINS_PER_KM * PREM_TARIFF_A_MIN);

		/** minimum fare */
		if (distance <= MIN_KM) {
			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}
		/** Tarrif C fare */
		else {
			dist_temp = dist_temp - MIN_KM;
			max_duration = dist_temp * TARRIF_B_PEAK_MINS_PER_KM;
			/** TODO What minute rate to apply here????? */
			max_fare += (dist_temp * PREM_TARIFF_C_KM + max_duration
					* PREM_TARIFF_C_MIN);

			journey[2] = max_duration;
			journey[3] = max_fare / 100;
		}

	}

}