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
import org.isel.jingle.util.queries.LazyQueries;
import org.isel.jingle.util.req.BaseRequest;
import org.isel.jingle.util.req.HttpRequest;

import static org.isel.jingle.util.queries.LazyQueries.from;
import static org.isel.jingle.util.queries.LazyQueries.last;

public class JingleService {

    final LastfmWebApi api;

    public JingleService(LastfmWebApi api) {
        this.api = api;
    }

    public JingleService() {
        this(new LastfmWebApi(new BaseRequest(HttpRequest::openStream)));
    }

    public Iterable<Artist> searchArtist(String name) {

        boolean hasMore[] = {true};

        Iterable<ArtistDto> artistDtos = LazyQueries.takeWhile(LazyQueries.flatMap(LazyQueries.iterate(1, page -> page + 1), page -> {
            ArtistDto[] artistsArr = api.searchArtist(name, page);
            hasMore[0] = artistsArr.length > 0;
            //return LazyQueries.cache(LazyQueries.from(artistsArr));
            return LazyQueries.from(artistsArr);
        }), a -> hasMore[0]);

        //return LazyQueries.cache(LazyQueries.map(artistDtos, this::toArtist));
        return LazyQueries.map(artistDtos, this::toArtist);
    }

    private Artist toArtist(ArtistDto artistDto) {

        String mbId = artistDto.getMbid();

        Iterable<Album> albums = () -> getAlbums(mbId).iterator();
        Iterable<Track> tracks = () -> getTracks(mbId).iterator();

        return new Artist(
                artistDto.getName(),
                artistDto.getListeners(),
                mbId,
                artistDto.getUrl(),
                null,
                albums,
                tracks);
    }

    private Iterable<Album> getAlbums(String artistMbid) {
        //Iterable<AlbumDto> albumsDto = LazyQueries.from(api.getAlbums(artistMbid, 1));
        //return LazyQueries.map(albumsDto, this::toAlbum);

        boolean hasMore[] = {true};

        Iterable<AlbumDto> albumsDto = LazyQueries.takeWhile(LazyQueries.flatMap(LazyQueries.iterate(1, page -> page + 1), page -> {
            AlbumDto[] albumsArr = api.getAlbums(artistMbid, page);
            hasMore[0] = albumsArr.length > 0;
            return LazyQueries.from(albumsArr);
        }), a -> hasMore[0]);

        return LazyQueries.map(albumsDto, this::toAlbum);
    }

    private Album toAlbum(AlbumDto albumDto) {

        Iterable<Track> tracks = () -> getAlbumTracks(albumDto.getMbid()).iterator();

        return new Album(
                albumDto.getName(),
                albumDto.getPlaycount(),
                albumDto.getMbid(),
                albumDto.getUrl(),
                null,
                tracks);
    }

    private Iterable<Track> getTracks(String artistMbid) {
        return LazyQueries.flatMap(getAlbums(artistMbid),(a) -> a.getTracks());
    }

    private Iterable<Track> getAlbumTracks(String albumMbid) {
        Iterable<TrackDto> tracksDto = LazyQueries.from(api.getAlbumInfo(albumMbid));
        return LazyQueries.map(tracksDto, this::toTrack);
    }

    private Track toTrack(TrackDto trackDto) {
        return trackDto != null ? new Track(trackDto.getName(), trackDto.getUrl(), trackDto.getDuration()) : null;
    }


}
