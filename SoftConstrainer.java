package timetable;

import java.util.List;

import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;

public class SoftConstrainer {
	private List<Lesson> lessonCombinations;
	private DependencyHelper<Lesson, String> helper;

	public SoftConstrainer(List<Lesson> lessonCombinations,
			DependencyHelper<Lesson, String> helper) {
		this.lessonCombinations = lessonCombinations;
		this.helper = helper;
	}

	public void setConstraints() throws ContradictionException {
		applyHistoryTeacherConstraint();
		applyArtsTeacherConstraint();
		applyChemistryTeacherConstraint();
		applyBiologyTeacherConstraint();
		// applyPhysicsTeacherConstraint();
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
				Group.values().length, lessons);
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
}
