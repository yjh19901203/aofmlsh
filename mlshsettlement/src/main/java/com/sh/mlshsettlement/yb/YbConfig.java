package com.sh.mlshsettlement.yb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ybconfig")
@Data
public class YbConfig {
    /**
     * 闪惠私钥
     **/
    private String shprivatekey;

    /**
     * appkey
     */
    private String appkey;
    /**
     * platecode
     */
    private String plateCode;
    /**
     * 闪惠私钥
     **/
    private String mch_privatekey;

    /**
     * appkey
     */
    private String mch_appkey;
    /**
     * platecode
     */
    private String mch_plateCode;

}
