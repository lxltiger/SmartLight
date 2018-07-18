package com.example.ledwisdom1.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class DataStorageImpl<E> implements DataStorage<E> {

    protected List<E> data;

    public DataStorageImpl() {
        this.data = new ArrayList<>();
    }
    

    @Override
    public void add(E e) {

        if (!this.contains(e)) {
            this.data.add(e);
        }
    }

    public void add(E e, int location) {
        if (!this.contains(e)) {
            this.data.add(0, e);
        }
    }

    @Override
    public void add(List<E> e) {
        this.data.addAll(e);
    }

    @Override
    public boolean contains(E e) {
        return this.data.contains(e);
    }

    @Override
    public boolean contains(String attributeName, Object attributeValue) {

        for (E e : this.data) {

            try {
                Field field = e.getClass().getField(attributeName);
                Object fieldValue = field.get(e);
                if (fieldValue.equals(attributeValue))
                    return true;
            } catch (NoSuchFieldException | IllegalAccessException
                    | IllegalArgumentException e1) {
            }

        }
        return false;
    }

    @Override
    public E get(String attributeName, Object attributeValue) {

        for (E e : this.data) {

            try {
                Field field = e.getClass().getField(attributeName);
                Object fieldValue = field.get(e);
                if (fieldValue.equals(attributeValue))
                    return e;

            } catch (NoSuchFieldException | IllegalAccessException
                    | IllegalArgumentException e1) {
                // e1.printStackTrace();
            }

        }
        return null;
    }

    @Override
    public List<E> get() {
        return this.data;
    }

    @Override
    public E get(int location) {
        return this.data.get(location);
    }

    @Override
    public void remove(int location) {

        if (location >= 0 && location < this.data.size()) {
            this.data.remove(location);
        }
    }

    @Override
    public void remove(E e) {
        this.data.remove(e);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public void clear() {
        this.data.clear();
    }
}
