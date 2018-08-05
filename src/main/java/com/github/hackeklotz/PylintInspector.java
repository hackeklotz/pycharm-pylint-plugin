package com.github.hackeklotz;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.injected.editor.VirtualFileWindowImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class PylintInspector extends LocalInspectionTool {

    private ThreadFactory threadFactory =
            new ThreadFactoryBuilder().setNameFormat("PylintRunner-%d").build();
    private ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);
    private volatile List<PylintError> pylintErrors = new ArrayList<>();

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager,
                                         boolean isOnTheFly) {

        VirtualFile vFile = file.getVirtualFile();

        // Workaround for the fact, that sometimes the virtual file is a strange deprecated VirtualFileWindowImpl
        // Following thread seems correlated: https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000407770-How-to-get-the-absolute-path-of-a-VirtualFile-
        if (file.getVirtualFile() instanceof VirtualFileWindowImpl) {
            vFile = ((VirtualFileWindowImpl) vFile).getDelegate();
        }
        Path path = Paths.get(vFile.getCanonicalPath());
        PropertiesComponent properties = PropertiesComponent.getInstance();
        Path pylintPath = Paths.get(properties.getValue("pylint.path"));
        String commandLineParameters = properties.getValue("pylint.commandLineParameters");

        executorService.submit(new PylintRunner(path, pylintPath, commandLineParameters));

        List<PylintError> errors = pylintErrors;
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

    private class PylintRunner implements Runnable {

        private Path path;
        private Path pylintPath;
        private String commandLineParameters;

        public PylintRunner(Path path, Path pylintPath, String commandLineParameters) {
            this.path = path;
            this.pylintPath = pylintPath;
            this.commandLineParameters = commandLineParameters;
        }

        @Override
        public void run() {
            PylintExecutor executor = new PylintExecutor();
            pylintErrors = executor.execute(path, pylintPath, commandLineParameters);
        }
    }
}