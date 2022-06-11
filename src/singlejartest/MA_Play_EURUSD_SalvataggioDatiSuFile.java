// serve un indicatore serio per definire con alta probalibilta di successo la direzione del mercato: RN, analisi statistica?
// serve un criterio corretto per determinare l'importo corretto da aprire
// serve una corretta gestione del trade

package singlejartest;

import Common.Sostituire.ContenitoreDati;
import com.dukascopy.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MA_Play_EURUSD_SalvataggioDatiSuFile implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private double[] ma1 = new double[Instrument.values().length];
    private IConsole console;
    
    FileWriter FW_1s;
    FileWriter FW_2s;
    FileWriter FW_10s;
    FileWriter FW_20s;
    FileWriter FW_30s;
    FileWriter FW_1m;
    FileWriter FW_5m;
    FileWriter FW_10m;
    FileWriter FW_15m;
    FileWriter FW_20m;
    FileWriter FW_30m;
    FileWriter FW_1h;
    FileWriter FW_4h;
    FileWriter FW_1d;
    FileWriter FW_1w;
    FileWriter FW_1M;
    
    ContenitoreDati CD;
    
//    ListaBarre LB_ASK_1s = new ListaBarre("ASK_1s"),
//               LB_BID_1s = new ListaBarre("BID_1s"),
//               LB_ASK_2s = new ListaBarre("ASK_2s"),
//               LB_BID_2s = new ListaBarre("BID_2s"),
//               LB_ASK_10s = new ListaBarre("ASK_10s"),
//               LB_BID_10s = new ListaBarre("BID_10s"),
//               LB_ASK_20s = new ListaBarre("ASK_20s"),
//               LB_BID_20s = new ListaBarre("BID_20s"),
//               LB_ASK_30s = new ListaBarre("ASK_30s"),
//               LB_BID_30s = new ListaBarre("BID_30s"),
//               LB_ASK_1m = new ListaBarre("ASK_1m"),
//               LB_BID_1m = new ListaBarre("BID_1m"),
//               LB_ASK_5m = new ListaBarre("ASK_5m"),
//               LB_BID_5m = new ListaBarre("BID_5m"),
//               LB_ASK_10m = new ListaBarre("ASK_10m"),
//               LB_BID_10m = new ListaBarre("BID_10m"),
//               LB_ASK_15m = new ListaBarre("ASK_15m"),
//               LB_BID_15m = new ListaBarre("BID_15m"),
//               LB_ASK_20m = new ListaBarre("ASK_20m"),
//               LB_BID_20m = new ListaBarre("BID_20m"),
//               LB_ASK_30m = new ListaBarre("ASK_30m"),
//               LB_BID_30m = new ListaBarre("BID_30m"),
//               LB_ASK_1h = new ListaBarre("ASK_1h"),
//               LB_BID_1h = new ListaBarre("BID_1h"),
//               LB_ASK_4h = new ListaBarre("ASK_4h"),
//               LB_BID_4h = new ListaBarre("BID_4h"),
//               LB_ASK_1d = new ListaBarre("ASK_1d"),
//               LB_BID_1d = new ListaBarre("BID_1d"),
//               LB_ASK_1w = new ListaBarre("ASK_1w"),
//               LB_BID_1w = new ListaBarre("BID_1w"),
//               LB_ASK_1M = new ListaBarre("ASK_1M"),
//               LB_BID_1M = new ListaBarre("BID_1M");


    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        String HEADER = "TIMESTAMP_ASK;ASK_VOLUME;ASK_OPEN;ASK_HIGH;ASK_LOW;ASK_CLOSE;"
                      + "BID_VOLUME;BID_OPEN;BID_HIGH;BID_LOW;BID_CLOSE\n";
        try {
            FW_1s = new FileWriter("EUR_USD_1s.csv");
            FW_1s.write(HEADER);
            FW_2s = new FileWriter("EUR_USD_2s.csv");
            FW_2s.write(HEADER);
            FW_10s = new FileWriter("EUR_USD_10s.csv");
            FW_10s.write(HEADER);
            FW_20s = new FileWriter("EUR_USD_20s.csv");
            FW_20s.write(HEADER);
            FW_30s = new FileWriter("EUR_USD_30s.csv");
            FW_30s.write(HEADER);
            FW_1m = new FileWriter("EUR_USD_1m.csv");
            FW_1m.write(HEADER);
            FW_5m = new FileWriter("EUR_USD_5m.csv");
            FW_5m.write(HEADER);
            FW_10m = new FileWriter("EUR_USD_10m.csv");
            FW_10m.write(HEADER);
            FW_15m = new FileWriter("EUR_USD_15m.csv");
            FW_15m.write(HEADER);
            FW_20m = new FileWriter("EUR_USD_20m.csv");
            FW_20m.write(HEADER);
            FW_30m = new FileWriter("EUR_USD_30m.csv");
            FW_30m.write(HEADER);
            FW_1h = new FileWriter("EUR_USD_1h.csv");
            FW_1h.write(HEADER);
            FW_4h = new FileWriter("EUR_USD_4h.csv");
            FW_4h.write(HEADER);
            FW_1d = new FileWriter("EUR_USD_1d.csv");
            FW_1d.write(HEADER);
            FW_1w = new FileWriter("EUR_USD_1w.csv");
            FW_1w.write(HEADER);
            FW_1M = new FileWriter("EUR_USD_1MES.csv");
            FW_1M.write(HEADER);
            
            
            if ( (new File("EURUSD_ALL_DATA.ser")).exists() ){
                CD = ContenitoreDati.leggiDaFile("EURUSD_ALL_DATA.ser");
            }
            else{
                CD = new ContenitoreDati("EURUSD");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_SalvataggioDatiSuFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MA_Play_EURUSD_SalvataggioDatiSuFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        if ( (new File("EURUSD_ALL_DATA.ser")).exists() ){
            (new File("EURUSD_ALL_DATA.ser")).renameTo(new File("EURUSD_ALL_DATA.old.ser"));
        }
        try {
            ContenitoreDati.scriviSuFile(CD,"EURUSD_ALL_DATA.ser");
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_SalvataggioDatiSuFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        console.getOut().println("Stopped");
        
        try {
            FW_1s.close();
            FW_2s.close();
            FW_10s.close();
            FW_20s.close();
            FW_30s.close();
            FW_1m.close();
            FW_5m.close();
            FW_10m.close();
            FW_15m.close();
            FW_20m.close();
            FW_30m.close();
            FW_1h.close();
            FW_4h.close();
            FW_1d.close();
            FW_1w.close();
            FW_1M.close();
            
//            ListaBarre.scriviSuFile(LB_ASK_1s);
//            ListaBarre.scriviSuFile(LB_BID_1s);
//            ListaBarre.scriviSuFile(LB_ASK_2s);
//            ListaBarre.scriviSuFile(LB_BID_2s);
//            ListaBarre.scriviSuFile(LB_ASK_10s);
//            ListaBarre.scriviSuFile(LB_BID_10s);
//            ListaBarre.scriviSuFile(LB_ASK_20s);
//            ListaBarre.scriviSuFile(LB_BID_20s);
//            ListaBarre.scriviSuFile(LB_ASK_30s);
//            ListaBarre.scriviSuFile(LB_BID_30s);
//            ListaBarre.scriviSuFile(LB_ASK_1m);
//            ListaBarre.scriviSuFile(LB_BID_1m);
//            ListaBarre.scriviSuFile(LB_ASK_5m);
//            ListaBarre.scriviSuFile(LB_BID_5m);
//            ListaBarre.scriviSuFile(LB_ASK_10m);
//            ListaBarre.scriviSuFile(LB_BID_10m);
//            ListaBarre.scriviSuFile(LB_ASK_15m);
//            ListaBarre.scriviSuFile(LB_BID_15m);
//            ListaBarre.scriviSuFile(LB_ASK_20m);
//            ListaBarre.scriviSuFile(LB_BID_20m);
//            ListaBarre.scriviSuFile(LB_ASK_30m);
//            ListaBarre.scriviSuFile(LB_BID_30m);
//            ListaBarre.scriviSuFile(LB_ASK_1h);
//            ListaBarre.scriviSuFile(LB_BID_1h);
//            ListaBarre.scriviSuFile(LB_ASK_4h);
//            ListaBarre.scriviSuFile(LB_BID_4h);
//            ListaBarre.scriviSuFile(LB_ASK_1d);
//            ListaBarre.scriviSuFile(LB_BID_1d);
//            ListaBarre.scriviSuFile(LB_ASK_1w);
//            ListaBarre.scriviSuFile(LB_BID_1w);
//            ListaBarre.scriviSuFile(LB_ASK_1M);
//            ListaBarre.scriviSuFile(LB_BID_1M);
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_SalvataggioDatiSuFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
        return;
    }

    ArrayList<IBar> ASKBars = new ArrayList();
    ArrayList<IBar> BIDBars = new ArrayList();
    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        String pattern = "yyyy.MM.dd_HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        FileWriter FW = FW_1s;
        if(period.equals(Period.ONE_SEC)){
            FW = FW_1s;
//            LB_ASK_1s.aggiungiBarra(askBar);
//            LB_BID_1s.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.TWO_SECS)){
            FW = FW_2s;
//            LB_ASK_2s.aggiungiBarra(askBar);
//            LB_BID_2s.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.TEN_SECS)){
            FW = FW_10s;
//            LB_ASK_10s.aggiungiBarra(askBar);
//            LB_BID_10s.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.TWENTY_SECS)){
            FW = FW_20s;
//            LB_ASK_20s.aggiungiBarra(askBar);
//            LB_BID_20s.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.THIRTY_SECS)){
            FW = FW_30s;
//            LB_ASK_30s.aggiungiBarra(askBar);
//            LB_BID_30s.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.ONE_MIN)){
            FW = FW_1m;
//            LB_ASK_1m.aggiungiBarra(askBar);
//            LB_BID_1m.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.FIVE_MINS)){
            FW = FW_5m;
//            LB_ASK_5m.aggiungiBarra(askBar);
//            LB_BID_5m.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.TEN_MINS)){
            FW = FW_10m;
//            LB_ASK_10m.aggiungiBarra(askBar);
//            LB_BID_10m.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.FIFTEEN_MINS)){
            FW = FW_15m;
//            LB_ASK_15m.aggiungiBarra(askBar);
//            LB_BID_15m.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.TWENTY_MINS)){
            FW = FW_20m;
//            LB_ASK_20m.aggiungiBarra(askBar);
//            LB_BID_20m.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.THIRTY_MINS)){
            FW = FW_30m;
////            LB_ASK_30m.aggiungiBarra(askBar);
//            LB_BID_30m.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.ONE_HOUR)){
            FW = FW_1h;
//            LB_ASK_1h.aggiungiBarra(askBar);
//            LB_BID_1h.aggiungiBarra(bidBar);
        }
        if(period.equals(Period.FOUR_HOURS)){
            FW = FW_4h;
//            LB_ASK_4h.aggiungiBarra(askBar);
//            LB_BID_4h.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.DAILY)){
            FW = FW_1d;
//            LB_ASK_1d.aggiungiBarra(askBar);
//            LB_BID_1d.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.WEEKLY)){
            FW = FW_1w;
//            LB_ASK_1w.aggiungiBarra(askBar);
//            LB_BID_1w.aggiungiBarra(bidBar);

        }
        if(period.equals(Period.MONTHLY)){
            FW = FW_1M;
//            LB_ASK_1M.aggiungiBarra(askBar);
//            LB_BID_1M.aggiungiBarra(bidBar);
        }
        // CD.aggiungiDato(period, askBar, bidBar);
        try {
            long l = askBar.getTime();
            FW.write(String.format("%s;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f\n",
                    simpleDateFormat.format(new Date(askBar.getTime())),
                    askBar.getVolume(),
                    askBar.getOpen(),
                    askBar.getHigh(),
                    askBar.getLow(),
                    askBar.getClose(),
                    bidBar.getVolume(),
                    bidBar.getOpen(),
                    bidBar.getHigh(),
                    bidBar.getLow(),
                    bidBar.getClose()
                    ));
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_SalvataggioDatiSuFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //count open positions
    protected int positionsTotal(Instrument instrument) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) {
            if (order.getState() == IOrder.State.FILLED) {
                counter++;
            }
        }
        return counter;
    }

    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label.substring(0, 2) + label.substring(3, 5);
        label = label + (tagCounter++);
        label = label.toLowerCase();
        return label;
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
    }
}