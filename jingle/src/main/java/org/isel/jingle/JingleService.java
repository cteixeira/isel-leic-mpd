/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2019, Miguel Gamboa (gamboa.pt)
 *
 *   All rights granted under this License are granted for the term of
 * copyright on the Program, and are irrevocable provided the stated
 * conditions are met.  This License explicitly affirms your unlimited
 * permission to run the unmodified Program.  The output from running a
 * covered work is covered by this License only if the output, given its
 * content, constitutes a covered work.  This License acknowledges your
 * rights of fair use or other equivalent, as provided by copyright law.
 *
 *   You may make, run and propagate covered works that you do not
 * convey, without conditions so long as your license otherwise remains
 * in force.  You may convey covered works to others for the sole purpose
 * of having them make modifications exclusively for you, or provide you
 * with facilities for running those works, provided that you comply with
 * the terms of this License in conveying all material for which you do
 * not control copyright.  Those thus making or running the covered works
 * for you must do so exclusively on your behalf, under your direction
 * and control, on terms that prohibit them from making any copies of
 * your copyrighted material outside their relationship with you.
 *
 *   Conveying under any other circumstances is permitted solely under
 * the conditions stated below.  Sublicensing is not allowed; section 10
 * makes it unnecessary.
 *
 */

package org.isel.jingle;

import org.isel.jingle.dto.AlbumDto;
import org.isel.jingle.dto.ArtistDto;
import org.isel.jingle.dto.TrackDto;
import org.isel.jingle.model.Album;
import org.isel.jingle.model.Artist;
import org.isel.jingle.model.Track;
import org.isel.jingle.req.BaseRequest;
import org.isel.jingle.req.HttpRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class JingleService {

    final LastfmWebApi api;

    public JingleService(LastfmWebApi api) {
        this.api = api;
    }

    public JingleService() {
        this(new LastfmWebApi(new BaseRequest(HttpRequest::openStream)));
    }

    public Stream<Artist> searchArtist(String name) {

        boolean hasMore[] = {true};

        return Stream
                .iterate(1, page -> page + 1)
                .takeWhile(page -> hasMore[0])
                .flatMap(page -> {
                    ArtistDto[] artistsArr = api.searchArtist(name, page);
                    hasMore[0] = artistsArr.length > 0;
                    return Stream.of(artistsArr);
                })
                .map(this::toArtist);
    }

    public Stream<Track> getTopTracks(String country) {

        boolean hasMore[] = {true};

        return Stream
                .iterate(1, page -> page + 1)
                .takeWhile(page -> hasMore[0])
                .flatMap(page -> {
                    TrackDto[] tracksArr = api.getTopTracks(country, page);
                    hasMore[0] = tracksArr.length > 0;
                    return Stream.of(tracksArr);
                })
                .map(this::toTrack);
    }

    private Artist toArtist(ArtistDto artistDto) {

        String mbId = artistDto.getMbid();

        Supplier<Stream<Album>> albumsSupplier = () -> getAlbums(mbId);
        Supplier<Stream<Track>> tracksSupplier = () -> getTracks(mbId);

        return new Artist(
                artistDto.getName(),
                artistDto.getListeners(),
                mbId,
                artistDto.getUrl(),
                null,
                albumsSupplier,
                tracksSupplier);
    }

    private Stream<Album> getAlbums(String artistMbid) {

        boolean hasMore[] = {true};

        return Stream
                .iterate(1, page -> page + 1)
                .takeWhile(page -> hasMore[0])
                .flatMap(page -> {
                    AlbumDto[] albumsArr = api.getAlbums(artistMbid, page);
                    hasMore[0] = albumsArr.length > 0;
                    return Stream.of(albumsArr);
                })
                .map(this::toAlbum);
    }

    private Album toAlbum(AlbumDto albumDto) {

        Supplier<Stream<Track>> tracksSupplier = () -> getAlbumTracks(albumDto.getMbid());

        return new Album(
                albumDto.getName(),
                albumDto.getPlaycount(),
                albumDto.getMbid(),
                albumDto.getUrl(),
                null,
                tracksSupplier);
    }

    private Stream<Track> getTracks(String artistMbid) {
        return getAlbums(artistMbid).flatMap(album -> album.getTracks());
    }

    private Stream<Track> getAlbumTracks(String albumMbid) {
        return Stream
                .of(api.getAlbumInfo(albumMbid))
                .map(this::toTrack);
    }

    private Track toTrack(TrackDto trackDto) {
        return trackDto != null ? new Track(trackDto.getName(), trackDto.getUrl(), trackDto.getDuration()) : null;
    }


}
