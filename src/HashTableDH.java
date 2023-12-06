package src;

import java.time.Duration;
import java.time.Instant;

public class HashTableDH<K,V>{
    private int numberOfEntries;
    private HashEntry<String,Customer>[] hashtable;
    private static String  SSForPAF;
    private static final double MAX_LOAD_FACTOR = 0.5;
    //test
    private static Instant start = Instant.now();
    public static Duration PROBE_TIME = Duration.between(start, start); //eq 0
    private static int COLLISION_COUNT;
    private boolean resized = false;

    public HashTableDH(int initial_capacity, String SSFForPAF){
        numberOfEntries = 0;

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[findNextPrime(initial_capacity)];
        hashtable = temp;
        this.SSForPAF = SSFForPAF;
        COLLISION_COUNT = 0;
    }

    private int getHashIndexSSF(String key) {
        String[] temp_key = key.replace("-", "").split("");
   
        long sum;
        int i;
        for (sum=0, i=0; i < temp_key.length; i++) {
          //sum += ch[i];
          sum += temp_key[i].charAt(0);
        }
        return (int)(sum*sum)%hashtable.length; //sum^2 kabul edilir mi emin değilim ama hızlandırıyor
      }

    // private int getHashIndexSSF(String s) { //string fold 
    //     s = s.replaceAll("-", "");
    //     long sum = 0, mul = 1;
    //     for (int i = 0; i < s.length(); i++) {
    //       mul = (i % 4 == 0) ? 1 : mul * 256;
    //       sum += s.charAt(i) * mul;
    //     }
    //     return (int)(Math.abs(sum) % hashtable.length);
    // }


    private int getHashIndexPAF(String key){ //gets hash index by PAF
        double hash =0;
        double tempHash=0;
        String[] temp_key = key.replace("-", "").split("");
        int z = 7;
        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            tempHash += next_char * Math.pow(z,temp_key.length-(1+i));
        }
        hash = (tempHash)%hashtable.length;

        return (int)hash%hashtable.length;
    }

    public Customer getValue(String key){
        Customer result = null;
        int index;
        if(SSForPAF.equalsIgnoreCase("1")){
            index = getHashIndexSSF(key);
            index = locate(index,key);
        }
        else {
            index = getHashIndexPAF(key);
            index = locate(index,key);
        }

        if (index != -1){
            result = hashtable[index].getValue();
        }
        return result;
    }

    public String remove(String key){
        String removedValue = null;
        int index;
        if(SSForPAF.equalsIgnoreCase("1")){
            index = getHashIndexSSF(key);
            index = locate(index,key);
        }
        else {
            index = getHashIndexPAF(key);
            index = locate(index,key);
        }


        if (index != -1){
            removedValue = hashtable[index].getValue().getCustomerName();
            hashtable[index].setToRemoved();
            numberOfEntries--;
        }
        return removedValue;
    }

    private int locate(int index,String key){
        boolean found = false;
        int start_index = index;

        while(!found && (hashtable[index] != null)){
            if (!hashtable[index].isRemoved() && key.equals(hashtable[index].getKey())){
                found = true;
            }
            else{
                index += hash2(start_index, key);
                index = index % hashtable.length;
            }                
        }
        int result = -1;
        if (found){
            result = index;
        }
        return result;
    }

    private int hash2(int hash1, String key) {
        return 7 - (hash1 % 7);
    }

    private int probe(int index, String key) {
        int hashed = hash2(index, key);

        while (hashtable[index] != null && !hashtable[index].isRemoved()) {
            if (key.equals(hashtable[index].getKey())) {
                return index; // Key already exists at this index
            }
            index += hashed;
            index = index % hashtable.length;
        }

        return index; // Return the index where the key can be inserted
    }

    public Customer put(String key,Customer value){
        if ((key==null) || (value==null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");

        else {
            Customer oldValue;
            int index;
            int start_index = 0;

            if(SSForPAF.equalsIgnoreCase("1")){
                index = getHashIndexSSF(key);
                start_index = index;
                index = probe(index,key);
            }
            else {
                index = getHashIndexPAF(key);
                start_index = index;
                index = probe(index,key);
            }

            assert (index >= 0) && (index < hashtable.length);

            if ((hashtable[index] == null) || hashtable[index].isRemoved() ){
                hashtable[index] = new HashEntry<>(key, value);
                numberOfEntries++;
                if ((start_index != index) && !resized)
                    COLLISION_COUNT++;
                oldValue = null;
            }
            else{
                oldValue = hashtable[index].getValue();
                oldValue.addPurchase(value.getPurchases().get(0).getDate(), value.getPurchases().get(0).getProductName());
                hashtable[index].setValue(oldValue);
            }

            if (isHashTableTooFull()) resize();

            return oldValue;
        }
    }

    private void resize(){
        //System.out.println("------------" + Double.valueOf(numberOfEntries)/Double.valueOf(hashtable.length));
        HashEntry<String,Customer>[] oldtable = hashtable;
        int oldsize = hashtable.length;
        int newsize = findNextPrime(oldsize*2);

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[newsize];
        hashtable = temp;
        numberOfEntries = 0;

        for (int index = 0; index < oldsize; index++){
            if ((oldtable[index] != null)){
                put(oldtable[index].getKey(),oldtable[index].getValue());
            }
        }
    }

    private boolean isHashTableTooFull(){
        return ((double)numberOfEntries/ (double)hashtable.length > MAX_LOAD_FACTOR);
    }

    private static int findNextPrime(int n) {
        if (n < 2) {
            return 2;
        }

        int nextNumber = n + 1;
        while (!isPrime(nextNumber)) {
            nextNumber++;
        }

        return nextNumber;
    }

    private static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }

        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
    public int get_numberofcustomers(){return numberOfEntries;}
    public int get_collisioncount(){return COLLISION_COUNT;}
}

