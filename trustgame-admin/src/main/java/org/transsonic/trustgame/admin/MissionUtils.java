package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryInt;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.PlayerorganizationRecord;

public class MissionUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);
        
        switch (click) {
        
        case "mission": {
            data.clearColumns("40%", "Mission");
            data.clearFormColumn("60%", "Edit Properties");
            showMissions(session, data, true, 0);
            break;
        }

        case "editMission": {
            showMissions(session, data, true, recordNr);
            editMission(session, data, recordNr, true);
            break;
        }

        case "viewMission": {
            showMissions(session, data, true, recordNr);
            editMission(session, data, recordNr, false);
            break;
        }

        case "saveMission": {
            recordNr = saveMission(request, data, recordNr);
            showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newMission": {
            showMissions(session, data, true, 0);
            editMission(session, data, 0, true);
            break;
        }
        
        default:
            break;
        }
        
        AdminServlet.makeColumnContent(data);
    }

    public static void showMissions(HttpSession session, AdminData data, boolean editButton,
            int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<PlayerorganizationRecord> playerOrganizationRecords = dslContext.selectFrom(Tables.PLAYERORGANIZATION)
                .fetch();

        s.append(AdminTable.startTable());
        for (PlayerorganizationRecord organization : playerOrganizationRecords) {
            TableRow tableRow = new TableRow(organization.getId(), selectedRecordNr, organization.getName(), "viewMission");
            if (editButton)
                tableRow.addButton("Edit", "editMission");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());
        
        if (editButton)
            s.append(AdminTable.finalButton("New Mission", "newMission"));
        
        data.getColumn(0).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    public static void editMission(HttpSession session, AdminData data, int organizationId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        PlayerorganizationRecord organization = organizationId == 0 ? dslContext.newRecord(Tables.PLAYERORGANIZATION)
                : dslContext.selectFrom(Tables.PLAYERORGANIZATION)
                        .where(Tables.PLAYERORGANIZATION.ID.eq(organizationId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("mission")
                .setEditMethod("editMission")
                .setSaveMethod("saveMission")
                .setRecordNr(organizationId)
                .startForm()
                .addEntry(new FormEntryString(Tables.PLAYERORGANIZATION.NAME)
                        .setRequired()
                        .setInitialValue(organization.getName())
                        .setLabel("Mission name")
                        .setMaxChars(45))
                .addEntry(new FormEntryText(Tables.PLAYERORGANIZATION.DESCRIPTION)
                        .setRequired()
                        .setInitialValue(organization.getDescription())
                        .setLabel("Description"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.TARGETPROFIT)
                        .setRequired()
                        .setInitialValue(organization.getTargetprofit())
                        .setMin(0)
                        .setLabel("Target profit"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.TARGETSATISFACTION)
                        .setRequired()
                        .setInitialValue(organization.getTargetsatisfaction())
                        .setMin(0)
                        .setLabel("Target satisfaction"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.TARGETSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(organization.getTargetsustainability())
                        .setMin(0)
                        .setLabel("Target sustainability"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.STARTPROFIT)
                        .setRequired()
                        .setInitialValue(organization.getStartprofit())
                        .setMin(0)
                        .setLabel("Start profit"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.STARTSATISFACTION)
                        .setRequired()
                        .setInitialValue(organization.getStartsatisfaction())
                        .setMin(0)
                        .setLabel("Start satisfaction"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.STARTSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(organization.getStartsustainability())
                        .setMin(0)
                        .setLabel("Start sustainability"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.MAXPROFIT)
                        .setRequired()
                        .setInitialValue(organization.getMaxprofit())
                        .setMin(0)
                        .setLabel("Max profit (graph)"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.MAXSATISFACTION)
                        .setRequired()
                        .setInitialValue(organization.getMaxsatisfaction())
                        .setMin(0)
                        .setLabel("Max satisfaction (graph)"))
                .addEntry(new FormEntryInt(Tables.PLAYERORGANIZATION.MAXSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(organization.getMaxsustainability())
                        .setMin(0)
                        .setLabel("Max sustainability (graph)"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Mission", form);
    }

    public static int saveMission(HttpServletRequest request, AdminData data, int organizationId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        PlayerorganizationRecord organization = organizationId == 0 ? dslContext.newRecord(Tables.PLAYERORGANIZATION)
                : dslContext.selectFrom(Tables.PLAYERORGANIZATION)
                        .where(Tables.PLAYERORGANIZATION.ID.eq(organizationId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(organization, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('mission')");
            return -1;
        } else {
            try {
                organization.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('mission')");
                return -1;
            }
        }
        return organization.getId();
    }

}
