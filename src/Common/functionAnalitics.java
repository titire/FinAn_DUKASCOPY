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
public class functionAnalitics {
    public double valore;
    private double valoreP;
    public double derivataPrima;
    private double derivataPrimaP;
    public double derivataSeconda;
    private double derivataSecondaP;

    public void addValue(double v){
        valoreP = valore;
        valore = v;
        derivataPrimaP = derivataPrima;
        derivataPrima = valore - valoreP;
        derivataSecondaP = derivataSeconda;
        derivataSeconda = derivataPrima - derivataPrimaP;
    }
}