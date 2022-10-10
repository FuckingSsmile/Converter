package com.converter.telegram;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;


public class ConverterFromCSVToExcel {

    private List<List> listRows = new ArrayList();

    private final String fName = System.getProperty("user.dir") + "/data_sql.csv";
    private final String oName = System.getProperty("user.dir") + "/data_sql.xls";

    public File converter() {

        try {

            FileInputStream fis = new FileInputStream(fName);

            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;


            while (((line = br.readLine()) != null)) {

                String[] split = line.split("~");

                listRows.add(Arrays.asList(split));
            }

            br.close();
            isr.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("new sheet");
            for (int k = 0; k < listRows.size(); k++) {

                HSSFRow row = sheet.createRow(k);

                for (int p = 0; p < listRows.get(k).size(); p++) {

                    HSSFCell cell = row.createCell(p);
                    String data = listRows.get(k).get(p).toString();
                    if (data.length() > 32500) data = data.substring(0, 32500);

                    if (data.startsWith("=")) {
                        cell.setCellType(CellType.STRING);
                        data = data.replaceAll("=", "");
                        cell.setCellValue(data);
                    } else if (data.startsWith("\"")) {
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(data);
                    } else {
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(data);
                    }
                }
            }

            File fileOut = new File(oName);
            hwb.write(fileOut);
            hwb.close();

            System.out.println("Your excel file has been generated");

            listRows.clear();
            return fileOut;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}