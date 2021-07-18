package nettyDemo.handler;

import java.io.Serializable;

public class CalcResponse implements Serializable {
    private static final long serialVersionUID = -6406656228231176013L;

    private double result;

    public CalcResponse(double result) {
        this.result = result;
    }

    public CalcResponse() {
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
