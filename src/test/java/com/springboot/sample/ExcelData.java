package com.springboot.sample;


import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: 灵枢
 * Date: 2018/12/05
 * Time: 17:21
 * Description:读取Excel数据
 */
public class ExcelData {
    private XSSFSheet sheet;

    /**
     * 构造函数，初始化excel数据
     *
     * @param filePath  excel路径
     * @param sheetName sheet表名
     */
    ExcelData(String filePath, String sheetName) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            ZipSecureFile.setMinInflateRatio(-1.0d);
            XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
            //获取sheet
            sheet = sheets.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据行和列的索引获取单元格的数据
     *
     * @param row
     * @param column
     * @return
     */
    public String getExcelDateByIndex(int row, int column) {
        XSSFRow row1 = sheet.getRow(row);
        String cell = row1.getCell(column).toString();
        return cell;
    }

    /**
     * 根据某一列值为“******”的这一行，来获取该行第x列的值
     *
     * @param caseName
     * @param currentColumn 当前单元格列的索引
     * @param targetColumn  目标单元格列的索引
     * @return
     */
    public String getCellByCaseName(String caseName, int currentColumn, int targetColumn) {
        String operateSteps = "";
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rows; i++) {
            XSSFRow row = sheet.getRow(i);
            String cell = row.getCell(currentColumn).toString();
            if (cell.equals(caseName)) {
                operateSteps = row.getCell(targetColumn).toString();
                break;
            }
        }
        return operateSteps;
    }


    //打印excel数据
    public void readExcelData() {
        List<CourseEntity> list = AreaController.list;
        list.clear();
        AreaController areaController = new AreaController();
        int in = 0;
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rows; i++) {
            //获取列数
            XSSFRow row = sheet.getRow(i);
            int columns = row.getPhysicalNumberOfCells();
            String companyName = null, lng = null, lat = null;
            XSSFCell cell1 = row.getCell(1);
            if (null != cell1) {
                String cell = cell1.toString();
                //System.out.print(cell);
                companyName = cell;
            }

            XSSFCell cell1Lng = row.getCell(25);
            if (null != cell1Lng) {
                String cell = cell1Lng.toString();
              //  System.out.print("," + cell);
                lat = cell;
            }

            XSSFCell cell1Lat = row.getCell(26);
            if (null != cell1Lat) {
                String cell = cell1Lat.toString();
              //  System.out.print("," + cell);
                lng = cell;
            }
            System.out.println("");
            String area = areaController.area(lng, lat);
            if ("".equals(area)) {
               // System.out.println("不在区域 ： "+ companyName);
            } else {

                //网格id，网格名称，归属区域，公司名称，归属分局，注册地址
                //网格名称
                String areaName = mapping.get(area);
                //归属区域
                String belongingArea = row.getCell(0).toString();
                //归属分局
                String belongingToTheSubBureau = row.getCell(2).toString();
                //注册地址
                String registryAddress = row.getCell(3).toString();
                System.out.println( area +"-" + areaName + "-" +belongingArea+"-" +companyName+"-"+belongingToTheSubBureau+"-"+registryAddress);
                in++;

                CourseEntity courseEntity = new CourseEntity();
                courseEntity.setArea(area);
                courseEntity.setAreaName(areaName);
                courseEntity.setBelongingArea(belongingArea);
                courseEntity.setCompanyName(companyName);
                courseEntity.setBelongingToTheSubBureau(belongingToTheSubBureau);
                courseEntity.setRegistryAddress(registryAddress);
                list.add(courseEntity);
            }


            /*for(int j=0;j<columns;j++){
                if (null != row) {
                    XSSFCell cell1 = row.getCell(j);
                    if (null != cell1) {
                        String cell = cell1.toString();
                        System.out.println(cell);
                    }
                }
            }*/
        }
        System.out.println("在区域的个数 " + in);
    }

    private static final Map<String,String> mapping = new HashMap<String,String>(){{
        put("60002341","人和星光网格-人和分局-城二");
        put("60002339","龙溪加州-三龙分局-城二");
        put("60002355","大石坝网格-南桥寺分局-城二");
    }};


    //测试方法
    public static void main(String[] args) {
      //  ExcelData sheet1 = new ExcelData("D:\\work\\company\\HaoJing\\需求文档\\公司圈归类\\网络天下模型集团数据明细终板（发至分局汇总版）.xlsx", "Sheet1");
      //  sheet1.readExcelData();
    /*    List<CourseEntity> list = new ArrayList<>();
        CourseEntity courseEntity = new CourseEntity();

        list.add(courseEntity);
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CourseEntity.class, list);
*/


        AreaController areaController = new AreaController();
        String area = areaController.area("106.499912", "29.56988709");
        System.out.println(area + "---" + mapping.getOrDefault(area,""));

        area = areaController.area("106.535182", "29.6251914");
        System.out.println(area + "---" + mapping.getOrDefault(area,""));
    }
}

