package backend;

import java.time.LocalDate;

class Image{
    private double height;
    private double lenght;
    private String location;
    private Tag[] tags;
    private LocalDate timetaken;

    public Image(double heigh, double lenght, String location, Tag[] tags, LocalDate timetaken){
        this.height=heigh;
        this.lenght=lenght;
        this.location=location;
        this.tags=tags;
        this.timetaken=timetaken;
    }
}