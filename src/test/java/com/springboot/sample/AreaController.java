package com.springboot.sample;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class AreaController {
    final String[] array60002341 = Area.array60002341();
    final String[] array60002339 = Area.array60002339();
    final String[] array60002355 = Area.array60002355();

    @RequestMapping("/area")
    public String area(String lng, String lat) {
        try {
            boolean area = Area.area(Double.parseDouble(lng), Double.parseDouble(lat), array60002341);
            if (area) {
                return "60002341";
            }
            boolean area1 = Area.area(Double.parseDouble(lng), Double.parseDouble(lat), array60002339);
            if (area1) {
                return "60002339";
            }
            boolean area2 = Area.area(Double.parseDouble(lng), Double.parseDouble(lat), array60002355);
            if (area2) {
                return "60002355";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping("/data")
    public String data() {
        ExcelData sheet1 = new ExcelData("D:\\work\\company\\HaoJing\\需求文档\\公司圈归类\\网络天下模型集团数据明细终板（发至分局汇总版）.xlsx", "Sheet1");
        sheet1.readExcelData();
        return "1";
    }

    public static List<CourseEntity> list = new ArrayList<>();

    @RequestMapping("/export")
    public String export(HttpServletResponse response) {


        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CourseEntity.class, list);
        // 判断数据
        if (workbook == null) {
            return "fail";
        }
        // 设置excel的文件名称
        String excelName = "测试excel";
        // 重置响应对象
        response.reset();
        // 当前日期，用于导出文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
        // 指定下载的文件名--设置响应头
        response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xls");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 写出数据输出流到页面
        try {
            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
