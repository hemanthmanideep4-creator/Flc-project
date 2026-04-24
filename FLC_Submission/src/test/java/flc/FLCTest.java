package flc;

import flc.model.*;
import flc.service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class FLCTest {

    private TimetableService timetableService;
    private BookingService bookingService;
    private MemberService memberService;
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        timetableService = new TimetableService();
        bookingService = new BookingService(timetableService);
        memberService = new MemberService();
        reportService = new ReportService(timetableService);
    }

    /**
     * Test 1: Booking a valid lesson succeeds and returns a SUCCESS message.
     */
    @Test
    public void testBookLesson_Success() {
        String result = bookingService.book("M001", "L001");
        assertTrue(result.startsWith("SUCCESS"), "Expected successful booking but got: " + result);
    }

    /**
     * Test 2: Duplicate booking for the same lesson is rejected.
     */
    @Test
    public void testBookLesson_DuplicateRejected() {
        bookingService.book("M001", "L001");
        String result = bookingService.book("M001", "L001");
        assertTrue(result.startsWith("ERROR"), "Expected duplicate booking to be rejected: " + result);
    }

    /**
     * Test 3: Lesson capacity is enforced — max 4 members allowed.
     */
    @Test
    public void testBookLesson_CapacityEnforced() {
        bookingService.book("M001", "L001");
        bookingService.book("M002", "L001");
        bookingService.book("M003", "L001");
        bookingService.book("M004", "L001");
        String result = bookingService.book("M005", "L001");
        assertTrue(result.startsWith("ERROR"), "Expected capacity error but got: " + result);
    }

    /**
     * Test 4: Cancelling an active booking marks it as CANCELLED.
     */
    @Test
    public void testCancelBooking_StatusUpdated() {
        bookingService.book("M001", "L001");
        // Booking ID starts from BK1000
        String result = bookingService.cancelBooking("BK1000");
        assertTrue(result.startsWith("SUCCESS"), "Expected successful cancellation: " + result);

        Booking booking = bookingService.getBookingById("BK1000").orElse(null);
        assertNotNull(booking);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    /**
     * Test 5: Attending a lesson with valid rating stores the review and average rating is correct.
     */
    @Test
    public void testAttendLesson_ReviewAndRatingRecorded() {
        bookingService.book("M001", "L001");
        bookingService.attendLesson("BK1000", "Great class!", 5);

        Lesson lesson = timetableService.getLessonById("L001").orElse(null);
        assertNotNull(lesson);
        assertEquals(1, lesson.getReviews().size());
        assertEquals(5.0, lesson.getAverageRating(), 0.01);
        assertEquals(BookingStatus.ATTENDED,
                bookingService.getBookingById("BK1000").get().getStatus());
    }
}
