/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tz.reteneurale;

import java.io.Serializable;

/**
 *
 * @author ariccini
 */
public class LayerGenerico implements Serializable {
    public int numeroInput;
    public int numeroOutput;
    public boolean inputLayer;
    
    public double input[];       // l'input associato al layer
    public double act_input[];   // i singoli input passatti attraverso la  funzione di attivazione
    public double output[];      // l'output
    
    public double pesi[][];      
    public double gradiente[][];
    public double sesitivity[];
    
    public final double bias = 1.;
    
    public LayerGenerico layerPrecedente;
    
    public LayerGenerico(int numeroInput, int numeroOutput, LayerGenerico layerPrecedente){
        this.numeroInput  = numeroInput;
        this.numeroOutput = numeroOutput;
        
        this.layerPrecedente = layerPrecedente;
        this.inputLayer = layerPrecedente == null;
        
        input = new double[this.numeroInput];
        act_input = new double[this.numeroInput];
        output = new double[this.numeroOutput];
        
        pesi = new double[numeroOutput][numeroInput + 1];  // il + 1 è per il bias (serve per migliorare l velocita di convergenza)
        gradiente = new double[numeroOutput][numeroInput + 1];
        sesitivity = new double[this.numeroInput];
    }

    public double funzioneAttivazione(double x){
        return Math.tanh(x);
    }
    
    public double funzioneAttivazioneDerivata(double x){
        return 1./Math.cosh(x);
    }
    
    public void setInputRete(double[] input) throws Exception{
        if( this.inputLayer ){
            if( input.length == numeroInput){
                System.arraycopy(input, 0, this.input, 0, input.length); 
            }
            else{
                throw new Exception("Impostazione valori di input nell'input layer: numero di input non corretto. Atteso: " + numeroInput + " Ricevuto: " + input.length );
            }
        }
        else{
            this.layerPrecedente.setInputRete(input);
        }
    }
    
    public double[] getOutput(double[] input) throws Exception{
        setInputRete(input);
        return getOutput();
    }
    
    public double[] getOutput() throws Exception{
        if( ! this.inputLayer ){
            System.arraycopy(this.layerPrecedente.getOutput(), 0, input, 0, numeroInput);
        }

        //System.out.println("####################### LAYER ##############################");
        for(int i=0; i<numeroInput; i++){
            act_input[i] = (this.inputLayer ? input[i] : funzioneAttivazione(input[i]));   // l'input layer non esegue il passaggio per la funzione di attivazione
            //System.out.printf("Input: %f   Attivazione(tanh): %f\n", INPUT[i], ACT_INPUT[i]);
        }
        for(int i=0; i<numeroOutput; i++){
            output[i]=0;
            for(int j=0; j<numeroInput; j++){
                output[i] += pesi[i][j] * act_input[j];
                //System.out.printf("Output %d - aggiunta peso: %f x attivazione: %f  =  %f\n", i, pesi[i][j], ACT_INPUT[j], pesi[i][j] * ACT_INPUT[j]);
            }
            output[i] += pesi[i][numeroInput] * bias;
            //System.out.printf("Output %d - aggiunta peso: %f x bias: %f  =  %f\n", i, pesi[i][numeroInput], bias, pesi[i][numeroInput] * bias);
            //System.out.printf("Output %d - valore finale: %f\n\n", i, OUT[i]);
        }
        return output;
    }

    public void inizializzaPesi(){
        for(int i=0; i<pesi.length; i++){
            for(int j=0; j<pesi[i].length; j++){
                pesi[i][j] = -1 + Math.random() * 2; 
            }
        }
    }    
}
