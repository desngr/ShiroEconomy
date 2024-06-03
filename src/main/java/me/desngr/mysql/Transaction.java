package me.desngr.mysql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "transactions")
public class Transaction {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true,
            foreignAutoRefresh = true,
            canBeNull = false)
    private EconomyUser from;

    @DatabaseField(foreign = true,
            foreignAutoRefresh = true,
            canBeNull = false)
    private EconomyUser to;

    @DatabaseField
    private double amount;

    @DatabaseField
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public Transaction(EconomyUser from, EconomyUser to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
