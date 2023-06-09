import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {
public static final String XLS_PATH="./cashflow2022.xlsx";
public static final LocalDate EXCEL_EPOCH_REFERENCE=LocalDate.of(1899, Month.DECEMBER,30);

    public static void main(String[] args) throws IOException {

        List<FinancialRecord>financialRecords=new ArrayList<>();
        Workbook workbook= WorkbookFactory.create(new File(XLS_PATH));

        Sheet sheet= workbook.getSheetAt(0);
        BigDecimal initialBalance=new BigDecimal("0");
        BigDecimal countFromEpoch;

        Iterator<Row>rowIterator= sheet.rowIterator();

        while (rowIterator.hasNext()){
            Row row= rowIterator.next();
            if (row.getRowNum()==0) continue;
            if (row.getRowNum()==1){
                initialBalance = BigDecimal.valueOf(row.getCell(2).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
                continue;
            }

            FinancialRecord financialRecord=new FinancialRecord();

            Iterator<Cell>cellIterator= row.cellIterator();


            while (cellIterator.hasNext()){
                Cell cell= cellIterator.next();

                switch (cell.getColumnIndex()){
                    case 0:
                        countFromEpoch = BigDecimal.valueOf(cell.getNumericCellValue());

                        LocalDate localDate=EXCEL_EPOCH_REFERENCE.plusDays(countFromEpoch.longValue());

                        financialRecord.setDate(localDate);
                        break;
                    case 1:
                        String observation= cell.getStringCellValue();
                        financialRecord.setObservation(observation);
                        break;
                    case 2:

                        break;
                    case 3:
                        BigDecimal encashment = BigDecimal.valueOf(cell.getNumericCellValue());
                        financialRecord.setType(FinancialType.ENCASHMENT);
                        financialRecord.setAmount(encashment);

                        break;
                    case 4:
                        BigDecimal payment = BigDecimal.valueOf(cell.getNumericCellValue());
                        financialRecord.setType(FinancialType.PAYMENT);
                        financialRecord.setAmount(payment);
                        break;
                }


            }

                financialRecords.add(financialRecord);
            //System.out.println(financialRecord);
                addFinRecToDb(financialRecord);


        }

     showBankAccount(initialBalance,financialRecords);



    }


    public static void showBankAccount(BigDecimal initialValue,List<FinancialRecord>bankAccount){
        System.out.printf("%58.2f %n",initialValue);
        Collections.sort(bankAccount);
        BigDecimal finalValue=initialValue;
        for (FinancialRecord record:
                bankAccount) {
            switch (record.getType()){
                case PAYMENT -> {finalValue=finalValue.subtract(record.getAmount());
                    System.out.printf("%8s %20s %15.2f %12.2f %n",record.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")),record.getObservation(),-record.getAmount().doubleValue(),finalValue);
                }
                case ENCASHMENT -> {finalValue=finalValue.add(record.getAmount());
                    System.out.printf("%8s %20s %15.2f %12.2f %n",record.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")),record.getObservation(),record.getAmount(),finalValue);
                }
            }

        }

    }

    public static void addFinRecToDb(FinancialRecord finRec){
        try {
            Connection con=DbConnection.getInstance().getConnection();
            Statement stm=con.createStatement();
            String query="INSERT INTO bank_financial_records (fin_rec_date,fin_type,amount,observation)\n" +
                    "VALUE('"  + finRec.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                    "','" + finRec.getType().ordinal() +
                    "'," + finRec.getAmount() +
                    ",'" + finRec.getObservation()+
                    "');";
            stm.executeUpdate(query);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
