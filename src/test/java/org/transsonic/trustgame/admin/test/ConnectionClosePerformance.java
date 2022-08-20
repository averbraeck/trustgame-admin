package org.transsonic.trustgame.admin.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;

public class ConnectionClosePerformance {

    AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // create 100 threads that query the database 100 times.
        // leave the data connections open.
        new ConnectionClosePerformance();
    }

    public ConnectionClosePerformance() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            this.count.incrementAndGet();
            new SelectThread(i, this.count).start();
        }

        while (this.count.get() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //
            }
        }

        System.out.println("Delta t = " + (System.currentTimeMillis() - time));
    }

    class SelectThread extends Thread {
        int nr;
        AtomicInteger count;

        public SelectThread(int nr, AtomicInteger count) {
            super();
            this.nr = nr;
            this.count = count;
        }

        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/trustgame", "trustgame",
                    "TG%s1naval%2105")) {
                DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL);
                for (int i = 0; i < 100; i++) {
                    List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                            .where(Tables.ROUND.GAME_ID.eq(1)).fetch().sortAsc(Tables.ROUND.ROUNDNUMBER);
                    if (roundRecords.size() != 7)
                        throw new Exception("Not 7 rounds");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.count.decrementAndGet();
        }
    }
}
