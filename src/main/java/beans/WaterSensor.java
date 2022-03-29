package beans;

import lombok.Data;

/**
 * 水位传感器：用于接收水位数据
 * <p>
 * id:传感器编号
 * ts:时间戳
 * vc:水位
 */
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class WaterSensor {
    private String id;
    private String ts;
    private String vc;



    public WaterSensor() {
    }


    public WaterSensor(String id, String ts, String vc) {
        this.id = id;
        this.ts = ts;
        this.vc = vc;
    }
}