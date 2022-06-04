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
// https://riptutorial.com/it/java/example/12930/diversi-modi-per-implementare-un-interfaccia-generica--o-estendere-una-classe-generica-
public class Funzione_Polinomiale implements Funzione{
    double parametri[];
    public Funzione_Polinomiale(int num_parametri){
        parametri = new double[num_parametri];
    }
    
    public double f(double x){
        double OUT = 0.;
        for(int i = 0; i < parametri.length; i++){
            OUT += parametri[i] * Math.pow(x, i);
        }
        return OUT;
    }
}
