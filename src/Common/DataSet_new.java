/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import Common.Sostituire.ContenitoreDati;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    public RandomAccessFile RAF;
    // single line size
    // timestamp long    8 bytes
    // ASK
    // volume double     8 bytes
    // open double
    // min  double
    // max  double
    // close double 
    // BID
    // volume
    // open double
    // min  double
    // max  double
    // close double 
    //  totale = 8 + 5 * 8 + 5 * 8 = 88 bytes
    
    public final int recordSize = 88; // bytes
    private HashMap<Long,Long> index;    // della forma Timestamp --> Posizione nel file
    
    
    DataSet_new(Period P, String ASSET ) throws FileNotFoundException, IOException{
        this.P = P;
        this.Asset = ASSET;
        TimeStamp = new ArrayList();
        AskBars = new ArrayList();
        BidBars = new ArrayList();
        
        RAF = new RandomAccessFile(this.Asset + "_" + this.P.toString() + ".dat", "rw");
        generateIndex();
    }
    
    private void generateIndex() throws IOException{
        index = new HashMap();
        long seek = 0;
        if ( RAF.length() > 0 ){
            while(seek < RAF.length()){
                RAF.seek(seek);
                index.put(RAF.readLong(), seek);
                seek += recordSize;
            }
        }
    }
    
    
    // database // TODO
    //public void connectDatabase(){
        
    //}
    private long currentSeek;
    private Barra currentAskBar;
    private Barra currentBidBar;
    
    private void readBars(long seek) throws IOException{
        RAF.seek(seek);
        currentSeek = seek;
        long TS = RAF.readLong();
        currentAskBar = new Barra(TS, RAF.readDouble(), RAF.readDouble(),RAF.readDouble(),RAF.readDouble(),RAF.readDouble());
        currentBidBar = new Barra(TS, RAF.readDouble(), RAF.readDouble(),RAF.readDouble(),RAF.readDouble(),RAF.readDouble());
    }
    private void writeBars(long seek, Barra askBar, Barra bidBar) throws IOException{
        RAF.seek(seek);
    // ASK
        RAF.writeLong(askBar.TS);
        RAF.writeDouble(askBar.Volume);
        RAF.writeDouble(askBar.Open);
        RAF.writeDouble(askBar.Low);
        RAF.writeDouble(askBar.High);
        RAF.writeDouble(askBar.Close);
    // BID
        RAF.writeDouble(bidBar.Volume);
        RAF.writeDouble(bidBar.Open);
        RAF.writeDouble(bidBar.Low);
        RAF.writeDouble(bidBar.High);
        RAF.writeDouble(bidBar.Close);
    }
    public void insertIntoDatabase(IBar AskBar, IBar BidBar) throws IOException{
        Barra askBar = new Barra(AskBar);
        Barra bidBar = new Barra(BidBar);
        if(index.containsKey(AskBar.getTime())){
            readBars(index.get(AskBar.getTime()));
            if ( ! currentAskBar.isEqual(askBar) || ! currentBidBar.isEqual(bidBar) ){
                this.writeBars(currentSeek, askBar, bidBar);
            }
        }
        else{
            index.put(askBar.TS, RAF.length());
            this.writeBars(RAF.length(), askBar, bidBar);
        }
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
    
    public Barra getAskBarAt(long Timestamp, Period P) throws Exception{
        if ( P.isSmallerThan(this.P) ){
            throw new Exception("Impossible to get a bar of period " + P.toString() + " from data with period " + this.P.toString());
        }
        // getting lower limit of the 
        if ( P.getInterval() = this.P.getInterval()){
            
            return this.AskBars.
        }
        long LowerLimit = Math.round((double)Timestamp / (double)P.getInterval())*P.getInterval();
        long UpperLimit = LowerLimit + P.getInterval();
        long tempo=LowerLimit;
        
        
        
        Barra OUT = new Barra();
        OUT.TS = LowerLimit;
        while(tempo <= UpperLimit){
            getAskBar
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
