package beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public  class VehicleMsg {
    public String vin;
    public String milage;
    public String sendtime;
    public String speed;
    public String veh_moudle;

    public VehicleMsg(String vin, String milage, String sendtime, String speed, String veh_moudle) {
        this.vin = vin;
        this.milage = milage;
        this.sendtime = sendtime;
        this.speed = speed;
        this.veh_moudle = veh_moudle;
    }

    public VehicleMsg() {
    }

    @Override
    public String toString() {
        return "VehicleMsg{" +
                "vin='" + vin + '\'' +
                ", milage='" + milage + '\'' +
                ", sendtime='" + sendtime + '\'' +
                ", speed='" + speed + '\'' +
                ", veh_moudle='" + veh_moudle + '\'' +
                '}';
    }
}