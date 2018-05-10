package com.merrichat.net.activity.groupmanage.sortlistview;

import com.merrichat.net.model.CompanyData;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.SortModel;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CompanyData> {

    public int compare(CompanyData o1, CompanyData o2) {
        if (o1.code.equals("@")
                || o2.code.equals("#")) {
            return -1;
        } else if (o1.code.equals("#")
                || o2.code.equals("@")) {
            return 1;
        } else {
            return o1.code.compareTo(o2.code);
        }
    }

}
