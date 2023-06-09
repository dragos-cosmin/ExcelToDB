import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinancialRecord implements Comparable<FinancialRecord> {
    private int id;
    private LocalDate date;
    private FinancialType type;
    private BigDecimal amount;
    private String observation;

    public FinancialRecord() {

    }


    public LocalDate getDate() {
        return date;
    }

    public FinancialType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getObservation() {
        return observation;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(FinancialType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public FinancialRecord(LocalDate date, FinancialType type, BigDecimal amount, String observation) {

        this.date = date;
        this.type = type;
        this.amount = amount;
        this.observation = observation;

    }


    @Override
    public String toString() {
        return "FinancialRecord{" +
                "id=" + id +
                ", date=" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                ", type=" + type +
                ", amount=" + amount +
                ", observation='" + observation + '\'' +
                '}' + '\n';
    }

    @Override
    public int compareTo(FinancialRecord o) {
        return date.compareTo(o.date);
    }
}

