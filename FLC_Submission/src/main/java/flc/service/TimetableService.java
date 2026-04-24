package flc.service;

import flc.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableService {

    private final List<Lesson> lessons = new ArrayList<>();

    public TimetableService() {
        initTimetable();
    }

    private void initTimetable() {
        // 8 weekends: weeks 1-4 in April (month=4), weeks 5-8 in May (month=5)
        // Each weekend: Saturday + Sunday, each day: MORNING, AFTERNOON, EVENING
        // Exercise rotation across weekends

        ExerciseType[][] satSchedule = {
            {ExerciseType.YOGA,       ExerciseType.ZUMBA,      ExerciseType.BOX_FIT},
            {ExerciseType.AQUACISE,   ExerciseType.BODY_BLITZ,  ExerciseType.YOGA},
            {ExerciseType.ZUMBA,      ExerciseType.BOX_FIT,    ExerciseType.AQUACISE},
            {ExerciseType.BODY_BLITZ, ExerciseType.YOGA,        ExerciseType.ZUMBA},
            {ExerciseType.BOX_FIT,    ExerciseType.AQUACISE,   ExerciseType.BODY_BLITZ},
            {ExerciseType.YOGA,       ExerciseType.ZUMBA,      ExerciseType.BOX_FIT},
            {ExerciseType.AQUACISE,   ExerciseType.BODY_BLITZ,  ExerciseType.YOGA},
            {ExerciseType.ZUMBA,      ExerciseType.BOX_FIT,    ExerciseType.AQUACISE},
        };

        ExerciseType[][] sunSchedule = {
            {ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE,   ExerciseType.YOGA},
            {ExerciseType.BOX_FIT,    ExerciseType.YOGA,        ExerciseType.ZUMBA},
            {ExerciseType.BODY_BLITZ, ExerciseType.ZUMBA,      ExerciseType.BOX_FIT},
            {ExerciseType.AQUACISE,   ExerciseType.BOX_FIT,    ExerciseType.BODY_BLITZ},
            {ExerciseType.YOGA,       ExerciseType.BODY_BLITZ,  ExerciseType.AQUACISE},
            {ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE,   ExerciseType.YOGA},
            {ExerciseType.BOX_FIT,    ExerciseType.YOGA,        ExerciseType.ZUMBA},
            {ExerciseType.BODY_BLITZ, ExerciseType.ZUMBA,      ExerciseType.BOX_FIT},
        };

        TimeSlot[] slots = {TimeSlot.MORNING, TimeSlot.AFTERNOON, TimeSlot.EVENING};
        int lessonNum = 1;

        for (int w = 1; w <= 8; w++) {
            int month = (w <= 4) ? 4 : 5;

            // Saturday
            for (int s = 0; s < 3; s++) {
                String id = "L" + String.format("%03d", lessonNum++);
                lessons.add(new Lesson(id, satSchedule[w-1][s], "Saturday", slots[s], w, month));
            }
            // Sunday
            for (int s = 0; s < 3; s++) {
                String id = "L" + String.format("%03d", lessonNum++);
                lessons.add(new Lesson(id, sunSchedule[w-1][s], "Sunday", slots[s], w, month));
            }
        }
    }

    public List<Lesson> getAllLessons() { return lessons; }

    public Optional<Lesson> getLessonById(String id) {
        return lessons.stream().filter(l -> l.getLessonId().equals(id)).findFirst();
    }

    public List<Lesson> getByDay(String day) {
        return lessons.stream()
                .filter(l -> l.getDay().equalsIgnoreCase(day))
                .collect(Collectors.toList());
    }

    public List<Lesson> getByExerciseType(ExerciseType type) {
        return lessons.stream()
                .filter(l -> l.getExerciseType() == type)
                .collect(Collectors.toList());
    }

    public List<Lesson> getByMonth(int month) {
        return lessons.stream()
                .filter(l -> l.getMonth() == month)
                .collect(Collectors.toList());
    }
}
  

