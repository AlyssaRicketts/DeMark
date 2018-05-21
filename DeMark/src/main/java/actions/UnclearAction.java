package actions;

import components.model.ClearRecord;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import main.java.utils.DemarkUtil;
import org.jetbrains.annotations.NotNull;
import components.DemarkProjectComponent;

import java.util.Set;
/**
 * AnAction class that represents the "Unclear" function of the plugin
 * The action is unavailable when no editors are opened.
 *
 * Uses: {@link DemarkUtil}, {@link DemarkProjectComponent}, {@link ClearRecord}
 */
public class UnclearAction extends AnAction {

    private Editor editor;
    private Project project;
    private Document document;

    // Initializes all fields
    private void init(@NotNull AnActionEvent anActionEvent) {
        editor = anActionEvent.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        project = editor.getProject();
        document = editor.getDocument();
    }
    @Override
    public void actionPerformed(AnActionEvent e) {
        init(e);
        if (editor == null || project == null || document == null) {
            return;
        }

        // gets the DeMark component from the project and get's the clear history from it
        DemarkProjectComponent demarkProjectComponent = project.getComponent(DemarkProjectComponent.class);
        ClearRecord prevClearedLines = demarkProjectComponent.popHistory(DemarkUtil.getDocumentName(document));

        // restore all previously cleared lines, if any, and re-bookmarks them
        if (prevClearedLines != null) {
            DemarkUtil.unclearLastClearAll(editor, prevClearedLines);

            Set<Integer> prevClearedLineNums = prevClearedLines.keySet();
            for(Integer lineNum : prevClearedLineNums) {
                DemarkUtil.addDemarkBookmark(editor, lineNum);
            }
        }
    }
}
