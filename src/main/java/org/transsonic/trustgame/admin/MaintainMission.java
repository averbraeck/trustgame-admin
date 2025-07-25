package org.transsonic.trustgame.admin;

import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class MaintainMission {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        case "mission": {
            data.clearColumns("25%", "Game", "25%", "Mission");
            data.clearFormColumn("50%", "Edit Properties");
            SessionUtils.showGames(session, data, 0, "Mission", "showMission");
            break;
        }

        case "showMission": {
            SessionUtils.showGames(session, data, recordNr, "Mission", "showMission");
            if (recordNr == 0)
                data.resetColumn(1);
            else
                showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "editMission": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            showMissions(session, data, true, recordNr);
            editMission(session, data, recordNr, true);
            break;
        }

        case "viewMission": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            showMissions(session, data, true, recordNr);
            editMission(session, data, recordNr, false);
            break;
        }

        case "saveMission": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            recordNr = saveMission(request, data, recordNr);
            showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteMission": {
            MissionRecord mission = SqlUtils.readMissionFromMissionId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Mission",
                    "<p>Delete mission " + mission.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteMissionOk', " + recordNr + ")", "Cancel", "clickMenu('mission')",
                    "clickMenu('mission')");
            data.setShowModalWindow(1);
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteMissionOk": {
            MissionRecord mission = SqlUtils.readMissionFromMissionId(data, recordNr);
            try {
                mission.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('mission')");
            }
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            showMissions(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newMission": {
            SessionUtils.showGames(session, data, data.getColumn(0).getSelectedRecordNr(), "Mission", "showMission");
            showMissions(session, data, true, 0);
            editMission(session, data, 0, true);
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    /* ********************************************************************************************************* */
    /* ****************************************** MISSION ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showMissions(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<MissionRecord> missionRecords = dslContext.selectFrom(Tables.MISSION)
                .where(Tables.MISSION.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (MissionRecord mission : missionRecords) {
            TableRow tableRow = new TableRow(mission.getId(), selectedRecordNr, mission.getName(), "viewMission");
            if (editButton)
                tableRow.addButton("Edit", "editMission");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton && missionRecords.size() == 0)
            s.append(AdminTable.finalButton("New Mission", "newMission"));

        data.getColumn(1).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editMission(HttpSession session, AdminData data, int missionId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        MissionRecord mission = missionId == 0 ? dslContext.newRecord(Tables.MISSION)
                : dslContext.selectFrom(Tables.MISSION).where(Tables.MISSION.ID.eq(missionId)).fetchOne();
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
                : dslContext.selectFrom(Tables.MISSION).where(Tables.MISSION.ID.eq(missionId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(mission, request, data);
        mission.setGameId(data.getColumn(0).getSelectedRecordNr());
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
