package Common;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import java.io.Serializable;

public class Barra implements Serializable{
    public double Volume, Open, Low, High, Close;
    long TS;      // inizio del periodo di riferimento della barra
    long TS_end;  // fine del periodo di riferimento della barra
    
    public Barra(){}
    public Barra(long TimeStamp, double Volume, double Open, double Low, double High, double Close){
        this.TS = TimeStamp;
        this.Volume = Volume;
        this.Open = Open;
        this.Low = Low;
        this.High = High;
        this.Close = Close;
    }
    public Barra(long TimeStamp_Start, long TimeStamp_End, double Volume, double Open, double Low, double High, double Close){
        this(TimeStamp_Start, Volume, Open, Low, High, Close);
        TS_end = TimeStamp_End;
    }
    
    public Barra(IBar bar){
        TS = bar.getTime();
        Volume = bar.getVolume();
        Open = bar.getOpen();
        Low = bar.getLow();
        High = bar.getHigh();
        Close = bar.getClose();
    }
    public Barra(IBar bar, Period P){
        TS = bar.getTime();
        TS_end = TS + P.getInterval();
        Volume = bar.getVolume();
        Open = bar.getOpen();
        Low = bar.getLow();
        High = bar.getHigh();
        Close = bar.getClose();
    }
    public Barra aggregateBar(Barra B) throws CloneNotSupportedException{
        if ( this.TS == B.TS ){
            return (Barra)this.clone();
        }
        return new Barra(Math.min(this.TS, B.TS),
                         Math.max(this.TS_end, B.TS_end),
                         this.Volume + B.Volume,
                         this.Open,
                         Math.min(this.Low, B.Low),
                         Math.max(this.High, B.High),
                         B.Close);
    }
   public boolean isEqual(Barra B){
       return  this.TS     == B.TS &&
               this.Volume == B.Volume && 
               this.Open   == B.Open && 
               this.Low    == B.Low && 
               this.High   == B.High && 
               this.Close  == B.Close; 
               
   }
}
