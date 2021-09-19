package org.transsonic.trustgame.admin;

import java.util.ArrayList;
import java.util.List;

public class TableRow {

    private final int id;
    private final int selectedId;
    private final String name;
    private final String viewMethod;
    private List<String> editButtons = new ArrayList<>();
    private List<String> editMethods = new ArrayList<>();
    
    public TableRow(int id, int selectedId, String name, String viewMethod) {
        this.id = id;
        this.selectedId = selectedId;
        this.name = name;
        this.viewMethod = viewMethod;
    }

    public void addButton(String buttonText, String buttonMethod) {
        this.editButtons.add(buttonText);
        this.editMethods.add(buttonMethod);
    }
    
    public String process() {
        StringBuilder s = new StringBuilder();
        if (this.id == this.selectedId)
            s.append("        <div class=\"tg-admin-line-selected\">\n");
        else
            s.append("        <div class=\"tg-admin-line\">\n");
        s.append("            <div class=\"tg-admin-line-field\">");
        s.append("<a href=\"#\" onClick=\"clickRecordId('");
        s.append(viewMethod);
        s.append("',");
        s.append(this.id);
        s.append("); return false;\">");
        s.append(this.name);
        s.append("</a></div>\n"); // tg-admin-line-field
        for (int i=0; i < editButtons.size(); i++) {
            s.append("            <div class=\"tg-admin-line-click\"><a href=\"#\" onClick=\"clickRecordId('");
            s.append(editMethods.get(i));
            s.append("',");
            s.append(this.id);
            s.append("); return false;\">");
            s.append(editButtons.get(i));
            s.append("</a></div>\n");
        }
        s.append("          </div>\n"); // tg-admin-line
        return s.toString();
    }


}
