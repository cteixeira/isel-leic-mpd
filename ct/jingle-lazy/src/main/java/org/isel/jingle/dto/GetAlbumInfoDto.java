package org.isel.jingle.dto;

public class GetAlbumInfoDto {

    private final AlbumDto album;

    public GetAlbumInfoDto(AlbumDto album) {
        this.album = album;
    }

    public AlbumDto getAlbum() {
        return album;
    }

}
