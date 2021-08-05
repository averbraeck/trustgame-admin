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

public class ConnectionOpenPerformance {

    Connection connection;
    DSLContext dslContext;
    AtomicInteger count = new AtomicInteger();
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // create 100 threads that query the database 100 times.
        // leave the data connections open.
        new ConnectionOpenPerformance();
    }

    public ConnectionOpenPerformance() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trustgame", "trustgame", "TG%s1naval%2105");
        this.dslContext = DSL.using(this.connection, SQLDialect.MYSQL);
        long time = System.currentTimeMillis();
        for (int i=0; i<1000; i++) {
            this.count.incrementAndGet();
            new SelectThread(i, this.count, this.dslContext).start();
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
        DSLContext dslContext;
        
        public SelectThread(int nr, AtomicInteger count, DSLContext dslContext) {
            super();
            this.nr = nr;
            this.count = count;
            this.dslContext = dslContext;
        }

        @Override
        public void run() {
            try {
                for (int i=0; i<100; i++) {
                    List<RoundRecord> roundRecords = dslContext.selectFrom(Tables.ROUND)
                            .where(Tables.ROUND.GAME_ID.eq(1)).fetch().sortAsc(Tables.ROUND.ROUNDNUMBER);
                    if (roundRecords.size() != 7)
                        throw new Exception("Not 7 rounds");
                }
                this.count.decrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
