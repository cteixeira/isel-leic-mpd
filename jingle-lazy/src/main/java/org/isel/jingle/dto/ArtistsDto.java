package org.isel.jingle.dto;

public class ArtistsDto {

    private final ArtistDto[] artist;

    public ArtistsDto(ArtistDto[] artist) {
        this.artist = artist;
    }

    public ArtistDto[] getArtists() {
        return artist;
    }

}
