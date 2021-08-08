package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryPickRecord;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.admin.form.FormEntryUInt;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.FbreportRecord;

public class FBReportUtils {

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

    public static void showFBReports(HttpSession session, AdminData data, boolean editButton,
            int selectedRecordNr) {
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
                .setRecordNr(fbreportId)
                .startForm()
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
                .addEntry(new FormEntryString(Tables.FBREPORT.BTWNR)
                        .setRequired()
                        .setInitialValue(fbReport.getBtwnr())
                        .setLabel("BTW number (max 16 chars)")
                        .setMaxChars(16))
                .addEntry(new FormEntryString(Tables.FBREPORT.ISO)
                        .setRequired()
                        .setInitialValue(fbReport.getIso())
                        .setLabel("ISO certification")
                        .setMaxChars(45))
                .addEntry(new FormEntryString(Tables.FBREPORT.CONTACT)
                        .setRequired()
                        .setInitialValue(fbReport.getContact())
                        .setLabel("Contact person")
                        .setMaxChars(45))
                .addEntry(new FormEntryText(Tables.FBREPORT.CAPACITY)
                        .setInitialValue(fbReport.getCapacity())
                        .setLabel("Capacity"))
                .addEntry(new FormEntryText(Tables.FBREPORT.EMPLOYEES)
                        .setInitialValue(fbReport.getEmployees())
                        .setLabel("Employees"))
                .addEntry(new FormEntryText(Tables.FBREPORT.RELIABILITY)
                        .setInitialValue(fbReport.getReliability())
                        .setLabel("Reliability"))
                .addEntry(new FormEntryText(Tables.FBREPORT.FLEETCHARACTERISTICS)
                        .setInitialValue(fbReport.getFleetcharacteristics())
                        .setLabel("Fleet characteristics"))
                .addEntry(new FormEntryText(Tables.FBREPORT.OTHERINFO)
                        .setInitialValue(fbReport.getOtherinfo())
                        .setLabel("Other info"))
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
