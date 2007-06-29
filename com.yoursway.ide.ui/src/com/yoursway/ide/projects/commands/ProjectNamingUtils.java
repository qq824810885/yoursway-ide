package com.yoursway.ide.projects.commands;

import java.util.Iterator;

import com.yoursway.utils.ChainedIterator;
import com.yoursway.utils.RandomStringIterable;
import com.yoursway.utils.StringTupleIterable;

public class ProjectNamingUtils {
    
    private final static RandomStringIterable EXTREMELY = new RandomStringIterable(new String[] { "really",
            "extremely", "absolutely" });
    
    private final static RandomStringIterable GREAT = new RandomStringIterable(new String[] { "cool",
            "great", "wonderful", "exciting" });
    
    private final static RandomStringIterable RAILS_APP = new RandomStringIterable(new String[] {
            "rails_application", "web20_masterpiece" });
    
    @SuppressWarnings("unchecked")
    private final static StringTupleIterable ITERABLE = new StringTupleIterable(
            (Iterable<String>[]) new Iterable<?>[] { GREAT, RAILS_APP });
    
    @SuppressWarnings("unchecked")
    private final static StringTupleIterable ITERABLE_2 = new StringTupleIterable(
            (Iterable<String>[]) new Iterable<?>[] { EXTREMELY, GREAT, RAILS_APP });
    
    private final static Iterable<String> PROJECT_NAMES_ITERABLE = new Iterable<String>() {
        
        public Iterator<String> iterator() {
            return new ChainedIterator<String>(new NamesIterator(ITERABLE), new ChainedIterator<String>(
                    new NamesIterator(ITERABLE_2), new DumbNamesIterator()));
        }
        
    };
    
    private static final String PREFIX = "my";
    
    private final static class NamesIterator implements Iterator<String> {
        
        private final Iterator<String[]> iterator;
        
        public NamesIterator(StringTupleIterable iterable) {
            iterator = iterable.iterator();
        }
        
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        public String next() {
            String[] pieces = iterator.next();
            StringBuilder result = new StringBuilder();
            result.append(PREFIX);
            for (String piece : pieces) {
                if (result.length() > 0)
                    result.append('_');
                result.append(piece);
            }
            return result.toString();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    private final static class DumbNamesIterator implements Iterator<String> {
        
        private int nextIndex = 1;
        
        public boolean hasNext() {
            return false;
        }
        
        public String next() {
            int index = nextIndex++;
            return "my_cool_app_" + index;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    public static Iterable<String> getProjectNamesIterable() {
        return PROJECT_NAMES_ITERABLE;
    }
    
}
