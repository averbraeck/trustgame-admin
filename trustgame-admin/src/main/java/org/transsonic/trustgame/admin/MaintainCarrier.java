package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryEnum;
import org.transsonic.trustgame.admin.form.FormEntryImage;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.enums.CarrierService;
import org.transsonic.trustgame.data.trustgame.enums.CarrierSustainability;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;

public class MaintainCarrier {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        case "carrier": {
            data.clearColumns("25%", "Game", "25%", "Carrier");
            data.clearFormColumn("50%", "Edit Properties");
            SessionUtils.showGames(session, data, 0, "Carrier", "showCarrier");
            break;
        }

        case "showCarrier": {
            SessionUtils.showGames(session, data, recordNr, "Carrier", "showCarrier");
            if (recordNr == 0)
                data.resetColumn(1);
            else
                showCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "editCarrier": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, recordNr);
            editCarrier(session, data, recordNr, true);
            break;
        }

        case "viewCarrier": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, recordNr);
            editCarrier(session, data, recordNr, false);
            break;
        }

        case "saveCarrier": {
            recordNr = saveCarrier(request, data, recordNr);
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteCarrier": {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Carrier",
                    "<p>Delete carrier " + carrier.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteCarrierOk', " + recordNr + ")", "Cancel", "clickMenu('carrier')",
                    "clickMenu('carrier')");
            data.setShowModalWindow(1);
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteCarrierOk": {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, recordNr);
            try {
                carrier.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('carrier')");
            }
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newCarrier": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Carrier", "showCarrier");
            showCarriers(session, data, true, 0);
            editCarrier(session, data, 0, true);
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

    public static void showCarriers(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<CarrierRecord> carrierRecords = dslContext.selectFrom(Tables.CARRIER)
                .where(Tables.CARRIER.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (CarrierRecord carrier : carrierRecords) {
            TableRow tableRow = new TableRow(carrier.getId(), selectedRecordNr, carrier.getName(), "viewCarrier");
            if (editButton)
                tableRow.addButton("Edit", "editCarrier");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Carrier", "newCarrier"));

        data.getColumn(1).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editCarrier(HttpSession session, AdminData data, int carrierId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        CarrierRecord carrier = carrierId == 0 ? dslContext.newRecord(Tables.CARRIER)
                : dslContext.selectFrom(Tables.CARRIER).where(Tables.CARRIER.ID.eq(carrierId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("carrier")
                .setEditMethod("editCarrier")
                .setSaveMethod("saveCarrier")
                .setDeleteMethod("deleteCarrier", "Delete", "Note: Carrier can only be deleted when it is not used in a Game")
                .setRecordNr(carrierId)
                .startMultipartForm()
                .addEntry(new FormEntryString(Tables.CARRIER.NAME)
                        .setRequired()
                        .setInitialValue(carrier.getName())
                        .setLabel("Carrier name")
                        .setMaxChars(45))
                .addEntry(new FormEntryString(Tables.CARRIER.SLOGAN)
                        .setInitialValue(carrier.getSlogan())
                        .setRequired()
                        .setLabel("Slogan")
                        .setMaxChars(45))
                .addEntry(new FormEntryImage(Tables.CARRIER.LOGO)
                        .setInitialValue(carrier.getLogo())
                        .setRequired()
                        .setLabel("Carrier logo")
                        .setImageServlet("imageCarrier")
                        .setImageRecordNr(carrierId))
                .addEntry(new FormEntryEnum<CarrierService>(Tables.CARRIER.SERVICE)
                        .setInitialValue(carrier.getService() == null ? 
                                CarrierService.Medium : 
                                carrier.getService())
                        .setLabel("Service")
                        .setRequired()
                        .setPickListEntries(CarrierService.values()))
                .addEntry(new FormEntryEnum<CarrierSustainability>(Tables.CARRIER.SUSTAINABILITY)
                        .setInitialValue(carrier.getSustainability() == null ? 
                                CarrierSustainability.Medium : 
                                carrier.getSustainability())
                        .setLabel("Sustainability")
                        .setRequired()
                        .setPickListEntries(CarrierSustainability.values()))
                .addEntry(new FormEntryText(Tables.CARRIER.COMPANYDESCRIPTION)
                        .setRequired()
                        .setInitialValue(carrier.getCompanydescription())
                        .setLabel("Company description"))
                .addEntry(new FormEntryString(Tables.CARRIER.WEBSITEURL)
                        .setRequired()
                        .setInitialValue(carrier.getWebsiteurl())
                        .setLabel("Website URL")
                        .setMaxChars(90))
                .addEntry(new FormEntryText(Tables.CARRIER.OFFICIALREPORTDESCRIPTION)
                        .setInitialValue(carrier.getOfficialreportdescription())
                        .setLabel("Report description"))
                .addEntry(new FormEntryText(Tables.CARRIER.OFFICIALREPORT)
                        .setInitialValue(carrier.getOfficialreport())
                        .setLabel("Official report"))
                .addEntry(new FormEntryString(Tables.CARRIER.CARRIERTYPE)
                        .setRequired()
                        .setInitialValue(carrier.getCarriertype())
                        .setLabel("Carrier type")
                        .setMaxChars(45))
                .addEntry(new FormEntryText(Tables.CARRIER.GOOGLEPAGE)
                        .setInitialValue(carrier.getGooglepage())
                        .setLabel("Google page (html)"))
                .addEntry(new FormEntryImage(Tables.CARRIER.GOOGLEIMAGE)
                        .setInitialValue(carrier.getGoogleimage())
                        .setLabel("Google page (image)")
                        .setLargeImage()
                        .setImageServlet("imageCarrierGoogle")
                        .setImageRecordNr(carrierId))
                .addEntry(new FormEntryText(Tables.CARRIER.CARRIERWEBPAGE)
                        .setInitialValue(carrier.getCarrierwebpage())
                        .setLabel("Web page (html)"))
                .addEntry(new FormEntryImage(Tables.CARRIER.CARRIERWEBIMAGE)
                        .setInitialValue(carrier.getCarrierwebimage())
                        .setLabel("Web page (image)")
                        .setLargeImage()
                        .setImageServlet("imageCarrierWebsite")
                        .setImageRecordNr(carrierId))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm(edit ? "Edit Carrier" : "View Carrier", form);
    }

    public static int saveCarrier(HttpServletRequest request, AdminData data, int carrierId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        CarrierRecord carrier = carrierId == 0 ? dslContext.newRecord(Tables.CARRIER)
                : dslContext.selectFrom(Tables.CARRIER).where(Tables.CARRIER.ID.eq(carrierId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(carrier, request, data);
        carrier.setGameId(data.getColumn(0).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('carrier')");
            return -1;
        } else {
            try {
                carrier.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('carrier')");
                return -1;
            }
        }
        return carrier.getId();
    }

}
