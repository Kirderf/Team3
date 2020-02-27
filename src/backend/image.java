class image{
    private double height;
    private double lenght;
    private String location;
    private tag[] tags;
    private LocalDate timetaken;

    public image(double heigh, double lenght, String location, tag[] tags, LocalDate timetaken){
        this.height=heigh;
        this.lenght=lenght;
        this.location=location;
        this.tags=tags;
        this.timetaken=timetaken;
    }
}