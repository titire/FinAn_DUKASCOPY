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
import java.util.ArrayList;

class GestoreStatistiche {
    public ArrayList<Double> EMA_deltaVolume;
    private double periodo = 200;
    private double parametroEMA;
    
    private double ASK_VOLUMI[];
    private double ASK_PREZZI[];
    private double BID_VOLUMI[];
    private double BID_PREZZI[];
    
    private double calcolo;
    
    public GestoreStatistiche(int lunghezzaStorico){
        periodo = lunghezzaStorico;
        parametroEMA = 2 / (periodo + 1);
        EMA_deltaVolume = new ArrayList();
        EMA_deltaVolume.add(0.);
    }
    
    public void addTick(ITick tick){
        ASK_VOLUMI = tick.getAskVolumes();
        ASK_PREZZI = tick.getAsks();
        
        BID_VOLUMI = tick.getBidVolumes();
        BID_PREZZI = tick.getBids();
        
        calcolo = 0;
        
        for(int i=0; i<ASK_VOLUMI.length; i++){
            calcolo += ASK_PREZZI[i] * ASK_VOLUMI[i];
        }
        for(int i=0; i<BID_VOLUMI.length; i++){
            calcolo -= BID_PREZZI[i] * BID_VOLUMI[i];
        }
        
        EMA_deltaVolume.add((1 - parametroEMA) * EMA_deltaVolume.get(EMA_deltaVolume.size() -1) + parametroEMA * calcolo);
        if( EMA_deltaVolume.size() > periodo ){
            EMA_deltaVolume.remove(0);
        }
    }
    
    public double getEMA_deltaVolume(int idx){
        return EMA_deltaVolume.get(EMA_deltaVolume.size() -1 - idx);
    }
    
}


public class MA_Play_PrevisioneByVolume implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private double[] ma1 = new double[Instrument.values().length];
    private IConsole console;
    
    private GestoreStatistiche GS;

    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        
        GS = new GestoreStatistiche(200);
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }

    double leva = 30.;
    
    
    
    int contatore = 0;
    boolean canTrade = false;
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        GS.addTick(tick);
        
        if( ! canTrade ){
            canTrade = (contatore++ > 200);
        }
        
        if( engine.getOrders().size() < 10 && canTrade ){
            if(GS.getEMA_deltaVolume(0) > GS.getEMA_deltaVolume(199)){   // indipendentemente dal segno perde --> totale scorrelazione
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 0, tick.getBid()
                        - instrument.getPipValue() * 30, tick.getBid() + instrument.getPipValue() * 5);
            }
            else{
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, 0.001, 0, 0, tick.getAsk()
                        + instrument.getPipValue() * 30, tick.getAsk() - instrument.getPipValue() * 5);
            }
        }
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
        
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