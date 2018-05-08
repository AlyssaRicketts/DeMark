package main.java.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.DocumentUtil;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;

public class SelectionUtil {

    private static final String COMMENT_MARKER = "//";

    /**
     * Given a range of selected text, returns the line start positions of each line in the range as a list of integers.
     *
     * @param editor The nonnull editor in which the text lives.
     *
     * @return An ArrayList of integers, where each integer represents the start positions of every line in the range.
     */
    public static ArrayList<Integer> getSelectionStarts(@Nonnull Editor editor) {
        Document document = editor.getDocument();

        // Character position of selected model
        int selectPosStart = editor.getSelectionModel().getSelectionStart();
        int selectPosEnd = editor.getSelectionModel().getSelectionEnd();


        ArrayList<Integer> lineStarts = new ArrayList<>();

        // User didn't select anything and is in cursor mode
        if (selectPosStart == selectPosEnd) {
            lineStarts.add(selectPosStart);
            return lineStarts;
        }

        int currPos = selectPosStart;

        // Add all line starts positions to the array
        while (currPos < selectPosEnd) {
            lineStarts.add(currPos);
            int currLine = document.getLineNumber(currPos);
            TextRange textRange = DocumentUtil.getLineTextRange(document, currLine);
            currPos = textRange.getEndOffset() + 1;
        }
        return lineStarts;
    }

    /**
     * Checks if the line at a line number is commented.
     * @param lineNum The line number of the line.
     *
     * @return True if the line is commented, false otherwise.
     */
    public static boolean isCommented(@Nonnull Editor editor, @Nonnegative int lineNum) {
        Document document = editor.getDocument();

        // Gets the line range from a document using the line number.
        TextRange textRange = DocumentUtil.getLineTextRange(document, lineNum);

        // Gets the string using the text range and then check if the line starts a comment.
        String lineBody = document.getText(textRange);
        return lineBody.startsWith(COMMENT_MARKER);
    }

    /**
     * Adds a comment to the line at a passed line number.
     *
     * @param editor , The editor to add the comment to.
     * @param lineNum The line number to add a comment to.
     */
    public static void addComment(@Nonnull Editor editor, @Nonnegative int lineNum) {
        Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null) {
            return;
        }
        /*
         If the line is not commented already, go ahead and add a comment to the line at the start position of the line.
         */
        if (!isCommented(editor, lineNum)) {
            int startPos = document.getLineStartOffset(lineNum);
            Runnable addComment = () -> document.insertString(startPos, COMMENT_MARKER);
            WriteCommandAction.runWriteCommandAction(project, addComment);
        }
    }

    /**
     * Remove a comment from a line given a line number.
     *
     * @param editor, the editor to remove the comment from.
     * @param lineNum The line number to remove the comment marker from.
     */
    public static void removeComment(@Nonnull Editor editor, @Nonnegative int lineNum) {
        Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null) {
            return;
        }

        // Only remove if the line is commented.
        if (isCommented(editor, lineNum)) {
            // Get the start of the line and then add the length of a comment marker.
            int startPos = document.getLineStartOffset(lineNum);
            int endPos = startPos + COMMENT_MARKER.length();
            // Delete the string given the range.
            Runnable removeComment = () -> document.deleteString(startPos, endPos);
            WriteCommandAction.runWriteCommandAction(project, removeComment);
        }
    }

    /**
     * Adds a line of text at a line number.
     *
     * @param editor, the editor to add the line to.
     * @param lineNum The line number to add the text at.
     * @param text The nonnull line of text to add to the document.
     */
    public static void addLine(@Nonnull Editor editor, @Nonnegative int lineNum, @Nonnull String text) {
        Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null) {
            return;
        }

        // Get the start position using the line number and add the line of text.
        int startPos = document.getLineStartOffset(lineNum);
        Runnable addLine = () -> document.insertString(startPos, text + "\n");
        WriteCommandAction.runWriteCommandAction(project, addLine);
    }

    /**
     * Removes a line of text from a document using the line number.
     *
     * @param editor, the editor to remove the line from.
     * @param lineNum The line number to delete.
     *
     * @return the String that is deleted from the document, empty string if line could not be removed.
     */
    public static String removeLine(@Nonnull Editor editor, @Nonnegative int lineNum) {
        Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null) {
            return "";
        }

        // Convert lines to character positions
        TextRange textRange = DocumentUtil.getLineTextRange(document, lineNum);
        String text = document.getText(textRange);

        // Remove the line content, make sure to check whether the offset will return over the length of the document.
        if (textRange.getEndOffset() == document.getTextLength()) {
            Runnable removeText = () -> document.deleteString(textRange.getStartOffset(), textRange.getEndOffset());
            WriteCommandAction.runWriteCommandAction(project, removeText);
        } else {
            Runnable removeText = () -> document.deleteString(textRange.getStartOffset(), textRange.getEndOffset() + 1);
            WriteCommandAction.runWriteCommandAction(project, removeText);
        }

        return text;
    }
}
