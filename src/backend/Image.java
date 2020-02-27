package backend;

import java.time.LocalDate;

class Image{
    private double height;
    private double lenght;
    private String location=null;
    private Tag[] tags=null;
    private LocalDate timetaken=null;

    public Image(double heigh, double lenght, String location, Tag[] tags, LocalDate timetaken){
        this.height=heigh;
        this.lenght=lenght;
        this.location=location;
        this.tags=tags;
        this.timetaken=timetaken;
    }
}