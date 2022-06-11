/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.Sostituire;

import java.util.ArrayList;

/**
 *
 * @author ariccini
 */
public class DataSet<X_type ,Y_type> {
    public ArrayList<X_type> X;
    public ArrayList<Y_type> Y;
    
    DataSet(){
        X = new ArrayList<X_type>();
        Y = new ArrayList<Y_type>();
    }
    
    public void addDataSetElement(X_type x, Y_type y){
        X.add(x);
        Y.add(y);
    }
}
