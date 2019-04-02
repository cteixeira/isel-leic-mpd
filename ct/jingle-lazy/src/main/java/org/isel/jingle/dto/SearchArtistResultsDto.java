package org.isel.jingle.dto;

public class SearchArtistResultsDto {

    private final ArtistsDto artistmatches;

    public SearchArtistResultsDto(ArtistsDto artistmatches) {
        this.artistmatches = artistmatches;
    }

    public ArtistsDto getMatches() {
        return artistmatches;
    }

}
