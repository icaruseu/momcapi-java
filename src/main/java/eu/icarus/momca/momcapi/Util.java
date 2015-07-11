package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 03.07.2015.
 */
public class Util {

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

}
