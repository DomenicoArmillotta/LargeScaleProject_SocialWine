package login;

import com.google.common.collect.Lists;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

public class SaveLogin {
    private DB db = null;

    //faccio override di quello di default e modifico il compare in modo da
    // eitare l'ordine lessicografico
    private static class MyDBComparator implements DBComparator
    {
        @Override
        public String name() {
            return "simple";
        }

        @Override
        public byte[] findShortestSeparator(byte[] start, byte[] limit) {
            return start;
        }

        @Override
        public byte[] findShortSuccessor(byte[] key) {
            return key;
        }

        @Override
        public int compare(byte[] o1, byte[] o2) {
            //convertire in string e fare lo split
            String k1 = asString(o1);
            String k2 = asString(o2);
            String[] parts1 = k1.split(":");
            String[] parts2 = k2.split(":");
            int comparison = 0;
            //facciamo la comparazione tra elementi
            for(int i = 0; i < parts1.length && i < parts2.length; i++)
            {
                if(i != 1)
                {
                    comparison = parts1[i].compareTo(parts2[i]);
                }
                else {
                    comparison = (Integer.valueOf(parts1[i])).compareTo(Integer.valueOf(parts2[i]));
                }
                if(comparison != 0) break;
            }
            return comparison;
        }
    }

    public void openDB() {
        Options options = new Options();
        options.createIfMissing(true);
        options.comparator(new MyDBComparator());
        try{
            //la destroy serve ad eliminare gli object presenti nella precedente istanza del file
            //factory.destroy(new File("example"), options);
            db = Iq80DBFactory.factory.open(new File("UserLogin"), options);

        }
        catch (IOException ioe) { closeDB(); }
    }

    public void putAsString(String key, String value) {
        db.put(bytes(key), bytes(value));
    }

    public ArrayList<String> findKeysByPrefix(String prefix) {
        //faccio un iterator
        //l'ordine sarà lessicografico
        try (DBIterator iterator = db.iterator()) {
            ArrayList<String> keys = Lists.newArrayList();
            //inizia dal primo
            for (iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()) {
                //prende la key dall'uterator
                String key = asString(iterator.peekNext().getKey());
                //stop quando la key non inizia più con quel prefisso
                if (!key.startsWith(prefix)) {
                    break;
                }
                //metto le key nella lista e poi la return
                keys.add(key.substring(prefix.length()));
            }
            return keys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void closeDB() {
        try {
            if( db != null) db.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

}