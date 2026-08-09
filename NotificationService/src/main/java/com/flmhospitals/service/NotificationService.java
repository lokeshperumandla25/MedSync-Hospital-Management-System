package com.flmhospitals.service;

import com.flmhospitals.dto.EmailRequest;

public interface NotificationService {

	void sendEmail(EmailRequest emailRequest);


}
