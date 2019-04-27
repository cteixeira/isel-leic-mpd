package org.isel.jingle;

@FunctionalInterface
public interface SupplierByCountry<T> {
    T get(String country);
}
