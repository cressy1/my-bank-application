package com.martins.martinsbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    @Schema(
            name = "user account name"
    )
    private String accountName;
    @Schema(
            name = "user account balance"
    )
    private BigDecimal accountBalance;
    @Schema(
            name = "user account number"
    )
    private String accountNumber;
}
