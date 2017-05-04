package com.xytest.Xls;

import com.xytest.TestDomain.ImportPojo;
import com.xytest.utils.JDBCUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by zhangmg on 2017/4/25.
 */

public class ImportXls {
    public static int sum=0;
    public static void main(String[] args) {
            //InnoDB表引擎支持事务
        String filepath = "C:\\Users\\zhangmg\\Desktop\\bi\\宏观经济指标数据\\宏观经济指标数据\\县级数据\\财政收入";
        File fileDir = new File(filepath);
        Connection conn = JDBCUtils.getConnection();

        JDBCUtils.beginTransaction(conn);
        try {
            if (fileDir.isDirectory()) {
                System.out.println("文件夹");
                String[] filelist = fileDir.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        insertFinance(readfile,conn);
                    }
                }
            }
            JDBCUtils.commitTransaction(conn);
            conn.close();
        } catch (Exception e) {
            JDBCUtils.rollBackTransaction(conn);
            e.printStackTrace();
        }
    }


    public static String getCellValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }

    public static String getProvinceName(String filename) {
        String provinceName = null;
        if (filename.length() == 6 || filename.length() == 7) {
            provinceName = filename.substring(0, filename.length() - 4);
        }
        if (filename.length() == 8) {
            provinceName = filename.substring(0, filename.length() - 6);
        }
        if (filename.length() == 9) {
            provinceName = filename.substring(0, filename.length() - 6);
        }
        if (filename.length() == 10) {
            provinceName = filename.substring(0, filename.length() - 7);
        }
        return provinceName;
    }

    public static void insertFinance(File file, Connection conn) throws Exception {
        String name = file.getName();
        String provinceName = getProvinceName(name); //省名
        FileInputStream fis = new FileInputStream(file);

        Statement statement = conn.createStatement();

        Workbook wb = WorkbookFactory.create(fis);
        Sheet sheet0 = wb.getSheetAt(0);  //第一页
        int lastRowNum = sheet0.getLastRowNum();  //sheet页行数

        Integer cellNum = Integer.valueOf(sheet0.getRow(0).getLastCellNum() + ""); //宽度
        ArrayList<ImportPojo> importPojos = new ArrayList<>();
        for (int i = 1; i < cellNum; i++) {  //遍历列
            //一列中通用的值
            String countyName = getCellValue(sheet0.getRow(1).getCell(i));
            String proName = getCellValue(sheet0.getRow(0).getCell(i));
            String unit = getCellValue(sheet0.getRow(3).getCell(i));
            ;

            //财政收入
            for (int j = 4; j < 25; j++) {
                ImportPojo currentPo = null;
                if (sheet0.getRow(j) == null) {
                    break;
                }
                String year = getCellValue(sheet0.getRow(j).getCell(0)); //获取交叉维度的年份
                boolean flag = false;
                for (int i1 = 0; i1 < importPojos.size(); i1++) {
                    ImportPojo importPojo = importPojos.get(i1);
                    if (importPojo.getCounty_name().equals(countyName) && importPojo.getYear().equals(year)) {
                        //集合中存在记录，则更新
                        updateIc(sheet0, j, i, unit, proName, importPojo);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {  //如果已经更新，则跳出
                    currentPo = new ImportPojo();//没有记录，在集合中新增
                    currentPo.setCounty_name(countyName);
                    currentPo.setYear(year);
                    currentPo.setProvince_name(provinceName);
                    updateIc(sheet0, j, i, unit, proName, currentPo);
                    importPojos.add(currentPo);
                }
            }
        }
        System.out.println(importPojos.get(0).getProvince_name() +":"+sum+importPojos.get(0).getCounty_name());
        sum++;
    }

    public static void updateIc(Sheet sheet0,Integer j,Integer i,String unit,String proName,ImportPojo currentPo){
        String incomeTemp = getCellValue(sheet0.getRow(j).getCell(i));
        if(incomeTemp!=null&&!incomeTemp.equals("")){
            BigDecimal  bigDecimal = new BigDecimal(incomeTemp);
            if(unit.indexOf("万")!=-1){
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(10000));
                incomeTemp = multiply.toString();
            }
            if(unit.indexOf("亿")!=-1){
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100000000));
                incomeTemp = multiply.toString();
            }
        }
        if(incomeTemp!=null){
            if(proName.indexOf("财政")!=-1){
                currentPo.setFinanInc(incomeTemp);
            }

            if(proName.indexOf("税收")!=-1){
                currentPo.setTaxInc(incomeTemp);
            }
        }
    }
}
