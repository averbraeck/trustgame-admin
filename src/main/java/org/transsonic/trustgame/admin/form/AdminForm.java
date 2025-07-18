package org.transsonic.trustgame.admin.form;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.transsonic.trustgame.admin.AdminData;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class AdminForm {

    private StringBuilder s;
    private int recordNr;
    private String cancelMethod = "";
    private int cancelRecordNr = 0;
    private String saveMethod = "";
    private String saveText = "Save";
    private String editMethod = "";
    private String deleteMethod = "";
    private String deleteButton = "Delete";
    private String deleteText = "";
    private List<String> additionalButtons = new ArrayList<>();
    private List<String> additionalMethods = new ArrayList<>();
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
            s.append("); return false;\">");
            s.append(this.saveText);
            s.append("</a></span>");
        }
        if (!this.edit && this.editMethod.length() > 0) {
            s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
            s.append(this.editMethod);
            s.append("', ");
            s.append(this.recordNr);
            s.append("); return false;\">Edit</a></span>");
        }
        if (this.edit && recordNr > 0 && this.deleteMethod.length() > 0) {
            s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
            s.append(this.deleteMethod);
            s.append("', ");
            s.append(this.recordNr);
            s.append("); return false;\">");
            s.append(this.deleteButton);
            s.append("</a></span>");
            if (this.deleteText.length() > 0)
                s.append("<i>&nbsp; &nbsp; " + this.deleteText + "</i>");
        }
        s.append("    </div>\n");
        
        for (int i = 0; i < this.additionalButtons.size(); i++) {
            s.append("<br/>      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
            s.append(this.additionalMethods.get(i));
            s.append("', ");
            s.append(this.recordNr);
            s.append("); return false;\">");
            s.append(this.additionalButtons.get(i));
            s.append("</a></span>\n");
        }
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

    public AdminForm setSaveMethod(String saveMethod, String saveText) {
        this.saveMethod = saveMethod;
        this.saveText = saveText;
        return this;
    }

    public AdminForm setEditMethod(String editMethod) {
        this.editMethod = editMethod;
        return this;
    }

    public AdminForm setDeleteMethod(String deleteeMethod) {
        this.deleteMethod = deleteeMethod;
        this.deleteButton = "Delete";
        this.deleteText = "";
        return this;
    }

    public AdminForm setDeleteMethod(String deleteMethod, String deleteButton) {
        this.deleteMethod = deleteMethod;
        this.deleteButton = deleteButton;
        this.deleteText = "";
        return this;
    }

    public AdminForm setDeleteMethod(String deleteMethod, String deleteButton, String deleteText) {
        this.deleteMethod = deleteMethod;
        this.deleteButton = deleteButton;
        this.deleteText = deleteText;
        return this;
    }

    public AdminForm addAddtionalButton(String method, String buttonText) {
        this.additionalButtons.add(buttonText);
        this.additionalMethods.add(method);
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
                    String reset = request.getParameter(entry.getTableField().getName() + "_reset");
                    boolean delete = reset != null && reset.equals("delete");
                    if (delete) {
                        errors += imageEntry.setRecordValue(record, (byte[]) null);
                    }
                    else if (filePart != null && filePart.getSubmittedFileName() != null
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
