package model;

public class Space {

    private String space;
    private float area;


    public Space( String space, String area ) {
        this.space = space;
        this.area = Float.parseFloat(area.trim().substring(1, area.length() - 12));
        int x = 0;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace( String space ) {
        this.space = space;
    }


    public float getArea() {
        return area;
    }

    public void setArea( float area ) {
        this.area = area;
    }
}
