package org.transsonic.trustgame.admin;

import javax.sql.DataSource;

import org.transsonic.trustgame.admin.column.FormColumn;
import org.transsonic.trustgame.admin.column.TableColumn;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;

public class AdminData {

    /**
     * the SQL datasource representing the database's connection pool.<br>
     * the datasource is shared among the servlets and stored as a ServletContext attribute.
     */
    private DataSource dataSource;

    /**
     * the name of the admin user logged in to this session. <br>
     * if null, no user is logged in.<br>
     * filled by the UserLoginServlet.<br>
     * used by: server and in servlet.
     */
    private String username;

    /**
     * the id of the admin user logged in to this session.<br>
     * if null, no user is logged in.<br>
     * filled by the UserLoginServlet.<br>
     * used by: server.
     */
    private Integer userId;

    /**
     * the admin User record for the logged in user.<br>
     * this record has the USERNAME to display on the screen.<br>
     * filled by the UserLoginServlet.<br>
     * used by: server and in servlet.<br>
     */
    private UserRecord user;

    private TableColumn[] tableColumns = new TableColumn[4];
    
    private FormColumn formColumn;

    private String contentHtml = "";

    /* ================================= */
    /* FULLY DYNAMIC INFO IN THE SESSION */
    /* ================================= */

    /**
     * which menu has been chosen, to maintain persistence after a POST. <br>
     */
    private int menuChoice = 0;

    /**
     * when 0, do not show popup; when 1: show popup. <br>
     * filled and updated by RoundServlet.
     */
    private int showModalWindow = 0;

    /**
     * client info (dynamic) for popup.
     */
    private String modalWindowHtml = "";

    /* ******************* */
    /* GETTERS AND SETTERS */
    /* ******************* */

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserRecord getUser() {
        return user;
    }

    public void setUser(UserRecord user) {
        this.user = user;
    }

    public int getShowModalWindow() {
        return showModalWindow;
    }

    public void setShowModalWindow(int showModalWindow) {
        this.showModalWindow = showModalWindow;
    }

    public int getMenuChoice() {
        return menuChoice;
    }

    public void setMenuChoice(int menuChoice) {
        this.menuChoice = menuChoice;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getModalWindowHtml() {
        return modalWindowHtml;
    }

    public void setModalWindowHtml(String modalClientWindowHtml) {
        this.modalWindowHtml = modalClientWindowHtml;
    }

    public void clearColumns(String... widthsAndHeaders) {
        for (int i = 0; i < widthsAndHeaders.length / 2; i++) {
            this.tableColumns[i] = new TableColumn(widthsAndHeaders[2 * i], widthsAndHeaders[2 * i + 1]);
        }
    }
    
    public void clearFormColumn(String width, String defaultHeader) {
        this.formColumn = new FormColumn(width, defaultHeader);
    }
    
    public void resetColumn(int nr) {
        this.tableColumns[nr].setHeader(this.tableColumns[nr].getDefaultHeader());
        this.tableColumns[nr].setContent("");
        this.tableColumns[nr].setSelectedRecordNr(0);
    }

    public void resetFormColumn() {
        this.formColumn.setHeader(this.formColumn.getDefaultHeader());
        this.formColumn.setForm(null);
    }
    
    public FormColumn getFormColumn() {
        return this.formColumn;
    }
    
    public TableColumn getColumn(int nr) {
        return this.tableColumns[nr];
    }

}
