package com.github.hackeklotz;
import com.intellij.codeInspection.ProblemHighlightType;

public enum PylintCategory {

	REFACTOR("refactor", ProblemHighlightType.WEAK_WARNING),
	CONVENTION("convention", ProblemHighlightType.WEAK_WARNING),
	WARNING("warning", ProblemHighlightType.GENERIC_ERROR_OR_WARNING),
	ERROR("error", ProblemHighlightType.ERROR),
	FATAL("fatal", ProblemHighlightType.ERROR);

	private final String abbreviation;
	private final ProblemHighlightType highlightType;

	PylintCategory(String abbreviation, ProblemHighlightType highlightType) {
		this.abbreviation = abbreviation;
		this.highlightType = highlightType;
	}

	public static PylintCategory getCategory(String abbreviation) {
		for (PylintCategory pylintCategory : PylintCategory.values()) {
			if (pylintCategory.abbreviation.equals(abbreviation)) {
				return pylintCategory;
			}
		}
		throw new IllegalArgumentException(
				"Pylint category for abbreviation " + abbreviation + " not found.");
	}

	public ProblemHighlightType getHighlightType() {
		return highlightType;
	}

}
