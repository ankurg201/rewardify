package com.reward.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reward.app.model.Transaction;
import com.reward.app.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class JsonDataLoader {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public void loadJsonData() throws IOException {
        //if (transactionRepository.count() != 0) {  // Prevent duplicate insertion
            // ✅ 1. Clear old data
            transactionRepository.deleteAll();
            System.out.println("✅ Old transactions deleted from database.");

            // ✅ 2. Load new data from JSON
            transactionRepository.saveAll(loadTransactionsFromJson());
            System.out.println(" Initial JSON data loaded into H2 database!");
       // } else {
       //     System.out.println(" Data already exists. Skipping JSON load in H2 Database.");
       // }
    }

    public List<Transaction> loadTransactionsFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ Register the module for LocalDate

        String jsonContent = new String(Files.readAllBytes(new ClassPathResource("transactions.json").getFile().toPath()));
        return objectMapper.readValue(jsonContent, new TypeReference<List<Transaction>>() {});
    }
}
