package bankApp.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private final LocalDateTime timestamp;
    private final String type;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;

 
    public Transaction(String type,
                       String fromAccount,
                       String toAccount,
                       BigDecimal amount) {

        this(LocalDateTime.now(), type, fromAccount,
                toAccount, amount);
    }

    // Constructor for restoring a saved transaction
    public Transaction(LocalDateTime timestamp,
                       String type,
                       String fromAccount,
                       String toAccount,
                       BigDecimal amount) {

        this.timestamp = timestamp;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Converts the transaction into a line for file storage
    public String toFileLine() {
        return timestamp + "\t"
                + type + "\t"
                + fromAccount + "\t"
                + toAccount + "\t"
                + amount.toPlainString();
    }

    // Converts a saved line back into a Transaction object
    public static Transaction fromFileLine(String line) {

        String[] fields = line.split("\t", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException(
                    "Invalid transaction record");
        }

        return new Transaction(
                LocalDateTime.parse(fields[0]),
                fields[1],
                fields[2],
                fields[3],
                new BigDecimal(fields[4]));
    }

    // Displays a readable transaction record
    public String toDisplayString() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss");

        return timestamp.format(formatter)
                + " | " + type
                + " | From: " + fromAccount
                + " | To: " + toAccount
                + " | Amount: " + amount.toPlainString();
    }
}

