package com.converter.telegram;

import com.github.opendevl.JFlat;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConverterFromJsonToExcel {

    private List<String> listRows2 = new ArrayList();
    private final String extractedJson = System.getProperty("user.dir") + "/extractedJson.csv";
    private final String fNamePathCsv = System.getProperty("user.dir") + "/Критические_ошибки_по_топику_contingent_from_mes_to_emias.csv";
    private final String fNamePathXls = System.getProperty("user.dir") + "/Критические_ошибки_по_топику_contingent_from_mes_to_emias.xls";


    public File convertedJson() {

        try {
            FileInputStream f1 = new FileInputStream(fNamePathCsv);


            InputStreamReader isr2 = new InputStreamReader(f1, "windows-1251");

            new BufferedReader(isr2).lines().skip(1)
                    .map(s -> s.split(";"))
                    .forEach(s -> listRows2.add(s[4]));

            String json = "";

            for (int i = 0; i < listRows2.size(); i++) {
                json = json + listRows2.get(i);
            }

            JFlat flatMe = new JFlat(listRows2.toString());

            //get the 2D representation of JSON document
            flatMe.json2Sheet().headerSeparator("_").getJsonAsSheet();

            //write the 2D representation in csv format
            flatMe.write2csv("extractedJson.csv");

            List<List> listRows = new ArrayList<>();

            FileInputStream fis = new FileInputStream(extractedJson);

            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;


            while (((line = br.readLine()) != null)) {

                String[] split = line.split(",");

                listRows.add(Arrays.asList(split));
            }

            br.close();
            isr.close();
            fis.close();


            HSSFWorkbook hwb = new HSSFWorkbook();


            HSSFSheet sheet = hwb.createSheet("new sheet");

            hwb.getCreationHelper().createFormulaEvaluator();


            for (int k = 0; k < listRows.size(); k++) {

                HSSFRow row = sheet.createRow(k);

                for (int p = 0; p < listRows.get(k).size(); p++) {

                    HSSFCell cell = row.createCell(p);
                    String data = listRows.get(k).get(p).toString();
                    if (data.length() > 32500) data = data.substring(0, 32500);

                    if (!data.startsWith("\"") && !data.matches("\\w+") && !data.isEmpty()) {
                        double l = Double.parseDouble(data);
                        cell.setCellValue(l);
                    } else if (data.startsWith("=")) {
                        cell.setCellType(CellType.STRING);
                        data = data.replaceAll("=", "");
                        cell.setCellValue(data);

                    } else if (!data.matches("\"\\d+\"")) {
                        cell.setCellType(CellType.STRING);
                        data = data.replaceAll("\"", "");
                        cell.setCellValue(data);
                    } else {
                        cell.setCellType(CellType.STRING);
                        data = data.replaceAll("\"", "");
                        cell.setCellValue(data);
                    }
                }
            }

            File fileOut = new File(fNamePathXls);
            hwb.write(fileOut);
            hwb.close();

            System.out.println("Your excel file has been generated");

            listRows.clear();

            return fileOut;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
