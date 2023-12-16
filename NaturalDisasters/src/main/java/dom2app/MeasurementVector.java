package dom2app;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/*
 * H class MeasurementVector ylopoiei to IMeasurementVector interface pou mas dinetai etoimo
 * me kapoies extra methodous pou prosthetoume emeis gia na kanoume thn anakthsh twn dedomenwn
 * apo to arxeio.
*/
public class MeasurementVector implements IMeasurementVector{
	private String[] splited_vector; // h grammh xwrismenh me vash ton delimiter
	
	// Constructor
	public MeasurementVector(String vector, String delimiter)	{
		this.splited_vector = splitVector(vector, delimiter);
	}

	// Edw spaw to string me vash ton delimiter
	public String[] splitVector(String vector, String delimiter)	{
		String[] splited_row = vector.split(delimiter);
		
		return splited_row;
	}
	
	// getter gia to onoma ths xwras
	public String getCountryName() {
		return splited_vector[1];
	}
	
	// getter gia ton typo ths katastrofis
	public String getIndicatorString() {
		return splited_vector[4];
	}
	
	// epistrefei ena arraylist me pairs apo <year, number of events>
	// gia to sigkekrimeno <country, indicator>
	public ArrayList<Pair<Integer, Integer>> getMeasurements() {
		ArrayList<Pair<Integer,Integer>> mCountryIndicator = new ArrayList<Pair<Integer,Integer>>();
		int year = 1980; // first year
		
		Pair<Integer,Integer> yearNumberOfEvents = new Pair<Integer,Integer>(year,Integer.parseInt(this.splited_vector[5]));
		mCountryIndicator.add(yearNumberOfEvents);
		year += 1; // increase year by one
		
		for(int i = 6; i < this.splited_vector.length; i++)	{
			yearNumberOfEvents = new Pair<Integer,Integer>(year,Integer.parseInt(splited_vector[i]));
			mCountryIndicator.add(yearNumberOfEvents);
			year += 1;
		}
		
		return mCountryIndicator;
	}
	
	// edw kanoume olous tous ypologismous twn vasikwn
	// statistikwn
	public ArrayList<Integer> calculateDescriptiveStats()	{
		ArrayList<Integer> statsList = new ArrayList<Integer>();
		
		int i;
		
		for(i = 5; i < splited_vector.length; i++) {
			statsList.add(Integer.parseInt(splited_vector[i]));
		}
		return statsList; 
	}
	
	// metatrepw ta stats se String kai to epistrefw
	public String getDescriptiveStatsAsString() {
		String stats = "";
		
		ArrayList<Integer> descriptiveStats = calculateDescriptiveStats();
		
		stats += ("count=") + (descriptiveStats.size());
        stats += (", min=") + (Collections.min(descriptiveStats));
        stats += (", gMean=") + (calculateGeometricMean(descriptiveStats));
        stats += (", mean=") + (calculateMean(descriptiveStats));
        stats += (", median=") + (calculateMedian(descriptiveStats));
        stats += (", max=") + (Collections.max(descriptiveStats));
        stats += (", kurtosis=") + (calculateKurtosis(descriptiveStats));
        stats += (", stdev=") + (calculateStandardDeviation(descriptiveStats));
        stats += (", sum=") + (calculateSum(descriptiveStats));
		return stats;
	}
	// edw ypologizoume to regression
	public SimpleRegression calculateRegression()	{
		SimpleRegression regression = new SimpleRegression();
        for (Pair<Integer, Integer> dataPoint : getMeasurements()) {
            regression.addData(dataPoint.getKey(), dataPoint.getValue());
        }
        return regression; 
	}
	// metatrepw to apotelesma tou regression se String kai to epistrefw
	public String getRegressionResultAsString() {
		SimpleRegression regression = calculateRegression();
        double intercept = regression.getIntercept();
        double slope = regression.getSlope();
        double slopeError = regression.getSlopeStdErr();
        String tendency = getLabel();

        return String.format("RegressionResult [intercept=%.4f, slope=%.4f, slopeError=%.4f, Tendency %s]",
                intercept, slope, slopeError, tendency);
	}
	
	public String getLabel() {
		SimpleRegression regression = calculateRegression();
        double slope = regression.getSlope();
        
        if (Double.isNaN(slope)) {
            return "Tendency Undefined";
        } else if (slope > 0.1) {
            return "Increased Tendency";
        } else if (slope < -0.1) {
            return "Decreased Tendency";
        } else {
            return "Tendency stable";
        }
    }
	//akolouthoun voithitikes synarthseis
	private double calculateGeometricMean(ArrayList<Integer> data) {
	   double product = 1.0;
	   for (int value : data) {
		   product *= value;
	   }
	  return Math.pow(product, 1.0 / data.size());
	}
	 
	private double calculateMean(ArrayList<Integer> data) {
		int sum = 0;
	    for (int value : data) {
	    	sum += value;
	    }
	    return (double) sum / data.size();
	}
	
	private double calculateMedian(ArrayList<Integer> data) {
        Collections.sort(data);
        int middle = data.size() / 2;
        if (data.size() % 2 == 0) {
            return (data.get(middle - 1) + data.get(middle)) / 2.0;
        } else {
            return data.get(middle);
        }
    }
	
	private double calculateStandardDeviation(ArrayList<Integer> data) {
        double mean = calculateMean(data);
        double sumSquaredDeviations = 0.0;
        for (int value : data) {
            sumSquaredDeviations += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumSquaredDeviations / data.size());
    }
	
	private int calculateSum(ArrayList<Integer> data) {
        int sum = 0;
        for (int value : data) {
            sum += value;
        }
        return sum;
	}
	
	private double calculateKurtosis(ArrayList<Integer> data) {
	    double mean = calculateMean(data);
	    double n = data.size();

	    double sumFourthPowerDeviations = 0.0;
	    for (int value : data) {
	        sumFourthPowerDeviations += Math.pow(value - mean, 4);
	    }

	    double numerator = (n * (n + 1) * (n - 1)) * sumFourthPowerDeviations;
	    double denominator = (n - 1) * (n - 2) * (n - 3) * Math.pow(calculateStandardDeviation(data), 4);

	    return numerator / denominator - (3 * Math.pow(n - 1, 2)) / ((n - 2) * (n - 3));
	}
}
