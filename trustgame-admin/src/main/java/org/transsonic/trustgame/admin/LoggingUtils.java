package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameplayRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameuserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserclickRecord;

public class LoggingUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        // Game

        case "logging": {
            data.clearColumns("15%", "Game", "20%", "GamePlay", "15%", "GameUsers", "50%", "Log Records");
            data.setFormColumn(null);
            showGames(session, data, 0);
            break;
        }

        // GamePlay

        case "loggingGamePlay": {
            showGames(session, data, recordNr);
            showGamePlay(session, data, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        // GameUser

        case "loggingGameUsers": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, recordNr);
            showGameUsers(session, data, 0);
            data.resetColumn(3);
            break;
        }

        // log records

        case "loggingGameUserLogs": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, recordNr);
            showGameUserLogs(session, data, recordNr);
            break;
        }

        case "loggingGamePlayLogs": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, recordNr);
            // showDetailedScore(session, data, recordNr);
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
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr, game.getName(), "loggingGamePlay");
            tableRow.addButton("GamePlay", "loggingGamePlay");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEPLAY ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showGamePlay(HttpSession session, AdminData data, int selectedGameplayRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameplayRecord> gamePlayRecords = dslContext.selectFrom(Tables.GAMEPLAY)
                .where(Tables.GAMEPLAY.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (GameplayRecord gamePlay : gamePlayRecords) {
            TableRow tableRow = new TableRow(gamePlay.getId(), selectedGameplayRecordNr, gamePlay.getGroupdescription(),
                    "loggingGameUsers");
            tableRow.addButton("Users", "loggingGameUsers");
            tableRow.addButton("Log", "loggingGamePlayLogs");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(1).setSelectedRecordNr(selectedGameplayRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEUSER ****************************************************** */
    /* ********************************************************************************************************* */

    public static void showGameUsers(HttpSession session, AdminData data, int selectedGameuserRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameuserRecord> gameUserRecords = dslContext.selectFrom(Tables.GAMEUSER)
                .where(Tables.GAMEUSER.GAMEPLAY_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (GameuserRecord gameUser : gameUserRecords) {
            UserRecord user = SqlUtils.readUserFromUserId(data, gameUser.getUserId());
            TableRow tableRow = new TableRow(gameUser.getId(), selectedGameuserRecordNr, user.getName(),
                    "loggingGameUserLogs");
            tableRow.addButton("Log", "loggingGameUserLogs");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(2).setSelectedRecordNr(selectedGameuserRecordNr);
        data.getColumn(2).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** USER LOG RECORDS ********************************************** */
    /* ********************************************************************************************************* */

    public static void showGameUserLogs(HttpSession session, AdminData data, int gameUserId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = dslContext.selectFrom(Tables.GAMEUSER).where(Tables.GAMEUSER.ID.eq(gameUserId))
                .fetchAny();
        UserRecord user = SqlUtils.readUserFromUserId(data, gameUser.getUserId());
        List<UserclickRecord> userClicks = dslContext.selectFrom(Tables.USERCLICK)
                .where(Tables.USERCLICK.GAMEUSER_ID.eq(gameUser.getId())).fetch().sortAsc(Tables.USERCLICK.TIMESTAMP);

        StringBuffer s = new StringBuffer();
        s.append("\n<div class=\"tg-logging\">\n");
        s.append("  <table width=\"100%\">\n");
        s.append("    <thead><tr><td>Time</td><td>Button</td><td>Value</td>"
                + "<td>Round</td><td>Order</td><td>Client</td><td>Carrier</td></tr></thead>\n");
        s.append("    <tbody>\n");

        for (UserclickRecord userClick : userClicks) {
            s.append("      <tr><td>");
            s.append(userClick.getTimestamp().toString().replaceFirst("T", " "));
            s.append("</td><td>");
            s.append(userClick.getButtonorfield());
            s.append("</td><td>");
            s.append(userClick.getValue() == null ? "" : userClick.getValue());
            s.append("</td><td>");
            s.append(userClick.getRoundnumber());
            s.append("</td><td>");
            s.append(userClick.getOrdernumber() == 0 ? "" : userClick.getOrdernumber());
            s.append("</td><td>");
            s.append(userClick.getClientname() == null ? "" : userClick.getClientname());
            s.append("</td><td>");
            s.append(userClick.getCarriername() == null ? "" : userClick.getCarriername());
            s.append("</td></tr>\n");
        }

        s.append("    </tbody>\n");
        s.append("  </table>\n");
        s.append("</div>\n"); // tg-logging

        data.getColumn(3).setHeader("Log records for " + user.getName());
        data.getColumn(3).setContent(s.toString());
    }

}
