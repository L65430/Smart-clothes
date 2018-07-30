package com.example.l.l_clothers.Info;

import com.example.l.l_clothers.Activity.SearchActivity;

/**
 * Created by L on 2017/6/5.
 */

public class TreatInfo {
    String Treatinfo;
    int Treatimage;

    public TreatInfo(String Treatinfo,int Treatimage){
        this.Treatimage=Treatimage;
        this.Treatinfo=Treatinfo;
    }

    public String getTreatinfo(){
        return Treatinfo;
    }
    public int getTreatimage(){
        return Treatimage;
    }

    public void setTreatinfo(String treatinfo){
        this.Treatinfo=treatinfo;
    }
    public void setTreatimage(int treatimage){
        this.Treatimage=treatimage;
    }

    @Override
    public String toString(){
        return "Treatinfo [Treatinfo=" + Treatinfo+ ", Treatimage="
                + Treatimage + "]";
    }
}
