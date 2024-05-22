package com.example.demo.ws.ui.model.Service;

import com.example.demo.ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressServiceIfc {
    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}
