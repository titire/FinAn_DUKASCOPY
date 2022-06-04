package tz.reteneurale;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ariccini
 */
public class Rete implements Serializable{
    private ArrayList<LayerGenerico> Strati;
    
    // costruttore
    public Rete(int[] NumeroNodi){
        Strati = new ArrayList();
        for(int i=0; i<NumeroNodi.length - 1; i++){
            Strati.add(new LayerGenerico(NumeroNodi[i], NumeroNodi[i+1], i==0? null : Strati.get(i-1)));
            Strati.get(i).inizializzaPesi();
        }
    }
    
    // dimensioni interfacce
    public int getDimensioneInput(){
        return Strati.get(0).numeroInput;
    }
    
    public int getDimensioneOutput(){
        return Strati.get(Strati.size()-1).numeroOutput;
    }
    
    // input output
    private void setInput(double[] input) throws Exception{
        Strati.get(0).setInputRete(input);
    }

    private double[] getOutput() throws Exception{
        return Strati.get(Strati.size()-1).getOutput();
    }
    
    public double[] getOutput(double[] input) throws Exception{
        setInput(input);
        return getOutput();
    }
    

    
    
    // ####################################    ADDESTRAMENTO  #################################################
    // tratto da "A Gentle Introduction to BackPropagation" Shashi Sathyanarayana  22-Luglio-2014
    public ArrayList<double[]> TS_INPUT;
    public ArrayList<double[]> TS_OUTPUT;
    public int TS_MaxDim = 1000;
    
    public void addTrainingSet(double[] input, double[] output){
        if(TS_INPUT == null){
            TS_INPUT = new ArrayList();
            TS_OUTPUT = new ArrayList();
        }
        TS_INPUT.add(input);
        TS_OUTPUT.add(output);
        if (TS_INPUT.size() > TS_MaxDim) {
            TS_INPUT.remove(0);
            TS_OUTPUT.remove(0);
        }
    }
    
    
    // funzioni utilità
    private double calcolaErrore(double[] rif, double[] test){
        double OUT = 0;
        for(int i = 0 ; i< rif.length; i++){
            OUT += Math.pow(test[i] - rif[i], 2.);
        }
        return OUT;
    }

    // step 1 -- definizione criteri di uscita e learning rate
    public double learningRate = 0.001;
    public double gradienteMinimo = 0.0005;
    public int iterazione = 0;
    public int MaxIterazione = 10000;
    
    public void backPropagation() throws Exception{
        double gradienteMassimo = gradienteMinimo + 1;
        while( iterazione++ < MaxIterazione && gradienteMassimo > gradienteMinimo){  // step 6  - exit condition (stopping criteria) 
            int elemento = (int) Math.floor(Math.random() * (TS_INPUT.size() + 1));  // step 2 - scelta casuale di un elemento del training set
            while(elemento >= TS_INPUT.size()){   // per risolvere il problema del bordo superiore
                elemento--;
            }
            
            // step 3 - compute the input and output to each layer (fatto in automatico dai layer)
            double[] output_originale = this.getOutput(TS_INPUT.get(elemento));
            //double errore_originale = calcolaErrore(TS_OUTPUT.get(elemento), output_originale);
            
            // step 4 - compute sensitivity
            LayerGenerico LG, LG_1 = null;
            for(int i = Strati.size() - 1; i >= 0; i--){
                LG = Strati.get(i);
                if( i == Strati.size() - 1 ){
                    for(int j = 0 ; j<LG.numeroInput; j++){
                        LG.sesitivity[j] = 0;
                        for(int k=0; k<LG.numeroOutput ; k++){
                            LG.sesitivity[j] += LG.funzioneAttivazioneDerivata(LG.input[j]) * LG.pesi[k][j] * (2 * (output_originale[k] - TS_OUTPUT.get(elemento)[k]));
                        }
                    }
                }
                else{
                    for(int j = 0 ; j<LG.numeroInput; j++){
                        LG.sesitivity[j] = 0;
                        for(int k=0; k<LG.numeroOutput ; k++){
                            LG.sesitivity[j] += LG.funzioneAttivazioneDerivata(LG.input[j]) * LG.pesi[k][j] * LG_1.sesitivity[k];
                        }
                    }
                    
                }
                LG_1 = LG;
            }
//            LayerGenerico LG, LG_1 = null;
//            for(int i = 0; i < Strati.size(); i++){
//                LG = Strati.get(i);
//                for(int j =0; j< LG.numeroInput; j++){
//                    LG.input[j] += learningRate;
//                    LG.sesitivity[j] = (this.calcolaErrore(TS_OUTPUT.get(elemento), getOutput()) - errore_originale) / learningRate;
//                    LG.input[j] -= learningRate;
//                }
//            }
                
        
            // step 5 - compute the gradient component (partial derivative of error with respect to weights) and update of weights
            gradienteMassimo = 0;
            for(int layer_id=0; layer_id < Strati.size(); layer_id++){
                LG=Strati.get(layer_id);
                if(layer_id < Strati.size()-1){
                    LG_1=Strati.get(layer_id+1);
                }
                for(int input_id=0; input_id < LG.numeroInput + 1; input_id++){
                    for(int output_id=0; output_id < LG.numeroOutput; output_id++){
                        if(layer_id < Strati.size()-1){
                            LG.gradiente[output_id][input_id] = LG.funzioneAttivazione(input_id != LG.numeroInput ? LG.input[input_id] : LG.bias) * LG_1.sesitivity[output_id];
                        }
                        else{
                            LG.gradiente[output_id][input_id] = LG.funzioneAttivazione(input_id != LG.numeroInput ? LG.input[input_id] : LG.bias) * (2 * (output_originale[output_id] - TS_OUTPUT.get(elemento)[output_id]));
                        }
                        if ( gradienteMassimo < LG.gradiente[output_id][input_id] ){
                            gradienteMassimo = LG.gradiente[output_id][input_id];
                        }
                        LG.pesi[output_id][input_id] -= LG.gradiente[output_id][input_id] * learningRate;
                    }
                }
            }

        }
    }
    
    private void backPropagation_ONIndex(int elemento) throws Exception{
        double gradienteMassimo = gradienteMinimo + 1;
        while( iterazione++ < MaxIterazione && gradienteMassimo > gradienteMinimo){  // step 6  - exit condition (stopping criteria) 
            // int elemento = elemento;  // step 2 - scelta casuale di un elemento del training set (forzato da esterrno)
            
            // step 3 - compute the input and output to each layer (fatto in automatico dai layer)
            double[] output_originale = this.getOutput(TS_INPUT.get(elemento));
            //double errore_originale = calcolaErrore(TS_OUTPUT.get(elemento), output_originale);
            
            // step 4 - compute sensitivity
            LayerGenerico LG, LG_1 = null;
            for(int i = Strati.size() - 1; i >= 0; i--){
                LG = Strati.get(i);
                if( i == Strati.size() - 1 ){
                    for(int j = 0 ; j<LG.numeroInput; j++){
                        LG.sesitivity[j] = 0;
                        for(int k=0; k<LG.numeroOutput ; k++){
                            LG.sesitivity[j] += LG.funzioneAttivazioneDerivata(LG.input[j]) * LG.pesi[k][j] * (2 * (output_originale[k] - TS_OUTPUT.get(elemento)[k]));
                        }
                    }
                }
                else{
                    for(int j = 0 ; j<LG.numeroInput; j++){
                        LG.sesitivity[j] = 0;
                        for(int k=0; k<LG.numeroOutput ; k++){
                            LG.sesitivity[j] += LG.funzioneAttivazioneDerivata(LG.input[j]) * LG.pesi[k][j] * LG_1.sesitivity[k];
                        }
                    }
                    
                }
                LG_1 = LG;
            }
//            LayerGenerico LG, LG_1 = null;
//            for(int i = 0; i < Strati.size(); i++){
//                LG = Strati.get(i);
//                for(int j =0; j< LG.numeroInput; j++){
//                    LG.input[j] += learningRate;
//                    LG.sesitivity[j] = (this.calcolaErrore(TS_OUTPUT.get(elemento), getOutput()) - errore_originale) / learningRate;
//                    LG.input[j] -= learningRate;
//                }
//            }
                
        
            // step 5 - compute the gradient component (partial derivative of error with respect to weights) and update of weights
            gradienteMassimo = 0;
            for(int layer_id=0; layer_id < Strati.size(); layer_id++){
                LG=Strati.get(layer_id);
                if(layer_id < Strati.size()-1){
                    LG_1=Strati.get(layer_id+1);
                }
                for(int input_id=0; input_id < LG.numeroInput + 1; input_id++){
                    for(int output_id=0; output_id < LG.numeroOutput; output_id++){
                        if(layer_id < Strati.size()-1){
                            LG.gradiente[output_id][input_id] = LG.funzioneAttivazione(input_id != LG.numeroInput ? LG.input[input_id] : LG.bias) * LG_1.sesitivity[output_id];
                        }
                        else{
                            LG.gradiente[output_id][input_id] = LG.funzioneAttivazione(input_id != LG.numeroInput ? LG.input[input_id] : LG.bias) * (2 * (output_originale[output_id] - TS_OUTPUT.get(elemento)[output_id]));
                        }
                        if ( gradienteMassimo < LG.gradiente[output_id][input_id] ){
                            gradienteMassimo = LG.gradiente[output_id][input_id];
                        }
                        LG.pesi[output_id][input_id] -= LG.gradiente[output_id][input_id] * learningRate;
                    }
                }
            }
        }
    }
    
    private double[] getErroreSuDATASET() throws Exception{
        double[] OUT = new double[TS_INPUT.size()];
        
        for(int i=0; i<TS_INPUT.size(); i++){
            OUT[i] = this.calcolaErrore(TS_OUTPUT.get(i), this.getOutput(TS_INPUT.get(i)));
        }
        return OUT;
    }
    
    private int getMaxValueIdx(double[] IN){
        int idx_out = 0;
        for(int i=0; i<IN.length;i++){
            if(IN[idx_out] < IN[i]){
                idx_out = i;
            }
        }
        return idx_out;
    }
    
    public void fullBackPropagation() throws Exception{
        // iterativamente esegue i seguenti passaggi;
        //  - calcolo dell'errore su ogni blocco del dataset
        //  - esegue la backPropagationOnIndex sull'elemento del dataset con errore maggiore
        //  - ricalcola l'errore complessivo
        //  - controlla se gli errori per ogni elemento del dataset sono inferiori al valore di soglia per uscire
        //  - altrimenti riesegue la backPropagation on index
        
        double[] errore = getErroreSuDATASET();
        double[] errore_old = new double[TS_INPUT.size()];
        double[] diff = new double[TS_INPUT.size()];
        
        do{
            backPropagation_ONIndex(getMaxValueIdx(errore));
            System.arraycopy(errore, 0, errore_old, 0, errore.length);
            errore = getErroreSuDATASET();
            for(int i=0; i<TS_INPUT.size(); i++){
                diff[i] = Math.abs(errore[i] - errore_old[i]);
            }
        }while(diff[getMaxValueIdx(diff)] >= this.gradienteMinimo);
    }
    
    
    // utilità su vattori
    public static double[] shift(double[] INPUT_v, double INPUT_s){
        for(int i =0; i<INPUT_v.length -1 ; i++){
            INPUT_v[i] = INPUT_v[i+1];
        }
        INPUT_v[INPUT_v.length -1] = INPUT_s;
        return INPUT_v;
    }
}
