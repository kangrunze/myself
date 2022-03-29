public class DataTest2 {
    public String SUBTYPE;
    public String TIME;
    public String RECVTIME;

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String SUBTYPE) {
        this.SUBTYPE = SUBTYPE;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getRECVTIME() {
        return RECVTIME;
    }

    public void setRECVTIME(String RECVTIME) {
        this.RECVTIME = RECVTIME;
    }

    public DataTest2() {
    }

    public DataTest2(String SUBTYPE, String TIME, String RECVTIME) {
        this.SUBTYPE = SUBTYPE;
        this.TIME = TIME;
        this.RECVTIME = RECVTIME;
    }

    @Override
    public String toString() {
        return "DataTest2{" +
                "SUBTYPE='" + SUBTYPE + '\'' +
                ", TIME='" + TIME + '\'' +
                ", RECVTIME='" + RECVTIME + '\'' +
                '}';
    }
}
