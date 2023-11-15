public class HashEntry<K,V>{
	
    private K key;
    private V value;
    private States state;
    private enum States {CURRENT,REMOVED}

    HashEntry(K search_key,V data_value) {
          key=search_key;
          value=data_value;
          state=HashEntry.States.CURRENT;
    }     

    public K getKey() {
          return key;
    }

    public V getValue() {
            return value;
    }

    public void setValue(V new_value){
            value = new_value;
    }
    public boolean isRemoved(){
            return (state == States.REMOVED);
    }

    public void setToRemoved(){
            state = States.REMOVED;
    }
    public void setCurrent(){
            state = States.CURRENT;
    }
}

