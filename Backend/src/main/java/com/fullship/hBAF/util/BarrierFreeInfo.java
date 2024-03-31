package com.fullship.hBAF.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarrierFreeInfo {
    private final Map<Character, String> bafInfo;

    public BarrierFreeInfo() {
        this.bafInfo = new HashMap<>();
        bafInfo.put('a', "화장실");
        bafInfo.put('b', "주차");
        bafInfo.put('c', "경사로");
        bafInfo.put('d', "접근로");
        bafInfo.put('e', "출입문");
        bafInfo.put('f', "승강기");
    }

    /**
     * db에 저장될 배리어프리 정보 입력
     * @return "abcedf"
     */
    public String makeBafInfo(String evalInfo) {
        StringBuilder bafInfo = new StringBuilder();

        if (evalInfo.contains("변기")) bafInfo.append("a");
        else if (evalInfo.contains("주차")) bafInfo.append("b");
        else if (evalInfo.contains("높이")) bafInfo.append("c");
        else if (evalInfo.contains("접근로")) bafInfo.append("d");
        else if (evalInfo.contains("문")) bafInfo.append("e");
        else if (evalInfo.contains("승강")) bafInfo.append("f");

        return bafInfo.toString();
    }

    /**
     * client에 반환할 배리어프리 배열 반환
     * @return ["승강기", "출입문"]
     */
    public List<String> makeBafArrInfo(String strBafInfo) {
        List<String> bafInfoList = new ArrayList<>();

        if (strBafInfo == null) return bafInfoList;

        for (int i = 0; i < strBafInfo.length(); i++) {
            Character value = strBafInfo.charAt(i);
            bafInfoList.add(bafInfo.get(value));
        }

        return bafInfoList;
    }
}
