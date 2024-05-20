package Hito4;

public class Photographer {
    private int photographerId;
    private String name;
    private boolean awarded;

    public Photographer(int photographerId, String name, boolean awarded) {
        this.photographerId = photographerId;
        this.name = name;
        this.awarded = awarded;
    }

    public int getPhotographerId() {
        return photographerId;
    }

    public void setPhotographerId(int photographerId) {
        this.photographerId = photographerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAwarded() {
        return awarded;
    }

    public void setAwarded(boolean awarded) {
        this.awarded = awarded;
    }

    @Override
    public String toString() {
        return name;
    }
}
