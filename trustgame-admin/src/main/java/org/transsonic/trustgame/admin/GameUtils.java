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
import org.transsonic.trustgame.admin.form.FormEntryPickRecord;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.admin.form.FormEntryUInt;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrderRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrdercarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;

public class GameUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);
        switch (click) {
        
        // Game
        
        case "game": {
            data.setMenuChoice(2);
            data.clearColumns("20%", "Game", "10%", "Round", "10%", "Order", "20%", "Carrier");
            data.clearFormColumn("40%", "Edit Properties");
            showGames(session, data, true, 0);
            break;
        }

        case "viewGame": {
            showGames(session, data, false, recordNr);
            editGame(session, data, recordNr, false);
            showRounds(session, data, false, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        case "editGame": {
            showGames(session, data, false, recordNr);
            editGame(session, data, recordNr, true);
            showRounds(session, data, false, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        case "saveGame": {
            recordNr = saveGame(request, data, recordNr);
            showGames(session, data, true, recordNr);
            showRounds(session, data, false, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "newGame": {
            showGames(session, data, false, 0);
            editGame(session, data, 0, true);
            data.resetColumn(1);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        // Round
        
        case "showRounds": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "viewRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, false, data.getColumn(0).getSelectedRecordNr());
            editRound(session, data, recordNr, false);
            showOrders(session, data, false, 0);
            data.resetColumn(3);
            break;
        }

        case "editRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, false, data.getColumn(0).getSelectedRecordNr());
            editRound(session, data, recordNr, true);
            showOrders(session, data, false, 0);
            data.resetColumn(3);
            break;
        }

        case "saveRound": {
            recordNr = saveRound(request, data, data.getColumn(0).getSelectedRecordNr());
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "newRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, false, data.getColumn(0).getSelectedRecordNr());
            editRound(session, data, 0, true);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        // Order
        
        case "showOrders": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "viewOrder": {
            showGames(session, data, false, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, false, data.getColumn(1).getSelectedRecordNr());
            editOrder(session, data, recordNr, false);
            showOrderCarriers(session, data, false, 0);
            break;
        }

        case "editOrder": {
            showGames(session, data, false, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, false, data.getColumn(1).getSelectedRecordNr());
            editOrder(session, data, recordNr, true);
            showOrderCarriers(session, data, false, 0);
            break;
        }

        case "saveOrder": {
            recordNr = saveOrder(request, data, recordNr);
            showOrders(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newOrder": {
            showOrders(session, data, false, 0);
            editOrder(session, data, 0, true);
            break;
        }

        // OrderCarrier
        
        case "viewOrderCarrier": {
            showOrderCarriers(session, data, false, recordNr);
            editOrderCarrier(session, data, recordNr, false);
            break;
        }

        case "editOrderCarrier": {
            showOrderCarriers(session, data, false, recordNr);
            editOrderCarrier(session, data, recordNr, true);
            break;
        }

        case "saveOrderCarrier": {
            recordNr = saveOrderCarrier(request, data, recordNr);
            showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newOrderCarrier": {
            showOrderCarriers(session, data, false, 0);
            editOrderCarrier(session, data, 0, true);
            break;
        }


        default:
            break;
        }

        AdminServlet.makeColumnContent(5, data);
    }

    public static void showGames(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedRecordNr, game.getName(), "viewGame");
            if (editButton) {
                tableRow.addButton("Rounds", "showRounds");
                tableRow.addButton("Edit", "editGame");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Game", "newGame"));

        data.getColumn(0).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    public static void editGame(HttpSession session, AdminData data, int gameId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameRecord game = gameId == 0 ? dslContext.newRecord(Tables.GAME)
                : dslContext.selectFrom(Tables.GAME).where(Tables.GAME.ID.eq(gameId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("game")
                .setEditMethod("editGame")
                .setSaveMethod("saveGame")
                .setRecordNr(gameId)
                .startForm()
                .addEntry(new FormEntryString(Tables.GAME.NAME)
                        .setRequired()
                        .setInitialValue(game.getName())
                        .setLabel("Game name")
                        .setMaxChars(45))
                .addEntry(new FormEntryPickRecord(Tables.GAME.ORGANIZATION_ID)
                        .setInitialValue(game.getOrganizationId() == null ? 0 : game.getOrganizationId())
                        .setLabel("Player Organization")
                        .setRequired()
                        .setPickTable(data, Tables.PLAYERORGANIZATION, Tables.PLAYERORGANIZATION.ID, Tables.PLAYERORGANIZATION.NAME))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Game", form);
    }

    public static int saveGame(HttpServletRequest request, AdminData data, int gameId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameRecord game = gameId == 0 ? dslContext.newRecord(Tables.GAME)
                : dslContext.selectFrom(Tables.GAME).where(Tables.GAME.ID.eq(gameId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(game, request, data);
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('game')");
            return -1;
        } else {
            try {
                game.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
                return -1;
            }
        }
        return game.getId();
    }

    /* ********************************************************************************************************* */
    /* ***************************************** ROUND ********************************************************* */
    /* ********************************************************************************************************* */

    public static void showRounds(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                .where(Tables.ROUND.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (RoundRecord round : roundRecords) {
            TableRow tableRow = new TableRow(round.getId(), selectedRecordNr, "Round " + round.getRoundnumber(),
                    "viewRound");
            if (editButton)
                tableRow.addButton("Edit", "editRound");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Round", "newRound"));

        data.getColumn(1).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    public static void editRound(HttpSession session, AdminData data, int roundId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        RoundRecord round = roundId == 0 ? dslContext.newRecord(Tables.ROUND)
                : dslContext.selectFrom(Tables.ROUND).where(Tables.ROUND.ID.eq(roundId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("game", data.getColumn(0).getSelectedRecordNr())
                .setEditMethod("editRound")
                .setSaveMethod("saveRound")
                .setRecordNr(roundId)
                .startForm()
                .addEntry(new FormEntryInt(Tables.ROUND.ROUNDNUMBER)
                        .setRequired()
                        .setInitialValue(round.getRoundnumber())
                        .setLabel("Round number")
                        .setMin(1)
                        .setMax(99))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Round", form);
    }

    public static int saveRound(HttpServletRequest request, AdminData data, int roundId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        RoundRecord round = roundId == 0 ? dslContext.newRecord(Tables.ROUND)
                : dslContext.selectFrom(Tables.ROUND).where(Tables.ROUND.ID.eq(roundId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(round, request, data);
        round.setGameId(data.getColumn(0).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('game')");
            return -1;
        } else {
            try {
                round.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
                return -1;
            }
        }
        return round.getId();
    }

    /* ********************************************************************************************************* */
    /* ***************************************** ORDER ********************************************************* */
    /* ********************************************************************************************************* */

    public static void showOrders(HttpSession session, AdminData data, boolean editButton, int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<OrderRecord> orderRecords = dslContext.selectFrom(Tables.ORDER)
                .where(Tables.ORDER.ROUND_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (OrderRecord order : orderRecords) {
            TableRow tableRow = new TableRow(order.getId(), selectedRecordNr, "Order #" + order.getOrdernumber(),
                    "viewOrder");
            if (editButton)
                tableRow.addButton("Edit", "editOrder");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Order", "newOrder"));

        data.getColumn(2).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(2).setContent(s.toString());
    }

    public static void editOrder(HttpSession session, AdminData data, int orderId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        OrderRecord order = orderId == 0 ? dslContext.newRecord(Tables.ORDER)
                : dslContext.selectFrom(Tables.ORDER).where(Tables.ORDER.ID.eq(orderId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("viewRound", data.getColumn(1).getSelectedRecordNr())
                .setEditMethod("editOrder")
                .setSaveMethod("saveOrder")
                .setRecordNr(orderId)
                .startForm()
                .addEntry(new FormEntryUInt(Tables.ORDER.ORDERNUMBER)
                        .setRequired()
                        .setInitialValue(order.getOrdernumber())
                        .setLabel("Order number")
                        .setMin(1)
                        .setMax(999))
                .addEntry(new FormEntryPickRecord(Tables.ORDER.CLIENT_ID)
                        .setInitialValue(order.getClientId() == null ? 0 : order.getClientId())
                        .setLabel("Client")
                        .setRequired()
                        .setPickTable(data, Tables.CLIENT, Tables.CLIENT.ID, Tables.CLIENT.NAME))
                .addEntry(new FormEntryText(Tables.ORDER.DESCRIPTION)
                        .setInitialValue(order.getDescription())
                        .setLabel("Description"))
                .addEntry(new FormEntryInt(Tables.ORDER.TRANSPORTEARNINGS)
                        .setRequired()
                        .setInitialValue(order.getTransportearnings())
                        .setLabel("Transport earnings"))
                .addEntry(new FormEntryString(Tables.ORDER.NOTE)
                        .setInitialValue(order.getNote())
                        .setLabel("Note (e.g., repeating)")
                        .setMaxChars(45))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Order", form);
    }

    public static int saveOrder(HttpServletRequest request, AdminData data, int orderId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        OrderRecord order = orderId == 0 ? dslContext.newRecord(Tables.ORDER)
                : dslContext.selectFrom(Tables.ORDER).where(Tables.ORDER.ID.eq(orderId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(order, request, data);
        order.setRoundId(data.getColumn(1).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('game')");
            return -1;
        } else {
            try {
                order.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
                return -1;
            }
        }
        return order.getId();
    }

    /* ********************************************************************************************************* */
    /* ***************************************** ORDER CARRIER ************************************************* */
    /* ********************************************************************************************************* */

    public static void showOrderCarriers(HttpSession session, AdminData data, boolean editButton,
            int selectedRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<OrdercarrierRecord> orderCarrierRecords = dslContext.selectFrom(Tables.ORDERCARRIER)
                .where(Tables.ORDERCARRIER.ORDER_ID.eq(data.getColumn(2).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (OrdercarrierRecord orderCarrier : orderCarrierRecords) {
            TableRow tableRow = new TableRow(orderCarrier.getId(), selectedRecordNr,
                    makeCarrierString(data, orderCarrier), "viewOrderCarrier");
            if (editButton)
                tableRow.addButton("Edit", "editOrderCarrier");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New OrderCarrier", "newOrderCarrier"));

        data.getColumn(3).setSelectedRecordNr(selectedRecordNr);
        data.getColumn(3).setContent(s.toString());
    }

    public static void editOrderCarrier(HttpSession session, AdminData data, int orderCarrierId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        OrdercarrierRecord orderCarrier = orderCarrierId == 0 ? dslContext.newRecord(Tables.ORDERCARRIER)
                : dslContext.selectFrom(Tables.ORDERCARRIER).where(Tables.ORDERCARRIER.ID.eq(orderCarrierId))
                        .fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("viewRound", data.getColumn(1).getSelectedRecordNr())
                .setEditMethod("editOrderCarrier")
                .setSaveMethod("saveOrderCarrier")
                .setRecordNr(orderCarrierId)
                .startForm()
                .addEntry(new FormEntryPickRecord(Tables.ORDERCARRIER.CARRIER_ID)
                        .setInitialValue(orderCarrier.getCarrierId() == null ? 0 : orderCarrier.getCarrierId())
                        .setLabel("Carrier")
                        .setRequired()
                        .setPickTable(data, Tables.CARRIER, Tables.CARRIER.ID, Tables.CARRIER.NAME))
                .addEntry(new FormEntryInt(Tables.ORDERCARRIER.QUOTEOFFER)
                        .setRequired()
                        .setInitialValue(orderCarrier.getQuoteoffer())
                        .setLabel("Quote offer"))
                .addEntry(new FormEntryInt(Tables.ORDERCARRIER.EXTRAPROFIT)
                        .setRequired()
                        .setInitialValue(orderCarrier.getExtraprofit())
                        .setLabel("Extra profit (e.g., fine)"))
                .addEntry(new FormEntryInt(Tables.ORDERCARRIER.OUTCOMESATISFACTION)
                        .setRequired()
                        .setInitialValue(orderCarrier.getOutcomesatisfaction())
                        .setLabel("Outcome satisfaction"))
                .addEntry(new FormEntryInt(Tables.ORDERCARRIER.OUTCOMESUSTAINABILITY)
                        .setRequired()
                        .setInitialValue(orderCarrier.getOutcomesustainability())
                        .setLabel("Outcome sustainability"))
                .addEntry(new FormEntryText(Tables.ORDERCARRIER.OUTCOMEMESSAGE)
                        .setInitialValue(orderCarrier.getOutcomemessage())
                        .setLabel("Outcome message"))
                .addEntry(new FormEntryText(Tables.ORDERCARRIER.TRANSPORTMESSAGE)
                        .setInitialValue(orderCarrier.getTransportmessage())
                        .setLabel("Transport message"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit OrderCarrier", form);
    }

    public static int saveOrderCarrier(HttpServletRequest request, AdminData data, int orderCarrierId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        OrdercarrierRecord orderCarrier = orderCarrierId == 0 ? dslContext.newRecord(Tables.ORDERCARRIER)
                : dslContext.selectFrom(Tables.ORDERCARRIER).where(Tables.ORDERCARRIER.ID.eq(orderCarrierId))
                        .fetchOne();
        String errors = data.getFormColumn().getForm().setFields(orderCarrier, request, data);
        orderCarrier.setOrderId(data.getColumn(2).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('game')");
            return -1;
        } else {
            try {
                orderCarrier.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
                return -1;
            }
        }
        return orderCarrier.getId();
    }

    private static String makeCarrierString(AdminData data, OrdercarrierRecord orderCarrier) {
        String s = "";
        if (orderCarrier.getCarrierId() != null && orderCarrier.getCarrierId() != 0) {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, orderCarrier.getCarrierId());
            if (carrier != null)
                s += carrier.getName();
            else
                s += "-";
        } else
            s += "-";
        return s;
    }

}
