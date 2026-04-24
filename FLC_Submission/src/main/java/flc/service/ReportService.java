package flc.service;

import flc.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final TimetableService timetableService;

    public ReportService(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    /**
     * Report 1: Number of attended members and average rating per lesson for a given month.
     */
    public void printMonthlyLessonReport(int month) {
        List<Lesson> monthLessons = timetableService.getByMonth(month);
        if (monthLessons.isEmpty()) {
            System.out.println("No lessons found for month " + month);
            return;
        }

        String monthName = month == 4 ? "April" : month == 5 ? "May" : "Month " + month;
        System.out.println("\n========================================");
        System.out.println("   MONTHLY LESSON REPORT — " + monthName.toUpperCase());
        System.out.println("========================================");
        System.out.printf("%-8s %-4s %-10s %-12s %-12s %-10s %-12s%n",
                "LessonID", "Wk", "Day", "Exercise", "TimeSlot", "Attended", "Avg Rating");
        System.out.println("------------------------------------------------------------------------");

        for (Lesson l : monthLessons) {
            String avgRating = l.getReviews().isEmpty() ? "N/A"
                    : String.format("%.1f / 5", l.getAverageRating());
            System.out.printf("%-8s %-4d %-10s %-12s %-12s %-10d %-12s%n",
                    l.getLessonId(), l.getWeekNumber(), l.getDay(),
                    l.getExerciseType(), l.getTimeSlot().name(),
                    l.getAttendedCount(), avgRating);
        }
        System.out.println("========================================\n");
    }

    /**
     * Report 2: Highest income exercise type for a given month.
     */
    public void printMonthlyChampionReport(int month) {
        List<Lesson> monthLessons = timetableService.getByMonth(month);
        if (monthLessons.isEmpty()) {
            System.out.println("No lessons found for month " + month);
            return;
        }

        Map<ExerciseType, Double> incomeMap = new LinkedHashMap<>();
        for (ExerciseType type : ExerciseType.values()) {
            double totalIncome = monthLessons.stream()
                    .filter(l -> l.getExerciseType() == type)
                    .mapToDouble(Lesson::getTotalIncome)
                    .sum();
            incomeMap.put(type, totalIncome);
        }

        String monthName = month == 4 ? "April" : month == 5 ? "May" : "Month " + month;
        System.out.println("\n========================================");
        System.out.println("  CHAMPION EXERCISE REPORT — " + monthName.toUpperCase());
        System.out.println("========================================");

        incomeMap.entrySet().stream()
                .sorted(Map.Entry.<ExerciseType, Double>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-15s : £%.2f%n", e.getKey(), e.getValue()));

        ExerciseType champion = incomeMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        System.out.println("----------------------------------------");
        System.out.println("  CHAMPION: " + champion + " — £" + String.format("%.2f", incomeMap.get(champion)));
        System.out.println("========================================\n");
    }
}
  

