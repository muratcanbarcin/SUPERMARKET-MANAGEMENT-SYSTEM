package src;

public interface DictionaryInterface<Key, Value>
{
    public void put(Key key, Value value);

    public void remove(Key key);

    public Customer getValue(Key key);

    public int locate(int index, Key key);

    public void resize(int capacity);
}

