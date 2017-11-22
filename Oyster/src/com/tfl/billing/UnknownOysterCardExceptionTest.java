package com.tfl.billing;

import org.testng.annotations.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnknownOysterCardExceptionTest {
      @Test
      public void unknownOysterCardExceptionTest()
      {
          assertEquals( "Oyster Card does not correspond to a known customer. Id: 38400000-8cf0-11bd-b23e-10b96e4ef00e" , new UnknownOysterCardException( UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00e") ) .getMessage() ) ;
      }
}