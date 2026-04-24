package flc.model;

public class Booking {
    private static int counter = 1000;

    private final String bookingId;
    private final String memberId;
    private String lessonId;
    private BookingStatus status;

    public Booking(String memberId, String lessonId) {
        this.bookingId = "BK" + (counter++);
        this.memberId = memberId;
        this.lessonId = lessonId;
        this.status = BookingStatus.BOOKED;
    }

    public void changeTo(String newLessonId) {
        this.lessonId = newLessonId;
        this.status = BookingStatus.CHANGED;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public void attend() {
        this.status = BookingStatus.ATTENDED;
    }

    public String getBookingId() { return bookingId; }
    public String getMemberId() { return memberId; }
    public String getLessonId() { return lessonId; }
    public BookingStatus getStatus() { return status; }

    public boolean isActive() {
        return status == BookingStatus.BOOKED || status == BookingStatus.CHANGED;
    }

    @Override
    public String toString() {
        return String.format("[%s] Member:%s Lesson:%s Status:%s",
                bookingId, memberId, lessonId, status);
    }
}
