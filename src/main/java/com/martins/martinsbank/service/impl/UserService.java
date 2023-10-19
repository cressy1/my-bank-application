package com.martins.martinsbank.service.impl;

import com.martins.martinsbank.dto.BankResponse;
import com.martins.martinsbank.dto.UserRequest;
import org.springframework.stereotype.Service;


public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
