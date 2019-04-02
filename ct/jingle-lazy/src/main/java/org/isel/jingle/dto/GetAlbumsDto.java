package org.isel.jingle.dto;

public class GetAlbumsDto {

    private final AlbumsDto topalbums;

    public GetAlbumsDto(AlbumsDto topalbums) {
        this.topalbums = topalbums;
    }

    public AlbumsDto getTopAlbums() {
        return topalbums;
    }

}
