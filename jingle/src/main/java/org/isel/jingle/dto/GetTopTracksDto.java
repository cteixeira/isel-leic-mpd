package org.isel.jingle.dto;

public class GetTopTracksDto {

    private final TracksDto tracks;

    public GetTopTracksDto(TracksDto tracks) {
        this.tracks = tracks;
    }

    public TracksDto getTopTracks() {
        return tracks;
    }

}
