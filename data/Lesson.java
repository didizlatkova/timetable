package timetable.data;

public class Lesson {
	private final Day day;
	private final int number;
	private final Group group;
	private final Subject subject;

	public Lesson(Day day, int number, Group group, Subject subject) {
		this.day = day;
		this.group = group;
		this.number = number;
		this.subject = subject;
	}

	public Day getDay() {
		return day;
	}

	public int getNumber() {
		return number;
	}

	public Group getGroup() {
		return group;
	}

	public Subject getSubject() {
		return subject;
	}

	@Override
	public String toString() {
		return day + " " + number + " " + group + " " + subject;
	}
}