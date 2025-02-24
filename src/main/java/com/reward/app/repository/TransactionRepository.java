package com.reward.app.repository;

import com.reward.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@link Transaction} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations
 * and custom queries for transaction-related database operations.
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves a list of transactions for a given customer that occurred after the specified date.
     *
     * @param customerId      the unique identifier of the customer
     * @param threeMonthsAgo  the cutoff date; transactions occurring after this date will be retrieved
     * @return a list of {@link Transaction} entities matching the criteria
     */
    List<Transaction> findByCustomerIdAndTransactionDateAfter(String customerId, LocalDate threeMonthsAgo);
}
