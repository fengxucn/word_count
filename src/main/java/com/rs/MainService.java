package com.rs;

import org.apache.log4j.Logger;

import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;
/**
 * created by Feng Xu on Oct. 19th
 */
public class MainService {
    static Logger logger = Logger.getLogger(MainService.class);
    public static void main(String[] args) {
        logger.info("loading the config file...");
        loadProps(args[0]);

        init();

        logger.info("start map reduce...");
        map();

        List<Map.Entry<String, Long>> result = sort(reduce());

        logger.info("saving now...");
        save(result, FINAL_RESULT + "word_count_all.txt");

        logger.info("I am DONE.");
    }
}
