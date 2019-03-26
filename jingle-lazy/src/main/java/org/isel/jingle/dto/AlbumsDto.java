package org.isel.jingle.dto;

public class AlbumsDto {

    private final AlbumDto[] album;

    public AlbumsDto(AlbumDto[] album) {
        this.album = album;
    }

    public AlbumDto[] getAlbums() {
        return album;
    }

}
