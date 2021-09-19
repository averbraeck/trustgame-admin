package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryImage;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.ClientRecord;

public class MaintainClient {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        case "client": {
            data.clearColumns("25%", "Game", "25%", "Client");
            data.clearFormColumn("50%", "Edit Properties");
            SessionUtils.showGames(session, data, 0, "Client", "showClient");
            break;
        }

        case "showClient": {
            SessionUtils.showGames(session, data, recordNr, "Client", "showClient");
            if (recordNr == 0)
                data.resetColumn(1);
            else
                showClients(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "viewClient": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, recordNr);
            editClient(session, data, recordNr, false);
            break;
        }

        case "editClient": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, recordNr);
            editClient(session, data, recordNr, true);
            break;
        }

        case "saveClient": {
            recordNr = saveClient(request, data, recordNr);
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteClient": {
            ClientRecord client = SqlUtils.readClientFromClientId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Client",
                    "<p>Delete client " + client.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteClientOk', " + recordNr + ")", "Cancel", "clickMenu('client')",
                    "clickMenu('client')");
            data.setShowModalWindow(1);
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteClientOk": {
            ClientRecord client = SqlUtils.readClientFromClientId(data, recordNr);
            try {
                client.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('client')");
            }
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newClient": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Client", "showClient");
            showClients(session, data, true, 0);
            editClient(session, data, 0, true);
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    /* ********************************************************************************************************* */
    /* ******************************************* CLIENT ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showClients(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<ClientRecord> clientRecords = dslContext.selectFrom(Tables.CLIENT)
                .where(Tables.CLIENT.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (ClientRecord client : clientRecords) {
            TableRow tableRow = new TableRow(client.getId(), selectedRecordNr, client.getName(), "viewClient");
            if (editButton)
                tableRow.addButton("Edit", "editClient");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Client", "newClient"));

        data.getColumn(1).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editClient(HttpSession session, AdminData data, int clientId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        ClientRecord client = clientId == 0 ? dslContext.newRecord(Tables.CLIENT)
                : dslContext.selectFrom(Tables.CLIENT).where(Tables.CLIENT.ID.eq(clientId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("client")
                .setEditMethod("editClient")
                .setSaveMethod("saveClient")
                .setDeleteMethod("deleteClient", "Delete", "Note: Client can only be deleted when it is not used in a Game")
                .setRecordNr(clientId)
                .startMultipartForm()
                .addEntry(new FormEntryString(Tables.CLIENT.NAME)
                        .setRequired()
                        .setInitialValue(client.getName())
                        .setLabel("Client name")
                        .setMaxChars(45))
                .addEntry(new FormEntryImage(Tables.CLIENT.LOGO)
                        .setRequired()
                        .setInitialValue(client.getLogo())
                        .setLabel("Client logo")
                        .setImageServlet("imageClient")
                        .setImageRecordNr(clientId))
                .addEntry(new FormEntryText(Tables.CLIENT.TYPE)
                        .setRequired()
                        .setInitialValue(client.getType())
                        .setLabel("Client description"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Client", form);
    }

    public static int saveClient(HttpServletRequest request, AdminData data, int clientId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        ClientRecord client = clientId == 0 ? dslContext.newRecord(Tables.CLIENT)
                : dslContext.selectFrom(Tables.CLIENT).where(Tables.CLIENT.ID.eq(clientId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(client, request, data);
        client.setGameId(data.getColumn(0).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('client')");
            return -1;
        } else {
            try {
                client.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('client')");
                return -1;
            }
        }
        return client.getId();
    }

}
