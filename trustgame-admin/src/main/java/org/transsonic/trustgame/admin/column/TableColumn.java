package org.transsonic.trustgame.admin.column;

public class TableColumn extends AbstractColumn {

    private String content;
    private int selectedRecordNr;
    
    public TableColumn(String width, String defaultHeader) {
        super(width, defaultHeader);
        this.content = "";
        this.selectedRecordNr = 0;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSelectedRecordNr() {
        return selectedRecordNr;
    }

    public void setSelectedRecordNr(int selectedRecordNr) {
        this.selectedRecordNr = selectedRecordNr;
    }

}
