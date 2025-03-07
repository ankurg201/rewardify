# rewardify
A Reward Points Calculation System
# Reward Points Calculation Application

This is a Spring Boot application that processes reward points for customers based on their recent transactions. The application calculates reward points based on the amount spent, categorizes them by month, and returns monthly reward details for particular customer.

## Features

- **Points Calculation:**  
  - For amounts greater than $100, the reward is 2 points per dollar exceeding $100.
  - For amounts between $50 and $100, the reward is 1 point per dollar between $50 and $100.
  - Transactions with amounts less than $50 do not earn any reward points.

- **Monthly Rewards Calculation:**  
  - Rewards are calculated for particular customer, grouped by month. Each transaction's points are accumulated for the respective month.
  - Points for customer are calculated per month, and total points are provided.

- **Exception Handling:**  
  - The application handles exceptions such as invalid date formats, missing data in transactions, and empty transaction lists.

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven
- Spring Boot

### Installation

1. Clone the repository:
git clone https://github.com/ankurg201/rewardify

2. Navigate to the project directory:
cd reward

3. Build the project using Maven:
mvn clean install

4. Run the Spring Boot application:
mvn spring-boot:run

Once the application starts, it will be available on the default port 8080.

### API Endpoints

Calculate Monthly Rewards
Endpoint: GET /rewards/calculate/{cutomerId}
Description: Calculate monthly and Total rewards points for a recent transactions for particular customer. .

   ### Response:

{
    "reward": {
        "customerId": "C001",
        "totalPoints": 135,
        "monthlyPoints": {
            "2025-01": 90,
            "2024-12": 45
        }
    }
}

### Running Tests:

Unit Tests
Run all unit tests:

mvn test

Integration Tests
Run integration tests:

mvn verify



