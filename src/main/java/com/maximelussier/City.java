package com.maximelussier;

import java.util.Comparator;

public class City
{
	// City name
    private String m_strName;
	// City country
    private String m_strCountry;
	// City state or province
    private String m_strState;
    // City latitude
    private Double m_dLatitude;
    // City longitude
    private Double m_dLongitude;
    // City population
    private Integer m_nPopulation;
    // City score
    private Double m_dScore;
    
    // Default constructor for the City data class
    public City(String strName,
    		    String strCountry,
    		    String m_strState,
    		    Double dLatitude,
    		    Double dLongitude,
    		    Integer nPopulation)
    {
        this.m_strName = strName;
        this.m_strCountry = strCountry;
        this.m_strState = m_strState;
        this.m_dLatitude = dLatitude;
        this.m_dLongitude = dLongitude;
        this.m_nPopulation = nPopulation;
        // Start the score at 1.0, all cities are equal.
        m_dScore = 1.0;
    }
    
    // Comparator for sorting the list by City Name
    public static Comparator<City> CityNameComparator = new Comparator<City>()
    {
    	public int compare(City cCity1, City cCity2)
    	{
    		return cCity1.getName().compareTo(cCity2.getName());
    	}
    };
    
    // Getters and setters for all member variables
    public String getName()
    {
        return m_strName;
    }

    public void setName(String strName)
    {
        this.m_strName = strName;
    }
    
    public String getCountry()
    {
        return m_strCountry;
    }

    public void setCountry(String strCountry)
    {
        this.m_strCountry = strCountry;
    }

    public String getState()
    {
        return m_strState;
    }

    public void setState(String strState)
    {
        this.m_strState = strState;
    }

    public Double getLatitude()
    {
        return m_dLatitude;
    }

    public void setLatitude(Double dLatitude)
    {
        this.m_dLatitude = dLatitude;
    }
    
    public Double getLongitude()
    {
        return m_dLongitude;
    }

    public void setLongitude(Double dLongitude)
    {
        this.m_dLongitude = dLongitude;
    }
    
    public Integer getPopulation()
    {
        return m_nPopulation;
    }

    public void setPopulation(Integer nPopulation)
    {
        this.m_nPopulation = nPopulation;
    }
    
    public Double getScore()
    {
        return m_dScore;
    }

    public void setScore(Double dScore)
    {
        this.m_dScore = dScore;
    }
}
