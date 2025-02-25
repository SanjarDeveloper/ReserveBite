package com.example.reservebite.service;

import com.example.reservebite.entity.Table;
import com.example.reservebite.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TableService {
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<Table> getAllTables(){
        return tableRepository.findAll();
    }

    public void saveTable(Table table){
        tableRepository.save(table);
    }

    public Table getTableByID(Long tableId){
        return tableRepository.findById(tableId).orElseThrow(() -> new NoSuchElementException("No such table"));
    }

    public void deleteTable(Long tableId){
        tableRepository.deleteById(tableId);
    }

}
