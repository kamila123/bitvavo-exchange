package com.bitvavo.exchange.utils;

import com.bitvavo.exchange.model.Order;
import com.bitvavo.exchange.model.Status;
import com.bitvavo.exchange.model.Type;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;

public class MatchEngineUtils {

    public static String generateMd5(String str) throws NoSuchAlgorithmException {
        
        byte[] bytesOfMessage = str.getBytes(StandardCharsets.UTF_8);

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] theMD5digest = md.digest(bytesOfMessage);
        
        StringBuilder hex = new StringBuilder();
        for (byte b : theMD5digest) {
            hex.append(String.format("%02x", b));
        }
        
        return hex.toString();
    }

    public static BigDecimal fromBigDecimalToString(String s) {
        return new BigDecimal(s);
    }

    public static Order createOrder(String id, Type type, String price, String quantity) {
        var order = new Order();
        order.setSequenceId(Long.valueOf(id));
        order.setType(type);
        order.setPrice(MatchEngineUtils.fromBigDecimalToString(price));
        order.setQuantity(MatchEngineUtils.fromBigDecimalToString(quantity));
        order.setStatus(Status.CREATED);
        order.setCreatedTime(Timestamp.from(Instant.now()));
        return order;
    }
    public static Type getTypeByChar(String str) {
        return switch (str) {
            case "B" -> Type.BUY;
            case "S" -> Type.SELL;
            default -> null;
        };
    }
}
