package timetable.data;

public enum Day {
	Mon, Tue, Wed, Thu, Fri;

	@Override
	public String toString() {
		return String.format("%-12s", this.name());
	}
}