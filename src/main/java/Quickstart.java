import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

public class Quickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("users.home"), "src/main/resources");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(GmailScopes.GMAIL_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void listLabels(Gmail service, String userId) throws IOException {
      ListLabelsResponse response = service.users().labels().list(userId).execute();
      List<Label> labels = response.getLabels();
      for (Label label : labels) {
        System.out.println(label.toPrettyString());
      }
    }

  public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
      String query) throws IOException {
    ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

    List<Message> messages = new ArrayList<Message>();
    while (response.getMessages() != null) {
      messages.addAll(response.getMessages());
      if (response.getNextPageToken() != null) {
        String pageToken = response.getNextPageToken();
        response = service.users().messages().list(userId).setQ(query)
            .setPageToken(pageToken).execute();
      } else {
        break;
      }
    }

    //for (Message message : messages) {
    //  System.out.println(message.toPrettyString());
    //}

    return messages;
  }

    public static List<com.google.api.services.gmail.model.Thread> listThreadsMatchingQuery (Gmail service, String userId,
        String query) throws IOException {
      ListThreadsResponse response = service.users().threads().list(userId).setQ(query).execute();
      List<com.google.api.services.gmail.model.Thread> threads = new ArrayList<com.google.api.services.gmail.model.Thread>();
      while(response.getThreads() != null) {
        threads.addAll(response.getThreads());
        if(response.getNextPageToken() != null) {
          String pageToken = response.getNextPageToken();
          response = service.users().threads().list(userId).setQ(query).setPageToken(pageToken).execute();
        } else {
          break;
        }
      }
       
      for(com.google.api.services.gmail.model.Thread thread : threads) {
        System.out.println(thread);
      }
      return threads;
    }

    public static List<com.google.api.services.gmail.model.Thread> listThreadsWithLabels (Gmail service, String userId,
      List<String> labelIds) throws IOException {
        ListThreadsResponse response = service.users().threads().list(userId).setLabelIds(labelIds).execute();
        List<com.google.api.services.gmail.model.Thread> threads = new ArrayList<com.google.api.services.gmail.model.Thread>();
        while(response.getThreads() != null) {
          threads.addAll(response.getThreads());
          if(response.getNextPageToken() != null) {
            String pageToken = response.getNextPageToken();
            response = service.users().threads().list(userId).setLabelIds(labelIds)
                .setPageToken(pageToken).execute();
          } else {
            break;
          }
        }

        for(com.google.api.services.gmail.model.Thread thread : threads) {
          System.out.println(thread.toPrettyString());
        }
        return threads;
    }

    public static Message getMessage(Gmail service, String userId, String messageId)
      throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();

        //System.out.println("Message snippet: " + message.getPayload().getHeaders());

        return message;
    }

    public static com.google.api.services.gmail.model.Thread getThread(Gmail service, String userId, String threadId) throws IOException {
        com.google.api.services.gmail.model.Thread thread = service.users().threads().get(userId, threadId).execute();
        System.out.println("Thread id: " + thread.getId());
        System.out.println("No. of messages in this thread: " + thread.getMessages().size());
        System.out.println(thread.toPrettyString());

        return thread;
    }

    public static Optional<String> headerValueByName(List<MessagePartHeader> headers, String key) {
        //System.out.printf("%n "+headers);
        for(MessagePartHeader header: headers){
            //System.out.printf("%n"+header.get("name").toString() + " equals " + key +" : "+(header.get("name").toString().equals(key)));
            if(header.get("name").toString().equals(key)) {
                //System.out.printf("%n got it");
                String value = header.get("value").toString();

                System.out.printf("%n"+key+": "+value);

                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static List<String> userHeaderValueByName(List<MessagePartHeader> headers, String key) {
        Optional<String> output = headerValueByName(headers, key);
        if(output.isPresent()) {
            List<String> users = new ArrayList<>();
            for(String u: output.get().split(",")) {
                users.add(u.trim());
            }
            return users;
        } else {
            return new ArrayList<>();
        }
    }

    public static void printHeaders(Message message) {
        Optional<String> subject = headerValueByName(message.getPayload().getHeaders(), "Subject");
        List<String> from = userHeaderValueByName(message.getPayload().getHeaders(), "From");
        List<String> to = userHeaderValueByName(message.getPayload().getHeaders(), "To");
        List<String> cc = userHeaderValueByName(message.getPayload().getHeaders(), "Cc");
        Optional<String> date = headerValueByName(message.getPayload().getHeaders(), "Date");


        System.out.printf("%n");
    }

    public static void parseUsers(com.google.api.services.gmail.model.Thread thread) {
        List<Message> messages = thread.getMessages();

        Set<String> users = new HashSet<>();

        for(Message message: messages) {
            Set<String> us = parseUsersFromMessage(message);
            users.addAll(us);
        }


        System.out.printf("%n"+users);
    }

    public static Set<String> parseUsersFromMessage(Message message) {
        Set<String> users = new HashSet<>();

        List<String> from = userHeaderValueByName(message.getPayload().getHeaders(), "From");
        List<String> to = userHeaderValueByName(message.getPayload().getHeaders(), "To");
        List<String> cc = userHeaderValueByName(message.getPayload().getHeaders(), "Cc");

        users.addAll(from);
        users.addAll(to);
        users.addAll(cc);

        return users;
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();

        // Print the labels in the user's account.
        String user = "me";
        //ListLabelsResponse listResponse =
        //    service.users().labels().list(user).execute();
        //List<Label> labels = listResponse.getLabels();
        //if (labels.size() == 0) {
        //    System.out.println("No labels found.");
        //} else {
        //    System.out.println("Labels:");
        //    for (Label label : labels) {
        //        System.out.printf("- %s\n", label.getName());
        //    }
        //}

        List<Message> messages = listMessagesMatchingQuery(service, user, "meet, introduce");
        System.out.printf("%n "+messages.size());

        Message message = messages.get(0);
        String messageId = message.get("id").toString();

        Message retreived = getMessage(service, user, messageId);

        printHeaders(retreived);

        //listThreadsMatchingQuery(service, user, "meet, introduce");
        //listLabels(service, user);

        List<String> labels = new ArrayList<String>();
        labels.add("SENT");

        List<com.google.api.services.gmail.model.Thread> threads = listThreadsWithLabels(service, user, labels);
        //System.out.printf("%n "+ from);
        System.out.printf("%n"+threads.size());

        String threadId = threads.get(14).get("id").toString();
        com.google.api.services.gmail.model.Thread thread = getThread(service, user, threadId);

        parseUsers(thread);

        System.out.printf("%n how come?!");
    }

}