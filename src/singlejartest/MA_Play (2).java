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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import tz.reteneurale.*;

public class MA_Play implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private double[] ma1 = new double[Instrument.values().length];
    private IConsole console;

    private Rete R;
    private int numeroInput;
    private int numeroOutput;
    private int dataSize;
    private int ritardo;
    private double[] TS_INPUT;  // buffer rotativo per il trainingset di input
    private double[] TS_OUTPUT;  // buffer rotativo per il trainingset di input
    private double[] buffer;      // buffer rotativo per il transiente tra input e output : il dato entra in output, passa in buffer e dopo il ritardo passa in input
    
    private double[] PREV_INPUT;
    private double[] PREV_OUTPUT;
    
    private boolean previsione_valida = false; // indica se ho riempito tutto il training set per poter effettuare la previsione
    private int TS_INPUT_data = 0;
    
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        
        // motore di previsione (impostazione parametri)
        numeroInput = 50;
        numeroOutput = 1;
        dataSize = 4;  // 4 valori costituiscono un dato (open, close, min, max)
        R = new Rete(new int[]{numeroInput * dataSize,90 * dataSize, 30 * dataSize,numeroOutput * dataSize});
        
        R.learningRate = 0.001;
        R.gradienteMinimo = 0.0001;
        
        TS_INPUT = new double[numeroInput * dataSize];
        TS_OUTPUT = new double[numeroOutput * dataSize];
        
        PREV_INPUT = new double[numeroInput * dataSize];
        PREV_OUTPUT = new double[numeroOutput * dataSize];
        
        
        ritardo = 2;
        buffer = new double[ritardo * dataSize];
        TS_INPUT_data = -ritardo;
                
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }
    
    private boolean readyToOpenPosition = false; 
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if( readyToOpenPosition ){
            if(last_askBar.getOpen() + instrument.getPipValue() * 15 < PREV_OUTPUT[1]  ){ // confronto con la previsione di chiusura
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 0, tick.getBid()
                        - instrument.getPipValue() * 10, tick.getBid() + instrument.getPipValue() * 15);
            }
            if(last_askBar.getOpen() - instrument.getPipValue() * 15 < PREV_OUTPUT[1]  ){ // confronto con la previsione di chiusura
                engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, 0.001, 0, 0, tick.getAsk()
                        + instrument.getPipValue() * 10, tick.getAsk() - instrument.getPipValue() * 15);
            }
        }
    }

    private IBar last_askBar, last_bidBar;
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
        
        // ####################################################à   PREVISIONE A RETE NEURALE #########################à
        // applico la previsione e definisco i parametri di entrata e di uscita
        // l'entrata e l'uscita venogno poi effettuati nel onTick
        if(instrument.equals(Instrument.EURUSD) && period.equals(Period.FIVE_MINS)){
            last_askBar = askBar;
            last_bidBar = bidBar;
            
            // per il momento lavoro solo sogli ask (considero minima la differenza perche strumento liquido)
            // open, close, min, max
            // open
            TS_INPUT = Rete.shift(TS_INPUT, buffer[0]);
            buffer = Rete.shift(buffer, TS_OUTPUT[0]);
            TS_OUTPUT = Rete.shift(TS_OUTPUT, askBar.getOpen());
            
            PREV_INPUT = Rete.shift(PREV_INPUT, askBar.getOpen());
            
            // close
            TS_INPUT = Rete.shift(TS_INPUT, buffer[0]);
            buffer = Rete.shift(buffer, TS_OUTPUT[0]);
            TS_OUTPUT = Rete.shift(TS_OUTPUT, askBar.getClose());
            
            PREV_INPUT = Rete.shift(PREV_INPUT, askBar.getClose());
                    
            // min
            TS_INPUT = Rete.shift(TS_INPUT, buffer[0]);
            buffer = Rete.shift(buffer, TS_OUTPUT[0]);
            TS_OUTPUT = Rete.shift(TS_OUTPUT, askBar.getLow());
            
            PREV_INPUT = Rete.shift(PREV_INPUT, askBar.getLow());
            
            // max
            TS_INPUT = Rete.shift(TS_INPUT, buffer[0]);
            buffer = Rete.shift(buffer, TS_OUTPUT[0]);
            TS_OUTPUT = Rete.shift(TS_OUTPUT, askBar.getHigh());
            
            PREV_INPUT = Rete.shift(PREV_INPUT, askBar.getHigh());
            
            if(! previsione_valida){
              if(TS_INPUT_data++ > numeroInput){
                previsione_valida = true;
              }
            }
            else{
                R.addTrainingSet(TS_INPUT, TS_OUTPUT);
                try {
                    R.fullBackPropagation();
                    PREV_OUTPUT = R.getOutput(PREV_INPUT);
                    
                    println("PrevisionSet: INPUT:"  + Arrays.toString(PREV_INPUT));
                    println("PrevisionSet: OUTPUT:" + Arrays.toString(PREV_OUTPUT));
                    
                } catch (Exception ex) {
                    Logger.getLogger(MA_Play.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private static void println(String string) {
        System.out.println(string);
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