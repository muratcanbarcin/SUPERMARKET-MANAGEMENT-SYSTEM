package src;

public class HashTable_DH<K,V>{
    private int numberOfEntries;
    private HashEntry<String,Customer>[] hashtable;

    private static String  SSForPAF;
    private static final double MAX_LOAD_FACTOR = 0.5;

    public HashTable_DH(int initial_capacity, String SSFForPAF){
        numberOfEntries = 0;

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[findNextPrime(initial_capacity)];
        hashtable = temp;
        this.SSForPAF = SSFForPAF;
    }

    public int getHashIndexSSF(String key) {  //gets hash index by SSF
        int hash = 0;

        String[] temp_key = key.replace("-", "").split("");

        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            hash += next_char;
        }
        return hash%hashtable.length;
    }

    public int getHashIndexPAF(String key){ //gets hash index by PAF

        int hash =0;
        double tempHash=0;
        String[] temp_key = key.replace("-", "").split("");
        int z = 33;
        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            tempHash += next_char * Math.pow(z,temp_key.length-(1+i));
            hash = ((int) tempHash)%hashtable.length;
        }
        return hash%hashtable.length;
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
        int i = 1;
        int start_index = index;

        while(!found && (hashtable[index] != null)){
            if (!hashtable[index].isRemoved() && key.equals(hashtable[index].getKey())){
                found = true;
            }
            else
                index = (index + i * hash2(index, key)) % hashtable.length;
                
            if (index == start_index){
                i++;
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
        int i = 1;
        
        int start_index = index;

        while (hashtable[index] != null && !hashtable[index].isRemoved()) {
            if (key.equals(hashtable[index].getKey())) {
                return index; // Key already exists at this index
            }
            index = (index + i * hash2(index, key)) % hashtable.length;

            if (start_index == index){
                i++;
            }
        }

        return index; // Return the index where the key can be inserted
    }

    public Customer put(String key,Customer value){
        if ((key==null) || (value==null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");

        else {
            Customer oldValue;
            int index;
            if(SSForPAF.equalsIgnoreCase("1")){
                index = getHashIndexSSF(key);
                index = probe(index,key);
            }
            else {
                index = getHashIndexPAF(key);
                index = probe(index,key);
            }

            assert (index >= 0) && (index < hashtable.length);

            if ((hashtable[index] == null) || hashtable[index].isRemoved() ){
                hashtable[index] = new HashEntry<>(key, value);
                numberOfEntries++;
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
        System.out.println("------------" + Double.valueOf(numberOfEntries)/Double.valueOf(hashtable.length));
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
        return ((Double.valueOf(numberOfEntries )/ Double.valueOf(hashtable.length) )== MAX_LOAD_FACTOR);
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
}

