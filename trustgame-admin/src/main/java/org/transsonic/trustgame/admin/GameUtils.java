package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryBoolean;
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
            data.clearColumns("20%", "Game", "10%", "Round", "10%", "Order", "20%", "Carrier");
            data.clearFormColumn("40%", "Edit Properties");
            showGames(session, data, true, 0); // view the list of games, no highlight
            break;
        }

        case "viewGame": {
            showGames(session, data, true, recordNr); // view the list of games, highlight viewed game
            editGame(session, data, recordNr, false); // view the selected game
            showRounds(session, data, true, 0); // allow editing of rounds for that game, no highlight
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        case "editGame": {
            showGames(session, data, true, recordNr); // view the list of games, highlight edited game
            editGame(session, data, recordNr, true); // edit the selected game
            showRounds(session, data, true, 0); // view the rounds belonging to that game, no highlight
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        case "saveGame": {
            recordNr = saveGame(request, data, recordNr); // save the edited game
            showGames(session, data, true, recordNr); // show the games for editing, highlighting the saved one
            showRounds(session, data, true, 0); // allow editing of rounds for that game, no highlight
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "deleteGame": {
            GameRecord game = SqlUtils.readGameFromGameId(data, recordNr);
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Game", "<p>Delete game " + game.getName() + "?</p>",
                    "DELETE", "clickRecordId('deleteGameOk', " + recordNr + ")", "Cancel", "clickMenu('game')",
                    "clickMenu('game')");
            data.setShowModalWindow(1);
            showGames(session, data, true, 0);
            data.resetColumn(1);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "deleteGameOk": {
            GameRecord game = SqlUtils.readGameFromGameId(data, recordNr);
            try {
                game.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
            }
            showGames(session, data, true, 0);
            data.resetColumn(1);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "newGame": {
            showGames(session, data, true, 0); // view the list of games, no highlight
            editGame(session, data, 0, true); // edit the new game
            data.resetColumn(1); // no rounds visible until save
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        // Round

        case "showRounds": {
            showGames(session, data, true, recordNr); // view the list of games for editing, highlight selected one
            if (recordNr == 0)
                data.resetColumn(1); // to solve 'cancel' for new round
            else
                showRounds(session, data, true, 0); // allow editing of rounds for the selected game, no highlight
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "viewRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, recordNr);
            editRound(session, data, recordNr, false);
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            break;
        }

        case "editRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, recordNr);
            editRound(session, data, recordNr, true);
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            break;
        }

        case "saveRound": {
            recordNr = saveRound(request, data, recordNr);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, recordNr);
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "deleteRound": {
            RoundRecord round = SqlUtils.readRoundFromRoundId(data, recordNr);
            GameRecord game = SqlUtils.readGameFromGameId(data, round.getGameId());
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Round",
                    "<p>Delete round " + round.getRoundnumber() + " in game " + game.getName() + "?</p>", "DELETE",
                    "clickRecordId('deleteRoundOk', " + recordNr + ")", "Cancel", "clickMenu('game')",
                    "clickMenu('game')");
            data.setShowModalWindow(1);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "deleteRoundOk": {
            RoundRecord round = SqlUtils.readRoundFromRoundId(data, recordNr);
            try {
                round.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
            }
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, 0);
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "newRound": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, recordNr);
            editRound(session, data, 0, true);
            data.resetColumn(2);
            data.resetColumn(3);
            break;
        }

        // Order

        case "showOrders": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, recordNr);
            if (recordNr == 0)
                data.resetColumn(2); // to solve 'cancel' for new order
            else
                showOrders(session, data, true, 0);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "viewOrder": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, recordNr);
            editOrder(session, data, recordNr, false);
            showOrderCarriers(session, data, true, 0);
            break;
        }

        case "editOrder": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, recordNr);
            editOrder(session, data, recordNr, true);
            showOrderCarriers(session, data, true, 0);
            break;
        }

        case "saveOrder": {
            recordNr = saveOrder(request, data, recordNr);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, recordNr);
            showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteOrder": {
            OrderRecord order = SqlUtils.readOrderFromOrderId(data, recordNr);
            RoundRecord round = SqlUtils.readRoundFromRoundId(data, order.getRoundId());
            GameRecord game = SqlUtils.readGameFromGameId(data, round.getGameId());
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete Order",
                    "<p>Delete order " + order.getOrdernumber() + " for round " + round.getRoundnumber() + " in game "
                            + game.getName() + "?</p>",
                    "DELETE", "clickRecordId('deleteOrderOk', " + recordNr + ")", "Cancel", "clickMenu('game')",
                    "clickMenu('game')");
            data.setShowModalWindow(1);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "deleteOrderOk": {
            OrderRecord order = SqlUtils.readOrderFromOrderId(data, recordNr);
            try {
                order.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
            }
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, 0);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        case "newOrder": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, 0);
            editOrder(session, data, 0, true);
            data.resetColumn(3);
            break;
        }

        // OrderCarrier

        case "showOrderCarriers": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, recordNr);
            if (recordNr == 0)
                data.resetColumn(3); // to solve 'cancel' for new order
            else
                showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "viewOrderCarrier": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, recordNr);
            editOrderCarrier(session, data, recordNr, false);
            break;
        }

        case "editOrderCarrier": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, recordNr);
            editOrderCarrier(session, data, recordNr, true);
            break;
        }

        case "saveOrderCarrier": {
            recordNr = saveOrderCarrier(request, data, recordNr);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteOrderCarrier": {
            OrdercarrierRecord orderCarrier = SqlUtils.readOrderCarrierFromOrderCarrierId(data, recordNr);
            OrderRecord order = SqlUtils.readOrderFromOrderId(data, orderCarrier.getOrderId());
            RoundRecord round = SqlUtils.readRoundFromRoundId(data, order.getRoundId());
            GameRecord game = SqlUtils.readGameFromGameId(data, round.getGameId());
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, orderCarrier.getCarrierId());
            ModalWindowUtils.make2ButtonModalWindow(data, "Delete OrderCarrier",
                    "<p>Delete OrderCarrier " + carrier.getName() + "<br>for order " + order.getOrdernumber()
                            + " for round " + round.getRoundnumber() + "<br>in game " + game.getName() + "?</p>",
                    "DELETE", "clickRecordId('deleteOrderCarrierOk', " + recordNr + ")", "Cancel", "clickMenu('game')",
                    "clickMenu('game')");
            data.setShowModalWindow(1);
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "deleteOrderCarrierOk": {
            OrdercarrierRecord orderCarrier = SqlUtils.readOrderCarrierFromOrderCarrierId(data, recordNr);
            try {
                orderCarrier.delete();
            } catch (Exception exception) {
                ModalWindowUtils.popup(data, "Error deleting record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('game')");
            }
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newOrderCarrier": {
            showGames(session, data, true, data.getColumn(0).getSelectedRecordNr());
            showRounds(session, data, true, data.getColumn(1).getSelectedRecordNr());
            showOrders(session, data, true, data.getColumn(2).getSelectedRecordNr());
            showOrderCarriers(session, data, true, recordNr);
            editOrderCarrier(session, data, 0, true);
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

    public static void showGames(HttpSession session, AdminData data, boolean editButton, int selectedGameRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();

        s.append(AdminTable.startTable());
        for (GameRecord game : gameRecords) {
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr, game.getName(), "viewGame");
            if (editButton) {
                tableRow.addButton("Rounds", "showRounds");
                tableRow.addButton("Edit", "editGame");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Game", "newGame"));

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
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
                .setDeleteMethod("deleteGame", "Delete", "<br>Note: Game can only be deleted when it is not used " 
                    + "<br>in a GamePlay, and when it has no associated rounds")
                .setRecordNr(gameId)
                .startForm()
                .addEntry(new FormEntryString(Tables.GAME.NAME)
                        .setRequired()
                        .setInitialValue(game.getName())
                        .setLabel("Game name")
                        .setMaxChars(45))
                .addEntry(new FormEntryPickRecord(Tables.GAME.ORGANIZATION_ID)
                        .setInitialValue(game.getOrganizationId() == null ? 0 : game.getOrganizationId())
                        .setLabel("Mission")
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

    public static void showRounds(HttpSession session, AdminData data, boolean editButton, int selectedRoundRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                .where(Tables.ROUND.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (RoundRecord round : roundRecords) {
            TableRow tableRow = new TableRow(round.getId(), selectedRoundRecordNr, "Round " + round.getRoundnumber(),
                    "viewRound");
            if (editButton) {
                tableRow.addButton("Orders", "showOrders");
                tableRow.addButton("Edit", "editRound");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Round", "newRound"));

        data.getColumn(1).setSelectedRecordNr(selectedRoundRecordNr);
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
                .setDeleteMethod("deleteRound", "Delete", "<br>Note: Round can only be deleted when it is has not been" 
                        + "<br> used in a GamePlay, and when it has no associated orders")
                .setRecordNr(roundId)
                .startForm()
                .addEntry(new FormEntryInt(Tables.ROUND.ROUNDNUMBER)
                        .setRequired()
                        .setInitialValue(round.getRoundnumber())
                        .setLabel("Round number")
                        .setMin(1)
                        .setMax(99))
                .addEntry(new FormEntryBoolean(Tables.ROUND.TESTROUND)
                        .setRequired()
                        .setInitialValue(round.getTestround())
                        .setLabel("Practice round?"))
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

    public static void showOrders(HttpSession session, AdminData data, boolean editButton, int selectedOrderRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<OrderRecord> orderRecords = dslContext.selectFrom(Tables.ORDER)
                .where(Tables.ORDER.ROUND_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (OrderRecord order : orderRecords) {
            TableRow tableRow = new TableRow(order.getId(), selectedOrderRecordNr, "Order #" + order.getOrdernumber(),
                    "viewOrder");
            if (editButton) {
                tableRow.addButton("Carriers", "showOrderCarriers");
                tableRow.addButton("Edit", "editOrder");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Order", "newOrder"));

        data.getColumn(2).setSelectedRecordNr(selectedOrderRecordNr);
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
                .setDeleteMethod("deleteOrder", "Delete", "<br>Note: Order can only be deleted when it is has not been" 
                        + "<br> used in a GamePlay, and when it has no associated OrderCarriers")
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
            int selectedOrderCarrierRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<OrdercarrierRecord> orderCarrierRecords = dslContext.selectFrom(Tables.ORDERCARRIER)
                .where(Tables.ORDERCARRIER.ORDER_ID.eq(data.getColumn(2).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (OrdercarrierRecord orderCarrier : orderCarrierRecords) {
            TableRow tableRow = new TableRow(orderCarrier.getId(), selectedOrderCarrierRecordNr,
                    makeCarrierString(data, orderCarrier), "viewOrderCarrier");
            if (editButton)
                tableRow.addButton("Edit", "editOrderCarrier");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New OrderCarrier", "newOrderCarrier"));

        data.getColumn(3).setSelectedRecordNr(selectedOrderCarrierRecordNr);
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
                .setDeleteMethod("deleteOrderCarrier", "Delete", "<br>Note: OrderCarrier can only be deleted when it is has" 
                        + "<br>not been used in a GamePlay")
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
