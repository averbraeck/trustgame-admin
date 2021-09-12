package org.transsonic.trustgame.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

public class MaintainLogging {

    public static void handleMenu(HttpServletRequest request, HttpServletResponse response, String click,
            int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        // Game

        case "logging": {
            data.clearColumns("15%", "Game", "18%", "GamePlay", "17%", "GameUsers", "50%", "Log Records");
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

        case "csvGamePlayLogs": {
            downloadGamePlayLogs(response, data, false, recordNr);
            return;
        }

        case "tsvGamePlayLogs": {
            downloadGamePlayLogs(response, data, true, recordNr);
            return;
        }

        case "csvGameUserLogs": {
            downloadGameUserLogs(response, data, false, recordNr);
            return;
        }

        case "tsvGameUserLogs": {
            downloadGameUserLogs(response, data, true, recordNr);
            return;
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
            tableRow.addButton("csv", "csvGamePlayLogs");
            tableRow.addButton("tsv", "tsvGamePlayLogs");
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
            TableRow tableRow = new TableRow(gameUser.getId(), selectedGameuserRecordNr, user.getUsername(),
                    "loggingGameUserLogs");
            tableRow.addButton("csv", "csvGameUserLogs");
            tableRow.addButton("tsv", "tsvGameUserLogs");
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
            s.append(userClick.getTimestamp().format(DATE_TIME_FORMATTER));
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

        data.getColumn(3).setHeader("Log records for " + user.getUsername());
        data.getColumn(3).setContent(s.toString());
    }

    public static void downloadGameUserLogs(HttpServletResponse response, AdminData data, boolean tab, int gameUserId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = dslContext.selectFrom(Tables.GAMEUSER).where(Tables.GAMEUSER.ID.eq(gameUserId))
                .fetchAny();
        UserRecord user = dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(gameUser.getUserId())).fetchAny();
        GameplayRecord gamePlay = dslContext.selectFrom(Tables.GAMEPLAY)
                .where(Tables.GAMEPLAY.ID.eq(data.getColumn(1).getSelectedRecordNr())).fetchAny();
        List<UserclickRecord> userClicks = dslContext.selectFrom(Tables.USERCLICK)
                .where(Tables.USERCLICK.GAMEUSER_ID.eq(gameUser.getId())).fetch().sortAsc(Tables.USERCLICK.TIMESTAMP);
        try {
            File tempFile = File.createTempFile("trustgame-", tab ? ".xls" : ".csv");
            tempFile.deleteOnExit();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                bw.write(csvHeader(tab));
                for (UserclickRecord userClick : userClicks) {
                    bw.write(csvLine(gamePlay, userClick, user, tab));
                }
            } catch (IOException exception) {
                ModalWindowUtils.popup(data, "Error writing to temporary file", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('logging')");
            }

            // stream the results for download
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd_HHmmss"));
            String ext = tab ? ".xls" : ".csv";
            response.setContentType(tab ? "text/tab-separated-values" : "text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + "GameUserData_" + gameUser.getId() + "_" + date + ext);

            final BufferedReader br = new BufferedReader(new FileReader(tempFile));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    response.getWriter().write(line + "\n");
                }
            } finally {
                br.close();
            }
        } catch (IOException exception) {
            ModalWindowUtils.popup(data, "Error creating temporary file", "<p>" + exception.getMessage() + "</p>",
                    "clickMenu('logging')");
        }
    }

    public static void downloadGamePlayLogs(HttpServletResponse response, AdminData data, boolean tab, int gamePlayId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameuserRecord> gameUserRecords = dslContext.selectFrom(Tables.GAMEUSER)
                .where(Tables.GAMEUSER.GAMEPLAY_ID.eq(gamePlayId)).fetch();
        GameplayRecord gamePlay = dslContext.selectFrom(Tables.GAMEPLAY).where(Tables.GAMEPLAY.ID.eq(gamePlayId))
                .fetchAny();
        try {
            File tempFile = File.createTempFile("trustgame-", tab ? ".xls" : ".csv");
            tempFile.deleteOnExit();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                bw.write(csvHeader(tab));
                for (GameuserRecord gameUser : gameUserRecords) {
                    UserRecord user = dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(gameUser.getUserId()))
                            .fetchAny();
                    List<UserclickRecord> userClicks = dslContext.selectFrom(Tables.USERCLICK)
                            .where(Tables.USERCLICK.GAMEUSER_ID.eq(gameUser.getId())).fetch()
                            .sortAsc(Tables.USERCLICK.TIMESTAMP);
                    for (UserclickRecord userClick : userClicks) {
                        bw.write(csvLine(gamePlay, userClick, user, tab));
                    }
                }
            } catch (IOException exception) {
                ModalWindowUtils.popup(data, "Error writing to temporary file", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('logging')");
            }

            // stream the results for download
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd_HHmmss"));
            String ext = tab ? ".xls" : ".csv";
            response.setContentType(tab ? "text/tab-separated-values" : "text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + "GamePlayData_" + gamePlay.getId() + "_" + date + ext);

            final BufferedReader br = new BufferedReader(new FileReader(tempFile));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    response.getWriter().write(line + "\n");
                }
            } finally {
                br.close();
            }
        } catch (IOException exception) {
            ModalWindowUtils.popup(data, "Error creating temporary file", "<p>" + exception.getMessage() + "</p>",
                    "clickMenu('logging')");
        }
    }

    public static String csvHeader(boolean tab) {
        StringBuffer s = new StringBuffer();
        String sep = tab ? "\t" : ",";
        s.append("gameNr");
        s.append(sep);
        s.append("gamePlayNr");
        s.append(sep);
        s.append("gameUserNr");
        s.append(sep);
        s.append("userCode");
        s.append(sep);
        s.append("userName");
        s.append(sep);
        s.append("time");
        s.append(sep);
        s.append("button");
        s.append(sep);
        s.append("value");
        s.append(sep);
        s.append("round");
        s.append(sep);
        s.append("order");
        s.append(sep);
        s.append("client");
        s.append(sep);
        s.append("carrier");
        s.append("\n");
        return s.toString();
    }

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public static String csvLine(GameplayRecord gamePlay, UserclickRecord userClick, UserRecord user, boolean tab) {
        StringBuffer s = new StringBuffer();
        String sep = tab ? "\t" : ",";
        s.append(gamePlay.getGameId());
        s.append(sep);
        s.append(gamePlay.getId());
        s.append(sep);
        s.append(userClick.getGameuserId());
        s.append(sep);
        s.append(user.getUsercode());
        s.append(sep);
        s.append(user.getUsername());
        s.append(sep);
        s.append(csvString(userClick.getTimestamp().format(DATE_TIME_FORMATTER)));
        s.append(sep);
        s.append(csvString(userClick.getButtonorfield()));
        s.append(sep);
        s.append(csvString(userClick.getValue() == null ? "" : userClick.getValue()));
        s.append(sep);
        s.append(userClick.getRoundnumber());
        s.append(sep);
        s.append(userClick.getOrdernumber());
        s.append(sep);
        s.append(csvString(userClick.getClientname() == null ? "" : userClick.getClientname()));
        s.append(sep);
        s.append(csvString(userClick.getCarriername() == null ? "" : userClick.getCarriername()));
        s.append("\n");
        return s.toString();
    }

    public static String csvString(String s) {
        String result = s;
        result.replaceAll("\"", "\"\"");
        return "\"" + result + "\"";
    }

}
