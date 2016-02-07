package timetable.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import timetable.data.Day;
import timetable.data.Group;
import timetable.data.Lesson;
import timetable.data.Subject;

public class SAT {
	private PseudoOptDecorator optimizer;
	private DependencyHelper<Lesson, String> helper;
	private HardConstrainer hardConstrainer;
	private SoftConstrainer softConstrainer;

	public SAT(IPBSolver solver, int timeout,
			HashMap<Subject, Integer> curriculum, int lessonsADay) {
		optimizer = new PseudoOptDecorator(solver);
		optimizer.setTimeout(timeout);
		optimizer.setVerbose(true);
		helper = new DependencyHelper<Lesson, String>(optimizer, true);

		List<Lesson> lessonCombinations = computeAllCombinations(lessonsADay);
		hardConstrainer = new HardConstrainer(curriculum, lessonsADay,
				lessonCombinations, helper);

		softConstrainer = new SoftConstrainer(curriculum, lessonCombinations,
				helper);
	}

	public int variables() {
		return helper.getNumberOfVariables();
	}

	public int constraints() {
		return helper.getNumberOfConstraints();
	}	
	
	public List<Lesson> solve() {
		List<Lesson> result = new ArrayList<Lesson>();
		try {
			hardConstrainer.setConstraints();
			softConstrainer.setConstraints();

			if (helper.hasASolution()) {
				System.out.println("Solution Found :)");

				Collection<Lesson> sol = helper.getASolution();
				for (Iterator<Lesson> it = sol.iterator(); it.hasNext();) {
					result.add(it.next());
				}
			} else {
				System.out.println("Solution Not Found :(");
				Set<String> reason = helper.why();
				for (String string : reason) {
					System.out.println(string);
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

	private List<Lesson> computeAllCombinations(int lessonsADay) {
		List<Lesson> lessonCombinations = new ArrayList<Lesson>();
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

		return lessonCombinations;
	}
}
