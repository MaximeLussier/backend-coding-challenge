package com.maximelussier;

import java.util.Comparator;

public class Suggestion
{
	// City name formatted as "Name, State/Province, Country"
    private String m_strName;
    // City latitude
    private Double m_dLatitude;
    // City longitude
    private Double m_dLongitude;
    // City score
    private Double m_dScore;
    
    // Default constructor for the Suggestion data class
    public Suggestion(String strName,
    		          Double dLatitude,
    		          Double dLongitude,
    		          Double dScore)
    {
        this.m_strName = strName;
        this.m_dLatitude = dLatitude;
        this.m_dLongitude = dLongitude;
        this.m_dScore = dScore;
    }
    
    // Comparator for sorting the list by score from highest to lowest
    public static Comparator<Suggestion> SuggestionScoreComparator = new Comparator<Suggestion>()
    {
    	public int compare(Suggestion cSuggestion1, Suggestion cSuggestion2)
    	{
    		return -(Double.compare(cSuggestion1.getScore(), cSuggestion2.getScore()));
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
    
    public Double getScore()
    {
        return m_dScore;
    }

    public void setScore(Double dScore)
    {
        this.m_dScore = dScore;
    }
}
