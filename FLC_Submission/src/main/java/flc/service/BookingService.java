package flc.service;

import flc.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class BookingService {

    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private final TimetableService timetableService;

    public BookingService(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    /**
     * Book a lesson for a member.
     * Returns the new Booking on success, or null with a reason message (use makeBooking result).
     */
    public String book(String memberId, String lessonId) {
        Lesson lesson = timetableService.getLessonById(lessonId).orElse(null);
        if (lesson == null) return "ERROR: Lesson not found.";
        if (!lesson.hasSpace()) return "ERROR: Lesson is full (max 4 members).";

        // Check duplicate booking
        boolean alreadyBooked = bookings.values().stream()
                .anyMatch(b -> b.getMemberId().equals(memberId)
                        && b.getLessonId().equals(lessonId)
                        && b.isActive());
        if (alreadyBooked) return "ERROR: You have already booked this lesson.";

        // Check time conflict (same day + timeslot + week)
        boolean conflict = bookings.values().stream()
                .filter(b -> b.getMemberId().equals(memberId) && b.isActive())
                .map(b -> timetableService.getLessonById(b.getLessonId()).orElse(null))
                .filter(Objects::nonNull)
                .anyMatch(l -> l.getWeekNumber() == lesson.getWeekNumber()
                        && l.getDay().equals(lesson.getDay())
                        && l.getTimeSlot() == lesson.getTimeSlot());
        if (conflict) return "ERROR: Time conflict with an existing booking.";

        lesson.bookMember(memberId);
        Booking booking = new Booking(memberId, lessonId);
        bookings.put(booking.getBookingId(), booking);
        return "SUCCESS: Booking confirmed. Your Booking ID is " + booking.getBookingId();
    }

    /**
     * Change an existing booking to a new lesson.
     */
    public String changeBooking(String bookingId, String newLessonId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) return "ERROR: Booking not found.";
        if (!booking.isActive()) return "ERROR: Booking is not active (status: " + booking.getStatus() + ").";

        Lesson newLesson = timetableService.getLessonById(newLessonId).orElse(null);
        if (newLesson == null) return "ERROR: New lesson not found.";
        if (!newLesson.hasSpace()) return "ERROR: New lesson is full.";

        // Check duplicate
        boolean alreadyBooked = bookings.values().stream()
                .anyMatch(b -> !b.getBookingId().equals(bookingId)
                        && b.getMemberId().equals(booking.getMemberId())
                        && b.getLessonId().equals(newLessonId)
                        && b.isActive());
        if (alreadyBooked) return "ERROR: You already have a booking for that lesson.";

        // Release old lesson
        timetableService.getLessonById(booking.getLessonId())
                .ifPresent(l -> l.releaseMember(booking.getMemberId()));

        // Book new lesson
        newLesson.bookMember(booking.getMemberId());
        booking.changeTo(newLessonId);
        return "SUCCESS: Booking " + bookingId + " changed to lesson " + newLessonId;
    }

    /**
     * Cancel a booking.
     */
    public String cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) return "ERROR: Booking not found.";
        if (!booking.isActive()) return "ERROR: Booking is not active.";

        timetableService.getLessonById(booking.getLessonId())
                .ifPresent(l -> l.releaseMember(booking.getMemberId()));
        booking.cancel();
        return "SUCCESS: Booking " + bookingId + " has been cancelled.";
    }

    /**
     * Attend a lesson — adds review and rating.
     */
    public String attendLesson(String bookingId, String reviewText, int rating) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) return "ERROR: Booking not found.";
        if (!booking.isActive()) return "ERROR: Booking is not active (status: " + booking.getStatus() + ").";
        if (rating < 1 || rating > 5) return "ERROR: Rating must be between 1 and 5.";

        Lesson lesson = timetableService.getLessonById(booking.getLessonId()).orElse(null);
        if (lesson == null) return "ERROR: Associated lesson not found.";

        Review review = new Review(booking.getMemberId(), reviewText, rating);
        lesson.addReview(review);
        booking.attend();
        return "SUCCESS: Attendance recorded. Thank you for your review!";
    }

    public List<Booking> getBookingsForMember(String memberId) {
        return bookings.values().stream()
                .filter(b -> b.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public Collection<Booking> getAllBookings() {
        return bookings.values();
    }
}
  

