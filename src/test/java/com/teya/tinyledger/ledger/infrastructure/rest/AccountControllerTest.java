package com.teya.tinyledger.ledger.infrastructure.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void depositsIntoAccount() throws Exception {
        mockMvc.perform(post("/accounts/ACCOUNT_1/deposits")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 10.50,
                      "referenceId": "deposit-ref"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountId").value("ACCOUNT_1"))
            .andExpect(jsonPath("$.type").value("DEPOSIT"))
            .andExpect(jsonPath("$.amount").value(10.5))
            .andExpect(jsonPath("$.referenceId").value("deposit-ref"));
    }

    @Test
    void withdrawsFromFundedAccount() throws Exception {
        mockMvc.perform(post("/accounts/ACCOUNT_1/deposits")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 10.50,
                      "referenceId": "seed-ref"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/accounts/ACCOUNT_1/withdrawals")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 4.25,
                      "referenceId": "withdraw-ref"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountId").value("ACCOUNT_1"))
            .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
            .andExpect(jsonPath("$.amount").value(4.25))
            .andExpect(jsonPath("$.referenceId").value("withdraw-ref"));
    }

    @Test
    void returnsCurrentBalance() throws Exception {
        mockMvc.perform(post("/accounts/ACCOUNT_1/deposits")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 10.50,
                      "referenceId": "balance-ref"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/accounts/ACCOUNT_1/balance"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value("ACCOUNT_1"))
            .andExpect(jsonPath("$.amount").value(10.5));
    }

    @Test
    void returnsTransactionHistory() throws Exception {
        mockMvc.perform(post("/accounts/ACCOUNT_1/deposits")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 10.50,
                      "referenceId": "deposit-ref"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/accounts/ACCOUNT_1/withdrawals")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 4.25,
                      "referenceId": "withdraw-ref"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/accounts/ACCOUNT_1/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value("ACCOUNT_1"))
            .andExpect(jsonPath("$.transactions.length()").value(2))
            .andExpect(jsonPath("$.transactions[0].type").value("DEPOSIT"))
            .andExpect(jsonPath("$.transactions[1].type").value("WITHDRAWAL"));
    }

    @Test
    void unknownAccountReturnsNotFound() throws Exception {
        mockMvc.perform(get("/accounts/UNKNOWN/balance"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Account not found: UNKNOWN"));
    }

    @Test
    void insufficientFundsReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(post("/accounts/ACCOUNT_1/withdrawals")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 1.00,
                      "referenceId": "no-funds"
                    }
                    """))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").value("Insufficient funds for account: ACCOUNT_1"));
    }
}
