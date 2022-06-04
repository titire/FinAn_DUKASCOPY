/*
 * Copyright (c) 2017 Dukascopy (Suisse) SA. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Dukascopy (Suisse) SA or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. DUKASCOPY (SUISSE) SA ("DUKASCOPY")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL DUKASCOPY OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF DUKASCOPY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package singlejartest;

import com.dukascopy.api.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import Common.functionAnalitics;

public class MA_Play_Derivate implements IStrategy {
    private IContext contesto = null;
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private double[] ma1 = new double[Instrument.values().length];
    private IConsole console;

    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        InitialAmount = context.getAccount().getBalance();
        console.getOut().println("Balance: " + String.valueOf(InitialAmount));
        contesto = context;
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if ( instrument.equals(Instrument.EURUSD) ){
//            if (positionsTotal(instrument) == 0) {
                if (sell) {
                    sell = false;
                    //engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, 0.001, 0, 0, tick.getAsk()
                    //        + instrument.getPipValue() * 10, tick.getAsk() - instrument.getPipValue() * 15);
                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, amt / 1000000 , 0, 0,
                            0, // tick.getAsk() + instrument.getPipValue() * 100,
                            tick.getAsk() - instrument.getPipValue() * 15);
                    
                }
                if (buy) {
                    buy = false;
                    //engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 0, tick.getBid()
                    //        - instrument.getPipValue() * 10, tick.getBid() + instrument.getPipValue() * 15);
                    engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, amt / 1000000 , 0, 0,
                            0, // tick.getBid() - instrument.getPipValue() * 100,
                            tick.getBid() + instrument.getPipValue() * 15);
                    
                }
            }
//        }
    }

    boolean buy = false, sell = false;
    boolean buy_setted = false, sell_setted = false;
    double amt = 10000;
    double InitialAmount;
    
    //double ema_el_0, ema_el_1;
    //double ema_l_0, ema_l_1;
    //double ema_s_0, ema_s_1;
    
    //double d_ema_el_0, d_ema_el_1;
    //double d_ema_l_0, d_ema_l_1;
    //double d_ema_s_0, d_ema_s_1;
    
    public functionAnalitics EMA_EL = new functionAnalitics();
    public functionAnalitics EMA_L = new functionAnalitics();
    public functionAnalitics EMA_S = new functionAnalitics();
    
        
    double MIN_storico = -1, MAX_storico = -1;
    int profondità_storico = 0;
    double percentuale_TradeOff = 0.6;
    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
        if ( instrument.equals(Instrument.EURUSD) ){
            if ( period.equals(Period.FIVE_MINS) ){
                // identificazione nuovi minimi/massimi
                if(MIN_storico > askBar.getLow() || MIN_storico == -1 ){
                    MIN_storico = askBar.getLow();
                }
                if(MAX_storico < bidBar.getHigh() || MAX_storico == -1 ){
                    MAX_storico = bidBar.getHigh();
                }
                
                // ricalibrazione MASSIMO e MINIMO adeguamento riduzione della volatilià
                if ( ++profondità_storico > 74880/12){   // il numero di intervalli da 5 minuti che sta in un anno (52 settimane * 5 gg/settimana * 24 h/gg * 12 intervalli/h)
                    MIN_storico += 0.001 * (askBar.getLow() - MIN_storico);
                    MAX_storico -= 0.001 * (MAX_storico - bidBar.getHigh());
                } 

                if(contesto.getAccount().getEquity() >= 1.2 * InitialAmount){
                    console.getOut().println("Reset Account: Chiusura trade aperti");
                    try {
                        for(IOrder I : engine.getOrders()){
                            I.close();
                        }
                    } catch (JFException ex) {
                        Logger.getLogger(MA_Play_Derivate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    InitialAmount = contesto.getAccount().getEquity();
                    console.getOut().println("Balance: " + String.valueOf(InitialAmount));
                }
                
                double esposizioneTotale_BUY = 0;
                double esposizioneTotale_SELL = 0;
                double risultato_BUY = 0;
                double risultato_SELL = 0;

                
                try {
                    for(IOrder I : engine.getOrders()){
                        I.getProfitLossInAccountCurrency();
                        if(I.isLong()){
                            esposizioneTotale_BUY += I.getAmount();
                            risultato_BUY += I.getProfitLossInAccountCurrency();
                        }
                        else{
                            esposizioneTotale_SELL += I.getAmount();
                            risultato_SELL += I.getProfitLossInAccountCurrency();
                        }
                    }
                    EMA_EL.addValue(indicators.ema(instrument, period, OfferSide.BID, IIndicators.AppliedPrice.CLOSE, period.ordinal(), 100));
                    EMA_L.addValue(indicators.ema(instrument, period, OfferSide.BID, IIndicators.AppliedPrice.CLOSE, period.ordinal(), 100));
                    EMA_S.addValue(indicators.ema(instrument, period, OfferSide.BID, IIndicators.AppliedPrice.CLOSE, period.ordinal(), 20));
                } catch (JFException ex) {
                    Logger.getLogger(MA_Play_Derivate.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                    
//                    // Metodo 1 : fallisce verso ottobre 2019
//                    // AGGIUNGERE CONDIZIONI OCN DERIVATE E DERIVATE SECONDE
//                    if ( EMA_EL.derivataPrima > 0 && EMA_S.derivataPrima > EMA_L.derivataPrima ){
//                        if( askBar.getClose() < MIN_storico + percentuale_TradeOff * (MAX_storico - MIN_storico)){
//                            buy = true;
//                            // valutazione ordersize
//                        }
//                    }
//                    if ( EMA_EL.derivataPrima < 0 && EMA_S.derivataPrima < EMA_L.derivataPrima ){
//                        if ( bidBar.getClose() > MIN_storico + (1. - percentuale_TradeOff) * (MAX_storico - MIN_storico)){
//                            sell = true;
//                            // valutazione ordersize
//                        }
//                    }
                // Metodo 2: derivate seconde per vedere la concavità/convessità per determinare l'andamento
                if (EMA_EL.derivataSeconda > 0 && EMA_EL.derivataPrima > 0 && EMA_L.derivataSeconda > 0 && EMA_L.derivataPrima > 0){
                    if( askBar.getClose() < MIN_storico + percentuale_TradeOff * (MAX_storico - MIN_storico)){
                        if(! buy_setted){
                            buy = true;
                            buy_setted = true;
                            sell_setted = false;
                        }
                        // valutazione ordersize
                    }
                }
                if (EMA_EL.derivataSeconda < 0 && EMA_EL.derivataPrima < 0 && EMA_L.derivataSeconda < 0 && EMA_L.derivataPrima < 0){
                    if ( bidBar.getClose() > MIN_storico + (1. - percentuale_TradeOff) * (MAX_storico - MIN_storico)){
                        if(! sell_setted ){
                            sell = true;
                            buy_setted = false;
                            sell_setted = true;
                        }
                        // valutazione ordersize
                    }
                }
                // tutti gli altri casi sono aprroccio all'estremo (minimo o massimo)
                   

//                System.out.printf("LOG;Ema_EL_0: %ld;Ema_EL_1: %ld;D_Ema_EL_0: %ld;D_Ema_EL_1: %ld;"
//                     + "Ema_L_0: %ld;Ema_L_1: %ld;D_Ema_L_0: %ld;D_Ema_L_1: %ld;"
//                     + "Ema_S_0: %ld;Ema_S_1: %ld;D_Ema_S_0: %ld;D_Ema_S_1: %ld;"
//                     + "Esposizione_Tot_BUY: %ld;Risultato_BUY: %ld;Esposizione_Tot_SELL: %ld;Risultato_SELL: %ld\n",
//                     ema_el_0, ema_el_1, d_ema_el_0, d_ema_el_1,
//                     ema_l_0, ema_l_1, d_ema_l_0, d_ema_l_1,
//                     ema_s_0, ema_s_1, d_ema_s_0, d_ema_s_1,
//                     esposizioneTotale_BUY, risultato_BUY,
//                     esposizioneTotale_SELL, risultato_SELL);

            }
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