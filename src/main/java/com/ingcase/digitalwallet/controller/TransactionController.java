package com.ingcase.digitalwallet.controller;

import com.ingcase.digitalwallet.model.dto.DepositRequest;
import com.ingcase.digitalwallet.model.dto.TransactionResponse;
import com.ingcase.digitalwallet.model.dto.TransactionUpdateRequest;
import com.ingcase.digitalwallet.model.dto.WithdrawRequest;
import com.ingcase.digitalwallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction-Controller", description = "Handles all operations related to transactions.")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Creates a new deposit transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit completed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid deposit request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #depositRequest.customerId.toString()")
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest depositRequest) {
        return new ResponseEntity<>(transactionService.deposit(depositRequest), HttpStatus.OK);
    }

    @Operation(summary = "Creates a new withdraw transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdraw completed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid withdraw request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #withdrawRequest.customerId.toString()")
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest withdrawRequest) {
        return new ResponseEntity<>(transactionService.withdraw(withdrawRequest), HttpStatus.OK);
    }

    @Operation(summary = "List transactions for a given wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions listed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid transaction request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transactions not found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #customerId.toString()")
    @GetMapping("/list")
    public ResponseEntity<List<TransactionResponse>> getWalletTransactions(@RequestHeader("walletId") Long walletId,
                                                                           @RequestHeader("customerId") Long customerId) {
        return new ResponseEntity<>(transactionService.getWalletTransactions(walletId, customerId), HttpStatus.OK);
    }

    @Operation(summary = "Approve or deny a pending transaction based on the provided status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid transaction request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(@Parameter(required = true) @PathVariable Long transactionId,
                                                                 @Valid @RequestBody TransactionUpdateRequest transactionUpdateRequest) {
        return new ResponseEntity<>(transactionService.updateTransaction(transactionId, transactionUpdateRequest), HttpStatus.OK);
    }
}
