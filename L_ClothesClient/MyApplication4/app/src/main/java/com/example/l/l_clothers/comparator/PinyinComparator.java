package com.example.l.l_clothers.comparator;

import com.example.l.l_clothers.Info.FriendInfo;

import java.util.Comparator;

/**
 * Created by L on 2017/6/6.
 */

public class PinyinComparator implements Comparator<FriendInfo> {
    @Override
    public int compare(FriendInfo o1, FriendInfo o2) {
        if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}