package timetable.logic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.sat4j.pb.SolverFactory;

import timetable.data.Day;
import timetable.data.Group;
import timetable.data.Lesson;
import timetable.data.Subject;

public class Main {

	private static SAT solver;
	private static HashMap<Subject, Integer> curriculum;
	private static final int LESSONS_A_DAY = 7;

	public static void main(String[] args) {
		curriculum = new HashMap<Subject, Integer>();
		curriculum.put(Subject.Arts, 1);
		curriculum.put(Subject.Biology, 2);
		curriculum.put(Subject.Chemistry, 2);
		curriculum.put(Subject.English, 5);
		curriculum.put(Subject.French, 2);
		curriculum.put(Subject.Geography, 2);
		curriculum.put(Subject.History, 2);
		curriculum.put(Subject.Informatics, 5);
		curriculum.put(Subject.Literature, 3);
		curriculum.put(Subject.Maths, 5);
		curriculum.put(Subject.Music, 1);
		curriculum.put(Subject.Sport, 3);
		curriculum.put(Subject.Physics, 2);

		solver = new SAT(SolverFactory.newCuttingPlanes(), 1000);
		List<Lesson> result = solver.solve(curriculum, LESSONS_A_DAY);
		// System.out.println(solver.variables());
		// System.out.println(solver.constraints());
		// solver.printStats();
		if (!result.isEmpty()) {
			printStudentSchedule(result);
			printTeacherSchedule(result);
		}
	}

	private static void printStudentSchedule(List<Lesson> result) {
		for (Group group : Group.values()) {
			System.out.println("Group " + group);
			for (Day day : Day.values()) {
				System.out.print(day);
			}
			System.out.println();
			System.out
					.println("-------------------------------------------------------------");

			for (int number = 1; number <= LESSONS_A_DAY; number++) {
				for (Day day : Day.values()) {
					final int n = number;
					Optional<Lesson> lesson = result
							.stream()
							.filter(l -> l.getGroup().equals(group)
									&& l.getDay().equals(day)
									&& l.getNumber() == n).findFirst();

					if (lesson.isPresent()) {
						System.out.print(lesson.get().getSubject());
					} else {
						System.out.print(String.format("%-12s", " "));
					}
				}
				System.out.println();
			}

			System.out.println();
		}
	}

	private static void printTeacherSchedule(List<Lesson> result) {
		for (Subject subject : Subject.values()) {
			System.out.println("Subject " + subject);
			for (Day day : Day.values()) {
				System.out.print(day);
			}
			System.out.println();
			System.out
					.println("-------------------------------------------------------------");

			for (int number = 1; number <= LESSONS_A_DAY; number++) {
				for (Day day : Day.values()) {
					final int n = number;
					Optional<Lesson> lesson = result
							.stream()
							.filter(l -> l.getSubject().equals(subject)
									&& l.getDay().equals(day)
									&& l.getNumber() == n).findFirst();
					if (lesson.isPresent()) {
						System.out.print(String.format("%-12s", "Group "
								+ lesson.get().getGroup()));
					} else {
						System.out.print(String.format("%-12s", " "));
					}
				}
				System.out.println();
			}

			System.out.println();
		}
	}

	static Comparator<Lesson> comp = new Comparator<Lesson>() {
		@Override
		public int compare(Lesson o1, Lesson o2) {
			return o1.getSubject().toString()
					.compareTo(o2.getSubject().toString());
		}
	};
}
