package com.tfl.billing;

import java.util.*;

class Cache {
      private final HashMap <UUID,  ArrayList< JourneyEvent >> journeys_Made_By_A_Customer = new HashMap<>();
      public ArrayList<JourneyEvent > get_Journeys_By_Customer (UUID cardId )
      {
           ArrayList <JourneyEvent> emptyArrayList = new ArrayList<>() ;
           if ( ! (journeys_Made_By_A_Customer.containsKey ( cardId ) ) )
           {
               return emptyArrayList ;
           }
          ArrayList <JourneyEvent> auxiliaryArrayList = journeys_Made_By_A_Customer.get(cardId) ;
          ArrayList <JourneyEvent> actualArrayList = new ArrayList<>() ;
          for ( JourneyEvent journeyEvent : auxiliaryArrayList )
          {
              actualArrayList.add ( journeyEvent ) ;
          }
          return actualArrayList ;
      }
      public void add_Journey ( JourneyEvent journeyEvent  )
      {
          ArrayList< JourneyEvent > journeys = new ArrayList < JourneyEvent > () ;
          UUID cardId = journeyEvent.cardId() ;
          if (  journeys_Made_By_A_Customer .containsKey ( cardId )  )
          {
              journeys = journeys_Made_By_A_Customer.get(cardId);
          }
          System.out.println ( cardId ) ;
          journeys.add(journeyEvent);
          journeys_Made_By_A_Customer.put(cardId, journeys);
      }
}
