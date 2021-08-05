package org.transsonic.trustgame.admin.column;

import org.transsonic.trustgame.admin.form.AdminForm;

public class FormColumn extends AbstractColumn {

    private AdminForm form;

    public FormColumn(String width, String defaultHeader) {
        super(width, defaultHeader);
        clearForm();
    }

    public String getContent() {
        return this.form == null ? "" : this.form.process();
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
}
