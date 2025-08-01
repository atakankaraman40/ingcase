package com.ingcase.digitalwallet.controller;

import com.ingcase.digitalwallet.model.dto.WalletCreateRequest;
import com.ingcase.digitalwallet.model.dto.WalletResponse;
import com.ingcase.digitalwallet.service.WalletService;
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

@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet-Controller", description = "Handles all operations related to wallets")
@RestController
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Creates a new wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Wallet created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid wallet request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #walletCreateRequest.customerId.toString()")
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletCreateRequest walletCreateRequest) {
        return new ResponseEntity<>(walletService.createWallet(walletCreateRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Lists all wallets for a given customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallets listed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WalletResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid wallet request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Wallets not found", content = @Content)

    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #customerId.toString()")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<WalletResponse>> getCustomerWallets(@Parameter(required = true) @PathVariable Long customerId) {
        return new ResponseEntity<>(walletService.getCustomerWallets(customerId), HttpStatus.OK);
    }
}
