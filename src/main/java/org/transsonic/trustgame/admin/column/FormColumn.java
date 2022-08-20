package org.transsonic.trustgame.admin.column;

import org.transsonic.trustgame.admin.form.AdminForm;

public class FormColumn extends AbstractColumn {

    private AdminForm form;
    private String htmlContents;

    public FormColumn(String width, String defaultHeader) {
        super(width, defaultHeader);
        clearForm();
    }

    public String getContent() {
        if (this.form != null)
            return this.form.process();
        if (this.htmlContents != null && this.htmlContents.length() > 0)
            return this.htmlContents;
        return "";
    }

    public AdminForm getForm() {
        return form;
    }

    public void setForm(AdminForm form) {
        this.form = form;
    }

    public void setHeaderForm(String header, AdminForm form) {
        setHeader(header);
        setForm(form);
    }

    public void clearForm() {
        this.form = null;
    }

    public String getHtmlContents() {
        return htmlContents;
    }

    public void setHtmlContents(String htmlContents) {
        this.htmlContents = htmlContents;
    }

}
