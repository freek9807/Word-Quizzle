package Server.Runnable;

import Models.Words;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
/**
 * Recupera da https://api.mymemory.translated.net la parola italiana
 *
 * @author Federico Pennino
 */
public class GetTranslationRunnable implements Callable<Words> {
    /**
     * La parola da tradurre
     */
    private final String word;
    /**
     * Il costruttore
     * @param word la parola da tradurre
     */
    public GetTranslationRunnable(String word) {
        this.word = word;
    }
    /**
     * Recupera in background la parola tradotta in modo poi da restituirla quando necessario
     * @return la coppia parola italiana-inglese
     * @throws Exception Se c'è un problema di connessione
     */
    @Override
    public Words call() throws Exception {
        URL url=new URL("https://api.mymemory.translated.net/get?q="+word+"&langpair=it|en");
        URLConnection uc=url.openConnection();
        uc.connect();

        BufferedReader in=new BufferedReader(new
                InputStreamReader(uc.getInputStream()));
        String line;
        StringBuilder sb=new StringBuilder();

        while((line=in.readLine())!=null){
            sb.append(line);
        }
        JsonObject object = new JsonParser().parse(sb.toString()).getAsJsonObject();
        return new Words(word,object.getAsJsonObject("responseData").getAsJsonPrimitive("translatedText").getAsString());
    }
}
