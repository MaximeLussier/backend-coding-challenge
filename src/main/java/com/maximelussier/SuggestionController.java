package com.maximelussier;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuggestionController
{
	CitiesHandler m_citiesHandler;
	
	public SuggestionController()
	{
		m_citiesHandler = new CitiesHandler();
		// Load the list of cities.
		m_citiesHandler.readTsvFile();
    	// Sort the list of cities by name to accelerate queries
		m_citiesHandler.sortCityList();
	}

    @RequestMapping("/suggestions")
    public SuggestionWrapper greeting(@RequestParam(value="q", defaultValue="") String strQuery,
    		                          @RequestParam(value="latitude", defaultValue="") String strLatitude,
    		                          @RequestParam(value="longitude", defaultValue="") String strLongitude)
    {
    	// Find cities with names starting with strQuery
    	List<City> lstCity = m_citiesHandler.findCitiesStartsWith(strQuery);

        // Calculate the score for each city
        m_citiesHandler.calculateScore(lstCity, strQuery, strLatitude, strLongitude);
        
    	// Convert the City class into suggestions
        List<Suggestion> lstSuggestions = m_citiesHandler.convertToSuggestions(lstCity);
        
        // Sort the list by score 
        Collections.sort(lstSuggestions, Suggestion.SuggestionScoreComparator);
        
        // Keep 5 best options
        lstSuggestions = lstSuggestions.subList(0, Math.min(5, lstSuggestions.size()));
        
        // Return the suggestions
        SuggestionWrapper cWrapper = new SuggestionWrapper();
        cWrapper.setSuggestions(lstSuggestions);
        return cWrapper;
    }
}
