/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

/**
 *
 * @author ariccini
 */
public class Minimizzatore {
    Funzione_Polinomiale F;
    DataSet DS;
    
    double scostamento = 0.0001;
    double parametro_approccio = 0.001;
    double exitDelta = 0.00001;
    
    Minimizzatore(Funzione_Polinomiale F){
        this.F = F;
    }
    
    
    public double minimizza(DataSet DS){
        this.DS = DS;
        double E1 = valutaErroreFunzioneSuDataSet();
        double E0 = -E1;
        double gradiente[] = new double[F.parametri.length];
        
        int contatore = 0;
        
        while(contatore++ < 100){
            // valutazione del gradiente
            for(int i = 0; i < F.parametri.length; i++ ){
                F.parametri[i] += scostamento;
                gradiente[i] = (valutaErroreFunzioneSuDataSet() - E0) / scostamento;
                F.parametri[i] -= scostamento;
            }

            int idx_max=0;
            for(int i = 0; i < F.parametri.length; i++ ){
                idx_max = (gradiente[i] > gradiente[idx_max]) ? i : idx_max;
            }
            minimizza_singolo_parametro(DS, idx_max);
        }
        return minimizza_gradiente(DS);
    }
    
    public double minimizza_singolo_parametro(DataSet DS, int idx_parametro){
        this.DS = DS;
        double E1 = valutaErroreFunzioneSuDataSet();
        double E0 = -E1;
                
        while( Math.abs(E1 - E0) > exitDelta ){
            E0 = E1;

            // valutazione del gradiente
            F.parametri[idx_parametro] += scostamento;
            F.parametri[idx_parametro] = F.parametri[idx_parametro] - scostamento - (valutaErroreFunzioneSuDataSet() - E0) / scostamento * parametro_approccio;
            
            E1 = valutaErroreFunzioneSuDataSet();
            System.out.println(E1);
        }
        
        return Math.abs(E1 - E0);
    }
    
    
    public double minimizza_gradiente(DataSet DS){
        this.DS = DS;
        double E1 = valutaErroreFunzioneSuDataSet();
        double E0 = -E1;
        double gradiente[] = new double[F.parametri.length];
        for(int i = 0; i < F.parametri.length; i++ ){
            gradiente[i] = 1.;
        }
        
        double prev_grad = 1.;
        while( Math.abs(E1 - E0) > exitDelta ){
            E0 = E1;

            // valutazione del gradiente
            for(int i=0; i<gradiente.length; i++){
                //prev_grad = gradiente[i];
                F.parametri[i] += prev_grad * scostamento;
                gradiente[i] = (valutaErroreFunzioneSuDataSet() - E0) / scostamento;
                F.parametri[i] -= prev_grad * scostamento;
            }

            // applicazione del gradiente
            System.out.println("-------------------------------------------------------");
            System.out.println(E0);
            for(int i=0; i<gradiente.length; i++){
                //System.out.println(i + ": " + F.parametri[i] + " + " + gradiente[i]);
                F.parametri[i] -= gradiente[i] * parametro_approccio;
                System.out.println(i + ": " + F.parametri[i]);
                //System.out.println();
                
            }
            
            E1 = valutaErroreFunzioneSuDataSet();
            System.out.println(E1);
            
        }
        
        return Math.abs(E1 - E0);
    }
    
    private double valutaErroreFunzioneSuDataSet(){
        double chi = 0.;
        
        for(int i=0; i<DS.X.size(); i++){
            chi += Math.pow((double)DS.Y.get(i) - F.f((double)DS.X.get(i)), 2.);
        }
        
        return Math.sqrt(chi);
    } 
}
