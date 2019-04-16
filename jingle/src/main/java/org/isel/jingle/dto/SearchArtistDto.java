package org.isel.jingle.dto;

public class SearchArtistDto {

    private final SearchArtistResultsDto results;

    public SearchArtistDto(SearchArtistResultsDto results) {
        this.results = results;
    }

    public SearchArtistResultsDto getResults() {
        return results;
    }

}
