package timetable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.TimeoutException;

public class SAT {
	private OptToPBSATAdapter optimizer;
	private DependencyHelper<Lesson, String> helper;
	private static List<Lesson> lessonCombinations;

	public SAT(IPBSolver solver, int timeout) {
		optimizer = new OptToPBSATAdapter(new PseudoOptDecorator(solver));
		optimizer.setTimeout(timeout);
		optimizer.setVerbose(true);
		helper = new DependencyHelper<Lesson, String>(optimizer, false);
	}
	
	public int variables(){
		return helper.getNumberOfVariables();
	}
	
	public int constraints(){
		return helper.getNumberOfConstraints();
	}


	public long bestValue() {
		return -helper.getSolutionCost().longValue();
	}

	public boolean isOptimal() {
		return optimizer.isOptimal();
	}

	public void printStats() {
		System.out.println(optimizer.toString(""));
		optimizer.printStat(new PrintWriter(System.out, true), "");
	}

	public List<Lesson> solve(HashMap<Subject, Integer> curriculum, int lessonsADay) {
		List<Lesson> result = new ArrayList<Lesson>();
		try {
			computeAllCombinations(lessonsADay);
			setHardConstraints(curriculum, lessonsADay);
			if (helper.hasASolution()) {
				System.out.println("hasASolution");
				IVec<Lesson> sol = helper.getSolution();
				for (Iterator<Lesson> it = sol.iterator(); it.hasNext();) {
					result.add(it.next());
				}
			}
		} catch (TimeoutException e) {
			System.out.println("TimeoutException");
			return Collections.emptyList();
		} catch (ContradictionException e) {
			System.out.println("ContradictionException");
			return Collections.emptyList();
		}

		return result;
	}

	private void computeAllCombinations(int lessonsADay) {
		lessonCombinations = new ArrayList<Lesson>();
		for (Day day : Day.values()) {
			for (int number = 1; number <= lessonsADay; number++) {
				for (Group group : Group.values()) {
					for (Subject subject : Subject.values()) {
						lessonCombinations.add(new Lesson(day, number, group,
								subject));
					}
				}
			}
		}
	}

	private void setHardConstraints(HashMap<Subject, Integer> curriculum, int lessonsADay)
			throws ContradictionException {
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

		helper.atMost(1, lessons);
	}

	private void applyOneGroupConstraint(Day day, int number, Subject subject)
			throws ContradictionException {
		Lesson[] groups = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(day) && l.getNumber() == number
						&& l.getSubject().equals(subject))
				.toArray(Lesson[]::new);

		helper.atMost(1, groups);
	}

	private void applyCurriculumConstraints(Group group, Subject subject,
			HashMap<Subject, Integer> curriculum) throws ContradictionException {
		Lesson[] lessons = lessonCombinations
				.stream()
				.filter(l -> l.getSubject().equals(subject)
						&& l.getGroup().equals(group)).toArray(Lesson[]::new);

		helper.atMost(curriculum.get(subject), lessons);
		helper.atLeast("Curriculum", curriculum.get(subject), lessons);
	}

	private void applyOneSubjectTypeADay(Day day, Group group, Subject subject)
			throws ContradictionException {
		Lesson[] days = lessonCombinations
				.stream()
				.filter(l -> l.getDay().equals(day)
						&& l.getGroup().equals(group)
						&& l.getSubject().equals(subject))
				.toArray(Lesson[]::new);

		helper.atMost(1, days);
	}
}
