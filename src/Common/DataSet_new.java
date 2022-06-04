/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ariccini
 */
public class DataSet_new implements Serializable {
    public ArrayList<Long> TimeStamp;
    public ArrayList<Barra> AskBars;
    public ArrayList<Barra> BidBars;
    public Period P;
    public String Asset;
    
    DataSet_new(Period P, String ASSET ){
        this.P = P;
        this.Asset = Asset;
        TimeStamp = new ArrayList();
        AskBars = new ArrayList();
        BidBars = new ArrayList();
    }
    
    // database // TODO
    //public void connectDatabase(){
        
    //}
    public void insertIntoDatabase(IBar AskBar, IBar BidBar){
        
    }
    
    public void addDataSetElement(IBar AskBar, IBar BidBar) throws Exception{
        if (AskBar.getTime() != BidBar.getTime()){
            throw new Exception("AskBar time differ from BidBar time.");
        }
        if (TimeStamp.contains(AskBar.getTime())) {
            throw new Exception("Data already present timestamp: " + AskBar.getTime());
        }
        
        // inserimento ordinato
        // do' per scontato che inserisco sempre informazioi piu aggiornato
        // quindi il controllo sull'ultimo dato inserito lo faccio per primo
        if ( AskBar.getTime() > TimeStamp.get(TimeStamp.size() - 1)){
            TimeStamp.add(AskBar.getTime());
            AskBars.add(new Barra(AskBar));
            BidBars.add(new Barra(BidBar));
        }
        else {
            int Idx = TimeStamp.size() - 1;
            while ( AskBar.getTime() < TimeStamp.get(Idx) ){
                Idx--;
            }
            Idx++;
            TimeStamp.add(Idx, AskBar.getTime());
            AskBars.add(Idx, new Barra(AskBar));
            BidBars.add(Idx, new Barra(BidBar));
        }
        
        insertIntoDatabase(AskBar, BidBar);
    }
    
    public void getAskBarAt(long Timestamp, Period P){
        if ( P.isSmallerThan(this.P) ){
            throw new Exception("Impossible to get a bar of period " + P.toString() + " from data with period " + this.P.toString());
        }
        
    }
    // Metodi di serializzazione/deserializzazione
    public static void scriviSuFile(ContenitoreDati CD, String nomeFile) throws FileNotFoundException, IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(nomeFile));
        objectOutputStream.writeObject(CD);
        objectOutputStream.close();
    }
    public static ContenitoreDati leggiDaFile(String NomeFile) throws FileNotFoundException, IOException, ClassNotFoundException{
        ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(NomeFile));
        ContenitoreDati CD = (ContenitoreDati) objectInputStream.readObject();
        objectInputStream.close();
        return CD;
    }
}
