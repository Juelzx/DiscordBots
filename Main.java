import jdk.internal.jline.internal.Log;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends ListenerAdapter {

    private final static String timerPattern = "(\\d{2})";

    private List<String> todos = new ArrayList<>();

    int solution = 0;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token);
        builder.addEventListener(new Main());
        builder.buildAsync();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getMessage().getContentRaw().equals("!help") || event.getMessage().getContentRaw().equals("!commands")) {
            botReply(event, "Available commands are: \n!servers \nTHE? \n!addtodo {text} " +
                    "\n!todos \n!cleartodos \n!challenge" +
                    "\n!google {text} \n!tl {translation}");
        }

        if (event.getMessage().getContentRaw().equals("THE?")) {
            botReply(event, "MOVE!");
        }

        if (event.getMessage().getContentRaw().equals("!servers")) {
            botReply(event, "Center 365 \nSE 369 \nExtinction 480");
        }

        if (event.getMessage().getContentRaw().equals("!challenge")) {
            createMathChallenge(event);
        }

        if (event.getMessage().getContentRaw().matches("\\d+")) {
            if (Integer.parseInt(event.getMessage().getContentRaw()) == solution) {
                botReply(event, "nice one!");
            }
        }

        final Pattern pattern = Pattern.compile("!addtodo\\s*((.)*)");
        if (event.getMessage().getContentRaw().matches(pattern.toString())) {
            String msg = event.getMessage().getContentRaw();
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                addTask(matcher.group(1));
                String task = matcher.group(1);
                botReply(event, "todo: " + task + " added");
            }
        }

        if (event.getMessage().getContentRaw().equals("!todos")) {
            if (todos.size() == 0) {
                botReply(event, "No todo's found. Type !addtodo {item} to add one.");
            }
            for (String s : todos) {
                botReply(event, s);
            }
        }

        if (event.getMessage().getContentRaw().equals("!cleartodos")) {
            todos.clear();
            botReply(event, "deleted all todos");
        }

        if (event.getMessage().getContentRaw().startsWith("!google")) {
            String path = buildGoogleSearch(event);
            if (path != null) {
                botReply(event, path.replace(" ", "+"));
            }
        }

        if (event.getMessage().getContentRaw().startsWith("!tl")) {
            String path = buildTranslationSearch(event);
            if (path != null) {
                botReply(event, path.replace(" ", "%20"));
            }
        }

        if (event.getMessage().getContentRaw().startsWith("!spam")) {
            buildSpam(event);
        }
    }

    private void botReply(MessageReceivedEvent event, String message) {
        event.getChannel().sendMessage(message).queue();
    }

    private void addTask(String task) {
        todos.add(task);
    }

    private void createMathChallenge(MessageReceivedEvent event) {
        int max = 18;
        int min = 4;
        final Random random = new Random();
        int randomNumber1 = random.nextInt((max - min) + 1) + min;
        int randomNumber2 = random.nextInt((max - min) + 1) + min;
        solution = randomNumber1 * randomNumber2;
        botReply(event, "What's " + randomNumber1 + " * " + randomNumber2 + "?");
    }

    private String buildGoogleSearch(MessageReceivedEvent event) {
        final Pattern pattern = Pattern.compile("!google\\s*((.)*)");
        if (event.getMessage().getContentRaw().startsWith("!google")) {
            String text = event.getMessage().getContentRaw();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String googlePath = "https://www.google.de/search?q=";
                return googlePath + matcher.group(1);
            }
        }
        return null;
    }

    private String buildTranslationSearch(MessageReceivedEvent event) {
        final Pattern pattern = Pattern.compile("!tl\\s*((.)*)");
        if (event.getMessage().getContentRaw().startsWith("!tl")) {
            String text = event.getMessage().getContentRaw();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String path = "https://www.deepl.com/translator#en/de/";
                return path + matcher.group(1);
            }
        }
        return null;
    }

    private void buildSpam(MessageReceivedEvent event) {
        final Pattern pattern = Pattern.compile("!spam\\s*(\\d+)\\s*((.)*)");
        if (event.getMessage().getContentRaw().startsWith("!spam")) {
            String text = event.getMessage().getContentRaw();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                int spam = Integer.parseInt(matcher.group(1));
                if (spam > 20) {
                    spam = 0;
                    botReply(event, "calm down with the spam!");
                }
                for (int i = 0; i < spam; i++) {
                    botReply(event, matcher.group(2));
                }
            }
        }
    }
}
