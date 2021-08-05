package org.transsonic.trustgame.admin.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.transsonic.trustgame.data.trustgame.Tables;
import org.transsonic.trustgame.data.trustgame.tables.records.RoundRecord;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolPerformance {

    Connection connection;
    // DSLContext dslContext;
    DataSource dataSource;
    AtomicInteger count = new AtomicInteger();
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // create 100 threads that query the database 100 times.
        // leave the data connections open.
        new ConnectionPoolPerformance();
    }

    public ConnectionPoolPerformance() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/trustgame");
        config.setUsername("trustgame");
        config.setPassword("TG%s1naval%2105");
        config.setMaximumPoolSize(10);
        /*final HikariDataSource */ this.dataSource = new HikariDataSource(config);
        // this.dslContext = DSL.using(dataSource, SQLDialect.MYSQL);
        
        long time = System.currentTimeMillis();
        for (int i=0; i<1000; i++) {
            this.count.incrementAndGet();
            new SelectThread(i, this.count, this.dataSource /*, this.dslContext*/).start();
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
        
        public SelectThread(int nr, AtomicInteger count, DataSource dataSource /*, DSLContext dslContext*/) {
            super();
            this.nr = nr;
            this.count = count;
            this.dslContext = DSL.using(dataSource, SQLDialect.MYSQL); //dslContext;
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
