package com.pos.salon.interfacesection;

import com.pos.salon.model.searchData.SearchItem;

public interface Selectedinterface {
    //public void select(String name,int parent,int child);



    void select(String modname, String modid, String varname, String varid, String varprice, SearchItem data);




    // void add(int i, SearchItem.Modifierrlist data1, SearchItem.Variations data2);
}
