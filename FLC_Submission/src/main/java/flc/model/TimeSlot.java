package flc.model;

public enum TimeSlot {
    MORNING("09:00"),
    AFTERNOON("13:00"),
    EVENING("18:00");

    private final String time;

    TimeSlot(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return name() + " (" + time + ")";
    }
}
