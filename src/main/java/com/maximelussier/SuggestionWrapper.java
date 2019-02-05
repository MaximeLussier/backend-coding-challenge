package com.maximelussier;

import java.util.List;

public class SuggestionWrapper
{
    // List of suggestions
    private List<Suggestion> m_lstSuggestions;
    
    /**
     * @return the suggestions
     */
    public List<Suggestion> getSuggestions()
    {
        return m_lstSuggestions;
    }

    /**
     * @param persons the persons to set
     */
    public void setSuggestions(List<Suggestion> lstSuggestions)
    {
        this.m_lstSuggestions = lstSuggestions;
    }
}
