/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import com.dukascopy.api.IBar;
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
public class ListaBarre implements Serializable{
    public String INFO;
    
    public ArrayList<IBar> Lista;
    
    public ListaBarre(String INFO){
        this.INFO = INFO;
        Lista = new ArrayList();
    }
    
    public void aggiungiBarra(IBar barra){
        Lista.add(barra);
    }    
    
    public int leggiNumeroBarre(){
        return Lista.size();
    }
    
    public IBar leggiBarra(int idx){
        return Lista.get(idx);
    }
    
    public static void scriviSuFile(ListaBarre CD) throws FileNotFoundException, IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(CD.INFO + ".ser"));
        objectOutputStream.writeObject(CD);
        objectOutputStream.close();
    }
    public static ListaBarre leggiDaFile(String NomeFile) throws FileNotFoundException, IOException, ClassNotFoundException{
        ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(NomeFile));
        ListaBarre CD = (ListaBarre) objectInputStream.readObject();
        objectInputStream.close();
        return CD;
    }
}


// esempio classe serializzabile
//import java.io.*;
//
//public class ObjectInputStreamExample {
//
//    public static class Person implements Serializable {
//        public String name = null;
//        public int    age  =   0;
//    }
//
//
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
//        ObjectOutputStream objectOutputStream =
//            new ObjectOutputStream(new FileOutputStream("data/person.bin"));
//
//        Person person = new Person();
//        person.name = "Jakob Jenkov";
//        person.age  = 40;
//
//        objectOutputStream.writeObject(person);
//        objectOutputStream.close();
//
//
//        ObjectInputStream objectInputStream =
//            new ObjectInputStream(new FileInputStream("data/person.bin"));
//
//        Person personRead = (Person) objectInputStream.readObject();
//
//        objectInputStream.close();
//
//        System.out.println(personRead.name);
//        System.out.println(personRead.age);
//    }
//}