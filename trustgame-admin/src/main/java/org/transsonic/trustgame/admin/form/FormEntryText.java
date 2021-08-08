package org.transsonic.trustgame.admin.form;

import org.jooq.TableField;

public class FormEntryText extends AbstractFormEntry<FormEntryText, String> {

    int maxChars;
    int rows;
    
    public FormEntryText(TableField<?, String> tableField) {
        super(tableField);
        this.maxChars = 65535;
        this.rows = 15;
    }

    public int getMaxChars() {
        return maxChars;
    }

    public FormEntryText setMaxChars(int maxChars) {
        this.maxChars = maxChars;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public FormEntryText setRows(int rows) {
        this.rows = rows;
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
        String s = value;
        if (s == null)
            s = "";
        s = s.replaceAll("[&]", "&amp;").replaceAll("[<]", "&lt;").replaceAll("[>]", "&gt;");
        return s;
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
        s.append("<textarea rows=\"");
        s.append(getRows());
        s.append("\" style=\"width:97%;\" maxlength=\"");
        s.append(getMaxChars());
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getTableField().getName());
        if (isReadOnly())
            s.append("\" readonly>\n");
        else
            s.append("\">\n");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        s.append("\n</textarea>\n");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }
    
}
