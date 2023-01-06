package musicinfo;

import java.util.ArrayList;

class Album extends MusicItem {
    public int releaseYear;


    public Album(String name, int releaseYear){
        super(name);
        this.releaseYear = releaseYear;
        register(Album.class, this);
    }

    public void show() {
        System.out.println("\n" + this.name + " " + "( " + releaseYear + " )");
        System.out.println("\n" + "About:\n" + this.info);
    }
}
