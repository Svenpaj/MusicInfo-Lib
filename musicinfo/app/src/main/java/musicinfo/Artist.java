package musicinfo;

import javax.sound.midi.Instrument;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

class Artist extends MusicItem {

    public int birthYear;
    public ArrayList<Album> albums = new ArrayList<Album>();
    public HashMap<Album, String> albumInstruments = new HashMap<Album, String>();

    public Artist(String name, int birthYear) {
        super(name);
        this.birthYear = birthYear;
        register(Artist.class, this);
    }

    public int myOwnIndex() {
        return getRegistryOf(Artist.class).indexOf(this);
    }

    public List<Band> getBandsView() {
        List<Band> bands = new ArrayList<>();
        for (MusicItem musicItem : getRegistryOf(Band.class)) {
            Band band = (Band) musicItem;
            if (band.artists.contains(this)) {
                bands.add(band);
            }
        }
        return bands;
    }

    public void addBand(Band band, int year) {
        band.addArtist(this, year);        
    }

    public void removeBand(int i, int year) {
        Band band = getBandsView().get(i);
        int myIndexInBand = band.artists.indexOf(this);
        band.removeArtist(myIndexInBand, year);
    }


    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void removeAlbum(int i) {
        Album album = albums.remove(i);
        albumInstruments.remove(album);
    }

    public void setInstrument(Album album, String instruments) {
        albumInstruments.put(album, instruments);
    }

    public void show() {
        System.out.println(this.name + " (" + this.birthYear + ")");
        LocalDate myObj = LocalDate.now();
        int year = myObj.getYear();
        System.out.println("Age: " + (year - birthYear) + " years old");

        System.out.println("\n" + this.name + "'s released albums:");
        int albumCounter = 1;
        for (Album album : albums) {
            System.out.println("(" + albumCounter + ") " + album + " (" + album.releaseYear + ")");
            albumCounter++;
        }

        System.out.println("\n" + this.name + "'s album instruments");
        int instrumentCounter = 1;
        for (Map.Entry<Album, String> entry : albumInstruments.entrySet()) {
            System.out.println("(" + instrumentCounter + ") " + entry.getKey() + " - " + entry.getValue());
            instrumentCounter++;
        }

        System.out.println("\n" + this.name + "'s bands:");
        enumerate(getBandsView());

        System.out.println("\nBand history:");
        for (MusicItem musicItem : getRegistryOf(Band.class)) {
            Band band = (Band) musicItem;
            List<Integer> history = band.getArtistHistory(this);
            if (history.size() != 0) {
                System.out.printf("%s:\n", band);
                for (int i = 0; i < history.size(); i++) {
                    if (i == 0) { 
                        System.out.printf("  - joined in %d\n", history.get(i)); 
                    } else if (i % 2 != 0) {
                        System.out.printf("  - left in %d\n", history.get(i));
                    } else {
                        System.out.printf("  - rejoined in %d\n", history.get(i));
                    }
                }
            }
        }
        System.out.println("\n" + "About:\n" + this.info);


    }
}






