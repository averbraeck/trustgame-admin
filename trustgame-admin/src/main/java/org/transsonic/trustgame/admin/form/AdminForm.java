package org.transsonic.trustgame.admin.form;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.jooq.Record;
import org.transsonic.trustgame.admin.AdminData;

public class AdminForm {

    private StringBuilder s;
    private int recordNr;
    private String cancelMethod = "";
    private int cancelRecordNr = 0;
    private String saveMethod = "";
    private String editMethod = "";
    List<AbstractFormEntry<?, ?>> entries = new ArrayList<>();
    private boolean multipart;
    private boolean edit;

    public AdminForm() {
        this.s = new StringBuilder();
    }

    public AdminForm startMultipartForm() {
        this.multipart = true;
        s.append("<div class=\"tg-form\">\n");
        s.append("  <form id=\"editForm\" action=\"/trustgame-admin/admin\" ");
        s.append("method=\"POST\" enctype=\"multipart/form-data\">\n");
        s.append("    <input id=\"editClick\" type=\"hidden\" name=\"editClick\" value=\"tobefilled\" />\n");
        s.append("    <input id=\"editRecordNr\" type=\"hidden\" name=\"editRecordNr\" value=\"0\" />\n");
        buttonRow();
        s.append("    <fieldset");
        if (isEdit())
            s.append(">\n");
        else
            s.append(" disabled=\"disabled\">\n");
        s.append("    <table width=\"100%\">\n");
        return this;
    }

    // Fieldset trick for read only:
    // https://stackoverflow.com/questions/3507958/how-can-i-make-an-entire-html-form-readonly
    public AdminForm startForm() {
        this.multipart = false;
        s.append("<div class=\"tg-form\">\n");
        s.append("  <form id=\"editForm\" action=\"/trustgame-admin/admin\" method=\"POST\" >\n");
        s.append("    <input id=\"editClick\" type=\"hidden\" name=\"editClick\" value=\"tobefilled\" />\n");
        s.append("    <input id=\"editRecordNr\" type=\"hidden\" name=\"editRecordNr\" value=\"0\" />\n");
        buttonRow();
        s.append("    <fieldset");
        if (isEdit())
            s.append(">\n");
        else
            s.append(" disabled=\"disabled\">\n");
        s.append("    <table width=\"100%\">\n");
        return this;
    }

    public AdminForm endForm() {
        s.append("    </table>\n");
        s.append("    </fieldset>\n");
        buttonRow();
        s.append("  </form>\n");
        s.append("</div>\n");
        return this;
    }

    private void buttonRow() {
        s.append("    <div class=\"tg-admin-form-buttons\">\n");
        s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
        s.append(this.cancelMethod);
        s.append("', ");
        if (cancelRecordNr > 0)
            s.append(this.cancelRecordNr);
        else
            s.append(this.recordNr);
        s.append("); return false;\">Cancel</a></span>");
        if (this.edit && this.saveMethod.length() > 0) {
            s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
            s.append(this.saveMethod);
            s.append("', ");
            s.append(this.recordNr);
            s.append("); return false;\">Save</a></span>");
        }
        if (!this.edit && this.editMethod.length() > 0) {
            s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
            s.append(this.editMethod);
            s.append("', ");
            s.append(this.recordNr);
            s.append("); return false;\">Edit</a></span>");
        }
        s.append("    </div>\n");
    }
    
    public AdminForm addEntry(AbstractFormEntry<?, ?> entry) {
        this.entries.add(entry);
        entry.setForm(this);
        s.append(entry.makeHtml());
        return this;
    }

    public AdminForm setCancelMethod(String cancelMethod) {
        this.cancelMethod = cancelMethod;
        return this;
    }

    public AdminForm setCancelMethod(String cancelMethod, int cancelRecordNr) {
        this.cancelMethod = cancelMethod;
        this.cancelRecordNr = cancelRecordNr;
        return this;
    }

    public int getCancelRecordNr() {
        return cancelRecordNr;
    }

    public AdminForm setSaveMethod(String saveMethod) {
        this.saveMethod = saveMethod;
        return this;
    }

    public AdminForm setEditMethod(String editMethod) {
        this.editMethod = editMethod;
        return this;
    }

    public AdminForm setRecordNr(int recordNr) {
        this.recordNr = recordNr;
        return this;
    }

    public boolean isMultipart() {
        return this.multipart;
    }

    public boolean isEdit() {
        return edit;
    }

    public AdminForm setEdit(boolean edit) {
        this.edit = edit;
        return this;
    }

    public String process() {
        return this.s.toString();
    }

    // for multipart: https://stackoverflow.com/questions/2422468/how-to-upload-files-to-server-using-jsp-servlet
    public String setFields(Record record, HttpServletRequest request, AdminData data) {
        String errors = "";
        for (AbstractFormEntry<?, ?> entry : this.entries) {
            if (isMultipart() && entry instanceof FormEntryImage) {
                try {
                    FormEntryImage imageEntry = (FormEntryImage) entry;
                    Part filePart = request.getPart(imageEntry.getTableField().getName());
                    if (filePart != null && filePart.getSubmittedFileName() != null
                            && filePart.getSubmittedFileName().length() > 0) {
                        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                        imageEntry.setFilename(fileName);
                        try (InputStream fileContent = filePart.getInputStream()) {
                            byte[] image = fileContent.readAllBytes();
                            errors += imageEntry.setRecordValue(record, image);
                        }
                    }
                } catch (ServletException | IOException exception) {
                    errors += "<p>Exception: " + exception.getMessage() + "</p>\n";
                }
            } else {
                String value = request.getParameter(entry.getTableField().getName());
                errors += entry.setRecordValue(record, value);
            }
        }
        return errors;
    }

}
