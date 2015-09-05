import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];

        List<String> lines = new ArrayList<String>();
        Map<String, Integer> wordCountMap = new HashMap<String, Integer>();

        // Open the file
        Scanner fileScanner = null;
        try {
            File file = new File(this.inputFileName);
            fileScanner = new Scanner(file);
            Integer[] indexes = this.getIndexes();

            //read file
            while (fileScanner.hasNextLine()) {
                String strLine = fileScanner.nextLine();
                strLine = strLine.toLowerCase().trim();
                lines.add(strLine);
            }

            for (Integer index : indexes) {
                //get the line
                String line = lines.get(index);
                StringTokenizer st = new StringTokenizer(line, delimiters=this.delimiters);

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (! Arrays.asList(this.stopWordsArray).contains(token) ) {
                        if (wordCountMap.containsKey(token)) {
                            int freq = wordCountMap.get(token);
                            wordCountMap.put(token, freq + 1);
                        } else {
                            wordCountMap.put(token, 1);
                        }
                    }
                }
            }

            //sort
            wordCountMap = sortByValue(wordCountMap);

            Map<String, Integer> wordsCountSortedLexicographilyMap = sortByValue(wordCountMap);
//            System.out.println(this.wordCountMap);
//            System.out.println("-------------------------");

            int i = 0;
            for (String key: wordsCountSortedLexicographilyMap.keySet()) {
                ret[i] = key;
                i++;
                if (i > (ret.length - 1)){
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        } finally {
            fileScanner.close();
        }


        return ret;
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {

                int compResult = o2.getValue().compareTo(o1.getValue());

                //lexico
                if(compResult == 0)
                    return o1.getKey().compareTo(o2.toString());

                return compResult;

            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
