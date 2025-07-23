package com.chatapp.service;

import com.chatapp.entity.Call;
import com.chatapp.repository.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallService {
    private final CallRepository callRepository;

    @Autowired
    public CallService(CallRepository callRepository) {
        this.callRepository = callRepository;
    }

    public Call saveCall(Call call) {
        return callRepository.save(call);
    }
}
