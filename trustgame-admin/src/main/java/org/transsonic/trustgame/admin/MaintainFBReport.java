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
import org.transsonic.trustgame.admin.form.FormEntryPickRecord;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryUInt;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.FbreportRecord;

public class MaintainFBReport {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        case "fbreport": {
            data.clearColumns("40%", "FBReport");
            data.clearFormColumn("60%", "Edit Properties");
            showFBReports(session, data, true, 0);
            break;
        }

        case "viewFBReport": {
            showFBReports(session, data, true, recordNr);
            editFBReport(session, data, recordNr, false);
            break;
        }

        case "editFBReport": {
            showFBReports(session, data, true, recordNr);
            editFBReport(session, data, recordNr, true);
            break;
        }

        case "saveFBReport": {
            recordNr = saveFBReport(request, data, recordNr);
            showFBReports(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteFBReport": {
            FbreportRecord fbReport = SqlUtils.readFBReportFromFBReportId(data, recordNr);
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, fbReport.getCarrierId());
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete FBReport",
                    "<p>Delete FreightBooking Report for Carrier " + carrier.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteFBReportOk', " + recordNr + ")", "Cancel", "clickMenu('fbreport')",
                    "clickMenu('fbreport')");
            data.setShowModalWindow(1);
            showFBReports(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteFBReportOk": {
            FbreportRecord fbReport = SqlUtils.readFBReportFromFBReportId(data, recordNr);
            try {
                fbReport.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('fbreport')");
            }
            showFBReports(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newFBReport": {
            showFBReports(session, data, true, 0);
            editFBReport(session, data, 0, true);
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    public static void showFBReports(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<FbreportRecord> FbreportRecords = dslContext.selectFrom(Tables.FBREPORT).fetch();

        s.append(AdminTable.startTable());
        for (FbreportRecord fbReport : FbreportRecords) {
            TableRow tableRow = new TableRow(fbReport.getId(), selectedRecordNr,
                    makeCarrierString(data, fbReport, "Report for carrier: "), "viewFBReport");
            if (editButton)
                tableRow.addButton("Edit", "editFBReport");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New FBReport", "newFBReport"));

        data.getColumn(0).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    private static String makeCarrierString(AdminData data, FbreportRecord fbReport, String title) {
        String s = title;
        if (fbReport.getCarrierId() != null && fbReport.getCarrierId() != 0) {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, fbReport.getCarrierId());
            if (carrier != null)
                s += carrier.getName();
            else
                s += "-";
        } else
            s += "-";
        return s;
    }

    public static void editFBReport(HttpSession session, AdminData data, int fbreportId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        FbreportRecord fbReport = fbreportId == 0 ? dslContext.newRecord(Tables.FBREPORT)
                : dslContext.selectFrom(Tables.FBREPORT).where(Tables.FBREPORT.ID.eq(fbreportId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("fbreport")
                .setEditMethod("editFBReport")
                .setSaveMethod("saveFBReport")
                .setDeleteMethod("deleteFBReport", "Delete", "Careful: FBReport can always be deleted!")
                .setRecordNr(fbreportId)
                .startMultipartForm()
                .addEntry(new FormEntryPickRecord(Tables.FBREPORT.CARRIER_ID)
                        .setInitialValue(fbReport.getCarrierId() == null ? 0 : fbReport.getCarrierId())
                        .setLabel("Carrier")
                        .setRequired()
                        .setPickTable(data, Tables.CARRIER, Tables.CARRIER.ID, Tables.CARRIER.NAME))
                .addEntry(new FormEntryString(Tables.FBREPORT.FBREGISTRATION)
                        .setRequired()
                        .setInitialValue(fbReport.getFbregistration())
                        .setLabel("FB Registration nr (8 chars)")
                        .setMaxChars(8))
                .addEntry(new FormEntryString(Tables.FBREPORT.COUNTRYCODE)
                        .setInitialValue(fbReport.getCountrycode())
                        .setRequired()
                        .setLabel("Country code (2 chars)")
                        .setMaxChars(2))
                .addEntry(new FormEntryString(Tables.FBREPORT.ADDRESS)
                        .setRequired()
                        .setInitialValue(fbReport.getAddress())
                        .setLabel("Address")
                        .setMaxChars(45))
                .addEntry(new FormEntryUInt(Tables.FBREPORT.FBMEMBERSINCE)
                        .setRequired()
                        .setInitialValue(fbReport.getFbmembersince())
                        .setLabel("Member since")
                        .setMax(2100)
                        .setMin(1980))
                .addEntry(new FormEntryImage(Tables.FBREPORT.SERVICEONTIME)
                        .setInitialValue(fbReport.getServiceontime())
                        .setRequired()
                        .setLabel("Service on-time")
                        .setLargeImage()
                        .setImageServlet("imageFB")
                        .setImageRecordNr(fbreportId)
                        .setImageNr(1))
                .addEntry(new FormEntryImage(Tables.FBREPORT.SERVICESATISFACTION)
                        .setInitialValue(fbReport.getServicesatisfaction())
                        .setRequired()
                        .setLabel("Service satisfaction")
                        .setLargeImage()
                        .setImageServlet("imageFB")
                        .setImageRecordNr(fbreportId)
                        .setImageNr(2))
                .addEntry(new FormEntryImage(Tables.FBREPORT.TECHNICALFLEET)
                        .setInitialValue(fbReport.getTechnicalfleet())
                        .setRequired()
                        .setLabel("Truck fleet")
                        .setLargeImage()
                        .setImageServlet("imageFB")
                        .setImageRecordNr(fbreportId)
                        .setImageNr(3))
                .addEntry(new FormEntryImage(Tables.FBREPORT.TECHNICALGREEN)
                        .setInitialValue(fbReport.getTechnicalgreen())
                        .setRequired()
                        .setLabel("Percent green")
                        .setLargeImage()
                        .setImageServlet("imageFB")
                        .setImageRecordNr(fbreportId)
                        .setImageNr(4))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit FBReport", form);
    }

    public static int saveFBReport(HttpServletRequest request, AdminData data, int fbreportId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        FbreportRecord fbreport = fbreportId == 0 ? dslContext.newRecord(Tables.FBREPORT)
                : dslContext.selectFrom(Tables.FBREPORT).where(Tables.FBREPORT.ID.eq(fbreportId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(fbreport, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('fbreport')");
            return -1;
        } else {
            try {
                fbreport.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('fbreport')");
                return -1;
            }
        }
        return fbreport.getId();
    }

}
