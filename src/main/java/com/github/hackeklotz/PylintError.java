package com.github.hackeklotz;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class PylintError {

    private final PylintCategory type;
    private final int lineNumber;
    private final String message;

    public PylintError(PylintCategory type, int lineNumber, String message) {
        this.message = message;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public ProblemDescriptor createProblemDesriptor(PsiFile file, InspectionManager manager) {
        Document document = FileDocumentManager.getInstance().getDocument(file.getVirtualFile());
        TextRange textRange = getTextRange(document, lineNumber);
        return manager.createProblemDescriptor(file, textRange, "Pylint: " + message,
                type.getHighlightType(), true);
    }

    @NotNull
    private TextRange getTextRange(Document document, int lineNumber) {
        int start = document.getLineStartOffset(lineNumber-1);
        int end = document.getLineEndOffset(lineNumber-1);
        return TextRange.create(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PylintError that = (PylintError) o;

        if (lineNumber != that.lineNumber) return false;
        if (!message.equals(that.message)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + lineNumber;
        return result;
    }

    @Override
    public String toString() {
        return "PylintError{" +
                "type=" + type +
                ", lineNumber=" + lineNumber +
                ", message='" + message + '\'' +
                '}';
    }
}
