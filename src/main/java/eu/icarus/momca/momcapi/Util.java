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
    public static String encode(@NotNull String string) throws UnsupportedEncodingException {

        List<String> encodedTokens = new ArrayList<>(0);
        for (String token : string.split("/")) {
            encodedTokens.add(URLEncoder.encode(URLDecoder.decode(token, "UTF-8"), "UTF-8"));
        }
        return String.join("/", encodedTokens);

    }

}
