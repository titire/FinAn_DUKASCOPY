// serve un indicatore serio per definire con alta probalibilta di successo la direzione del mercato: RN, analisi statistica?
// serve un criterio corretto per determinare l'importo corretto da aprire
// serve una corretta gestione del trade

package singlejartest;

import Common.AppProperties;
import com.dukascopy.api.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MA_Play_EURUSD_Completo implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private double[] ma1 = new double[Instrument.values().length];
    private IConsole console;
    
    FileWriter FW;

    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        
        try {
            FW = new FileWriter("EUR_USD.csv");
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_Completo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
        
        try {
            FW.close();
        } catch (IOException ex) {
            Logger.getLogger(MA_Play_EURUSD_Completo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
        return;
        //for(IOrder ordine : engine.getOrders()){
        //    if(ordine.getProfitLossInUSD() <= -300. ){
        //        ordine.close();
        //    }
        //}
//        if (ma1[instrument.ordinal()] == -1) {
//            ma1[instrument.ordinal()] = indicators.ema(instrument, Period.TEN_SECS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, 14, 1);
//        }
//        double ma0 = indicators.ema(instrument, Period.TEN_SECS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, 14, 0);
//        if (ma0 == 0 || ma1[instrument.ordinal()] == 0) {
//            ma1[instrument.ordinal()] = ma0;
//            return;
//        }
//
//        double diff = (ma1[instrument.ordinal()] - ma0) / (instrument.getPipValue());
//
//        if (positionsTotal(instrument) == 0) {
//            if (diff > 1) {
//                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, 0.001, 0, 0, tick.getAsk()
//                        + instrument.getPipValue() * 10, tick.getAsk() - instrument.getPipValue() * 15);
//            }
//            if (diff < -1) {
//                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 0, tick.getBid()
//                        - instrument.getPipValue() * 10, tick.getBid() + instrument.getPipValue() * 15);
//            }
//        }
//        ma1[instrument.ordinal()] = ma0;
    }

    ArrayList<IBar> ASKBars = new ArrayList();
    ArrayList<IBar> BIDBars = new ArrayList();
    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if( instrument.equals(Instrument.EURUSD) && period.equals(Period.FIFTEEN_MINS) ){
            ASKBars.add(askBar);
            if(ASKBars.size() > 20){
                ASKBars.remove(0);
            }

            BIDBars.add(bidBar);
            if(BIDBars.size() > 20){
                BIDBars.remove(0);
            }
            if(ASKBars.size() < 20){
                return;
            }
            IBar prevAskBar = ASKBars.get(ASKBars.size()-2), prevBidBar = BIDBars.get(BIDBars.size()-2);
            
            double media = 0;
            double std_dev = 0;
            
            for(int i = 0; i < ASKBars.size() -1 ; i++ ){
                media += ASKBars.get(i).getClose();
                std_dev += ASKBars.get(i).getClose() * ASKBars.get(i).getClose();
            }
            media /= ASKBars.size()-1;
            std_dev /= ASKBars.size()-1;
            std_dev -= media * media;
            std_dev = Math.sqrt(std_dev);
            
            System.out.println(media + " + " + std_dev);
            
//  METODO 1
//            // 
//            if( askBar.getVolume() > 2 * prevAskBar.getVolume() ){
//                if( prevAskBar.getClose() <= askBar.getOpen() &&
//                    prevAskBar.getClose() <  askBar.getClose() ){
//                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, AppProperties.importoTrade, 0, 0,
//                            0, // askBar.getClose() - instrument.getPipValue() * 10,
//                            askBar.getClose() + instrument.getPipValue() * 15);
//                }
//                if( prevAskBar.getClose() >= askBar.getOpen() &&
//                    prevAskBar.getClose() >  askBar.getClose() ){
//                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, AppProperties.importoTrade, 0, 0,
//                            0, // askBar.getClose() + instrument.getPipValue() * 10,
//                            askBar.getClose() - instrument.getPipValue() * 15);
//                }
//            }

////  METODO 2: 3 barre consecutive crescenti o descrescenti
//            if(ASKBars.get(ASKBars.size()-4).getClose() < ASKBars.get(ASKBars.size()-3).getClose() &&
//               ASKBars.get(ASKBars.size()-3).getClose() < ASKBars.get(ASKBars.size()-2).getClose() &&
//               ASKBars.get(ASKBars.size()-2).getClose() < ASKBars.get(ASKBars.size()-1).getClose()){
//                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, AppProperties.importoTrade, 0, 0,
//                            askBar.getClose() + instrument.getPipValue() * 10,
//                            askBar.getClose() - instrument.getPipValue() * 15);
////                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, AppProperties.importoTrade, 0, 0,
////                            0, // askBar.getClose() - instrument.getPipValue() * 10,
////                            askBar.getClose() + instrument.getPipValue() * 15);
//            }
//            if(ASKBars.get(ASKBars.size()-4).getClose() > ASKBars.get(ASKBars.size()-3).getClose() &&
//               ASKBars.get(ASKBars.size()-3).getClose() > ASKBars.get(ASKBars.size()-2).getClose() &&
//               ASKBars.get(ASKBars.size()-2).getClose() > ASKBars.get(ASKBars.size()-1).getClose()){
//                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, AppProperties.importoTrade, 0, 0,
//                            askBar.getClose() - instrument.getPipValue() * 10,
//                            askBar.getClose() + instrument.getPipValue() * 15);
////                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, AppProperties.importoTrade, 0, 0,
////                            0, // askBar.getClose() + instrument.getPipValue() * 10,
////                            askBar.getClose() - instrument.getPipValue() * 15);
//            }
//        }

// METODO 3: quando una barra sfora dalla media di piu di N deviazioni standard
            if( ASKBars.get(ASKBars.size()-1).getClose() > media + 5 * std_dev ){
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, AppProperties.importoTrade, 0, 0,
                        0, //askBar.getClose() + instrument.getPipValue() * 10,
                        askBar.getClose() - instrument.getPipValue() * 15);
            }
            if( ASKBars.get(ASKBars.size()-1).getClose() < media - 5 * std_dev ){
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, AppProperties.importoTrade, 0, 0,
                        0, //askBar.getClose() - instrument.getPipValue() * 10,
                        askBar.getClose() + instrument.getPipValue() * 15);
            }
        }

//        try {
        //    FW.write(String.format("%ld;%ld;",
        //            askBar.getOpen(),
        //            askBar.getClose(),
        //            askBar.getLow(),
        //            askBar.getHigh(),
        //            askBar.getVolume()));
//        } catch (IOException ex) {
//            Logger.getLogger(MA_Play_EURUSD_Completo.class.getName()).log(Level.SEVERE, null, ex);
//        }


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