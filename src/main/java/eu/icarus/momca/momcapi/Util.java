package eu.icarus.momca.momcapi;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions.
 *
 * @author Daniel Jeller
 *         Created on 03.07.2015.
 */
public class Util {

    /**
     * Decodes a string that is %-encoded, e.g. {@code user%40mail.com} gets decoded to {@code user@mail.com}.
     *
     * @param string The string to decode.
     * @return The decoded string.
     */
    @NotNull
    public static String decode(@NotNull String string) {

        List<String> decodedTokens = new ArrayList<>(0);
        for (String token : string.split("/")) {
            try {
                decodedTokens.add(URLDecoder.decode(token, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return String.join("/", decodedTokens);

    }

    /**
     * %-encodes a string, e.g. {@code user@mail.com} gets encoded to {@code user%40mail.com}.
     *
     * @param string The string to encode.
     * @return The encoded string.
     */
    @NotNull
    public static String encode(@NotNull String string) {

        List<String> encodedTokens = new ArrayList<>(0);
        for (String token : string.split("/")) {
            try {
                encodedTokens.add(URLEncoder.encode(URLDecoder.decode(token, "UTF-8"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return String.join("/", encodedTokens);

    }

    @NotNull
    public static Element parseXml(@NotNull String xml) {

        Builder builder = new Builder();
        try {

            return builder.build(xml, null).getRootElement();

        } catch (ParsingException e) {
            throw new IllegalArgumentException("Failed to parse xml.", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
