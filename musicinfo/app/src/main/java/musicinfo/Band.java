package musicinfo;
import java.util.*;

class Band extends MusicItem {
    public int bandStart;
    public Integer bandEnd;
    public ArrayList<Artist> artists = new ArrayList<>();
    public HashMap<Artist, String> artistInstruments = new HashMap<>();
    public LinkedHashMap<Artist, List<Integer>> artistHistories = new LinkedHashMap<>();
    public ArrayList<Album> albums = new ArrayList<>();

    public Band(String name, int bandStart, Integer bandEnd) {
        super(name);
        this.bandStart = bandStart;
        this.bandEnd = bandEnd;
        register(Band.class, this);
    }

    public void setInstrument(Artist artist, String instruments){
        artistInstruments.put(artist, instruments);
    }

    public List<Integer> getArtistHistory(Artist artist) {
        if (!artistHistories.containsKey(artist)) {
            artistHistories.put(artist, new ArrayList<Integer>());
        }
        return artistHistories.get(artist);
    }

    public void addArtist(Artist artist, int year) {
        List<Integer> artistHistory = getArtistHistory(artist);
        if (artistHistory.size() % 2 != 0) {
            System.out.printf("artist %s can't join band %s; already in it\n", artist, this);
        } else {
            artists.add(artist);
            artistHistory.add(year);
        }
    }

    public void removeArtist(int i, int year) {
        Artist artist = artists.get(i);
        List<Integer> artistHistory = getArtistHistory(artist);
        artists.remove(i);
        artistHistory.add(year);
    }

    public void show(){
            String bandEnd = (this.bandEnd == null) ? "" : String.valueOf(this.bandEnd);
            System.out.println(this.name + " (" + this.bandStart + " - " + bandEnd + ")");
            System.out.println( "\nMembers:");
            int indexMembers = 1;
            for (Artist value : artists) {
                 System.out.println("(" + indexMembers + ") " + value.name);
                 indexMembers++;
            }

            System.out.println("\n" + "Albums:");
            int albumCounter = 1;
            for (Album album : albums) {
                System.out.println("(" + albumCounter + ") " + album + " (" + album.releaseYear + ")");
                albumCounter++;
            }

            System.out.println("\nInstruments:");
            int indexInstruments = 1;
            for (Map.Entry<Artist, String> entry : artistInstruments.entrySet()) {
            System.out.print("(" + indexInstruments + ") ");
            System.out.println(entry.getKey() + " - " + entry.getValue());
            indexInstruments++;
            }
            System.out.println("\n" + "About:\n" + this.info);

            System.out.println("\nMember history:");
            for (Map.Entry<Artist, List<Integer>> artistHistory : artistHistories.entrySet()) {
                System.out.printf("%s:\n", artistHistory.getKey());
                List<Integer> history = artistHistory.getValue();
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

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void removeAlbum(int i) {
        albums.remove(i);
    }


}
