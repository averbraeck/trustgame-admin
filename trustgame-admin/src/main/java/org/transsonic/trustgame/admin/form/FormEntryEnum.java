package org.transsonic.trustgame.admin.form;

import org.jooq.EnumType;
import org.jooq.TableField;

public class FormEntryEnum<T extends EnumType> extends AbstractFormEntry<FormEntryEnum<T>, T> {

    private T[] pickListEntries;
    
    public FormEntryEnum(TableField<?, T> tableField) {
        super(tableField);
    }

    @Override
    public String codeForEdit(T value) {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public T codeForDatabase(String s) {
        for (T entry : this.pickListEntries) {
            if (entry.getLiteral().equals(s))
                return entry;
        }
        return null; 
    }

    public T[] getPickListEntries() {
        return pickListEntries;
    }

    public FormEntryEnum<T> setPickListEntries(T[] pickListEntries) {
        this.pickListEntries = pickListEntries;
        return this;
    }

    @Override
    public String makeHtml() {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"25%\">");
        s.append(getLabel());
        s.append("      </td>");
        s.append("      <td width=\"75%\">\n");
        s.append("        <select ");
        if (isRequired())
            s.append(" required name=\"");
        else
            s.append(" name=\"");
        s.append(getTableField().getName());
        s.append("\">\n");
        for (T entry : getPickListEntries()) {
            s.append("        <option value=\"");
            s.append(entry.getLiteral());
            s.append("\"");
            if (entry.getLiteral().equals(getLastEnteredValue())) {
                s.append(" selected");
            }
            s.append(">");
            s.append(entry.getLiteral());
            s.append("</option>\n");
        }
        s.append("        </select>\n");
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
