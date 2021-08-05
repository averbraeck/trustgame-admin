package org.transsonic.trustgame.admin.form;

import org.jooq.TableField;

public class FormEntryInt extends AbstractFormEntry<FormEntryInt, Integer> {

    int min;
    int max;
    int step;
    String pattern;
    
    public FormEntryInt(TableField<?, Integer> tableField) {
        super(tableField);
        this.min = -Integer.MAX_VALUE;
        this.max = Integer.MAX_VALUE;
        this.step = 1;
        this.pattern = "\\d+";
    }

    public int getMin() {
        return min;
    }

    public FormEntryInt setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMax() {
        return max;
    }

    public FormEntryInt setMax(int max) {
        this.max = max;
        return this;
    }

    public int getStep() {
        return step;
    }

    public FormEntryInt setStep(int step) {
        this.step = step;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public FormEntryInt setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String codeForEdit(Integer value) {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public Integer codeForDatabase(String s) {
        return Integer.valueOf(s);
    }

    @Override
    protected void validate(String value) {
        super.validate(value);
        try {
            int v = Integer.valueOf(value);
            if (v < getMin())
                addError("Value lower than minimum " + getMin());
            if (v > getMax())
                addError("Value larger than maximum " + getMin());
            // TODO: step, pattern
        }
        catch (Exception exception) {
            addError("Exception: " + exception.getMessage());
        }
    }

    @Override
    public String makeHtml() {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"25%\">");
        s.append(getLabel());
        s.append("      </td>");
        s.append("      <td width=\"75%\">");
        s.append("<input type=\"number\" min=\"");
        s.append(getMin());
        s.append("\" max=\"");
        s.append(getMax());
        s.append("\" step=\"");
        s.append(getStep());
        s.append("\" pattern=\"");
        s.append(getPattern());
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
