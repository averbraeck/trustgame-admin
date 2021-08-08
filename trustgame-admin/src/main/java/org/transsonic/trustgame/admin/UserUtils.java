package org.transsonic.trustgame.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryBoolean;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UsergroupRecord;

public class UserUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        case "user": {
            data.clearColumns("30%", "UserGroup", "30%", "User");
            data.clearFormColumn("40%", "Edit Properties");
            showUserGroups(session, data, true, 0);
            break;
        }

        case "viewUserGroup": {
            showUserGroups(session, data, true, recordNr);
            showUsers(session, data, recordNr, true, 0);
            editUserGroup(session, data, recordNr, false);
            break;
        }

        case "editUserGroup": {
            showUserGroups(session, data, true, recordNr);
            showUsers(session, data, recordNr, true, 0);
            editUserGroup(session, data, recordNr, true);
            break;
        }

        case "saveUserGroup": {
            recordNr = saveUserGroup(request, data, recordNr);
            showUserGroups(session, data, true, recordNr);
            showUsers(session, data, recordNr, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newUserGroup": {
            showUserGroups(session, data, true, 0);
            data.resetColumn(1);
            editUserGroup(session, data, 0, true);
            break;
        }

        case "showUsers": {
            showUserGroups(session, data, true, recordNr);
            if (recordNr == 0)
                data.resetColumn(1);
            else
                showUsers(session, data, recordNr, true, 0);
            data.resetFormColumn();
            break;
        }

        case "viewUser": {
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, recordNr);
            editUser(session, data, recordNr, false);
            break;
        }

        case "editUser": {
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, recordNr);
            editUser(session, data, recordNr, true);
            break;
        }

        case "newUser": {
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            editUser(session, data, 0, true);
            break;
        }

        case "saveUser": {
            recordNr = saveUser(request, data, recordNr);
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            data.resetFormColumn();
            break;
        }

        case "readUsers":
        case "generateUsers": {
            // TODO: implement readUsers and generateUsers
            // makeUserGroupContent(session, data, true, 0);
            // data.resetColumn(1);
            ModalWindowUtils.popup(data, "Method not yet implemented",
                    "<p>User generation and reading not yet implemented</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    public static void showUserGroups(HttpSession session, AdminData data, boolean editButton,
            int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<UsergroupRecord> userGroupRecords = dslContext.selectFrom(Tables.USERGROUP).fetch();

        s.append(AdminTable.startTable());
        for (UsergroupRecord group : userGroupRecords) {
            TableRow tableRow = new TableRow(group.getId(), selectedRecordNr, group.getGroupname(), "viewUserGroup");
            if (editButton) {
                tableRow.addButton("Edit", "editUserGroup");
                tableRow.addButton("Users", "showUsers");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New UserGroup", "newUserGroup"));

        data.getColumn(0).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    public static void showUsers(HttpSession session, AdminData data, int userGroupId, boolean editButton,
            int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<UserRecord> userRecords = dslContext.selectFrom(Tables.USER)
                .where(Tables.USER.USERGROUP_ID.eq(userGroupId)).fetch();

        s.append(AdminTable.startTable());
        for (UserRecord user : userRecords) {
            TableRow tableRow = new TableRow(user.getId(), selectedRecordNr, user.getUsername(), "viewUser");
            if (editButton)
                tableRow.addButton("Edit", "editUser");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton) {
            s.append(AdminTable.finalButton("New User", "newUser"));
            s.append(AdminTable.finalButton("Read Users from File", "readUsers"));
            s.append(AdminTable.finalButton("Generate User Batch", "generateUsers"));
        }

        data.getColumn(1).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editUserGroup(HttpSession session, AdminData data, int userGroupId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UsergroupRecord userGroup = userGroupId == 0 ? dslContext.newRecord(Tables.USERGROUP)
                : dslContext.selectFrom(Tables.USERGROUP).where(Tables.USERGROUP.ID.eq(userGroupId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showUsers")
                .setEditMethod("editUserGroup")
                .setSaveMethod("saveUserGroup")
                .setRecordNr(userGroupId)
                .startForm()
                .addEntry(new FormEntryString(Tables.USERGROUP.GROUPNAME)
                        .setLabel("Group name")
                        .setRequired()
                        .setInitialValue(userGroup.getGroupname())
                        .setMaxChars(45))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit UserGroup", form);
    }

    public static int saveUserGroup(HttpServletRequest request, AdminData data, int userGroupId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UsergroupRecord userGroup = userGroupId == 0 ? dslContext.newRecord(Tables.USERGROUP)
                : dslContext.selectFrom(Tables.USERGROUP).where(Tables.USERGROUP.ID.eq(userGroupId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(userGroup, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('user')");
            return -1;
        } else {
            try {
                userGroup.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('user')");
                return -1;
            }
        }
        return userGroup.getId();
    }

    public static void editUser(HttpSession session, AdminData data, int userId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserRecord user = userId == 0 ? dslContext.newRecord(Tables.USER)
                : dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showUsers", data.getColumn(0).getSelectedRecordNr())
                .setEditMethod("editUser")
                .setSaveMethod("saveUser")
                .setRecordNr(userId)
                .startForm()
                .addEntry(new FormEntryString(Tables.USER.USERNAME)
                        .setLabel("Login name")
                        .setRequired()
                        .setInitialValue(user.getUsername())
                        .setMaxChars(45))
                .addEntry(new FormEntryString(Tables.USER.PASSWORD)
                        .setLabel("Password")
                        .setRequired(userId == 0)
                        .setInitialValue("")
                        .setMaxChars(255))
                .addEntry(new FormEntryString(Tables.USER.NAME)
                        .setLabel("Name")
                        .setRequired()
                        .setInitialValue(user.getName())
                        .setMaxChars(45))
                .addEntry(new FormEntryString(Tables.USER.EMAIL)
                        .setLabel("Email")
                        .setInitialValue(user.getEmail())
                        .setMaxChars(90))
                .addEntry(new FormEntryBoolean(Tables.USER.ADMINISTRATOR)
                        .setLabel("Administrator")
                        .setInitialValue(user.getAdministrator()))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit User", form);
    }

    public static int saveUser(HttpServletRequest request, AdminData data, int userId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserRecord user = userId == 0 ? dslContext.newRecord(Tables.USER)
                : dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne();
        String hashedPassword = userId == 0 ? "" : user.getPassword();
        String errors = data.getFormColumn().getForm().setFields(user, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors,
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return -1;
        } else {
            user.set(Tables.USER.USERGROUP_ID, data.getColumn(0).getSelectedRecordNr());
            user.set(Tables.USER.CREATETIME, LocalDateTime.now());

            if (user.getPassword().length() > 0) {
                MessageDigest md;
                try {
                    // https://www.baeldung.com/java-md5
                    md = MessageDigest.getInstance("MD5");
                    md.update(user.getPassword().getBytes());
                    byte[] digest = md.digest();
                    hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
                } catch (NoSuchAlgorithmException e1) {
                    throw new RuntimeException(e1);
                }
            }
            user.set(Tables.USER.PASSWORD, hashedPassword); // restore old password if not changed

            try {
                user.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
                return -1;
            }
            return user.getId();
        }
    }

}
