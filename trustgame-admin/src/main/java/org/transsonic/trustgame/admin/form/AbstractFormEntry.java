package org.transsonic.trustgame.admin.form;

import org.jooq.Record;
import org.jooq.TableField;

public abstract class AbstractFormEntry<F extends AbstractFormEntry<F, T>, T> {

    private AdminForm form;
    private final TableField<?, T> tableField;
    private String label;
    private String type;
    private boolean required;
    private boolean readOnly;
    protected String errors; // cumulative error register
    private T initialValue; // to be able to reset the form
    private String lastEnteredValue; // to restore the form after error

    public AbstractFormEntry(TableField<?, T> tableField) {
        this.tableField = tableField;
        this.label = this.tableField.getName();
        this.type = this.tableField.getType().getName().toUpperCase();
        this.required = false;
        this.readOnly = false;
        this.errors = "";
    }

    public AdminForm getForm() {
        return form;
    }

    public void setForm(AdminForm form) {
        this.form = form;
    }

    public String getLabel() {
        return label;
    }

    @SuppressWarnings("unchecked")
    public F setLabel(String label) {
        this.label = label;
        return (F) this;
    }

    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public F setType(String type) {
        this.type = type;
        return (F) this;
    }

    public boolean isRequired() {
        return required;
    }

    @SuppressWarnings("unchecked")
    public F setRequired(boolean required) {
        this.required = required;
        return (F) this;
    }

    public F setRequired() {
        return setRequired(true);
    }

    public TableField<?, ?> getTableField() {
        return tableField;
    }

    public T getInitialValue() {
        return initialValue;
    }

    @SuppressWarnings("unchecked")
    public F setInitialValue(T initialValue) {
        this.initialValue = initialValue;
        setLastEnteredValue(codeForEdit(initialValue));
        return (F) this;
    }

    public String getLastEnteredValue() {
        return lastEnteredValue;
    }

    public void setLastEnteredValue(String lastEnteredValue) {
        this.lastEnteredValue = lastEnteredValue;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @SuppressWarnings("unchecked")
    public F setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return (F) this;
    }

    @SuppressWarnings("unchecked")
    public F setReadOnly() {
        this.readOnly = true;
        return (F) this;
    }

    public String getErrors() {
        return this.errors;
    }

    public abstract String codeForEdit(T value);

    public abstract T codeForDatabase(String s);

    protected void addError(String error)
    {
        this.errors += "<p>Field: '" + getLabel() + "' " + error + "</p>\n";
    }
    
    protected void validate(String value) {
        this.lastEnteredValue = value;
        this.errors = "";
        if (value.length() == 0 && (isRequired() || !tableField.getDataType().nullable()))
            addError("should not be empty");
    }

    public String setRecordValue(Record record, String value) {
        validate(value);
        if (this.errors.length() == 0) {
            try {
                record.set((TableField<?, T>) tableField, codeForDatabase(value));
            } catch (Exception exception) {
                addError("Exception: " + exception.getMessage());
            }
        }
        return this.errors;
    }
    
    public abstract String makeHtml();
    
}
