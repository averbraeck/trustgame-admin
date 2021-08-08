package org.transsonic.trustgame.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.admin.form.AdminForm;
import org.transsonic.trustgame.admin.form.FormEntryDouble;
import org.transsonic.trustgame.admin.form.FormEntryPickRecord;
import org.transsonic.trustgame.admin.form.FormEntryString;
import org.transsonic.trustgame.admin.form.FormEntryText;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierreviewRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.ReviewRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;

public class ReviewUtils {

    public static void handleMenu(HttpServletRequest request, String click, int recordNr) {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        switch (click) {

        // Game

        case "review": {
            data.clearColumns("20%", "Game", "10%", "Round", "20%", "Review", "20%", "OverallReview");
            data.clearFormColumn("30%", "Edit Properties");
            showGames(session, data, 0);
            break;
        }

        // Round

        case "showReviewRounds": {
            showGames(session, data, recordNr); // view the list of games for editing, highlight selected one
            if (recordNr == 0)
                data.resetColumn(1); // to solve 'cancel' for new round
            else
                showReviewRounds(session, data, 0); // allow editing of rounds for the selected game, no highlight
            data.resetColumn(2);
            data.resetColumn(3);
            data.resetFormColumn();
            break;
        }

        // Review

        case "showReviews": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, recordNr);
            if (recordNr == 0) {
                data.resetColumn(2); // to solve 'cancel' for new review
                data.resetColumn(3);
            } else {
                showReviews(session, data, true, 0);
                showCarrierReviews(session, data, true, 0);
            }
            data.resetFormColumn();
            break;
        }

        case "viewReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, recordNr);
            showCarrierReviews(session, data, true, 0);
            editReview(session, data, recordNr, false);
            break;
        }

        case "editReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, recordNr);
            showCarrierReviews(session, data, true, 0);
            editReview(session, data, recordNr, true);
            break;
        }

        case "saveReview": {
            recordNr = saveReview(request, data, recordNr);
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, recordNr);
            showCarrierReviews(session, data, true, 0);
            data.resetFormColumn();
            break;
        }

        case "newReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, 0);
            showCarrierReviews(session, data, true, 0);
            editReview(session, data, 0, true);
            break;
        }

        // CarrierReview

        case "viewCarrierReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, 0);
            showCarrierReviews(session, data, true, recordNr);
            editCarrierReview(session, data, recordNr, false);
            break;
        }

        case "editCarrierReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, 0);
            showCarrierReviews(session, data, true, recordNr);
            editCarrierReview(session, data, recordNr, true);
            break;
        }

        case "saveCarrierReview": {
            recordNr = saveCarrierReview(request, data, recordNr);
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, 0);
            showCarrierReviews(session, data, true, recordNr);
            data.resetFormColumn();
            break;
        }

        case "newCarrierReview": {
            showGames(session, data, data.getColumn(0).getSelectedRecordNr());
            showReviewRounds(session, data, data.getColumn(1).getSelectedRecordNr());
            showReviews(session, data, true, 0);
            showCarrierReviews(session, data, true, 0);
            editCarrierReview(session, data, 0, true);
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
            TableRow tableRow = new TableRow(game.getId(), selectedGameRecordNr, game.getName(), "showReviewRounds");
            tableRow.addButton("Rounds", "showReviewRounds");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(0).setSelectedRecordNr(selectedGameRecordNr);
        data.getColumn(0).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** ROUND ********************************************************* */
    /* ********************************************************************************************************* */

    public static void showReviewRounds(HttpSession session, AdminData data, int selectedRoundRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                .where(Tables.ROUND.GAME_ID.eq(data.getColumn(0).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (RoundRecord round : roundRecords) {
            TableRow tableRow = new TableRow(round.getId(), selectedRoundRecordNr, "Round " + round.getRoundnumber(),
                    "showReviews");
            tableRow.addButton("Reviews", "showReviews");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        data.getColumn(1).setSelectedRecordNr(selectedRoundRecordNr);
        data.getColumn(1).setContent(s.toString());
    }

    /* ********************************************************************************************************* */
    /* ***************************************** REVIEW ******************************************************** */
    /* ********************************************************************************************************* */

    public static void showReviews(HttpSession session, AdminData data, boolean editButton,
            int selectedReviewRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<ReviewRecord> reviewRecords = dslContext.selectFrom(Tables.REVIEW)
                .where(Tables.REVIEW.ROUND_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (ReviewRecord review : reviewRecords) {
            TableRow tableRow = new TableRow(review.getId(), selectedReviewRecordNr, makeCarrierString(data, review),
                    "viewReview");
            if (editButton) {
                tableRow.addButton("Edit", "editReview");
            }
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Review", "newReview"));

        data.getColumn(2).setSelectedRecordNr(selectedReviewRecordNr);
        data.getColumn(2).setContent(s.toString());
    }

    public static void editReview(HttpSession session, AdminData data, int reviewId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        ReviewRecord review = reviewId == 0 ? dslContext.newRecord(Tables.REVIEW)
                : dslContext.selectFrom(Tables.REVIEW).where(Tables.REVIEW.ID.eq(reviewId)).fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showReviews", data.getColumn(1).getSelectedRecordNr())
                .setEditMethod("editReview")
                .setSaveMethod("saveReview")
                .setRecordNr(reviewId)
                .startForm()
                .addEntry(new FormEntryPickRecord(Tables.REVIEW.CARRIER_ID)
                        .setRequired()
                        .setLabel("Carrier")
                        .setInitialValue(review.getCarrierId() == null ? 0 : review.getCarrierId())
                        .setPickTable(data, Tables.CARRIER, Tables.CARRIER.ID, Tables.CARRIER.NAME))
                .addEntry(new FormEntryDouble(Tables.REVIEW.STARS)
                        .setRequired()
                        .setLabel("Number of stars")
                        .setInitialValue(review.getStars())
                        .setMin(1.0)
                        .setStep(0.5)
                        .setMax(5.0))
                .addEntry(new FormEntryString(Tables.REVIEW.WHEN)
                        .setRequired()
                        .setLabel("When (e.g., 1 week ago)")
                        .setInitialValue(review.getWhen())
                        .setMaxChars(45))
                .addEntry(new FormEntryText(Tables.REVIEW.REVIEWTEXT)
                        .setInitialValue(review.getReviewtext())
                        .setLabel("Review text"))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Review", form);
    }

    public static int saveReview(HttpServletRequest request, AdminData data, int reviewId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        ReviewRecord review = reviewId == 0 ? dslContext.newRecord(Tables.REVIEW)
                : dslContext.selectFrom(Tables.REVIEW).where(Tables.REVIEW.ID.eq(reviewId)).fetchOne();
        String errors = data.getFormColumn().getForm().setFields(review, request, data);
        review.setRoundId(data.getColumn(1).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('showReviews')");
            return -1;
        } else {
            try {
                review.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('showReviews')");
                return -1;
            }
        }
        return review.getId();
    }

    /* ********************************************************************************************************* */
    /* ***************************************** CARRIER REVIEW ************************************************ */
    /* ********************************************************************************************************* */

    public static void showCarrierReviews(HttpSession session, AdminData data, boolean editButton,
            int selectedCarrierReviewRecordNr) {
        StringBuffer s = new StringBuffer();
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<CarrierreviewRecord> carrierReviewRecords = dslContext.selectFrom(Tables.CARRIERREVIEW)
                .where(Tables.CARRIERREVIEW.ROUND_ID.eq(data.getColumn(1).getSelectedRecordNr())).fetch();

        s.append(AdminTable.startTable());
        for (CarrierreviewRecord carrierReview : carrierReviewRecords) {
            TableRow tableRow = new TableRow(carrierReview.getId(), selectedCarrierReviewRecordNr,
                    makeCarrierString(data, carrierReview), "viewCarrierReview");
            if (editButton)
                tableRow.addButton("Edit", "editCarrierReview");
            s.append(tableRow.process());
        }
        s.append(AdminTable.endTable());

        if (editButton)
            s.append(AdminTable.finalButton("New Overall Review", "newCarrierReview"));

        data.getColumn(3).setSelectedRecordNr(selectedCarrierReviewRecordNr);
        data.getColumn(3).setContent(s.toString());
    }

    public static void editCarrierReview(HttpSession session, AdminData data, int carrierReviewId, boolean edit) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        CarrierreviewRecord carrierReview = carrierReviewId == 0 ? dslContext.newRecord(Tables.CARRIERREVIEW)
                : dslContext.selectFrom(Tables.CARRIERREVIEW).where(Tables.CARRIERREVIEW.ID.eq(carrierReviewId))
                        .fetchOne();
        //@formatter:off
        AdminForm form = new AdminForm()
                .setEdit(edit)
                .setCancelMethod("showReviews", data.getColumn(1).getSelectedRecordNr())
                .setEditMethod("editCarrierReview")
                .setSaveMethod("saveCarrierReview")
                .setRecordNr(carrierReviewId)
                .startForm()
                .addEntry(new FormEntryPickRecord(Tables.CARRIERREVIEW.CARRIER_ID)
                        .setInitialValue(carrierReview.getCarrierId() == null ? 0 : carrierReview.getCarrierId())
                        .setLabel("Carrier")
                        .setRequired()
                        .setPickTable(data, Tables.CARRIER, Tables.CARRIER.ID, Tables.CARRIER.NAME))
                .addEntry(new FormEntryDouble(Tables.CARRIERREVIEW.OVERALLSTARS)
                        .setRequired()
                        .setLabel("Overall nr stars")
                        .setInitialValue(carrierReview.getOverallstars())
                        .setMin(1.0)
                        .setStep(0.5)
                        .setMax(5.0))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit CarrierReview", form);
    }

    public static int saveCarrierReview(HttpServletRequest request, AdminData data, int carrierReviewId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        CarrierreviewRecord carrierReview = carrierReviewId == 0 ? dslContext.newRecord(Tables.CARRIERREVIEW)
                : dslContext.selectFrom(Tables.CARRIERREVIEW).where(Tables.CARRIERREVIEW.ID.eq(carrierReviewId))
                        .fetchOne();
        String errors = data.getFormColumn().getForm().setFields(carrierReview, request, data);
        carrierReview.setRoundId(data.getColumn(1).getSelectedRecordNr());
        if (errors.length() > 0) {
            ModalWindowUtils.popup(data, "Error storing record", errors, "clickMenu('showReviews')");
            return -1;
        } else {
            try {
                carrierReview.store();
            } catch (DataAccessException exception) {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickMenu('showReviews')");
                return -1;
            }
        }
        return carrierReview.getId();
    }

    private static String makeCarrierString(AdminData data, ReviewRecord review) {
        String s = "";
        if (review.getCarrierId() != null && review.getCarrierId() != 0) {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, review.getCarrierId());
            if (carrier != null)
                s += carrier.getName();
            else
                s += "-";
        } else
            s += "-";
        return s;
    }

    private static String makeCarrierString(AdminData data, CarrierreviewRecord carrierReview) {
        String s = "";
        if (carrierReview.getCarrierId() != null && carrierReview.getCarrierId() != 0) {
            CarrierRecord carrier = SqlUtils.readCarrierFromCarrierId(data, carrierReview.getCarrierId());
            if (carrier != null)
                s += carrier.getName();
            else
                s += "-";
        } else
            s += "-";
        return s;
    }

}
