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
public class TestComponent {
    public static void main(String[] argv){
        DataSet<Double,Double> DS = new DataSet<>();
        
        DS.addDataSetElement(1.,2.);
DS.addDataSetElement(1.,4.);
DS.addDataSetElement(2.,-1.);
DS.addDataSetElement(3.,-2.);
DS.addDataSetElement(4.,1.);
DS.addDataSetElement(5.,8.);
DS.addDataSetElement(6.,19.);
DS.addDataSetElement(7.,34.);
DS.addDataSetElement(8.,53.);
DS.addDataSetElement(9.,76.);
DS.addDataSetElement(10.,103.);
DS.addDataSetElement(11.,134.);
DS.addDataSetElement(12.,169.);
DS.addDataSetElement(13.,208.);
DS.addDataSetElement(14.,251.);
DS.addDataSetElement(15.,298.);
DS.addDataSetElement(16.,349.);
DS.addDataSetElement(17.,404.);
DS.addDataSetElement(18.,463.);
DS.addDataSetElement(19.,526.);
DS.addDataSetElement(20.,593.);
DS.addDataSetElement(21.,664.);
DS.addDataSetElement(22.,739.);
DS.addDataSetElement(23.,818.);
DS.addDataSetElement(24.,901.);
DS.addDataSetElement(25.,988.);
DS.addDataSetElement(26.,1079.);
DS.addDataSetElement(27.,1174.);
DS.addDataSetElement(28.,1273.);
DS.addDataSetElement(29.,1376.);
DS.addDataSetElement(30.,1483.);


       
        /*
a	13
b	-11
c	9
d	-7
e	5
f	1

a + b x + c x^2 ...
        */
        Funzione_Polinomiale FP = new Funzione_Polinomiale(3);
        FP.parametri[0]=0;
        FP.parametri[1]=0;
        FP.parametri[2]=0;
        //FP.parametri[3]=-6;
        //FP.parametri[4]=7;
        //FP.parametri[5]=2;
        
        Minimizzatore M = new Minimizzatore(FP);
        
        M.minimizza(DS);
        
        System.out.println("a: " + FP.parametri[0]);
        System.out.println("b: " + FP.parametri[1]);
        //System.out.println("c: " + FP.parametri[2]);
        //System.out.println("d: " + FP.parametri[3]);
        //System.out.println("e: " + FP.parametri[4]);
        //System.out.println("f: " + FP.parametri[5]);
        

    }
}
