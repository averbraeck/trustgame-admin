package org.transsonic.trustgame.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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

        case "deleteUserGroup": {
            UsergroupRecord userGroup = SqlUtils.readUserGroupFromUserGroupId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete UserGroup",
                    "<p>Delete user group " + userGroup.getGroupname() + "?</p>", "DELETE",
                    "clickRecordId('deleteUserGroupOk', " + recordNr + ")", "Cancel", "clickMenu('user')",
                    "clickMenu('user')");
            data.setShowModalWindow(1);
            showUserGroups(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteUserGroupOk": {
            UsergroupRecord userGroup = SqlUtils.readUserGroupFromUserGroupId(data, recordNr);
            try {
                userGroup.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('user')");
            }
            showUserGroups(session, data, true, 0);
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

        case "deleteUser": {
            UserRecord user = SqlUtils.readUserFromUserId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete User", "<p>Delete user " + user.getName() + "?</p>",
                    "DELETE", "clickRecordId('deleteUserOk', " + recordNr + ")", "Cancel", "clickMenu('user')",
                    "clickMenu('user')");
            data.setShowModalWindow(1);
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteUserOk": {
            UserRecord user = SqlUtils.readUserFromUserId(data, recordNr);
            try {
                user.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('user')");
            }
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            data.resetFormColumn();
            break;
        }

        case "generateUserParameters": {
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            generateUserParameters(request, data);
            break;
        }

        case "generateUsers": {
            showUserGroups(session, data, true, data.getColumn(0).getSelectedRecordNr());
            generateUsers(request, data);
            showUsers(session, data, data.getColumn(0).getSelectedRecordNr(), true, 0);
            data.resetFormColumn();
            break;
        }

        case "readUsers": {
            // TODO: implement readUsers
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

    public static void showUserGroups(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
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
            s.append(AdminTable.finalButton("Generate User Batch", "generateUserParameters"));
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
                .setDeleteMethod("deleteUserGroup", "Delete", "Note: UserGroup can only be deleted when it is not used for a User")
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

        if (userId == 0)
            user.setUsercode(makeUniqueUserCode(data));

        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showUsers", data.getColumn(0).getSelectedRecordNr())
                .setEditMethod("editUser")
                .setSaveMethod("saveUser")
                .setDeleteMethod("deleteUser", "Delete", "Note: User can only be deleted when not used in a GamePlay")
                .setRecordNr(userId)
                .startForm()
                .addEntry(new FormEntryString(Tables.USER.USERCODE)
                        .setLabel("Login code")
                        .setRequired()
                        .setReadOnly()
                        .setInitialValue(user.getUsercode())
                        .setMaxChars(5))
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

    /** make unique user code */
    private static String makeUniqueUserCode(AdminData data) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String code = "";
        boolean same = true;
        while (same) {
            Random r = new Random();
            code = "";
            for (int i = 0; i < 5; i++) {
                int p = r.nextInt(chars.length()); // bounds is exclusive
                code += chars.charAt(p);
            }
            UserRecord codeUser = dslContext.selectFrom(Tables.USER).where(Tables.USER.USERCODE.eq(code)).fetchAny();
            same = codeUser != null;
        }
        return code;
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

    private static void generateUserParameters(HttpServletRequest request, AdminData data) {
        StringBuffer s = new StringBuffer();
        s.append("<p>Note: The user numbers will be generated at the places of the %-sign in the username \n"
                + "and password, with the indicated number of digits. If password is blank, users can only \n"
                + "login using the generated code. The username and login name will be the same.</p>\n");

        s.append("<div class=\"tg-form\">\n");
        s.append("  <form id=\"editForm\" action=\"/trustgame-admin/admin\" method=\"POST\" >\n");
        s.append("    <input id=\"editClick\" type=\"hidden\" name=\"editClick\" value=\"tobefilled\" />\n");
        s.append("    <input id=\"editRecordNr\" type=\"hidden\" name=\"editRecordNr\" value=\"0\" />\n");
        s.append(buttonRow(data));
        s.append("    <fieldset>\n");
        s.append("     <table width=\"100%\">\n");

        s.append(makeStringField("Name (with %)", true, "username", ""));
        s.append(makeIntField("Start number", true, "startnumber", 1));
        s.append(makeIntField("Number of users", true, "nrusers", 10));
        s.append(makeIntField("Nr of digits for %", true, "nrdigits", 2));
        s.append(makeStringField("Password (can be empty)", false, "password", ""));

        s.append("     </table>\n");
        s.append("    </fieldset>\n");
        s.append(buttonRow(data));
        s.append("  </form>\n");
        s.append("</div>\n");

        data.getFormColumn().setHtmlContents(s.toString());
        data.getFormColumn().setHeader("Generate User Batch");
    }

    private static String makeStringField(String label, boolean required, String name, String initialValue) {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"40%\">");
        s.append(label);
        s.append("      </td>");
        s.append("      <td width=\"60%\">");
        s.append("<input type=\"text\" style=\"width:97%;\" ");
        if (required)
            s.append("required name=\"");
        else
            s.append("name=\"");
        s.append(name);
        s.append("\" value=\"");
        s.append(initialValue);
        s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

    private static String makeIntField(String label, boolean required, String name, int initialValue) {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"40%\">");
        s.append(label);
        s.append("      </td>");
        s.append("      <td width=\"60%\">");
        s.append("<input type=\"number\" style=\"width:97%;\" ");
        if (required)
            s.append("required name=\"");
        else
            s.append("name=\"");
        s.append(name);
        s.append("\" value=\"");
        s.append(initialValue);
        s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

    private static String buttonRow(AdminData data) {
        StringBuffer s = new StringBuffer();
        s.append("    <div class=\"tg-admin-form-buttons\">\n");

        s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
        s.append("showUsers");
        s.append("', ");
        s.append(data.getColumn(0).getSelectedRecordNr());
        s.append("); return false;\">Cancel</a></span>\n");

        s.append("      <span class=\"tg-admin-form-button\" /><a href=\"#\" onClick=\"submitEditForm('");
        s.append("generateUsers");
        s.append("', ");
        s.append(data.getColumn(0).getSelectedRecordNr());
        s.append("); return false;\">");
        s.append("Generate");
        s.append("</a></span>\n");

        s.append("    </div>\n");
        return s.toString();
    }

    private static void generateUsers(HttpServletRequest request, AdminData data) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        int userGroupNr = data.getColumn(0).getSelectedRecordNr();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String sstartnr = request.getParameter("startnumber");
        String snrusers = request.getParameter("nrusers");
        String snrdigits = request.getParameter("nrdigits");

        // check validity
        if (!username.contains("%")) {
            ModalWindowUtils.popup(data, "Error in username", "<p>No % sign in username</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return;
        }
        if (username.indexOf('%') != username.lastIndexOf('%')) {
            ModalWindowUtils.popup(data, "Error in username", "<p>Multiple % sign in username</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return;
        }
        if (password.length() > 0 && !password.contains("%")) {
            ModalWindowUtils.popup(data, "Error in password", "<p>No % sign in pasword</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return;
        }
        if (password.indexOf('%') != password.lastIndexOf('%')) {
            ModalWindowUtils.popup(data, "Error in password", "<p>Multiple % sign in password</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return;
        }
        try {
            Integer.parseInt(sstartnr);
            Integer.parseInt(snrusers);
            Integer.parseInt(snrdigits);
        } catch (NumberFormatException nfe) {
            ModalWindowUtils.popup(data, "Error in numeric values", "<p>startnr / nrusers / nrdigits wrong</p>",
                    "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
            return;
        }

        int startnumber = Integer.parseInt(sstartnr);
        int nrusers = Integer.parseInt(snrusers);
        int nrdigits = Integer.parseInt(snrdigits);

        // make the users
        for (int i = startnumber; i < startnumber + nrusers; i++) {
            UserRecord user = dslContext.newRecord(Tables.USER);
            user.setUsercode(makeUniqueUserCode(data));
            String nr = "" + i;
            while (nr.length() < nrdigits) {
                nr = "0" + nr;
            }
            String name = username.replaceFirst("\\%", nr);
            String pwd;
            if (password.length() == 0) {
                pwd = "" + new Random().nextInt();
            } else {
                pwd = password.replaceFirst("\\%", nr);
            }

            user.setName(name);
            user.setUsername(name);
            user.setUsergroupId(userGroupNr);
            user.setCreatetime(LocalDateTime.now());
            user.setAdministrator((byte) 0);
            user.setEmail("");

            String hashedPassword = "";
            MessageDigest md;
            try {
                // https://www.baeldung.com/java-md5
                md = MessageDigest.getInstance("MD5");
                md.update(pwd.getBytes());
                byte[] digest = md.digest();
                hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
            } catch (NoSuchAlgorithmException e1) {
                throw new RuntimeException(e1);
            }
            user.setPassword(hashedPassword);

            try {
                user.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing user record", "<p>" + exception.getMessage() + "</p>",
                        "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordNr() + ")");
                return;
            }
        }
    }

}
