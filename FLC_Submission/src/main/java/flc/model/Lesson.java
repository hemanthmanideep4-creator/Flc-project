package flc.model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private static final int MAX_CAPACITY = 4;

    private final String lessonId;
    private final ExerciseType exerciseType;
    private final String day;       // "Saturday" or "Sunday"
    private final TimeSlot timeSlot;
    private final int weekNumber;   // 1-8
    private final int month;        // e.g. 4 for April, 5 for May

    private final List<String> bookedMemberIds = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    public Lesson(String lessonId, ExerciseType exerciseType, String day,
                  TimeSlot timeSlot, int weekNumber, int month) {
        this.lessonId = lessonId;
        this.exerciseType = exerciseType;
        this.day = day;
        this.timeSlot = timeSlot;
        this.weekNumber = weekNumber;
        this.month = month;
    }

    public boolean hasSpace() {
        return bookedMemberIds.size() < MAX_CAPACITY;
    }

    public boolean bookMember(String memberId) {
        if (!hasSpace()) return false;
        if (bookedMemberIds.contains(memberId)) return false;
        bookedMemberIds.add(memberId);
        return true;
    }

    public boolean releaseMember(String memberId) {
        return bookedMemberIds.remove(memberId);
    }

    public boolean hasMemberBooked(String memberId) {
        return bookedMemberIds.contains(memberId);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public int getAttendedCount() { return reviews.size(); }
    public int getBookedCount() { return bookedMemberIds.size(); }

    public double getTotalIncome() {
        return getAttendedCount() * exerciseType.getPrice();
    }

    // Getters
    public String getLessonId() { return lessonId; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public String getDay() { return day; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public int getWeekNumber() { return weekNumber; }
    public int getMonth() { return month; }
    public List<Review> getReviews() { return reviews; }
    public List<String> getBookedMemberIds() { return bookedMemberIds; }

    @Override
    public String toString() {
        return String.format("[%s] Week%d %s %s | %s | £%.2f | Spaces: %d/4",
                lessonId, weekNumber, day, timeSlot, exerciseType,
                exerciseType.getPrice(), MAX_CAPACITY - bookedMemberIds.size());
    }
}
  

