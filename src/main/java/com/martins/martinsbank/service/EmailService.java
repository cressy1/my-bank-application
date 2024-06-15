package com.martins.martinsbank.service;

import com.martins.martinsbank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
