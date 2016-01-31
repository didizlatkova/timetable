package timetable.data;

public enum Subject {
	Maths, English, Physics, Chemistry, Sport, Informatics, Literature, Geography, Music, Biology, French, Arts, History;

	@Override
	public String toString() {
		return String.format("%-12s", this.name());
	}
}