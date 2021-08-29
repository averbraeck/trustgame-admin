package org.transsonic.trustgame.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.BriefingRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierreviewRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.ClientRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.FbreportRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.GameplayRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrderRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.OrdercarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.PlayerorganizationRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.ReviewRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UsergroupRecord;

public final class SqlUtils {

    private SqlUtils() {
        // utility class
    }

    public static Connection dbConnection() throws SQLException, ClassNotFoundException {
        String jdbcURL = "jdbc:mysql://localhost:3306/trustgame";
        String dbUser = "trustgame";
        String dbPassword = "TG%s1naval%2105";

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
    }

    public static GameRecord readGameFromGameId(final AdminData data, final Integer gameId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.GAME).where(Tables.GAME.ID.eq(gameId)).fetchAny();
    }

    public static RoundRecord readRoundFromRoundId(final AdminData data, final Integer roundId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.ROUND).where(Tables.ROUND.ID.eq(roundId)).fetchAny();
    }

    public static OrderRecord readOrderFromOrderId(final AdminData data, final Integer orderId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.ORDER).where(Tables.ORDER.ID.eq(orderId)).fetchAny();
    }

    public static OrdercarrierRecord readOrderCarrierFromOrderCarrierId(final AdminData data,
            final Integer orderCarrierId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.ORDERCARRIER).where(Tables.ORDERCARRIER.ID.eq(orderCarrierId)).fetchAny();
    }

    public static PlayerorganizationRecord readPlayerOrganizationFromId(final AdminData data,
            final Integer playerOrganizationId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.PLAYERORGANIZATION)
                .where(Tables.PLAYERORGANIZATION.ID.eq(playerOrganizationId)).fetchAny();
    }

    public static GameplayRecord readGamePlayFromGamePlayId(final AdminData data, final Integer gamePlayId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.GAMEPLAY).where(Tables.GAMEPLAY.ID.eq(gamePlayId)).fetchAny();
    }

    public static UserRecord readUserFromUserId(final AdminData data, final Integer userId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchAny();
    }

    public static UsergroupRecord readUserGroupFromUserGroupId(final AdminData data, final Integer userGroupId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.USERGROUP).where(Tables.USERGROUP.ID.eq(userGroupId)).fetchAny();
    }

    public static UserRecord readUserFromUsername(final AdminData data, final String username) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.USER).where(Tables.USER.USERNAME.eq(username)).fetchAny();
    }

    public static ClientRecord readClientFromClientId(final AdminData data, final Integer clientId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.CLIENT).where(Tables.CLIENT.ID.eq(clientId)).fetchAny();
    }

    public static CarrierRecord readCarrierFromCarrierId(final AdminData data, final Integer carrierId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.CARRIER).where(Tables.CARRIER.ID.eq(carrierId)).fetchAny();
    }

    public static ReviewRecord readReviewFromReviewId(final AdminData data, final Integer reviewId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.REVIEW).where(Tables.REVIEW.ID.eq(reviewId)).fetchAny();
    }

    public static CarrierreviewRecord readCarrierReviewFromCarrierReviewId(final AdminData data,
            final Integer carrierReviewId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.CARRIERREVIEW).where(Tables.CARRIERREVIEW.ID.eq(carrierReviewId))
                .fetchAny();
    }

    public static FbreportRecord readFBReportForCarrierId(final AdminData data, final int carrierId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.FBREPORT).where(Tables.FBREPORT.CARRIER_ID.eq(carrierId)).fetchAny();
    }

    public static FbreportRecord readFBReportFromFBReportId(final AdminData data, final int fbReportId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.FBREPORT).where(Tables.FBREPORT.ID.eq(fbReportId)).fetchAny();
    }

    public static BriefingRecord readBriefingFromBriefingId(final AdminData data, final Integer briefingId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.BRIEFING).where(Tables.BRIEFING.ID.eq(briefingId)).fetchAny();
    }

    public static void loadAttributes(HttpSession session) {
        AdminData data = SessionUtils.getData(session);
        data.setMenuChoice("");
    }
}
