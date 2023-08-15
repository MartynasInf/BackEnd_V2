package com.example.Street_manager.Interface;
/**
 * The DataChecker interface is used for methods to check data in the service layer of entities.
 * @param <T> The type of entity for which data is checked
 */
public interface DataChecker<T> {
    Boolean checkIfIdExists(Long id);
}
