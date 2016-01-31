package timetable.logic;

import java.util.HashMap;
import java.util.List;

import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;

import timetable.data.Day;
import timetable.data.Group;
import timetable.data.Lesson;
import timetable.data.Subject;

public class SoftConstrainer {

	private HashMap<Subject, Integer> curriculum;
	private List<Lesson> lessonCombinations;
	private DependencyHelper<Lesson, String> helper;
	private int lessonsADay;

	public SoftConstrainer(HashMap<Subject, Integer> curriculum,
			List<Lesson> lessonCombinations, int lessonsADay,
			DependencyHelper<Lesson, String> helper) {
		this.curriculum = curriculum;
		this.lessonCombinations = lessonCombinations;
		this.helper = helper;
		this.lessonsADay = lessonsADay;
	}

	public void setConstraints() throws ContradictionException {
		applyHistoryTeacherConstraint();
		applyArtsTeacherConstraint();
		applyChemistryTeacherConstraint();
		applyBiologyTeacherConstraint();
		applyGeographyTeacherConstraint();
		applyFrenchTeacherConstraint();
		applyPhysicsTeacherConstraint();
	}

	private void applyHistoryTeacherConstraint() throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getNumber() > 3
						&& l.getSubject().equals(Subject.History))
				.toArray(Lesson[]::new);

		for (Lesson lesson : lessons) {
			helper.setFalse(lesson, "History teacher has only morning classes.");
		}
	}

	private void applyArtsTeacherConstraint() throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(Day.Fri)
						&& l.getSubject().equals(Subject.Arts))
				.toArray(Lesson[]::new);

		helper.atLeast("Arts teacher has all classes on Friday.",
				Group.values().length * curriculum.get(Subject.Arts), lessons);
	}

	private void applyChemistryTeacherConstraint()
			throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getNumber() < 3
						&& l.getSubject().equals(Subject.Chemistry))
				.toArray(Lesson[]::new);

		for (Lesson lesson : lessons) {
			helper.setFalse(lesson,
					"Chemistry teacher doesn't have morning classes.");
		}
	}

	private void applyBiologyTeacherConstraint() throws ContradictionException {
		for (Day day : Day.values()) {
			Lesson[] lessons = lessonCombinations
					.stream()
					.filter(l -> l.getDay().equals(day)
							&& l.getSubject().equals(Subject.Biology))
					.toArray(Lesson[]::new);

			helper.atMost(
					"Biology teacher doesn't have more than 3 classes a day.",
					3, lessons);
		}
	}

	private void applyGeographyTeacherConstraint()
			throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> (l.getDay().equals(Day.Mon) || l.getDay().equals(
						Day.Wed))
						&& l.getSubject().equals(Subject.Geography))
				.toArray(Lesson[]::new);

		helper.atLeast(
				"Geography teacher has all classes on Monday and Wednesday.",
				Group.values().length * curriculum.get(Subject.Geography),
				lessons);
	}

	private void applyFrenchTeacherConstraint() throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(Day.Fri)
						&& l.getSubject().equals(Subject.French))
				.toArray(Lesson[]::new);

		for (Lesson lesson : lessons) {
			helper.setFalse(lesson,
					"Frech teacher doesn't have classes on Friday.");
		}
	}

	private void applyPhysicsTeacherConstraint() throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> (l.getNumber() < 4 || l.getDay().equals(Day.Mon))
						&& l.getSubject().equals(Subject.Physics))
				.toArray(Lesson[]::new);

		for (Lesson lesson : lessons) {
			helper.setFalse(
					lesson,
					"Physics teacher doesn't have morning classes and doesn't have classes on Monday.");
		}
	}
}
