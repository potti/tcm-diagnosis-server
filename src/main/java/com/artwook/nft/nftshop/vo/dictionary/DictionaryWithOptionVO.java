package com.artwook.nft.nftshop.vo.dictionary;

import java.util.List;

import com.artwook.nft.nftshop.db.model.Dictionary;

public class DictionaryWithOptionVO extends Dictionary {

    private List<Dictionary> options;

    public List<Dictionary> getOptions() {
        return options;
    }

    public void setOptions(List<Dictionary> options) {
        this.options = options;
    }
}
