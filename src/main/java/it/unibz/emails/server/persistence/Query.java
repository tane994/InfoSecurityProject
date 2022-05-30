package it.unibz.emails.server.persistence;

import it.unibz.emails.server.persistence.exceptions.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.*;

public class Query<T> {
    private final String query;
    private final List<?> parameters;
    public final Class<T> returnClass;

    public Query(String query, Class<T> returnClass, Object... parameters) {
        this.query = query;
        this.returnClass = returnClass;
        this.parameters = List.of(parameters);
    }

    public List<T> run() {
        return createObjects(Db.getInstance().select(query, parameters));
    }

    private List<T> createObjects(List<Map<String,Object>> results) {
        List<T> elements = new ArrayList<>();

        if (returnClass.getConstructors().length==0 || returnClass.getConstructors()[0].getParameters().length==0 || results.size()==0)
            return elements;
        else if (returnClass.getConstructors()[0].getParameters().length != results.get(0).size()) {
            throw new InstantiationException("Number of parameters mismatched when creating objects");
        }
        else {
            Constructor<T> primaryConstructor = (Constructor<T>) returnClass.getConstructors()[0];

            for (Map<String, Object> res : results) {
                try {
                    elements.add(primaryConstructor.newInstance(res.values().toArray()));
                } catch (Exception e) {
                    throw new InstantiationException("Type mismatch when creating objects", e);
                }
            }
        }
        return elements;
    }

}
