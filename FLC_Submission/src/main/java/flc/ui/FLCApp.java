package flc.ui;

import flc.model.*;
import flc.service.*;

import java.util.*;

public class FLCApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static TimetableService timetableService;
    private static MemberService memberService;
    private static BookingService bookingService;
    private static ReportService reportService;

    public static void main(String[] args) {
        timetableService = new TimetableService();
        memberService = new MemberService();
        bookingService = new BookingService(timetableService);
        reportService = new ReportService(timetableService);

        System.out.println("==========================================");
        System.out.println("  FURZEFIELD LEISURE CENTRE (FLC) SYSTEM");
        System.out.println("==========================================");

        // Load sample data
        DataSeeder.seed(bookingService, timetableService);

        // Select member
        Member currentMember = selectMember();
        if (currentMember == null) {
            System.out.println("Exiting system. Goodbye!");
            return;
        }

        System.out.println("\nWelcome, " + currentMember.getName() + "!");
        mainMenu(currentMember);
    }

    private static Member selectMember() {
        System.out.println("\nRegistered Members:");
        memberService.getAllMembers().forEach(m -> System.out.println("  " + m));
        System.out.print("\nEnter your Member ID to login (or 'exit' to quit): ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (input.equalsIgnoreCase("exit")) return null;
        return memberService.getMemberById(input.toUpperCase()).orElseGet(() -> {
            System.out.println("Member not found.");
            return selectMember();
        });
    }

    private static void mainMenu(Member member) {
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Book a group exercise lesson");
            System.out.println("2. Change / Cancel a booking");
            System.out.println("3. Attend a lesson (write review & rating)");
            System.out.println("4. Monthly lesson report");
            System.out.println("5. Monthly champion exercise report");
            System.out.println("6. View my bookings");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": bookLesson(member); break;
                case "2": changeOrCancelBooking(member); break;
                case "3": attendLesson(member); break;
                case "4": monthlyLessonReport(); break;
                case "5": monthlyChampionReport(); break;
                case "6": viewMyBookings(member); break;
                case "0":
                    System.out.println("Goodbye, " + member.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ─── 1. BOOK A LESSON ─────────────────────────────────────────────────────

    private static void bookLesson(Member member) {
        System.out.println("\n--- BOOK A LESSON ---");
        System.out.println("View timetable by:");
        System.out.println("  1. Day (Saturday / Sunday)");
        System.out.println("  2. Exercise type");
        System.out.print("Select: ");
        String opt = scanner.nextLine().trim();

        List<Lesson> lessons = new ArrayList<>();

        if (opt.equals("1")) {
            System.out.print("Enter day (Saturday/Sunday): ");
            String day = scanner.nextLine().trim();
            if (!day.equalsIgnoreCase("Saturday") && !day.equalsIgnoreCase("Sunday")) {
                System.out.println("Invalid day. Must be Saturday or Sunday.");
                return;
            }
            lessons = timetableService.getByDay(day);
            printLessons(lessons);
        } else if (opt.equals("2")) {
            System.out.println("Exercise types: YOGA, ZUMBA, AQUACISE, BOX_FIT, BODY_BLITZ");
            System.out.print("Enter exercise type: ");
            String typeName = scanner.nextLine().trim().toUpperCase().replace(" ", "_");
            try {
                ExerciseType type = ExerciseType.valueOf(typeName);
                lessons = timetableService.getByExerciseType(type);
                printLessons(lessons);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid exercise type.");
                return;
            }
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (lessons.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }

        System.out.print("\nEnter Lesson ID to book (or 'back' to cancel): ");
        String lessonId = scanner.nextLine().trim().toUpperCase();
        if (lessonId.equalsIgnoreCase("back")) return;

        String result = bookingService.book(member.getMemberId(), lessonId);
        System.out.println(result);
    }

    // ─── 2. CHANGE / CANCEL ───────────────────────────────────────────────────

    private static void changeOrCancelBooking(Member member) {
        System.out.println("\n--- CHANGE / CANCEL BOOKING ---");
        viewMyBookings(member);

        System.out.print("Enter Booking ID (or 'back'): ");
        String bookingId = scanner.nextLine().trim().toUpperCase();
        if (bookingId.equalsIgnoreCase("back")) return;

        Optional<Booking> opt = bookingService.getBookingById(bookingId);
        if (opt.isEmpty()) { System.out.println("Booking not found."); return; }
        Booking booking = opt.get();
        if (!booking.getMemberId().equals(member.getMemberId())) {
            System.out.println("This booking does not belong to you.");
            return;
        }
        if (!booking.isActive()) {
            System.out.println("Booking is not active (status: " + booking.getStatus() + ").");
            return;
        }

        System.out.println("1. Change to a new lesson");
        System.out.println("2. Cancel this booking");
        System.out.print("Select: ");
        String action = scanner.nextLine().trim();

        if (action.equals("1")) {
            System.out.println("\nAvailable lessons:");
            printLessons(timetableService.getAllLessons());
            System.out.print("Enter new Lesson ID: ");
            String newLessonId = scanner.nextLine().trim().toUpperCase();
            System.out.println(bookingService.changeBooking(bookingId, newLessonId));
        } else if (action.equals("2")) {
            System.out.println(bookingService.cancelBooking(bookingId));
        } else {
            System.out.println("Invalid option.");
        }
    }

    // ─── 3. ATTEND A LESSON ───────────────────────────────────────────────────

    private static void attendLesson(Member member) {
        System.out.println("\n--- ATTEND A LESSON ---");
        viewMyBookings(member);

        System.out.print("Enter Booking ID of lesson to attend (or 'back'): ");
        String bookingId = scanner.nextLine().trim().toUpperCase();
        if (bookingId.equalsIgnoreCase("back")) return;

        Optional<Booking> opt = bookingService.getBookingById(bookingId);
        if (opt.isEmpty()) { System.out.println("Booking not found."); return; }
        if (!opt.get().getMemberId().equals(member.getMemberId())) {
            System.out.println("This booking does not belong to you."); return;
        }

        System.out.print("Write your review: ");
        String reviewText = scanner.nextLine().trim();
        if (reviewText.isEmpty()) reviewText = "No comment.";

        int rating = 0;
        while (rating < 1 || rating > 5) {
            System.out.print("Rating (1=Very Dissatisfied, 5=Very Satisfied): ");
            try { rating = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a number 1-5."); }
        }

        System.out.println(bookingService.attendLesson(bookingId, reviewText, rating));
    }

    // ─── 4. MONTHLY LESSON REPORT ─────────────────────────────────────────────

    private static void monthlyLessonReport() {
        int month = promptMonth();
        if (month == -1) return;
        reportService.printMonthlyLessonReport(month);
    }

    // ─── 5. MONTHLY CHAMPION REPORT ───────────────────────────────────────────

    private static void monthlyChampionReport() {
        int month = promptMonth();
        if (month == -1) return;
        reportService.printMonthlyChampionReport(month);
    }

    // ─── 6. VIEW MY BOOKINGS ──────────────────────────────────────────────────

    private static void viewMyBookings(Member member) {
        System.out.println("\n--- MY BOOKINGS ---");
        List<Booking> myBookings = bookingService.getBookingsForMember(member.getMemberId());
        if (myBookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for (Booking b : myBookings) {
            timetableService.getLessonById(b.getLessonId()).ifPresent(l ->
                System.out.printf("  %s | Lesson: %s | %s %s Week%d | Status: %s%n",
                        b.getBookingId(), b.getLessonId(),
                        l.getDay(), l.getTimeSlot().name(), l.getWeekNumber(),
                        b.getStatus())
            );
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private static void printLessons(List<Lesson> lessons) {
        System.out.printf("%n%-8s %-4s %-10s %-12s %-12s %-8s %-8s%n",
                "ID", "Wk", "Day", "Exercise", "Time", "Price", "Spaces");
        System.out.println("------------------------------------------------------------------");
        for (Lesson l : lessons) {
            System.out.printf("%-8s %-4d %-10s %-12s %-12s £%-7.2f %-8d%n",
                    l.getLessonId(), l.getWeekNumber(), l.getDay(),
                    l.getExerciseType(), l.getTimeSlot().name(),
                    l.getExerciseType().getPrice(),
                    4 - l.getBookedCount());
        }
    }

    private static int promptMonth() {
        System.out.print("Enter month number (4=April, 5=May, or 'back'): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("back")) return -1;
        try {
            int m = Integer.parseInt(input);
            if (m == 4 || m == 5) return m;
            System.out.println("Only months 4 and 5 are covered by the timetable.");
            return -1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return -1;
        }
    }
}
