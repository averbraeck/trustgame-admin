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
import java.util.SortedMap;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameplayRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameuserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.MissionRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrderRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrdercarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.SelectedcarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UsercarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserclickRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserorderRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserroundRecord;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MaintainResult {

    public static void handleMenu(HttpServletRequest request, HttpServletResponse response, String click,
            int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        // Game

        case "result": {
            data.clearColumns("15%", "Game", "25%", "GamePlay", "30%", "GameUsers plus Scores", "30%",
                    "Detailed Scores");
            data.setFormColumn(null);
            showGames(session, data, 0);
            break;
        }

        // GamePlay

        case "resultGamePlay": {
            showGames(session, data, recordNr);
            showGamePlay(session, data, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        case "csvResultGamePlay": {
            downloadResults(response, data, false, recordNr);
            return;
        }

        case "tsvResultGamePlay": {
            downloadResults(response, data, true, recordNr);
            return;
        }

        // GameUser plus scores

        case "resultGameUsers": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, recordNr);
            showGameUsers(session, data, 0);
            data.resetColumn(3);
            break;
        }

        // detailed scores

        case "resultGameUserDetail": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showGamePlay(session, data, data.getColumn(1).getSelectedRecordNr());
            showGameUsers(session, data, recordNr);
            showDetailedScore(session, data, recordNr);
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

    private static void showGames(HttpSession session, AdminData data, int selectedGameRecordNr) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr, game.getName(), "resultGamePlay");
            tableRow.addButton("GamePlay", "resultGamePlay");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEPLAY ****************************************************** */
    /* ********************************************************************************************************* */

    private static void showGamePlay(HttpSession session, AdminData data, int selectedGameplayRecordNr) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameplayRecord> gamePlayRecords = dslContext.selectFrom(Tables.GAMEPLAY)
                .where(Tables.GAMEPLAY.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (GameplayRecord gamePlay : gamePlayRecords) {
            TableRow tableRow = new TableRow(gamePlay.getId(), selectedGameplayRecordNr, gamePlay.getGroupdescription(),
                    "resultGameUsers");
            tableRow.addButton("csv", "csvResultGamePlay");
            tableRow.addButton("tsv", "tsvResultGamePlay");
            tableRow.addButton("Users", "resultGameUsers");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(1).setSelectedRecordNr(selectedGameplayRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** GAMEUSER ****************************************************** */
    /* ********************************************************************************************************* */

    private static void showGameUsers(HttpSession session, AdminData data, int selectedGameuserRecordNr) {
        StringBuilder s = new StringBuilder();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameuserRecord> gameUserRecords = dslContext.selectFrom(Tables.GAMEUSER)
                .where(Tables.GAMEUSER.GAMEPLAY_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append("\n      <div class=\"tg-users-score\">\n");
        s.append(AdminTable.startTable());
        s.append("        <table width=\"100%\">\n");
        s.append("          <thead>\n");
        s.append("            <tr>\n");
        s.append("              <td width=\"40%\" align=\"left\">Name</td>\n");
        s.append("              <td width=\"15%\" align=\"center\">Played</td>\n");
        s.append("              <td width=\"15%\" align=\"center\">Profit</td>\n");
        s.append("              <td width=\"15%\" align=\"center\">Satisf</td>\n");
        s.append("              <td width=\"15%\" align=\"center\">Sustai</td>\n");
        s.append("            </tr>\n");
        s.append("          </thead>\n");
        s.append("          <tbody>\n");
        for (GameuserRecord gameUser : gameUserRecords) {
            s.append("            <tr>\n");
            s.append("              <td width=\"40%\" align=\"left\">");
            UserRecord user = SqlUtils.readUserFromUserId(data, gameUser.getUserId());
            if (gameUser.getId() == selectedGameuserRecordNr)
                s.append("              <span class=\"tg-admin-highlight\">\n");
            else
                s.append("              <span>\n");
            s.append("<a href=\"#\" onClick=\"clickRecordId('");
            s.append("resultGameUserDetail");
            s.append("',");
            s.append(gameUser.getId());
            s.append("); return false;\">");
            s.append(user.getUsername());
            s.append("</a></span>\n"); // tg-admin-line-field
            s.append("                </span>\n"); // tg-admin-line
            s.append("</td>\n");
            s.append("              <td width=\"15%\" align=\"center\">");
            s.append(userHasPlayed(data, gameUser) ? "Y" : "N");
            s.append("</td>\n");
            s.append("              <td width=\"15%\" align=\"center\">");
            s.append(gameUser.getScoreprofit());
            s.append("</td>\n");
            s.append("              <td width=\"15%\" align=\"center\">");
            s.append(gameUser.getScoresatisfaction());
            s.append("</td>\n");
            s.append("              <td width=\"15%\" align=\"center\">");
            s.append(gameUser.getScoresustainability());
            s.append("</td>\n");
            s.append("            </tr>\n");
        }
        s.append("          </tbody>\n");
        s.append("        </table>\n");
        s.append("      </div>");
        s.append(AdminTable.endTable());

        data.getColumn(2).setSelectedRecordNr(selectedGameuserRecordNr);
        data.getColumn(2).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** DETAILED SCORE ************************************************ */
    /* ********************************************************************************************************* */

    private static void showDetailedScore(HttpSession session, AdminData data, int gameUserId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameuserRecord gameUser = dslContext.selectFrom(Tables.GAMEUSER).where(Tables.GAMEUSER.ID.eq(gameUserId))
                .fetchAny();
        UserRecord user = SqlUtils.readUserFromUserId(data, gameUser.getUserId());
        GameplayRecord gamePlay = dslContext.selectFrom(Tables.GAMEPLAY)
                .where(Tables.GAMEPLAY.ID.eq(gameUser.getGameplayId())).fetchAny();
        GameRecord game = SqlUtils.readGameFromGameId(data, gamePlay.getGameId());
        MissionRecord mission = dslContext.selectFrom(Tables.MISSION).where(Tables.MISSION.GAME_ID.eq(game.getId()))
                .fetchAny();
        List<UsercarrierRecord> userCarriers = dslContext.selectFrom(Tables.USERCARRIER)
                .where(Tables.USERCARRIER.GAMEUSER_ID.eq(gameUser.getId())).fetch(); // bought reports

        // rounds
        List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                .where(Tables.ROUND.GAME_ID.eq(game.getId())).fetch().sortAsc(Tables.ROUND.ROUNDNUMBER);
        SortedMap<Integer, RoundRecord> roundMap = new TreeMap<>();
        for (RoundRecord roundRecord : roundRecords) {
            roundMap.put(roundRecord.getRoundnumber(), roundRecord);
        }

        // orders
        SortedMap<Integer, List<OrderRecord>> orderMap = new TreeMap<>();
        for (RoundRecord roundRecord : roundRecords) {
            List<OrderRecord> orderList = dslContext.selectFrom(Tables.ORDER)
                    .where(Tables.ORDER.ROUND_ID.eq(roundRecord.getId())).fetch().sortAsc(Tables.ORDER.ORDERNUMBER);
            orderMap.put(roundRecord.getRoundnumber(), orderList);
        }

        // highest rounds number
        int highestRoundNumber = gameUser.getRoundnumber().intValue();

        StringBuilder s = new StringBuilder();
        s.append("\n<div class=\"tg-detail-score\">\n");
        s.append("  <table width=\"100%\">\n");
        s.append("    <thead><tr><td>Round</td><td>Order</td><td>Chosen Carrier</td>"
                + "<td>Profit</td><td>Satisf</td><td>Sustai</td></tr></thead>\n");
        s.append("    <tbody>\n");

        s.append("           <tr><td>Start</td><td>&nbsp;</td><td>&nbsp;</td><td>");
        s.append(mission.getStartprofit());
        s.append("</td><td>");
        s.append(mission.getStartsatisfaction());
        s.append("</td><td>");
        s.append(mission.getStartsustainability());
        s.append("</td></tr>\n");

        for (int round = 1; round < highestRoundNumber; round++) {
            UserroundRecord userRound = dslContext.selectFrom(Tables.USERROUND)
                    .where(Tables.USERROUND.ROUND_ID.eq(roundMap.get(round).getId())
                            .and(Tables.USERROUND.GAMEPLAY_ID.eq(gamePlay.getId()))
                            .and(Tables.USERROUND.GAMEUSER_ID.eq(gameUser.getId())))
                    .fetchAny();

            // bought reports
            for (UsercarrierRecord userCarrier : userCarriers) {
                if (userCarrier.getRoundnumber() == round) {
                    CarrierRecord carrier = dslContext.selectFrom(Tables.CARRIER)
                            .where(Tables.CARRIER.ID.eq(userCarrier.getCarrierId())).fetchAny();
                    s.append("           <tr><td>");
                    s.append(round);
                    s.append("</td><td>");
                    s.append("Report");
                    s.append("</td><td>");
                    s.append(carrier == null ? "-" : carrier.getName());
                    s.append("</td><td>");
                    s.append("-5");
                    s.append("</td><td>");
                    s.append("-");
                    s.append("</td><td>");
                    s.append("-");
                    s.append("</td></tr>\n");
                }
            }

            // orders and their scores
            for (OrderRecord order : orderMap.get(round)) {
                UserorderRecord userOrder = userRound == null ? null
                        : dslContext.selectFrom(Tables.USERORDER).where(Tables.USERORDER.ORDER_ID.eq(order.getId())
                                .and(Tables.USERORDER.USERROUND_ID.eq(userRound.getId()))).fetchAny();
                SelectedcarrierRecord selectedCarrier = userOrder == null ? null
                        : dslContext.selectFrom(Tables.SELECTEDCARRIER)
                                .where(Tables.SELECTEDCARRIER.USERORDER_ID.eq(userOrder.getId())).fetchAny();
                OrdercarrierRecord orderCarrier = selectedCarrier == null ? null
                        : dslContext.selectFrom(Tables.ORDERCARRIER)
                                .where(Tables.ORDERCARRIER.ID.eq(selectedCarrier.getOrdercarrierId())).fetchAny();
                CarrierRecord carrier = selectedCarrier == null ? null
                        : dslContext.selectFrom(Tables.CARRIER).where(Tables.CARRIER.ID.eq(orderCarrier.getCarrierId()))
                                .fetchAny();
                boolean practice = roundMap.get(round).getTestround() != 0;
                s.append("           <tr><td>");
                s.append(round);
                if (practice)
                    s.append(" (Practice)");
                s.append("</td><td>");
                s.append(order.getOrdernumber());
                s.append("</td><td>");
                s.append(carrier == null ? "-" : carrier.getName());
                s.append("</td><td>");
                if (order == null || orderCarrier == null)
                    s.append("-");
                else {
                    int profit = order.getTransportearnings() - orderCarrier.getQuoteoffer()
                            + orderCarrier.getExtraprofit();
                    if (practice)
                        s.append("(" + profit + ")");
                    else
                        s.append(profit);
                }
                s.append("</td><td>");
                String satis = orderCarrier == null ? "-" : Integer.toString(orderCarrier.getOutcomesatisfaction());
                if (practice)
                    s.append("(" + satis + ")");
                else
                    s.append(satis);
                s.append("</td><td>");
                String sust = orderCarrier == null ? "-" : Integer.toString(orderCarrier.getOutcomesustainability());
                if (practice)
                    s.append("(" + sust + ")");
                else
                    s.append(sust);
                s.append("</td></tr>\n");
            }
        }

        s.append("           <tr><td>Total</td><td>&nbsp;</td><td>&nbsp;</td><td>");
        s.append(gameUser.getScoreprofit());
        s.append("</td><td>");
        s.append(gameUser.getScoresatisfaction());
        s.append("</td><td>");
        s.append(gameUser.getScoresustainability());
        s.append("</td></tr>\n");

        s.append("    </tbody>\n");
        s.append("  </table>\n");
        s.append("</div>\n"); // tg-detail-score

        data.getColumn(3).setHeader("Detailed Scores for " + user.getUsername());
        data.getColumn(3).setContent(s.toString());
    }

    private static boolean userHasPlayed(AdminData data, GameuserRecord gameUser) {
        if (gameUser.getRoundnumber().intValue() > 1 || gameUser.getRoundstatus() != 0)
            return true;
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserclickRecord userClick = dslContext.selectFrom(Tables.USERCLICK)
                .where(Tables.USERCLICK.GAMEUSER_ID.eq(gameUser.getId())).fetchAny(); // clicks
        if (userClick != null)
            return true;
        UserroundRecord userRound = dslContext.selectFrom(Tables.USERROUND)
                .where(Tables.USERROUND.GAMEUSER_ID.eq(gameUser.getId())).fetchAny(); // rounds
        if (userRound != null)
            return true;
        UsercarrierRecord userCarrier = dslContext.selectFrom(Tables.USERCARRIER)
                .where(Tables.USERCARRIER.GAMEUSER_ID.eq(gameUser.getId())).fetchAny(); // bought reports
        if (userCarrier != null)
            return true;
        return false;
    }

    private static void downloadResults(HttpServletResponse response, AdminData data, boolean tab, int gamePlayId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameuserRecord> gameUserRecords = dslContext.selectFrom(Tables.GAMEUSER)
                .where(Tables.GAMEUSER.GAMEPLAY_ID.eq(gamePlayId)).fetch();
        GameplayRecord gamePlay = dslContext.selectFrom(Tables.GAMEPLAY).where(Tables.GAMEPLAY.ID.eq(gamePlayId))
                .fetchAny();
        GameRecord game = SqlUtils.readGameFromGameId(data, gamePlay.getGameId());
        MissionRecord mission = dslContext.selectFrom(Tables.MISSION).where(Tables.MISSION.GAME_ID.eq(game.getId()))
                .fetchAny();

        // rounds
        List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                .where(Tables.ROUND.GAME_ID.eq(game.getId())).fetch().sortAsc(Tables.ROUND.ROUNDNUMBER);
        SortedMap<Integer, RoundRecord> roundMap = new TreeMap<>();
        for (RoundRecord roundRecord : roundRecords) {
            roundMap.put(roundRecord.getRoundnumber(), roundRecord);
        }

        // orders
        SortedMap<Integer, List<OrderRecord>> orderMap = new TreeMap<>();
        for (RoundRecord roundRecord : roundRecords) {
            List<OrderRecord> orderList = dslContext.selectFrom(Tables.ORDER)
                    .where(Tables.ORDER.ROUND_ID.eq(roundRecord.getId())).fetch().sortAsc(Tables.ORDER.ORDERNUMBER);
            orderMap.put(roundRecord.getRoundnumber(), orderList);
        }

        try {
            File tempFile = File.createTempFile("trustgame-", tab ? ".xls" : ".csv");
            tempFile.deleteOnExit();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                bw.write(csvHeader(tab));
                for (GameuserRecord gameUser : gameUserRecords) {
                    List<UsercarrierRecord> userCarriers = dslContext.selectFrom(Tables.USERCARRIER)
                            .where(Tables.USERCARRIER.GAMEUSER_ID.eq(gameUser.getId())).fetch(); // bought reports
                    UserRecord user = dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(gameUser.getUserId()))
                            .fetchAny();
                    boolean hasPlayed = userHasPlayed(data, gameUser);

                    // highest rounds number for user
                    int highestRoundNumber = gameUser.getRoundnumber().intValue();

                    bw.write(csvLine(tab, gamePlay, gameUser.getId(), user.getUsercode(), user.getUsername(), hasPlayed,
                            "Start", false, "", "", mission.getStartprofit(), mission.getStartsatisfaction(),
                            mission.getStartsustainability()));

                    for (int round = 1; round < highestRoundNumber; round++) {
                        UserroundRecord userRound = dslContext.selectFrom(Tables.USERROUND)
                                .where(Tables.USERROUND.ROUND_ID.eq(roundMap.get(round).getId())
                                        .and(Tables.USERROUND.GAMEPLAY_ID.eq(gamePlay.getId()))
                                        .and(Tables.USERROUND.GAMEUSER_ID.eq(gameUser.getId())))
                                .fetchAny();

                        // bought reports
                        for (UsercarrierRecord userCarrier : userCarriers) {
                            if (userCarrier.getRoundnumber() == round) {
                                CarrierRecord carrier = dslContext.selectFrom(Tables.CARRIER)
                                        .where(Tables.CARRIER.ID.eq(userCarrier.getCarrierId())).fetchAny();
                                bw.write(csvLine(tab, gamePlay, gameUser.getId(), user.getUsercode(),
                                        user.getUsername(), hasPlayed, Integer.toString(round), true, "",
                                        carrier.getName(), -5, 0, 0));
                            }
                        }

                        // scores per order
                        for (OrderRecord order : orderMap.get(round)) {
                            UserorderRecord userOrder = userRound == null ? null
                                    : dslContext.selectFrom(Tables.USERORDER)
                                            .where(Tables.USERORDER.ORDER_ID.eq(order.getId())
                                                    .and(Tables.USERORDER.USERROUND_ID.eq(userRound.getId())))
                                            .fetchAny();
                            SelectedcarrierRecord selectedCarrier = userOrder == null ? null
                                    : dslContext.selectFrom(Tables.SELECTEDCARRIER)
                                            .where(Tables.SELECTEDCARRIER.USERORDER_ID.eq(userOrder.getId()))
                                            .fetchAny();
                            OrdercarrierRecord orderCarrier = selectedCarrier == null ? null
                                    : dslContext.selectFrom(Tables.ORDERCARRIER)
                                            .where(Tables.ORDERCARRIER.ID.eq(selectedCarrier.getOrdercarrierId()))
                                            .fetchAny();
                            CarrierRecord carrier = selectedCarrier == null ? null
                                    : dslContext.selectFrom(Tables.CARRIER)
                                            .where(Tables.CARRIER.ID.eq(orderCarrier.getCarrierId())).fetchAny();

                            if (order != null && orderCarrier != null && carrier != null) {
                                int profit = order.getTransportearnings() - orderCarrier.getQuoteoffer()
                                        + orderCarrier.getExtraprofit();
                                bw.write(csvLine(tab, gamePlay, gameUser.getId(), user.getUsercode(),
                                        user.getUsername(), hasPlayed, Integer.toString(round), false,
                                        Integer.toString(order.getOrdernumber().intValue()), carrier.getName(), profit,
                                        orderCarrier.getOutcomesatisfaction(),
                                        orderCarrier.getOutcomesustainability()));
                            }

                        }
                    }

                    bw.write(csvLine(tab, gamePlay, gameUser.getId(), user.getUsercode(), user.getUsername(), hasPlayed,
                            "Total", false, "", "", gameUser.getScoreprofit(), gameUser.getScoresatisfaction(),
                            gameUser.getScoresustainability()));
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
                    "attachment; filename=" + "GameResultData_" + gamePlay.getId() + "_" + date + ext);

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

    private static String csvHeader(boolean tab) {
        StringBuilder s = new StringBuilder();
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
        s.append("played");
        s.append(sep);
        s.append("round");
        s.append(sep);
        s.append("boughtReport");
        s.append(sep);
        s.append("order");
        s.append(sep);
        s.append("carrier");
        s.append(sep);
        s.append("profit");
        s.append(sep);
        s.append("satisfaction");
        s.append(sep);
        s.append("sustainability");
        s.append("\n");
        return s.toString();
    }

    private static String csvLine(boolean tab, GameplayRecord gamePlay, int gameUserNr, String userCode,
            String userName, boolean played, String roundNr, boolean boughtReport, String orderNr, String carrierName,
            int profit, int satisfaction, int sustainability) {
        StringBuilder s = new StringBuilder();
        String sep = tab ? "\t" : ",";
        s.append(gamePlay.getGameId());
        s.append(sep);
        s.append(gamePlay.getId());
        s.append(sep);
        s.append(gameUserNr);
        s.append(sep);
        s.append(csvString(userCode));
        s.append(sep);
        s.append(csvString(userName));
        s.append(sep);
        s.append(played ? "\"Y\"" : "\"N\"");
        s.append(sep);
        s.append(csvString(roundNr));
        s.append(sep);
        s.append(boughtReport ? "\"Y\"" : "\"N\"");
        s.append(sep);
        s.append(csvString(orderNr));
        s.append(sep);
        s.append(csvString(carrierName));
        s.append(sep);
        s.append(profit);
        s.append(sep);
        s.append(satisfaction);
        s.append(sep);
        s.append(sustainability);
        s.append("\n");
        return s.toString();
    }

    private static String csvString(String s) {
        String result = s;
        result.replaceAll("\"", "\"\"");
        return "\"" + result + "\"";
    }
}
