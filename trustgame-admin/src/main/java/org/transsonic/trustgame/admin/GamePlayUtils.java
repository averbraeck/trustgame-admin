package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryDateTime;
import org.transsonic.trustgame.admin.form.FormEntryInt;
import org.transsonic.trustgame.admin.form.FormEntryPickRecord;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryUInt;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameplayRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameuserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.PlayerorganizationRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;

public class GamePlayUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        // Game

        case "gameplay": {
            data.clearColumns("20%", "Game", "20%", "GamePlay", "20%", "GameUsers");
            data.clearFormColumn("40%", "Edit Properties");
            showGames(session, data, 0);
            break;
        }

        // GamePlay

        case "showGamePlay": {
            showGames(session, data, recordNr);
            if (recordNr == 0)
                data.resetColumn(1);
            else
                showGamePlay(session, data, true, 0);
            data.resetColumn(2);
            data.resetFormColumn();
            break;
        }

        case "viewGamePlay": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, recordNr);
            editGamePlay(session, data, recordNr, false);
            showGameUsers(session, data, true, 0);
            break;
        }

        case "editGamePlay": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, recordNr);
            editGamePlay(session, data, recordNr, true);
            showGameUsers(session, data, true, 0);
            break;
        }

        case "saveGamePlay": {
            recordNr = saveGamePlay(request, data, recordNr);
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, recordNr);
            showGameUsers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newGamePlay": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, recordNr);
            editGamePlay(session, data, 0, true);
            data.resetColumn(2);
            break;
        }

        // GameUser

        case "showGameUsers": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, recordNr);
            if (recordNr == 0)
                data.resetColumn(2); // to solve 'cancel' for new gameUser
            else
                showGameUsers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "viewGameUser": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, true, recordNr);
            editGameUser(session, data, recordNr, false);
            break;
        }

        case "editGameUser": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, true, recordNr);
            editGameUser(session, data, recordNr, true);
            break;
        }

        case "saveGameUser": {
            recordNr = saveGameUser(request, data, recordNr);
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, true, recordNr);
            data.resetFormColumn();
            break;
        }

        case "newGameUser": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, true, recordNr);
            editGameUser(session, data, 0, true);
            break;
        }

        default:
            break;
        }

        AdminServlet.makeColumnContent(data);
    }

    /* ********************************************************************************************************* */
    /* ****************************************** GAME ********************************************************* */
    /* ********************************************************************************************************* */

    public static void showGames(HttpSession session, AdminData data, int selectedGameRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr, game.getName(), "showGamePlay");
            tableRow.addButton("GamePlay", "showGamePlay");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEPLAY ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showGamePlay(HttpSession session, AdminData data, boolean editButton,
            int selectedGameplayRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameplayRecord> gamePlayRecords = dslContext.selectFrom(Tables.GAMEPLAY)
                .where(Tables.GAMEPLAY.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (GameplayRecord gamePlay : gamePlayRecords) {
            TableRow tableRow = new TableRow(gamePlay.getId(), selectedGameplayRecordNr, gamePlay.getGroupdescription(),
                    "viewGamePlay");
            if (editButton) {
                tableRow.addButton("GameUsers", "showGameUsers");
                tableRow.addButton("Edit", "editGamePlay");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New GamePlay", "newGamePlay"));

        data.getColumn(1).setSelectedRecordNr(selectedGameplayRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editGamePlay(HttpSession session, AdminData data, int gamePlayId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameplayRecord gamePlay = gamePlayId == 0 ? dslContext.newRecord(Tables.GAMEPLAY)
                : dslContext.selectFrom(Tables.GAMEPLAY).where(Tables.GAMEPLAY.ID.eq(gamePlayId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("game", data.getColumn(0).getSelectedRecordNr())
                .setEditMethod("editGamePlay")
                .setSaveMethod("saveGamePlay")
                .setRecordNr(gamePlayId)
                .startForm()
                .addEntry(new FormEntryString(Tables.GAMEPLAY.GROUPDESCRIPTION)
                        .setRequired()
                        .setInitialValue(gamePlay.getGroupdescription())
                        .setLabel("Group description"))
                .addEntry(new FormEntryDateTime(Tables.GAMEPLAY.STARTPLAYDATE)
                        .setInitialValue(gamePlay.getStartplaydate())
                        .setLabel("Earliest play date"))
                .addEntry(new FormEntryDateTime(Tables.GAMEPLAY.ENDPLAYDATE)
                        .setInitialValue(gamePlay.getEndplaydate())
                        .setLabel("Latest play date"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit GamePlay", form);
    }

    public static int saveGamePlay(HttpServletRequest request, AdminData data, int gamePlayId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameplayRecord gamePlay = gamePlayId == 0 ? dslContext.newRecord(Tables.GAMEPLAY)
                : dslContext.selectFrom(Tables.GAMEPLAY).where(Tables.GAMEPLAY.ID.eq(gamePlayId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(gamePlay, request, data);
        gamePlay.setGameId(data.getColumn(0).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('gameplay')");
            return -1;
        } else {
            try {
                gamePlay.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('gameplay')");
                return -1;
            }
        }
        return gamePlay.getId();
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEUSER ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showGameUsers(HttpSession session, AdminData data, boolean editButton,
            int selectedGameuserRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameuserRecord> gameUserRecords = dslContext.selectFrom(Tables.GAMEUSER)
                .where(Tables.GAMEUSER.GAMEPLAY_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (GameuserRecord gameUser : gameUserRecords) {
            UserRecord user = SqlUtils.readUserFromUserId(data, gameUser.getUserId());
            TableRow tableRow = new TableRow(gameUser.getId(), selectedGameuserRecordNr, user.getName(),
                    "viewGameUser");
            if (editButton) {
                tableRow.addButton("Edit", "editGameUser");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New GameUser", "newGameUser"));

        data.getColumn(2).setSelectedRecordNr(selectedGameuserRecordNr);
        data.getColumn(2).setContent(s.toString());
    }

    public static void editGameUser(HttpSession session, AdminData data, int gameUserId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = gameUserId == 0 ? initializeGameUser(data)
                : dslContext.selectFrom(Tables.GAMEUSER).where(Tables.GAMEUSER.ID.eq(gameUserId)).fetchOne();

        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showGameUsers", data.getColumn(1).getSelectedRecordNr())
                .setEditMethod("editGameUser")
                .setSaveMethod("saveGameUser")
                .setRecordNr(gameUserId)
                .startForm()
                .addEntry(new FormEntryPickRecord(Tables.GAMEUSER.USER_ID)
                        .setInitialValue(gameUser.getUserId() == null ? 0 : gameUser.getUserId())
                        .setLabel("User")
                        .setRequired()
                        .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME))
                .addEntry(new FormEntryDateTime(Tables.GAMEUSER.FIRSTLOGIN)
                        .setInitialValue(gameUser.getFirstlogin())
                        .setLabel("First login")
                        .setReadOnly())
                .addEntry(new FormEntryInt(Tables.GAMEUSER.SCOREPROFIT)
                        .setInitialValue(gameUser.getScoreprofit())
                        .setLabel("Score profit")
                        .setReadOnly())
                .addEntry(new FormEntryInt(Tables.GAMEUSER.SCORESATISFACTION)
                        .setInitialValue(gameUser.getScoresatisfaction())
                        .setLabel("Score satisfaction")
                        .setReadOnly())
                .addEntry(new FormEntryInt(Tables.GAMEUSER.SCORESUSTAINABILITY)
                        .setInitialValue(gameUser.getScoresustainability())
                        .setLabel("Score sustainability")
                        .setReadOnly())
                .addEntry(new FormEntryUInt(Tables.GAMEUSER.ROUNDNUMBER)
                        .setInitialValue(gameUser.getRoundnumber())
                        .setLabel("Round number")
                        .setReadOnly())
                .addEntry(new FormEntryInt(Tables.GAMEUSER.ROUNDSTATUS)
                        .setInitialValue(gameUser.getRoundstatus())
                        .setLabel("Round status")
                        .setReadOnly())
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit GameUser", form);
    }

    public static int saveGameUser(HttpServletRequest request, AdminData data, int gameUserId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = gameUserId == 0 ? initializeGameUser(data)
                : dslContext.selectFrom(Tables.GAMEUSER).where(Tables.GAMEUSER.ID.eq(gameUserId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(gameUser, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('gameplay')");
            return -1;
        } else {
            try {
                gameUser.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('gameplay')");
                return -1;
            }
        }
        return gameUser.getId();
    }

    private static GameuserRecord initializeGameUser(AdminData data) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = dslContext.newRecord(Tables.GAMEUSER);
        int gamePlayId = data.getColumn(1).getSelectedRecordNr();
        int gameId = data.getColumn(0).getSelectedRecordNr();
        GameRecord game = SqlUtils.readGameFromGameId(data, gameId);
        gameUser.setGameplayId(gamePlayId);
        PlayerorganizationRecord organization = SqlUtils.readPlayerOrganizationFromId(data,
                game.getOrganizationId());
        gameUser.setScoreprofit(organization.getStartprofit());
        gameUser.setScoresatisfaction(organization.getStartsatisfaction());
        gameUser.setScoresustainability(organization.getStartsustainability());
        gameUser.setRoundnumber(UInteger.valueOf(1));
        gameUser.setRoundstatus(0);
        return gameUser;
    }
}
