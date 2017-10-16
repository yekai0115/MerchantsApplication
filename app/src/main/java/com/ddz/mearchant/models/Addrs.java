package com.ddz.mearchant.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StormShadow on 2017/4/2.
 * Knowledge is power.
 * 省市县列表
 */

public class Addrs {

    // Province List ---------------------------------------
    private static List<Proivince> provinceObjects = new ArrayList<>();
    private static List<String> provinceNameList = new ArrayList<>();
    public static void setProvinceObjects(List<Proivince> pList) {
        provinceObjects.clear();
        provinceNameList.clear();
        provinceObjects.addAll(pList);
        for (Proivince addrObject : provinceObjects) {
            provinceNameList.add(addrObject.getRegion_name());
        }
    }

    public static List<Proivince> getProvinceObjects() {
        return provinceObjects;
    }

    public static List<String> getProvinceNameList() {
        return provinceNameList;
    }

    // CityList ---------------------------------------
    private static List<Proivince> cityObjects = new ArrayList<>();
    private static List<String> cityNameList = new ArrayList<>();

    public static void setCityObjects(List<Proivince> pList) {
        cityObjects.clear();
        cityNameList.clear();
        cityObjects.addAll(pList);
        for (Proivince addrObject : cityObjects) {
            cityNameList.add(addrObject.getRegion_name());
        }
    }

    public static List<Proivince> getCityObjects() {
        return cityObjects;
    }

    public static List<String> getCityNameList() {
        return cityNameList;
    }

    // AreaList ---------------------------------------
    private static List<Proivince> areaObjects = new ArrayList<>();
    private static List<String> areaNameList = new ArrayList<>();

    public static void setAreaObjects(List<Proivince> pList) {
        areaObjects.clear();
        areaNameList.clear();
        areaObjects.addAll(pList);
        for (Proivince addrObject : areaObjects) {
            areaNameList.add(addrObject.getRegion_name());
        }
    }

    public static List<Proivince> getAreaObjects() {
        return areaObjects;
    }

    public static List<String> getAreaNameList() {
        return areaNameList;
    }

}
