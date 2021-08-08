package org.transsonic.trustgame.admin.form;

import org.jooq.TableField;

public class FormEntryDouble extends AbstractFormEntry<FormEntryDouble, Double> {

    double min;
    double max;
    Double step;
    
    public FormEntryDouble(TableField<?, Double> tableField) {
        super(tableField);
        this.min = -Double.MAX_VALUE;
        this.max = Double.MAX_VALUE;
        this.step = Double.NaN;
    }

    public double getMin() {
        return min;
    }

    public FormEntryDouble setMin(double min) {
        this.min = min;
        return this;
    }

    public double getMax() {
        return max;
    }

    public FormEntryDouble setMax(double max) {
        this.max = max;
        return this;
    }

    public double getStep() {
        return step;
    }

    public FormEntryDouble setStep(double step) {
        this.step = step;
        return this;
    }

    @Override
    public String codeForEdit(Double value) {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public Double codeForDatabase(String s) {
        return Double.valueOf(s);
    }

    @Override
    protected void validate(String value) {
        super.validate(value);
        try {
            double v = Double.valueOf(value);
            if (v < getMin())
                addError("Value lower than minimum " + getMin());
            if (v > getMax())
                addError("Value larger than maximum " + getMin());
            // TODO: step
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
        if (!Double.isNaN(getStep())) {
            s.append("\" step=\"");
            s.append(getStep());
        }
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getTableField().getName());
        s.append("\" value=\"");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        if (isReadOnly())
            s.append("\" readonly />");
        else
            s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
