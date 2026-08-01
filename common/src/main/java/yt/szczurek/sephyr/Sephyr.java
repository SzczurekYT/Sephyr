package yt.szczurek.sephyr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sepple.Sepple;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOG.info("The string from Rust is \"{}\"", Sepple.getString());
    }
}
