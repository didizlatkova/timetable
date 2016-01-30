package timetable;

import java.util.HashMap;
import java.util.List;

import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;

public class HardConstrainer {

	private HashMap<Subject, Integer> curriculum;
	private int lessonsADay;
	private List<Lesson> lessonCombinations;
	private DependencyHelper<Lesson, String> helper;

	public HardConstrainer(HashMap<Subject, Integer> curriculum,
			int lessonsADay, List<Lesson> lessonCombinations,
			DependencyHelper<Lesson, String> helper) {
		this.curriculum = curriculum;
		this.lessonsADay = lessonsADay;
		this.lessonCombinations = lessonCombinations;
		this.helper = helper;
	}

	public void setConstraints() throws ContradictionException {
		for (Day day : Day.values()) {
			for (int number = 1; number <= lessonsADay; number++) {
				for (Group group : Group.values()) {
					applyOneLessonConstraint(day, number, group);
				}
			}
		}

		for (Day day : Day.values()) {
			for (int number = 1; number <= lessonsADay; number++) {
				for (Subject subject : Subject.values()) {
					applyOneGroupConstraint(day, number, subject);
				}
			}
		}

		for (Group group : Group.values()) {
			for (Subject subject : Subject.values()) {
				applyCurriculumConstraints(group, subject, curriculum);
			}
		}

		for (Day day : Day.values()) {
			for (Group group : Group.values()) {
				for (Subject subject : Subject.values()) {
					applyOneSubjectTypeADay(day, group, subject);
				}
			}
		}
	}

	private void applyOneLessonConstraint(Day day, int number, Group group)
			throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(day) && l.getNumber() == number
						&& l.getGroup().equals(group)).toArray(Lesson[]::new);

		helper.atMost("One group listens to maximum 1 lesson at a given time.",
				1, lessons);
	}

	private void applyOneGroupConstraint(Day day, int number, Subject subject)
			throws ContradictionException {
		Lesson[] groups = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(day) && l.getNumber() == number
						&& l.getSubject().equals(subject))
				.toArray(Lesson[]::new);

		helper.atMost(
				"One teacher teaches to maximum 1 group at a given time.", 1,
				groups);
	}

	private void applyCurriculumConstraints(Group group, Subject subject,
			HashMap<Subject, Integer> curriculum) throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getSubject().equals(subject)
						&& l.getGroup().equals(group)).toArray(Lesson[]::new);

		helper.atMost(
				"Subject should be taught to each group as many times as it is in curriculum.",
				curriculum.get(subject), lessons);
		helper.atLeast(
				"Subject should be taught to each group as many times as it is in curriculum.",
				curriculum.get(subject), lessons);
	}

	private void applyOneSubjectTypeADay(Day day, Group group, Subject subject)
			throws ContradictionException {
		Lesson[] days = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(day)
						&& l.getGroup().equals(group)
						&& l.getSubject().equals(subject))
				.toArray(Lesson[]::new);

		helper.atMost(
				"Group listens to maximum 1 lesson of a given subject a day.",
				1, days);
	}
}
