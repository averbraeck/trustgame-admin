/*
 * This file is generated by jOOQ.
 */
package org.transsonic.trustgame.data.trustgame.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.transsonic.trustgame.data.trustgame.Indexes;
import org.transsonic.trustgame.data.trustgame.Keys;
import org.transsonic.trustgame.data.trustgame.Trustgame;
import org.transsonic.trustgame.data.trustgame.tables.records.CarrierreviewRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Carrierreview extends TableImpl<CarrierreviewRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>trustgame.carrierreview</code>
     */
    public static final Carrierreview CARRIERREVIEW = new Carrierreview();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CarrierreviewRecord> getRecordType() {
        return CarrierreviewRecord.class;
    }

    /**
     * The column <code>trustgame.carrierreview.ID</code>.
     */
    public final TableField<CarrierreviewRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>trustgame.carrierreview.Round_ID</code>.
     */
    public final TableField<CarrierreviewRecord, Integer> ROUND_ID = createField(DSL.name("Round_ID"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>trustgame.carrierreview.Carrier_ID</code>.
     */
    public final TableField<CarrierreviewRecord, Integer> CARRIER_ID = createField(DSL.name("Carrier_ID"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>trustgame.carrierreview.OverallStars</code>. Whole of half numbers from 1.0 to 5.0
     */
    public final TableField<CarrierreviewRecord, Double> OVERALLSTARS = createField(DSL.name("OverallStars"), SQLDataType.FLOAT.nullable(false), this, "Whole of half numbers from 1.0 to 5.0");

    private Carrierreview(Name alias, Table<CarrierreviewRecord> aliased) {
        this(alias, aliased, null);
    }

    private Carrierreview(Name alias, Table<CarrierreviewRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>trustgame.carrierreview</code> table reference
     */
    public Carrierreview(String alias) {
        this(DSL.name(alias), CARRIERREVIEW);
    }

    /**
     * Create an aliased <code>trustgame.carrierreview</code> table reference
     */
    public Carrierreview(Name alias) {
        this(alias, CARRIERREVIEW);
    }

    /**
     * Create a <code>trustgame.carrierreview</code> table reference
     */
    public Carrierreview() {
        this(DSL.name("carrierreview"), null);
    }

    public <O extends Record> Carrierreview(Table<O> child, ForeignKey<O, CarrierreviewRecord> key) {
        super(child, key, CARRIERREVIEW);
    }

    @Override
    public Schema getSchema() {
        return Trustgame.TRUSTGAME;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CARRIERREVIEW_FK_CARRIERREVIEW_CARRIER1_IDX, Indexes.CARRIERREVIEW_FK_CARRIERREVIEW_ROUND1_IDX);
    }

    @Override
    public Identity<CarrierreviewRecord, Integer> getIdentity() {
        return (Identity<CarrierreviewRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<CarrierreviewRecord> getPrimaryKey() {
        return Keys.KEY_CARRIERREVIEW_PRIMARY;
    }

    @Override
    public List<UniqueKey<CarrierreviewRecord>> getKeys() {
        return Arrays.<UniqueKey<CarrierreviewRecord>>asList(Keys.KEY_CARRIERREVIEW_PRIMARY, Keys.KEY_CARRIERREVIEW_ID_UNIQUE);
    }

    @Override
    public List<ForeignKey<CarrierreviewRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CarrierreviewRecord, ?>>asList(Keys.FK_CARRIERREVIEW_ROUND1, Keys.FK_CARRIERREVIEW_CARRIER1);
    }

    private transient Round _round;
    private transient Carrier _carrier;

    public Round round() {
        if (_round == null)
            _round = new Round(this, Keys.FK_CARRIERREVIEW_ROUND1);

        return _round;
    }

    public Carrier carrier() {
        if (_carrier == null)
            _carrier = new Carrier(this, Keys.FK_CARRIERREVIEW_CARRIER1);

        return _carrier;
    }

    @Override
    public Carrierreview as(String alias) {
        return new Carrierreview(DSL.name(alias), this);
    }

    @Override
    public Carrierreview as(Name alias) {
        return new Carrierreview(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Carrierreview rename(String name) {
        return new Carrierreview(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Carrierreview rename(Name name) {
        return new Carrierreview(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, Integer, Integer, Double> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
