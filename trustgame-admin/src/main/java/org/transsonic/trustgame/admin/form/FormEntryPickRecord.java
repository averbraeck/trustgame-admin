package org.transsonic.trustgame.admin.form;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.AdminData;

public class FormEntryPickRecord extends AbstractFormEntry<FormEntryPickRecord, Integer> {

    /** Entries alphabetically sorted on pick name. */
    private SortedMap<String, Integer> records = new TreeMap<>();
    
    public FormEntryPickRecord(TableField<?, Integer> tableField) {
        super(tableField);
    }

    @Override
    public String codeForEdit(Integer value) {
        if (value != null)
            return value.toString();
        return "0";
    }

    @Override
    public Integer codeForDatabase(String s) {
        return Integer.valueOf(s); 
    }

    public FormEntryPickRecord setPickTable(AdminData data, Table<?> table, TableField<?, Integer> id, TableField<?, String> name) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<? extends Record> tableRecords = dslContext.selectFrom(table).fetch();
        for (Record record : tableRecords) {
            this.records.put(record.get(name), record.get(id));
        }
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
        if (isReadOnly())
            s.append("\" style=\"pointer-events: none;\">\n");
        else
            s.append("\">\n");
        for (String name : this.records.keySet()) {
            int id = this.records.get(name);
            s.append("        <option value=\"");
            s.append(id);
            s.append("\"");
            // System.out.println(getLastEnteredValue());
            if (codeForEdit(id).equals(getLastEnteredValue())) {
                s.append(" selected");
            }
            s.append(">");
            s.append(name);
            s.append("</option>\n");
        }
        s.append("        </select>\n");
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
