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
import java.util.HashMap;
import java.util.TreeSet;

/**
 *
 * @author ariccini
 */
public class ContenitoreDati implements Serializable {
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
    
    String ASSET;
    HashMap<Period,HashMap<Long,Barra>> DATI_ASK;  // domanda
    HashMap<Period,HashMap<Long,Barra>> DATI_BID;  // offerta
    
    public ContenitoreDati(String ASSET){
        this.ASSET = ASSET;
        DATI_ASK = new HashMap();
        DATI_BID = new HashMap();
    }
    
    public void aggiungiDato(Period P, IBar askBar, IBar bidBar){
        if(DATI_ASK.containsKey(P)){
            if( ! DATI_ASK.get(P).containsKey(askBar.getTime()) ){
                DATI_ASK.get(P).put(Long.valueOf(askBar.getTime()), new Barra(askBar));
            }
        }
        else{
            DATI_ASK.put(P, new HashMap<Long,Barra>());
            DATI_ASK.get(P).put(Long.valueOf(askBar.getTime()), new Barra(askBar));
        }
        if(DATI_BID.containsKey(P)){
            if( ! DATI_BID.get(P).containsKey(bidBar.getTime()) ){
                DATI_BID.get(P).put(Long.valueOf(bidBar.getTime()), new Barra(bidBar));
            }
        }
        else{
            DATI_BID.put(P, new HashMap<Long,Barra>());
            DATI_BID.get(P).put(Long.valueOf(bidBar.getTime()), new Barra(bidBar));
        }
    }
    
    public ArrayList<Barra> recuperaDati(Period P, String ASK_BID){
        HashMap D;
        ArrayList<Barra> OUT = new ArrayList();
        if( ASK_BID.equals("ASK") ){
            D = DATI_ASK;
        }
        else{
            D = DATI_BID;
        }
        if( D.containsKey(P) ){
            D = (HashMap<Long,Barra>) D.get(P);
        }
        else{
            return null;
        }
        TreeSet<Long> TS = new TreeSet();
        for(Object L : D.keySet()){
            TS.add((Long) L);
        }
        for(Long L : (Long[]) TS.toArray()){
            
            OUT.add((Barra) D.get(L));
        }
        return OUT;
    }
}
