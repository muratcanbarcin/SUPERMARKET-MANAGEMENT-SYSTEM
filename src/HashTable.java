public class HashTable<K,V>{
    private int numberOfEntries;
    private HashEntry<String,Customer>[] hashtable;
    private boolean initialized = false;
    private static final double MAX_LOAD_FACTOR = 0.5;

    public HashTable(int initial_capacity){
        numberOfEntries = 0;

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[initial_capacity];
        hashtable = temp;
        initialized=true;
    }

    public int getHashIndex(String key) {  //gets hash index by SSF
        int hash = 0;

        String[] temp_key = key.replace("-", "").split("");

        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            hash += next_char;
        }
        return hash%hashtable.length;
    }

    public Customer getValue(String key){
        Customer result = null;

        int index = getHashIndex(key);
        index = locate(index,key);

        if (index != -1){
            result = hashtable[index].getValue();
        }
        return result;
    }

    public String remove(String key){
        String removedValue = null;

        int index = getHashIndex(key);
        index = locate(index,key);

        if (index != -1){
            removedValue = hashtable[index].getValue().getCustomerName();
            hashtable[index].setToRemoved();
            numberOfEntries--;
        }
        return removedValue;
    }

    private int locate(int index,String key){
        boolean found = false;

        while(!found && (hashtable[index] != null)){
            if (!hashtable[index].isRemoved() && key.equals(hashtable[index].getKey())){
                found = true;
            }
            else
                index = (index+1) % hashtable.length;
        }
        int result = -1;
        if (found){
            result = index;
        }
        return result;
    }

    public Customer put(String key,Customer value,String purchase_date,String product_name){
        if ((key==null) || (value==null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        
        else {
            Customer oldValue;
            int index = getHashIndex(key);
            index = probe(index,key);

            assert (index >= 0) && (index < hashtable.length);

            if ((hashtable[index] == null) || hashtable[index].isRemoved() ){
                hashtable[index] = new HashEntry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            }
            else{
                oldValue = hashtable[index].getValue();
                oldValue.addPurchase(purchase_date, product_name);
                hashtable[index].setValue(oldValue);
            }

            if (isHashTableTooFull()) enlargeHashTable();

            return oldValue;
        }
    }

    private int probe(int index, String key){
        boolean found = false;
        int removedStateIndex = -1;

        while( !found && (hashtable[index] != null)){
            if (!hashtable[index].isRemoved()){
                if (key.equals(hashtable[index].getKey())){
                    found = true;
                }
                else 
                    index = (index + 1) % hashtable.length;
            }
            else{
                if (removedStateIndex == -1){
                    removedStateIndex = index;
                }
                index = (index+1) % hashtable.length;
            }
        }

        if (found || (removedStateIndex == -1)){
            return index;
        }
        else
            return removedStateIndex;
    }

    private void enlargeHashTable(){
        System.out.println("------------" + Double.valueOf(numberOfEntries)/Double.valueOf(hashtable.length));
        HashEntry<String,Customer>[] oldtable = hashtable;
        int oldsize = hashtable.length;
        int newsize = oldsize*2;

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[newsize];
        hashtable = temp;
        numberOfEntries = 0;

        for (int index = 0; index < oldsize; index++){
            if ((oldtable[index] != null)){
                put(oldtable[index].getKey(),oldtable[index].getValue(),"","");
                numberOfEntries++;
            }
        }
    }

    private boolean isHashTableTooFull(){
        return ((Double.valueOf(numberOfEntries )/ Double.valueOf(hashtable.length) )== MAX_LOAD_FACTOR);
    }
}
