package com.martins.martinsbank.service.serviceImpl;

import com.martins.martinsbank.config.JwtTokenProvider;
import com.martins.martinsbank.dto.*;
import com.martins.martinsbank.entity.Roles;
import com.martins.martinsbank.entity.User;
import com.martins.martinsbank.repository.UserRepository;
import com.martins.martinsbank.service.EmailService;
import com.martins.martinsbank.service.TransactionService;
import com.martins.martinsbank.service.UserService;
import com.martins.martinsbank.util.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .roles(Roles.valueOf("ROLE_ADMIN"))
                .build();
        User savedUser = userRepository.save(newUser);
        //send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created. " +
                        "\nYour Account Details: \n" +
                        "Account Name: " +
                        savedUser.getFirstName() +
                        " " + savedUser.getLastName() +
                        " " + savedUser.getOtherName() +
                        "\nAccount Number: " +
                        savedUser.getAccountNumber())

                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto) {
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

                EmailDetails loginAlert = EmailDetails.builder()
                        .subject("you are logged in!!!")
                        .recipient(loginDto.getEmail())
                        .messageBody("You logged into your account. If you did not initiate this request please contact customer care on 09051548332. \n Thank you")
                        .build();
                emailService.sendEmailAlert(loginAlert);
                return BankResponse.builder()
                        .responseCode("Login Successful")
                        .responseMessage(jwtTokenProvider.generateJwtToken(authentication))
                        .build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if account number exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " "
                                + foundUser.getLastName() + " "
                                + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }


    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        //check if amount is sufficient
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit..also check if the account exists
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
            if (!isDestinationAccountExists) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
            //checks if the account's balance is greater than the amount to transfer(the "compareTo" method returns -1 meaning false and 1 meaning true)
            if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            sourceAccountUser.setAccountBalance(sourceAccountUser
                    .getAccountBalance()
                    .subtract(request
                            .getAmount()));
            String sourceUserName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();


            userRepository.save(sourceAccountUser);
            EmailDetails debitAlert = EmailDetails.builder()
                    .subject("DEBIT ALERT")
                    .recipient(sourceAccountUser.getEmail())
                    .messageBody("The sum of " + request.getAmount() + " has been deducted from your account, Your current Balance is " + sourceAccountUser.getAccountBalance())
                    .build();

            emailService.sendEmailAlert(debitAlert);

            User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
            destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()) );
       //   String recipientUserName = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
            userRepository.save(destinationAccountUser);
            EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been credited to your account from, "  + sourceUserName + "," + " Your current balance is " + destinationAccountUser.getAccountBalance())
                .build();

            emailService.sendEmailAlert(creditAlert);

            TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

        //check if the amount you want to debit is more than the current balance

        //debit the account

        //get the account to credit

        //credit the account
    }
}
