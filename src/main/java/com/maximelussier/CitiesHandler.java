package com.maximelussier;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CitiesHandler
{
	// List of cities (A Trie would be better but Java has no native Trie)
	private ArrayList<City> m_lstCities;
	
	/**
	 * Read TSV file and add each line to the cities list.
	 * 
	 * @param strPath      Path of the TSV file
	 */
	public void readTsvFile()
	{
		m_lstCities = new ArrayList<City>();
		
		try
		{
			// Open the file
			InputStream fstream = CitiesHandler.class.getResourceAsStream("/cities_canada-usa.tsv");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			// Ignore first line that contains headers.
			br.readLine();
			
			// Read file line By line
			String strLine;
			while ((strLine = br.readLine()) != null)
			{
				m_lstCities.add(parseCity(strLine.split("\t")));
			}
			// Close the input stream
			in.close();
		}
		catch (Exception e)
		{
			// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Sort the list of cities. This will have a limited impact
	 * but will allow the search loop to exit quicker. It would
	 * also allow for search algorithms like binary search to work.
	 */
	public void sortCityList()
	{
		Collections.sort(m_lstCities, City.CityNameComparator);
	}
	
	/**
	 * Find cities with names starting with the query.
	 * 
	 * !Improvement: Could also use alternate names
	 *               to find more matches.
	 *
	 * @param strQuery      List of City objects
	 * @return List of City object found.
	 */
	public ArrayList<City> findCitiesStartsWith(String strQuery)
	{
		ArrayList<City> lstFound = new ArrayList<City>();
		if (strQuery.isEmpty() == false)
		{
		    for (City cCity : m_lstCities)
		    {
		    	if (cCity.getName().toLowerCase().startsWith(strQuery.toLowerCase()))
		    	{
		    		lstFound.add(cCity);
		    	}
		    	else if (lstFound.size() != 0)
		    	{
		    		// Stop searching, the list is sorted we
		    		// found entries, there will be no more.
		    		break;
		    	}
		    }
		}
	    return lstFound;
	}
	
	/**
	 * Calculate city score based on distance (if available) population and completion.
	 *
	 * @param lstCity       List of City objects
	 * @param strQuery      String of the query name
	 * @param strQueryLat   String of the query latitude
	 * @param srtQueryLong  String of the query longitude
	 */
	public void calculateScore(List<City> lstCity, String strQuery, String strQueryLat, String srtQueryLong)
	{
		Double dLatitude = 0.0;
		Double dLongitude = 0.0;
		Boolean bUseLocation = false;
		
		if (strQueryLat.isEmpty() == false && srtQueryLong.isEmpty() == false)
		{
			try
			{
				bUseLocation = true;
				dLatitude = Double.parseDouble(strQueryLat);
				dLongitude += Double.parseDouble(srtQueryLong);
			}
			catch (NumberFormatException e)
			{
				// Ignore parsing errors but do not use location
				bUseLocation = false;
			}
		}

		Integer nMaxPopulation = 0;
	    for (City cCity : lstCity)
	    {
	    	if (cCity.getPopulation() > nMaxPopulation)
	    	{
	    		nMaxPopulation = cCity.getPopulation();
	    	}
	    }
		
	    for (City cCity : lstCity)
	    {
	    	Double dDistanceScore = 0.0;
	    	
	    	// Compare strings length to get a first metric of completion.
	    	// The longer part of the city the query is, the more likely
	    	// it is to be relevant. Up to a maximum of 75%.
	    	double dCompletionScore = Math.min((double) strQuery.length() / (double) cCity.getName().length(), 0.75);
	    	
	    	double dPopulationScore = Math.sqrt(cCity.getPopulation()) / Math.sqrt(nMaxPopulation);
	    
	    	if (bUseLocation)
	    	{
	    		// Calculate a factor for the distance.
	    		Double dDistance = calculateDistance(dLatitude, dLongitude, cCity.getLatitude(), cCity.getLongitude());

	    		dDistanceScore = 1.0 - (Math.sqrt(dDistance) / Math.sqrt(2.0 * Math.PI * 6371.0));
	    		
	    		// Weights for distance, population and completion can be adjusted.
	    		cCity.setScore(Math.round(((0.6 * dDistanceScore) + (0.2 * dCompletionScore) + (0.2 * dPopulationScore)) * 10.0) / 10.0);
	    	}
	    	else
	    	{
	    		// Weights for population and completion can be adjusted.
	    		cCity.setScore(Math.round(((0.4 * dCompletionScore) + (0.6 * dPopulationScore)) * 10.0) / 10.0);
	    	}
	    }
	}
	
	/**
	 * Convert an array of City object to Suggestion object used for JSON.
	 *
	 * @param lstCity   List of City objects
	 * @return List of Suggestion object to be used for JSON generation.
	 */
	public ArrayList<Suggestion> convertToSuggestions(List<City> lstCity)
	{
		String strNameTemplate = "%s, %s, %s";
		
		ArrayList<Suggestion> lstSuggestions = new ArrayList<Suggestion>();
	    for (City cCity : lstCity)
	    {
	    	String strName = String.format(strNameTemplate,
	    			                       cCity.getName(),
	    			                       cCity.getState(),
	    			                       cCity.getCountry());
	    	
	    	Suggestion cSuggestion = new Suggestion(strName,
	    			                                cCity.getLatitude(),
	    			                                cCity.getLongitude(),
	    			                                cCity.getScore());
	    	lstSuggestions.add(cSuggestion);
	    }
	    return lstSuggestions;
	}
	
	/**
	 * Calculate distance between coordinates in kilometers using haversine formula.
	 *
	 * @param dLatitudeQuery   latitude of the query
	 * @param dLongitudeQuery  longitude of the query
	 * @param dLatitudeTarget  latitude of the target
	 * @param dLongitudeTarget longitude of the target
	 * @return Distance between the query and the target, in kilometers.
	 */
	private double calculateDistance(double dLatitudeQuery,
			                         double dLongitudeQuery,
			                         double dLatitudeTarget,
			                         double dLongitudeTarget)
	{
		double dDeltaLatitude  = Math.toRadians((dLatitudeTarget - dLatitudeQuery));
        double dDeltaLongitude = Math.toRadians((dLongitudeTarget - dLongitudeQuery));

        dLatitudeQuery = Math.toRadians(dLatitudeQuery);
        dLatitudeTarget = Math.toRadians(dLatitudeTarget);
        
        double dA = Math.pow(Math.sin(dDeltaLatitude / 2), 2) + Math.cos(dLatitudeQuery) * Math.cos(dLatitudeTarget) * Math.pow(Math.sin(dDeltaLongitude / 2), 2);
        double dC = 2 * Math.atan2(Math.sqrt(dA), Math.sqrt(1 - dA));

        // Earth radius in KM at equator.
        return 6371.0 * dC;
	}
	
	/**
	 * Parse an array of string into a City object.
	 *
	 * @param astrCityRow   Array containing one city
	 * @return City object containing the values.
	 */
	private City parseCity(String[] astrCityRow)
	{
		String strName = astrCityRow[1];
		String strLatitude = astrCityRow[4];
		String strLongitude = astrCityRow[5];
		String strCountry = astrCityRow[8];
		String strState = astrCityRow[10];
		String strPopulation = astrCityRow[14];
		if (strCountry.equalsIgnoreCase("CA"))
		{
			strState = convertProvince(strState);
		}
		strCountry = convertCountry(strCountry);
		Double dLatitude = Double.parseDouble(strLatitude);
		Double dLongitude = Double.parseDouble(strLongitude);
		Integer nPopulation = Integer.parseInt(strPopulation);
		City cCity = new City(strName, strCountry, strState, dLatitude, dLongitude, nPopulation);
		return cCity;
	}
	
	/**
	 * Convert country code into usable name.
	 *
	 * @param strCountry   Country string as contained in TSV file.
	 * @return String value used for json return.
	 */
	private String convertCountry(String strCountry)
	{
		String strConverted = "";
	
		if (strCountry.equalsIgnoreCase("CA"))
		{
			strConverted = "Canada";
		}
		else if (strCountry.equalsIgnoreCase("US"))
		{
			strConverted = "USA";
		}
		else
		{
			strConverted = "UNKNOWN";
		}
		
		return strConverted;
	}
	
	/**
	 * Convert province code into usable name.
	 *
	 * @param strCode   Province code as contained in TSV file.
	 * @return String value used for json return.
	 */
	private String convertProvince(String strCode)
	{
		int nCode = 0;
		String strProvince = "";
		try
		{
			nCode = Integer.parseInt(strCode);
		}
		catch (NumberFormatException e)
		{
			// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		switch (nCode)
		{
			case 1: strProvince = "AB"; break; // Alberta
			case 2: strProvince = "BC"; break; // British Columbia
			case 3: strProvince = "MB"; break; // Manitoba
			case 4: strProvince = "NB"; break; // New Brunswick
			case 5: strProvince = "NF"; break; // Newfoundland and Labrador
			case 7: strProvince = "NS"; break; // Nova Scotia
			case 8: strProvince = "ON"; break; // Ontario
			case 9: strProvince = "PE"; break; // Prince Edward Island
			case 10: strProvince = "QC"; break; // Quebec
			case 11: strProvince = "SK"; break; // Saskatchewan
			case 12: strProvince = "YT"; break; // Yukon
			case 13: strProvince = "NT"; break; // Northwest Territories
			case 14: strProvince = "NU"; break; // Nunavut
		    default: strProvince = "INVALID"; break;
		}
		
		return strProvince;
	}
}


