package Common;

import com.dukascopy.api.IBar;
import java.io.Serializable;

public class Barra implements Serializable{
    public double Volume, Open, High, Low, Close;
    //long TS;
    public Barra(IBar bar){
        // TS = bar.getTime();
        Volume = bar.getVolume();
        Open = bar.getOpen();
        Close = bar.getClose();
        High = bar.getHigh();
        Low = bar.getLow();
    }
}
