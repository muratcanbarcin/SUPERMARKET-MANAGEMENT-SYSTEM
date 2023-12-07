package src;

public class HashTableLP<Key, Value> implements DictionaryInterface<Key, Value> {
    // Class variables
    private int numberOfEntries;
    private HashEntry<Key, Value>[] hashtable;
    private static String SSForPAF;
    private static double MAX_LOAD_FACTOR = 0.5;
    // Test variable to count collisions
    private static long COLLISION_COUNT;

    // Constructor
    public HashTableLP(int initial_capacity, String SSFForPAF, double MAX_LOAD_FACTOR) {
        numberOfEntries = 0;
        this.MAX_LOAD_FACTOR = MAX_LOAD_FACTOR;
        HashEntry<Key, Value>[] temp = (HashEntry<Key, Value>[]) new HashEntry[findNextPrime(initial_capacity)];
        hashtable = temp;
        this.SSForPAF = SSFForPAF;
        COLLISION_COUNT = 0;
    }

    // Hash function using a simple summation
    private int getHashIndexSSF(Key key) {
        String[] temp_key = key.toString().replace("-", "").split("");
        long sum;
        int i;
        for (sum = 0, i = 0; i < temp_key.length; i++) {
            sum += temp_key[i].charAt(0);
        }
        return (int) (sum) % hashtable.length;
    }

    // Hash function using Polynomial Accumulation Function (PAF)
    private int getHashIndexPAF(Key key) {
        double hash = 0;
        double tempHash = 0;
        String[] temp_key = key.toString().replace("-", "").split("");
        int z = 7;
        for (int i = 0; i < temp_key.length; i++) {
            char next_char = temp_key[i].charAt(0);
            tempHash += next_char * Math.pow(z, temp_key.length - (1 + i));
        }
        hash = (tempHash) % hashtable.length;
        return (int) hash % hashtable.length;
    }

    // Method to get a customer value associated with a key
    public Customer getValue(Key key) {
        Customer result = null;
        int index;
        // Determine the hashing strategy and get the hash index
        if (SSForPAF.equalsIgnoreCase("1")) {
            index = getHashIndexSSF(key);
            index = locate(index, key);
        } else {
            index = getHashIndexPAF(key);
            index = locate(index, key);
        }

        if (index != -1) {
            result = (Customer) hashtable[index].getValue();
        }
        return result;
    }

    // Method to remove a key-value pair from the hash table
    public void remove(Key key) {
        int index;
        // Determine the hashing strategy and get the hash index
        if (SSForPAF.equalsIgnoreCase("1")) {
            index = getHashIndexSSF(key);
            index = locate(index, key);
        } else {
            index = getHashIndexPAF(key);
            index = locate(index, key);
        }

        if (index != -1) {
            hashtable[index].setToRemoved();
            numberOfEntries--;
        }
    }

    // Method to locate an index for a given key
    public int locate(int index, Key key) {
        boolean found = false;

        while (!found && (hashtable[index] != null)) {
            if (!hashtable[index].isRemoved() && key.toString().equals(hashtable[index].getKey().toString())) {
                found = true;
            } else
                index = (index + 1) % hashtable.length;
        }
        int result = -1;
        if (found) {
            result = index;
        }
        return result;
    }

    // Method to insert a key-value pair into the hash table
    public void put(Key key, Value value) {
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else {
            Customer oldValue;
            int index;
            // Determine the hashing strategy and get the hash index
            if (SSForPAF.equalsIgnoreCase("1")) {
                index = getHashIndexSSF(key);
                index = probe(index, key, value);
            } else {
                index = getHashIndexPAF(key);
                index = probe(index, key, value);
            }

            assert (index >= 0) && (index < hashtable.length);

            // If the entry is empty or marked as removed, add a new entry
            if ((hashtable[index] == null) || hashtable[index].isRemoved()) {
                hashtable[index] = new HashEntry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            } else {
                // If the entry exists, update the value and handle purchases
                oldValue = (Customer) hashtable[index].getValue();
                if (oldValue.getPurchases().size() > 0)
                    oldValue.addPurchase(((Customer) value).getPurchases().get(0).getDate(),
                            ((Customer) value).getPurchases().get(0).getProductName());
                hashtable[index].setValue((Value) oldValue);
            }

            // Check if the hashtable is too full and needs to be resized
            if (isHashTableTooFull())
                resize(hashtable.length);
        }
    }

    // Method to probe and find the appropriate index for a key
    public int probe(int index, Key key, Value value) {
        boolean found = false;
        int removedStateIndex = -1;

        while (!found && (hashtable[index] != null)) {
            if (!hashtable[index].isRemoved()) {
                if (key.toString().equals(hashtable[index].getKey().toString())
                        && ((Customer) value).getCustomerName()
                        .equals(((Customer) hashtable[index].getValue()).getCustomerName())) {
                    found = true;
                } else {
                    index++;
                    COLLISION_COUNT++;
                    index = index % hashtable.length;
                }
            } else {
                if (removedStateIndex == -1) {
                    removedStateIndex = index;
                }
                index++;
                index = index % hashtable.length;
            }
        }

        if (found || (removedStateIndex == -1)) {
            return index;
        } else
            return removedStateIndex;
    }

    // Method to resize the hashtable when it becomes too full
    public void resize(int capacity) {
        HashEntry<Key, Value>[] oldtable = hashtable;
        int oldsize = capacity;
        int newsize = findNextPrime(oldsize * 2);

        @SuppressWarnings("unchecked")
        HashEntry<Key, Value>[] temp = (HashEntry<Key, Value>[]) new HashEntry[newsize];
        hashtable = temp;
        numberOfEntries = 0;

        // Rehash the entries into the new hashtable
        for (int index = 0; index < oldsize; index++) {
            if ((oldtable[index] != null)) {
                put(oldtable[index].getKey(), oldtable[index].getValue());
            }
        }
    }

    // Method to check if the hashtable is too full
    private boolean isHashTableTooFull() {
        return (((double) (numberOfEntries) / (double) (hashtable.length)) > MAX_LOAD_FACTOR);
    }

    // Method to find the next prime number
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

    // Method to check if a number is prime
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

    // Method to get the number of customers in the hashtable
    public int get_numberofcustomers() {
        return numberOfEntries;
    }

    // Method to get the collision count
    public long get_collisioncount() {
        return COLLISION_COUNT;
    }
}
