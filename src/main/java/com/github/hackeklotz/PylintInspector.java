package com.github.hackeklotz;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PylintInspector extends LocalInspectionTool {

	public static Logger logger = Logger.getInstance(PylintInspector.class);

	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager,
			boolean isOnTheFly) {
		Path path = Paths.get(file.getVirtualFile().getCanonicalPath());
		PylintExecutor executor = new PylintExecutor();
		PropertiesComponent properties = PropertiesComponent.getInstance();
		Path pylintPath = Paths.get(properties.getValue("pylint.path"));
		String commandLineParameters = properties.getValue("pylint.commandLineParameters");
		List<PylintError> errors = executor.execute(path, pylintPath, commandLineParameters);
		List<ProblemDescriptor> problemDescriptors = transformToProblemDescriptions(errors,
				file, manager);
		return problemDescriptors.toArray(new ProblemDescriptor[0]);
	}

	private List<ProblemDescriptor> transformToProblemDescriptions(List<PylintError> pylintErrors,
			@NotNull PsiFile file, @NotNull InspectionManager manager) {
		List<ProblemDescriptor> problemDescriptors = new ArrayList<>();
		for (PylintError pylintError : pylintErrors) {
			ProblemDescriptor problemDescriptor = pylintError.createProblemDesriptor(file, manager);
			problemDescriptors.add(problemDescriptor);
		}
		return problemDescriptors;
	}

}
