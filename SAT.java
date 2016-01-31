package timetable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.TimeoutException;

public class SAT {
	private PseudoOptDecorator optimizer;
	private DependencyHelper<Lesson, String> helper;

	public SAT(IPBSolver solver, int timeout) {
		optimizer = new PseudoOptDecorator(solver);
		optimizer.setTimeout(timeout);
		optimizer.setVerbose(true);
		helper = new DependencyHelper<Lesson, String>(optimizer, true);
	}

	public int variables() {
		return helper.getNumberOfVariables();
	}

	public int constraints() {
		return helper.getNumberOfConstraints();
	}

	public void printStats() {
		System.out.println(optimizer.toString(""));
		optimizer.printStat(new PrintWriter(System.out, true), "");
	}

	public List<Lesson> solve(HashMap<Subject, Integer> curriculum,
			int lessonsADay) {
		List<Lesson> result = new ArrayList<Lesson>();
		try {
			List<Lesson> lessonCombinations = computeAllCombinations(lessonsADay);
			HardConstrainer hardConstrainer = new HardConstrainer(curriculum,
					lessonsADay, lessonCombinations, helper);
			hardConstrainer.setConstraints();

			SoftConstrainer softConstrainer = new SoftConstrainer(
					lessonCombinations, helper);
			softConstrainer.setConstraints();

			if (helper.hasASolution()) {
				System.out.println("hasASolution");
				IVec<Lesson> sol = helper.getSolution();
				for (Iterator<Lesson> it = sol.iterator(); it.hasNext();) {
					result.add(it.next());
				}
			} else {
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
