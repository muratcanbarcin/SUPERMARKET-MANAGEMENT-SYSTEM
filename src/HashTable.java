public class HashTable<K,V>{
    private int numberOfEntries;
    private HashEntry<String,Customer>[] hashtable;
    private static final double MAX_LOAD_FACTOR = 0.75;

    public HashTable(int initial_capacity){
        numberOfEntries = 0;
        initial_capacity = getNextPrime(initial_capacity);
        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[initial_capacity];
        hashtable = temp;
    }

    public int getHashIndex_SSF(String key) {  //gets hash index by SSF
        int hash = 0;

        String[] temp_key = key.replace("-", "").split("");

        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            hash += next_char;
        }
        return hash%hashtable.length;
    }

    public int getHashIndex_PAF(String key){
        int hash = 0;

        String[] temp_key = key.replace("-", "").split("");

        int power = temp_key.length -1;

        int z = 31; //constant

        for (int i = 0; i < temp_key.length; i++){
            char next_char = temp_key[i].charAt(0);
            hash += Math.pow(z, power) * next_char;
            power--;
        }
        return hash%hashtable.length;
    }

    public Customer getValue(String key){
        Customer result = null;

        int index = getHashIndex_PAF(key);
        index = locate(index,key);

        if (index != -1){
            result = hashtable[index].getValue();
        }
        return result;
    }

    public String remove(String key){
        String removedValue = null;

        int index = getHashIndex_PAF(key);
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
            if (hashtable[index].isCurrrent() && key.equals(hashtable[index].getKey())){
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

    public Customer put(String key,Customer value){
        if ((key==null) || (value==null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        
        else {
            Customer oldValue;
            int index = getHashIndex_PAF(key);
            index = probe(index,key);

            assert (index >= 0) && (index < hashtable.length);

            String purchase_date = value.getPurchases().get(0).getDate();
            String product_name = value.getPurchases().get(0).getProductName();

            if ((hashtable[index] == null) || (hashtable[index].isRemoved()) ){ 
                value.addPurchase(purchase_date, product_name);
                hashtable[index] = new HashEntry<String,Customer>(key, value);
                numberOfEntries++;
                oldValue = null;
            }
            else{
                oldValue = hashtable[index].getValue();
                oldValue.addPurchase(purchase_date, product_name);
                hashtable[index].setValue(oldValue);
            }

            if (isHashTableTooFull()) 
                enlargeHashTable();

            return oldValue;
        }
    }

    private int probe(int index, String key){
        boolean found = false;
        int removedStateIndex = -1;

        while( !found && (hashtable[index] != null)){
            if (hashtable[index].isCurrrent()){
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
        // System.out.println("------------" + Double.valueOf(numberOfEntries)/Double.valueOf(hashtable.length));
        // System.out.println("\n\n\n\n\n\n");
        
        HashEntry<String,Customer>[] oldtable = hashtable;
        int oldsize = hashtable.length;
        int newsize = getNextPrime(oldsize*2);

        @SuppressWarnings("unchecked")
        HashEntry<String,Customer>[] temp = (HashEntry<String,Customer>[])new HashEntry[newsize];

        hashtable = temp;

        numberOfEntries=0;

        for (int index = 0; index < oldsize; index++){
            if ((oldtable[index] != null) && oldtable[index].isCurrrent()){
                put(oldtable[index].getKey(),oldtable[index].getValue());
            }
        }
    }

    private boolean isHashTableTooFull(){
        return ((Double.valueOf(numberOfEntries )/ Double.valueOf(hashtable.length) )>= MAX_LOAD_FACTOR);
    }

    public int getentries(){ //for test
        return numberOfEntries;
    }

    public double getload(){ //for test
        return (Double.valueOf(numberOfEntries )/ Double.valueOf(hashtable.length));
    }

    public int getNextPrime(int num){
        int nextprime = -1;

        while (true){
            if (isPrime(num)){
                nextprime = num;
                break;
            }
            else 
                num+=1;
        }

        return nextprime;
    }

    public static  boolean isPrime(int num)
    {
        if(num<=1)
        {
            return false;
        }
       for(int i=2;i<=num/2;i++)
       {
           if((num%i)==0)
               return  false;
       }
       return true;
    }
}
