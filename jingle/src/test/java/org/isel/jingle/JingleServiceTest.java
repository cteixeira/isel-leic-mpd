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

import org.isel.jingle.model.Album;
import org.isel.jingle.model.Artist;
import org.isel.jingle.model.Track;
import org.isel.jingle.req.BaseRequest;
import org.isel.jingle.req.HttpRequest;
import org.isel.jingle.util.StreamUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class JingleServiceTest {

    static class HttpGet implements Function<String, InputStream> {
        int count = 0;
        @Override
        public InputStream apply(String path) {
            System.out.println("Requesting..." + ++count);
            return HttpRequest.openStream(path);
        }
    }

    @Test
    public void searchHiperAndCountAllResults() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Supplier<Stream<Artist>> artists = () -> service.searchArtist("hiper");
        assertEquals(0, httpGet.count);
        long countArtists = artists.get().count();
        assertEquals(702, countArtists); //GROUP5: CHANGED FROM 700 TO 702
        assertEquals(25, httpGet.count);
        Artist last = artists.get().skip(countArtists-1).findFirst().get();
        assertEquals("Coma - Hipertrofia.(2008)", last.getName());
        assertEquals(49, httpGet.count);
    }

    @Test
    public void searchHiperAndCountAllResultsWithCache() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Supplier<Stream<Artist>> artists = () -> service.searchArtist("hiper");
        Supplier<Stream<Artist>> artistsSupplier = StreamUtils.cache(artists);
        Object[] expected = artistsSupplier.get().limit(700).toArray();
        Object[] actual = artistsSupplier.get().limit(700).toArray();
        assertArrayEquals(expected,actual);
        assertEquals(24, httpGet.count);
    }

    @Test
    public void getFirstAlbumOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Stream<Artist> artists = service.searchArtist("muse");
        assertEquals(0, httpGet.count);
        Artist muse = artists.findFirst().get();
        assertEquals(1, httpGet.count);
        Stream<Album> albums = muse.getAlbums();
        assertEquals(1, httpGet.count);
        Album first = albums.findFirst().get();
        assertEquals(2, httpGet.count);
        assertEquals("Black Holes and Revelations", first.getName());
    }

    @Test
    public void get111AlbumsOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Artist muse = service.searchArtist("muse").findFirst().get();
        long countAlbuns = muse.getAlbums().limit(111).count();
        assertEquals(111, countAlbuns);
        assertEquals(4, httpGet.count); // 1 for artist + 3 pages of albums each with 50 albums
    }

    @Test
    public void getSecondSongFromBlackHolesAlbumOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Album blackHoles = service.searchArtist("muse").findFirst().get().getAlbums().findFirst().get();
        assertEquals(2, httpGet.count); // 1 for artist + 1 page of albums
        assertEquals("Black Holes and Revelations", blackHoles.getName());
        Track song = blackHoles.getTracks().skip(1).findAny().get();
        assertEquals(3, httpGet.count); // + 1 to getTracks
        assertEquals("Starlight", song.getName());
    }

    @Test
    public void get42thTrackOfMuse() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Stream<Track> tracks = service.searchArtist("muse").findFirst().get().getTracks();
        assertEquals(1, httpGet.count); // 1 for artist + 0 for tracks because it fetches lazily
        Track track = tracks.skip(42).findFirst().get(); // + 1 to getAlbums + 4 to get tracks of first 4 albums.
        assertEquals("MK Ultra", track.getName());
        assertEquals(6, httpGet.count);
    }
    @Test
    public void getLastTrackOfMuseOf500() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        long countTracks = service.searchArtist("muse").findFirst().get().getTracks().limit(500).count();
        assertEquals(500, countTracks);
        assertEquals(78, httpGet.count); // Each page has 50 albums => 50 requests to get their tracks. Some albums have no tracks.
    }

    @Test
    public void getTop100TracksOfSpain() {
        HttpGet httpGet = new HttpGet();
        JingleService service = new JingleService(new LastfmWebApi(new BaseRequest(httpGet)));
        Supplier<Stream<Track>> tracksSupplier = () -> service.getTopTracks("spain");
        assertEquals(0, httpGet.count);
        long count = tracksSupplier.get().limit(100).count();
        assertEquals(100, count);
        assertEquals(2, httpGet.count);
        Track first = tracksSupplier.get().findFirst().get();
        assertEquals("The Less I Know the Better", first.getName());
    }
}
