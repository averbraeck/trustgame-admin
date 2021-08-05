package org.transsonic.trustgame.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.ClientRecord;
import org.transsonic.trustgame.data.trustgame.tables.records.UserRecord;

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

    public static UserRecord readUserFromUserId(final AdminData data, final Integer userId) {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        return dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchAny();
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

    public static void loadAttributes(HttpSession session) {
        AdminData data = SessionUtils.getData(session);
        data.setMenuChoice(0);
    }
}
