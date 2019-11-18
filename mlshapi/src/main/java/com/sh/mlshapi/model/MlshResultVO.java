package com.sh.mlshapi.model;

import com.sh.mlshcommon.vo.ResultVO;
import lombok.Data;

@Data
public class MlshResultVO {

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;

    private int rs;
    private String info;

    public MlshResultVO(int rs, String info) {
        this.rs = rs;
        this.info = info;
    }

    public static MlshResultVO success(){
        return new MlshResultVO(SUCCESS,"成功");
    }

    public static MlshResultVO error(String failMsg){
        return new MlshResultVO(FAIL,failMsg);
    }
}
