package com.bitvavo.engine;

public class EngineApp {
    public static void main(String[] args) {
        MatchEngineService matchEngineService = new MatchEngineService();
        matchEngineService.receiveAndProcessOrders();
    }
    
}
