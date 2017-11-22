package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public class TestCustomerDatabase implements EntityDatabase {



    @Override
    public boolean isRegisteredId(UUID cardId) {

       return  CustomerDatabase.getInstance().isRegisteredId(cardId);
    }

    @Override
    public List<Customer> getCustomers() {
        return CustomerDatabase.getInstance().getCustomers();
    }
}
