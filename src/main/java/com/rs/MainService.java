package com.rs;

import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;
/**
 * created by Feng Xu on Oct. 19th
 */
public class MainService {

    public static void main(String[] args) {
        loadProps(args[0]);

        init();

        map();

        List<Map.Entry<String, Long>> result = sort(reduce());

        save(result, FINAL_RESULT + "word_count_all.txt");
    }
}
