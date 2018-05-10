package com.merrichat.net.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AMSSY1 on 2018/1/4.
 */

public class MemberDraftModel implements Serializable {
    private String memberId;

    public ArrayList<DraftModel> getMemberDraftList() {
        return memberDraftList;
    }

    public void setMemberDraftList(ArrayList<DraftModel> memberDraftList) {
        this.memberDraftList = memberDraftList;
    }

    private ArrayList<DraftModel> memberDraftList;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

}
