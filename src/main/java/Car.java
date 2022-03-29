public  class Car {
    public String vin;
    public String type;
    public String item;

    @Override
    public String toString() {
        return "Car{" +
                "vin='" + vin + '\'' +
                ", type='" + type + '\'' +
                ", item='" + item + '\'' +
                '}';
    }

    public Car() {}

    public Car(String vin, String type, String item) {
        this.vin = vin;
        this.type = type;
        this.item = item;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}