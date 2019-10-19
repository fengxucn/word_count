package com.rs.utils;

import com.rs.memory.DataPool;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Worker.dirInit;

public class Utils {
    static Logger logger = Logger.getLogger(Utils.class);

    public static List<Map.Entry<String, Long>> sort(Map<String, Long> map) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(map.entrySet());

        // Sort the list
        Collections.sort(list, (a,b) -> b.getValue().compareTo(a.getValue()));
        return list;
    }

    public static void save(List<Map.Entry<String, Long>> data, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            long total_words = 0;
            for(Map.Entry<String, Long> kv : data){
                total_words += kv.getValue();
            }
            writer.write( "Total words : " + total_words + " (Escape stop words: " + needEscapeStopWords() + ")");
            writer.newLine();
            writer.newLine();
            int topN = getTopN();
            writer.write( "-----------Top " + topN + " words-----------");
            writer.newLine();
            int index = 0;
            for (;index<topN;index++) {
                Map.Entry<String, Long> kv = data.get(index);
                writer.write(kv.getKey() + "," + kv.getValue());
                writer.newLine();
            }
            writer.write( "-----------Other words-----------");
            writer.newLine();
            for (;index<data.size();index++) {
                Map.Entry<String, Long> kv = data.get(index);
                writer.write(kv.getKey() + "," + kv.getValue());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO
        }
    }

    public static void init() {
        dirInit(FINAL_RESULT);
    }

    public static Map<String, Long> reduce() {
        int executors = getExecutorNumber();
        while (!isAllDone(executors)) {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, Long> count = new HashMap<>();
        for (List<Map.Entry<String, Long>> list : map_result) {
            for (Map.Entry<String, Long> kv : list) {
                String key = kv.getKey();
                Long value = kv.getValue();
                count.put(key, count.getOrDefault(key, (long) 0) + value);
            }
        }
        return count;
    }

    public static int getTotalSize(String path) {
        int size = 0;
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + Long.MAX_VALUE);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            logger.info("Respnse Code: " + responseCode);
            size = urlConnection.getContentLength();
            logger.info("Content-Length: " + size);

            if (responseCode != HttpURLConnection.HTTP_PARTIAL) {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
                size = 0;
            }
            urlConnection.disconnect();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            logger.error(mue);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error(ioe);
        }
        return size;
    }


    public static void map() {
        DataPool pool = new DataPool();

        Producer producer = new Producer(getUrls(), pool);
        logger.info("Start Producer: " + producer.getName());
        producer.start();

        int executors = getExecutorNumber();

        for (int i = 0; i < executors; i++) {
            Consumer consumer = new Consumer(pool);
            logger.info("Start Consumer: " + consumer.getName());
            consumer.start();
        }
    }

    private static CopyOnWriteArrayList<List<Map.Entry<String, Long>>> map_result = new CopyOnWriteArrayList<>();

    public static void done(List<Map.Entry<String, Long>> result) {
        map_result.add(result);
    }

    private static boolean isAllDone(int n) {
        return map_result.size() == n;
    }

    public static List<String> getWords(String line) {
        List<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile("[\\p{L}']+");
        Matcher m = p.matcher(line);

        while (m.find()) {
            String word = line.substring(m.start(), m.end());
            if (!needEscapeStopWords() || !isStopWords(word))
                result.add(word);
        }
        return result;
    }

    private static String[] STOP_WORDS_ARRAY = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
    private static Set<String> stopWords = null;

    private static boolean isStopWords(String word) {
        if (stopWords == null)
            stopWords = getStopWords();
        if (word.length() <= 1)
            return true;
        return stopWords.contains(word.toLowerCase());
    }
}
