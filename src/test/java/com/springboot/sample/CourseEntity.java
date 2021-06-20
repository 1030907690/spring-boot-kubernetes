package com.springboot.sample;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

@ExcelTarget("courseEntity")
public class CourseEntity implements java.io.Serializable {

    @Excel(name = "网格id")
    private String area;
    @Excel(name = "网格名称")
    private String areaName;
    @Excel(name = "归属区域")
    private String belongingArea;
    @Excel(name = "公司名称")
    private String companyName;
    @Excel(name = "归属分局")
    private String belongingToTheSubBureau;
    @Excel(name = "注册地址")
    private String registryAddress;

    public void setArea(String area) {
        this.area = area;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setBelongingArea(String belongingArea) {
        this.belongingArea = belongingArea;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setBelongingToTheSubBureau(String belongingToTheSubBureau) {
        this.belongingToTheSubBureau = belongingToTheSubBureau;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getArea() {
        return area;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getBelongingArea() {
        return belongingArea;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getBelongingToTheSubBureau() {
        return belongingToTheSubBureau;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }
}