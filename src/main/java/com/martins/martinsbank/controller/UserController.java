package com.martins.martinsbank.controller;

import com.martins.martinsbank.dto.*;
import com.martins.martinsbank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User account management APIs"
)
public class UserController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "create new user account",
            description = "creating a new user and assigning an account number to the new user"
    )
    @ApiResponse(
            responseCode = "201",
            description = "http status 201 CREATED"
    )
    @PostMapping("/createAccount")
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @Operation(
            summary = "balance enquiry",
            description = "Get details on user's balance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "http status 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
   public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
    return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }
}
