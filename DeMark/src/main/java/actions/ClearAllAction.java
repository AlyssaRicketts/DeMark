package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import main.java.utils.DemarkUtil;
import org.jetbrains.annotations.NotNull;
import components.DemarkProjectComponent;

import java.util.HashMap;


public class ClearAllAction extends AnAction {


    private Editor editor;
    private Project project;
    private Document document;

    // TODO: Check the ones that may be null
    // Initializes all fields
    private void init(@NotNull AnActionEvent anActionEvent) {
        editor = anActionEvent.getData(LangDataKeys.EDITOR);
        project = editor.getProject();
        document = editor.getDocument();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        init(anActionEvent);
        DemarkProjectComponent demarkProjectComponent = project.getComponent(DemarkProjectComponent.class
        );

        HashMap<Integer, String> clearedLines = DemarkUtil.clearAllDemarkBookmarks(editor);
        demarkProjectComponent.pushHistory(clearedLines);
    }
}
