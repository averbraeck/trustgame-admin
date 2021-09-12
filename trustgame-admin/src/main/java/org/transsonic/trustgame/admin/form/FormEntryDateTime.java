package org.transsonic.trustgame.admin.form;

import java.time.LocalDateTime;

import org.jooq.TableField;

public class FormEntryDateTime extends AbstractFormEntry<FormEntryDateTime, LocalDateTime> {

    public FormEntryDateTime(TableField<?, LocalDateTime> tableField) {
        super(tableField);
    }

    @Override
    public String codeForEdit(LocalDateTime value) {
        if (value == null)
            return "";
        return value.toString().replaceFirst("T", ", ");
    }

    @Override
    public LocalDateTime codeForDatabase(String s) {
        if (s != null && s.length() > 10) {
            return LocalDateTime.parse(s.replaceFirst(", ", "T"));
        }
        return null;
    }

    @Override
    protected void validate(String value) {
        super.validate(value);
        if (value != null && value.length() > 10) {
            try {
                LocalDateTime.parse(value.replaceFirst(", ", "T"));
            } catch (Exception exception) {
                addError("Exception: " + exception.getMessage());
            }
        }
    }

    @Override
    public String makeHtml() {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"25%\">");
        s.append(getLabel());
        if (isRequired())
            s.append(" *");
        s.append("      </td>");
        s.append("      <td width=\"75%\">");
        s.append("<input class=\"form-control\" ");
        if (isRequired())
            s.append("required name=\"");
        else
            s.append("name=\"");
        s.append(getTableField().getName());
        s.append("\" value=\"");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        if (isReadOnly())
            s.append("\" readonly />");
        else
            s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");

        if (!isReadOnly()) {
            s.append("    <script>\n");
            s.append("      instance = new dtsel.DTS('input[name=\"");
            s.append(getTableField().getName());
            s.append(
                    "\"]', { direction: 'BOTTOM', dateFormat: \"yyyy-mm-dd\", showTime: true, timeFormat: \"HH:MM\"});");
            s.append("    </script>\n");
        }
        return s.toString();
    }

}
