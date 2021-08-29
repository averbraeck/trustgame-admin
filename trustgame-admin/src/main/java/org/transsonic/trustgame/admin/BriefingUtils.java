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
import org.transsonic.trustgame.data.trustgame.tables.records.BriefingRecord;

public class BriefingUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);
        
        switch (click) {
        
        case "briefing": {
            data.clearColumns("40%", "Briefing");
            data.clearFormColumn("60%", "Edit Properties");
            showBriefings(session, data, true, 0);
            break;
        }

        case "viewBriefing": {
            showBriefings(session, data, true, recordNr);
            editBriefing(session, data, recordNr, false);
            break;
        }

        case "editBriefing": {
            showBriefings(session, data, true, recordNr);
            editBriefing(session, data, recordNr, true);
            break;
        }

        case "saveBriefing": {
            recordNr = saveBriefing(request, data, recordNr);
            showBriefings(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteBriefing": {
            BriefingRecord briefing = SqlUtils.readBriefingFromBriefingId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Briefing",
                    "<p>Delete briefing " + briefing.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteBriefingOk', " + recordNr + ")", "Cancel", "clickMenu('briefing')",
                    "clickMenu('briefing')");
            data.setShowModalWindow(1);
            showBriefings(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteBriefingOk": {
            BriefingRecord briefing = SqlUtils.readBriefingFromBriefingId(data, recordNr);
            try {
                briefing.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('briefing')");
            }
            showBriefings(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newBriefing": {
            showBriefings(session, data, true, 0);
            editBriefing(session, data, 0, true);
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    public static void showBriefings(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<BriefingRecord> briefingRecords = dslContext.selectFrom(Tables.BRIEFING).fetch();

        s.append(AdminTable.startTable());
        for (BriefingRecord briefing : briefingRecords) {
            TableRow tableRow = new TableRow(briefing.getId(), selectedRecordNr, briefing.getName(), "viewBriefing");
            if (editButton)
                tableRow.addButton("Edit", "editBriefing");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());
        
        if (editButton)
            s.append(AdminTable.finalButton("New Briefing", "newBriefing"));

        data.getColumn(0).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    public static void editBriefing(HttpSession session, AdminData data, int briefingId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        BriefingRecord briefing = briefingId == 0 ? dslContext.newRecord(Tables.BRIEFING)
                : dslContext.selectFrom(Tables.BRIEFING).where(Tables.BRIEFING.ID.eq(briefingId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("briefing")
                .setEditMethod("editBriefing")
                .setSaveMethod("saveBriefing")
                .setDeleteMethod("deleteBriefing", "Delete", "Note: Briefing can only be deleted when it is not used in a GamePlay")
                .setRecordNr(briefingId)
                .startMultipartForm()
                .addEntry(new FormEntryString(Tables.BRIEFING.NAME)
                        .setRequired()
                        .setInitialValue(briefing.getName())
                        .setLabel("Briefing name")
                        .setMaxChars(45))
                .addEntry(new FormEntryImage(Tables.BRIEFING.BRIEFINGIMAGE)
                        .setInitialValue(briefing.getBriefingimage())
                        .setLabel("Briefing image")
                        .setImageServlet("imageBriefing")
                        .setImageRecordNr(briefingId)
                        .setLargeImage())
                .addEntry(new FormEntryText(Tables.BRIEFING.BRIEFINGTEXT)
                        .setRequired()
                        .setInitialValue(briefing.getBriefingtext())
                        .setLabel("Briefing text"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Briefing", form);
    }

    public static int saveBriefing(HttpServletRequest request, AdminData data, int briefingId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        BriefingRecord briefing = briefingId == 0 ? dslContext.newRecord(Tables.BRIEFING)
                : dslContext.selectFrom(Tables.BRIEFING).where(Tables.BRIEFING.ID.eq(briefingId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(briefing, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('briefing')");
            return -1;
        } else {
            try {
                briefing.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('briefing')");
                return -1;
            }
        }
        return briefing.getId();
    }

}
