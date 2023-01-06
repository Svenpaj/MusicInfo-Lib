package musicinfo;

import static java.util.Map.entry;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;

public class App {
    public static String type = "(band|artist|album)";
    public static String text = "([ a-zA-Z0-9.åäö]+?)";
    public static String freetext = "([ a-zA-Z0-9.?!,:;åäö]+?)";
    public static String n = "(\\d+)";
    public static Map<String, Class<? extends MusicItem>> strToTypes = Map.of(
        "band", Band.class,
        "artist", Artist.class,
        "album", Album.class
    );

    public static int toInt(String str) {
        return Integer.parseInt(str);
    }

    public static void main(String[] args) {
        System.out.println("-".repeat(96) + "\n\nWelcome to MusicInfo, developed by Emanuel, Dennis, Max & Pontus!\n\nThe program is quite hard to understand, if you ever may need help please use the 'help' command!\n\n" + "-".repeat(96));
        Scanner in = new Scanner(System.in);
        while (true) { 
            String input = in.nextLine();
            boolean found = false;
            for (Map.Entry<String, Consumer<Matcher>> cmd : cmds.entrySet()) {
                Matcher m = Pattern.compile(cmd.getKey()).matcher(input);
                if (m.matches()) { 
                    try {
                        cmd.getValue().accept(m);
                        System.out.println();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.printf("A music item with that index does not exist (%s)%n",
                                          e.getMessage());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) { 
                System.out.printf("Command \"%s\" not found. " +
                                  "Type \"help\" for a command reference.%n",
                                  input);
            }
        } 
    }

    public static Map<String, Consumer<Matcher>> cmds = Map.ofEntries(
        entry("help", m -> { 
            String help = """
                        help: print this help message
                        save (filename): saves the current state of the program
                        load (filename): load the saved file
                        
                        show (bands|artists|albums): lists all of the musicItems
                        show (band|artist|album) (index): show musicItem
                        
                        new band (name) (bandStart) [bandEnd]: creates a new band
                        new artist (name) (birthYear): creates a new artist
                        new album (name) (releaseYear): creates a new album
                        
                        *** use the list command to find index ***
                        
                        delete band (bandIndex) : deletes the band of the suggested index
                        delete artist (artistIndex) : deletes the artist of the suggested index
                        delete album (albumIndex) : deletes the album of the suggested index
                        
                        add artist (artistIndex) to band (bandIndex) in (joinyear): add artist to band
                        add band (bandIndex) to artist (artistIndex) in (joinyear): add band to artist
                        add album (albumIndex) to artist (artistIndex): add album to artist
                        add album (albumIndex) to band (bandIndex): add album to band
                        
                        remove album (albumIndex) from band (bandIndex): remove album from band
                        remove album (albumIndex) from artist (artistIndex): remove album from artist
                        remove artist (artistIndex) from band (bandIndex) in (leaveYear): remove artist that left in year from band
                        remove band (bandIndex) from artist (artistIndex) in (leaveYear): remove band from artist that left in year

                        set album (albumIndex) instrument (text) for artist (artistIndex): sets an instrument to a artist on a album
                        set artist (artistIndex) instrument (text) for band (bandIndex) : sets an instrument to a artist in a band
                        set (band|artist|album) (Index) info (freetext) : adds "about" information given (band|artist|album)
                        
                    """;
            System.out.println(help);
        }),
        entry("save " + text, m -> {
            MusicItem.serialize(m.group(1));
            System.out.println("Successfully saved file to disk!");
        }),
        entry("load " + text, m -> {
            MusicItem.deserialize(m.group(1));
            System.out.println("Successfully loaded data from file!");
        }),

        entry(String.format("show %ss", type), m -> {
            Class<? extends MusicItem> type = strToTypes.get(m.group(1));

            if (MusicItem.getRegistryOf(type).toArray().length == 0) {
                System.out.println("The list is empty, please create something here first!");
            } else {
                MusicItem.enumerate(MusicItem.getRegistryOf(type));
            }
        }),

         entry(String.format("show band %s", n), m -> {
              Band band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(1)) - 1);
              band.show();
         }),

         entry(String.format("show artist %s", n), m -> {
              Artist artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(1)) - 1);
              artist.show();
         }),

         entry(String.format("show album %s", n), m -> {
               Album album = (Album) MusicItem.getFromRegistry(Album.class, toInt(m.group(1)) - 1);
               album.show();
         }),

         entry(String.format("new band %s %s( %s)?", text, n, n), m -> {
              Integer bandEnd = (m.group(4) != null) ? toInt(m.group(4)) : null;
              new Band(m.group(1), toInt(m.group(2)), bandEnd);
             System.out.println("Successfully created a new band!");
        }),

         entry(String.format("new artist %s %s", text, n), m -> {
             new Artist(m.group(1), toInt(m.group(2)));
             System.out.println("Successfully created a new artist!");
         }),

         entry(String.format("new album %s %s", text, n), m -> {
             new Album(m.group(1), toInt(m.group(2)));
             System.out.println("Successfully created a new album!");
         }),

         entry(String.format("delete band %s", n), m -> {
             MusicItem.unregister(Band.class, toInt(m.group(1)) - 1);
             System.out.println("Successfully deleted the band!");
         }),

         entry(String.format("delete artist %s", n), m -> {
            var artist = (Artist) MusicItem.unregister(Artist.class, toInt(m.group(1)) - 1);
            for (MusicItem musicItem : MusicItem.getRegistryOf(Band.class)) {
                Band band = (Band) musicItem;
                band.artists.remove(artist);
                band.artistHistories.remove(artist);
                band.artistInstruments.remove(artist);
            }
            System.out.println("Successfully deleted the artist!");
        }),
        entry(String.format("delete album %s", n), m -> {
              var album = (Album) MusicItem.unregister(Album.class, toInt(m.group(1)) - 1);
              for (MusicItem musicItem : MusicItem.getRegistryOf(Band.class)) {
                    Band band = (Band) musicItem;
                    band.albums.remove(album);
              }
              for (MusicItem musicItem : MusicItem.getRegistryOf(Artist.class)) {
                    Artist artist = (Artist) musicItem;
                    artist.albums.remove(album);
                    artist.albumInstruments.remove(album);
              }
            System.out.println("Successfully deleted the album!");
        }),

        entry(String.format("remove album %s from band %s", n, n), m -> {
            Band band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(2)) - 1);
            band.removeAlbum(toInt(m.group(1)) - 1);
            System.out.println("Successfully removed the album from the band!");
        }),

        entry(String.format("remove album %s from artist %s", n, n), m -> {
            Artist artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(2)) - 1);
            artist.removeAlbum(toInt(m.group(1)) - 1);
            System.out.println("Successfully removed the album from the artist!");
        }),

        entry(String.format("remove artist %s from band %s in %s", n, n, n), m -> {
             int artist = toInt(m.group(1)) - 1;
             Band band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(2)) - 1);
             int year = toInt(m.group(3));
             band.removeArtist(artist, year);
            System.out.println("Successfully removed the artist from the band!");
        }),

        entry(String.format("remove band %s from artist %s in %s", n, n, n), m -> {
             int band = toInt(m.group(1)) - 1;
             var artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(2)) - 1);
             int year = toInt(m.group(3));
             artist.removeBand(band, year);
            System.out.println("Successfully removed the band from the artist!");
        }),

        entry(String.format("add artist %s to band %s in %s", n, n, n), m -> {
             var artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(1)) - 1);
             var band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(2)) - 1);
             int year = toInt(m.group(3));
             band.addArtist(artist, year);
            System.out.println("Successfully added the artist to the band!");
        }),

        entry(String.format("add band %s to artist %s in %s", n, n, n), m -> {
             var band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(1)) - 1);
             var artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(2)) - 1);
             int year = toInt(m.group(3));
             artist.addBand(band, year);
            System.out.println("Successfully added the band to the artist!");
        }),


        entry(String.format("add album %s to artist %s", n, n), m -> {
              Album album = (Album) MusicItem.getFromRegistry(Album.class, toInt(m.group(1)) - 1);
              Artist artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(2)) - 1);
              artist.addAlbum(album);
            System.out.println("Successfully added the album to the artist!");
        }),

        entry(String.format("add album %s to band %s", n, n), m -> {
              Album album = (Album) MusicItem.getFromRegistry(Album.class, toInt(m.group(1)) - 1);
              Band bands = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(2)) - 1);
              bands.addAlbum(album);
            System.out.println("Successfully added the album to the band!");
        }),

        entry(String.format("set album %s instrument %s for artist %s", n, text, n), m -> {
             var artist = (Artist) MusicItem.getFromRegistry(Artist.class, toInt(m.group(3)) - 1);
             Album album = artist.albums.get(toInt(m.group(1)) - 1);
             artist.albumInstruments.put(album, m.group(2));
            System.out.println("Successfully set the artists instrument!");
        }),

        entry(String.format("set artist %s instrument %s for band %s", n, text, n), m -> {
              var band = (Band) MusicItem.getFromRegistry(Band.class, toInt(m.group(3)) - 1);
              Artist artist = band.artists.get(toInt(m.group(1)) - 1);
              band.artistInstruments.put(artist, m.group(2));
            System.out.println("Successfully set the band members instrument!");
        }),


        entry(String.format("set %s %s info %s", type, n, freetext), m -> {
            Class<? extends MusicItem> type = strToTypes.get(m.group(1));
            MusicItem.getFromRegistry(type, toInt(m.group(2)) - 1).info = m.group(3);
            System.out.println("Successfully changed the information text!");
        })


    );


}

