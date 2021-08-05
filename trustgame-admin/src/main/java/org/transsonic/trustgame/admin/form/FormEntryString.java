package org.transsonic.trustgame.admin.form;

import org.jooq.TableField;

public class FormEntryString extends AbstractFormEntry<FormEntryString, String> {

    int maxChars;
    
    public FormEntryString(TableField<?, String> tableField) {
        super(tableField);
        this.maxChars = 65535;
    }

    public int getMaxChars() {
        return maxChars;
    }

    public FormEntryString setMaxChars(int maxChars) {
        this.maxChars = maxChars;
        return this;
    }

    @Override
    public void validate(String s) {
        super.validate(s);
        if (s.length() > getMaxChars())
            addError("Length is over " + getMaxChars() + " characters");
    }

    @Override
    public String codeForEdit(String value) {
        if (value == null)
            return "";
        return value;
    }

    @Override
    public String codeForDatabase(String s) {
        return s;
    }

    @Override
    public String makeHtml() {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"25%\">");
        s.append(getLabel());
        s.append("      </td>");
        s.append("      <td width=\"75%\">");
        s.append("<input type=\"text\" style=\"width:97%;\" maxlength=\"");
        s.append(getMaxChars());
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getTableField().getName());
        s.append("\" value=\"");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
