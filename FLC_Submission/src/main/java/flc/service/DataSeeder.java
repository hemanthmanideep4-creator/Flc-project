package flc.service;

/**
 * Seeds the system with sample bookings and 20+ reviews for demo/report purposes.
 */
public class DataSeeder {

    public static void seed(BookingService bookingService, TimetableService timetableService) {
        String[][] bookData = {
            {"M001", "L001"}, {"M001", "L004"},
            {"M002", "L001"}, {"M002", "L007"},
            {"M003", "L002"}, {"M003", "L010"},
            {"M004", "L003"}, {"M004", "L013"},
            {"M005", "L004"}, {"M005", "L016"},
            {"M006", "L005"}, {"M006", "L019"},
            {"M007", "L006"}, {"M007", "L022"},
            {"M008", "L007"}, {"M008", "L025"},
            {"M009", "L008"}, {"M009", "L028"},
            {"M010", "L009"}, {"M010", "L031"},
            {"M001", "L010"}, {"M002", "L013"},
        };

        for (String[] b : bookData) {
            bookingService.book(b[0], b[1]);
        }

        String[] reviewTexts = {
            "Fantastic Yoga session, very relaxing!",
            "Great start to the weekend.",
            "Yoga was a bit slow for me but ok.",
            "Loved the Zumba energy!",
            "Aquacise was refreshing and fun.",
            "Box Fit was intense, loved it!",
            "Body Blitz is tough but worth it.",
            "Zumba instructor was brilliant.",
            "Box Fit pushed my limits.",
            "Aquacise not as intense as expected.",
            "Body Blitz helped me sweat it out!",
            "Good Yoga class overall.",
            "Zumba was super fun and energetic.",
            "Aquacise is a great low-impact workout.",
            "Box Fit left me sore in a good way.",
            "Body Blitz class was well-structured.",
            "Yoga session helped my flexibility.",
            "Zumba steps were hard to follow.",
            "Aquacise was enjoyable and relaxing.",
            "Box Fit instructor was very motivating.",
            "Yoga morning class is the best way to start.",
            "Aquacise is underrated, highly recommend."
        };

        int[] ratings = {5, 4, 3, 5, 4, 5, 4, 5, 4, 3, 5, 4, 5, 4, 5, 4, 5, 2, 4, 5, 5, 4};

        for (int i = 0; i < reviewTexts.length; i++) {
            String bookingId = "BK" + (1000 + i);
            bookingService.attendLesson(bookingId, reviewTexts[i], ratings[i]);
        }

        System.out.println("[Seeder] Sample data loaded: " + bookData.length + " bookings, " + reviewTexts.length + " reviews.");
    }
}
