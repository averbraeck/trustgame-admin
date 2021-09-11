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
import org.transsonic.trustgame.data.trustgame.tables.records.MissionRecord;

public class MaintainMission {

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

        case "deleteMission": {
            MissionRecord mission = SqlUtils.readPlayerMissionFromId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Mission",
                    "<p>Delete mission " + mission.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteMissionOk', " + recordNr + ")", "Cancel", "clickMenu('mission')",
                    "clickMenu('mission')");
            data.setShowModalWindow(1);
            showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteMissionOk": {
            MissionRecord mission = SqlUtils.readPlayerMissionFromId(data, recordNr);
            try {
                mission.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('mission')");
            }
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

    public static void showMissions(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<MissionRecord> missionRecords = dslContext.selectFrom(Tables.MISSION)
                .fetch();

        s.append(AdminTable.startTable());
        for (MissionRecord mission : missionRecords) {
            TableRow tableRow = new TableRow(mission.getId(), selectedRecordNr, mission.getName(),
                    "viewMission");
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

    public static void editMission(HttpSession session, AdminData data, int missionId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        MissionRecord mission = missionId == 0 ? dslContext.newRecord(Tables.MISSION)
                : dslContext.selectFrom(Tables.MISSION)
                        .where(Tables.MISSION.ID.eq(missionId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("mission")
                .setEditMethod("editMission")
                .setSaveMethod("saveMission")
                .setDeleteMethod("deleteMission", "Delete", "Note: Mission can only be deleted when it is not used in a Game")
                .setRecordNr(missionId)
                .startForm()
                .addEntry(new FormEntryString(Tables.MISSION.NAME)
                        .setRequired()
                        .setInitialValue(mission.getName())
                        .setLabel("Mission name")
                        .setMaxChars(45))
                .addEntry(new FormEntryText(Tables.MISSION.DESCRIPTION)
                        .setRequired()
                        .setInitialValue(mission.getDescription())
                        .setLabel("Description"))
                .addEntry(new FormEntryInt(Tables.MISSION.TARGETPROFIT)
                        .setRequired()
                        .setInitialValue(mission.getTargetprofit())
                        .setMin(0)
                        .setLabel("Target profit"))
                .addEntry(new FormEntryInt(Tables.MISSION.TARGETSATISFACTION)
                        .setRequired()
                        .setInitialValue(mission.getTargetsatisfaction())
                        .setMin(0)
                        .setLabel("Target satisfaction"))
                .addEntry(new FormEntryInt(Tables.MISSION.TARGETSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(mission.getTargetsustainability())
                        .setMin(0)
                        .setLabel("Target sustainability"))
                .addEntry(new FormEntryInt(Tables.MISSION.STARTPROFIT)
                        .setRequired()
                        .setInitialValue(mission.getStartprofit())
                        .setMin(0)
                        .setLabel("Start profit"))
                .addEntry(new FormEntryInt(Tables.MISSION.STARTSATISFACTION)
                        .setRequired()
                        .setInitialValue(mission.getStartsatisfaction())
                        .setMin(0)
                        .setLabel("Start satisfaction"))
                .addEntry(new FormEntryInt(Tables.MISSION.STARTSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(mission.getStartsustainability())
                        .setMin(0)
                        .setLabel("Start sustainability"))
                .addEntry(new FormEntryInt(Tables.MISSION.MAXPROFIT)
                        .setRequired()
                        .setInitialValue(mission.getMaxprofit())
                        .setMin(0)
                        .setLabel("Max profit (graph)"))
                .addEntry(new FormEntryInt(Tables.MISSION.MAXSATISFACTION)
                        .setRequired()
                        .setInitialValue(mission.getMaxsatisfaction())
                        .setMin(0)
                        .setLabel("Max satisfaction (graph)"))
                .addEntry(new FormEntryInt(Tables.MISSION.MAXSUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(mission.getMaxsustainability())
                        .setMin(0)
                        .setLabel("Max sustainability (graph)"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Mission", form);
    }

    public static int saveMission(HttpServletRequest request, AdminData data, int missionId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        MissionRecord mission = missionId == 0 ? dslContext.newRecord(Tables.MISSION)
                : dslContext.selectFrom(Tables.MISSION)
                        .where(Tables.MISSION.ID.eq(missionId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(mission, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('mission')");
            return -1;
        } else {
            try {
                mission.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('mission')");
                return -1;
            }
        }
        return mission.getId();
    }

}
