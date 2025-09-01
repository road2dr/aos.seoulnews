package com.tobeitech.api.vo.bastaprofile;

import java.util.List;

/**
 * Created by ParkJinYoung
 */
public class BastaResultVo {

    private List<BastaPhotoItemVo> photolist;
    private List<BastaContentItemVo> contentlist;

    private BastaProfileVo basta;

    public List<BastaPhotoItemVo> getPhotolist() {
        return photolist;
    }

    public List<BastaContentItemVo> getContentlist() {
        return contentlist;
    }

    public BastaProfileVo getBasta() {
        return basta;
    }
}
